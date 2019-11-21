package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.SamplingDataOrderSpecifier;
import ca.gc.dfo.psffs.domain.objects.*;
import ca.gc.dfo.psffs.domain.objects.lookups.*;
import ca.gc.dfo.psffs.domain.repositories.SamplingEntryRepository;
import ca.gc.dfo.psffs.export.extract.ExtractReadyService;
import ca.gc.dfo.psffs.forms.DTSamplingDataAdvancedForm;
import ca.gc.dfo.psffs.forms.SamplingDataListForm;
import ca.gc.dfo.psffs.json.CellHeader;
import ca.gc.dfo.psffs.json.DTSDList;
import ca.gc.dfo.psffs.json.SamplingDataPatchRequest;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.psffs.domain.repositories.SamplingDataRepository;
import ca.gc.dfo.psffs.forms.SamplingDataAdvancedForm;
import ca.gc.dfo.psffs.forms.SamplingDataForm;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ca.gc.dfo.psffs.domain.QueryDSLExpressionUtil.qAnd;
import static ca.gc.dfo.psffs.domain.QueryDSLExpressionUtil.qOr;

@Service
public class SamplingDataService extends BaseSamplingService implements ExtractReadyService<SamplingData>
{
    @Autowired
    private SamplingDataRepository samplingDataRepository;

    @Autowired
    private SamplingEntryRepository samplingEntryRepository;

    @Autowired
    private LookupService lookupService;

    /* [Possible solution to cross join issue]
    @PersistenceContext
    private EntityManager entityManager;


    private List<Community> getList(int pageNo, String keyword, int rowsOnPage){

        int offset = (pageNo -1) * rowsOnPage;
        int limit = rowsOnPage;

        JPAQuery<Community> query = new JPAQuery<Community>(entityManager);

        QCommunity qCommunity = QCommunity.community;
        QAccount qAccount = QAccount.account;
        QAccountProfile qAccountProfile = QAccountProfile.accountProfile;

        return query
            .from(qCommunity)
            .innerJoin(qCommunity.account ,qAccount)
            .innerJoin(qAccount.profile, qAccountProfile)
            .where(qAccountProfile.nickname.like("%"+keyword+"%"))
            .orderBy(qCommunity.articleId.desc())
            .offset(offset)
            .limit(limit)
        .fetch();

     */

    @PersistenceContext
    private EntityManager entityManager;

    private static final List<Integer> UNMARKABLE_STATUS_LIST = Arrays.asList(SamplingDataStatus.ENTERED_STS_ID);

    public DTSDList fetchDataByAdvancedForm(DTSamplingDataAdvancedForm advancedForm)
    {
        BooleanExpression exp = getAdvancedCellCriteriaExpression(advancedForm);
        DTSDList sdList;

        Long originalRecordCount = manualCountSamplingEntries(exp);
        boolean filtered = false;
        if (originalRecordCount > 0) {
            String searchFilter = advancedForm.getSearchValue();
            if (searchFilter != null) {
                searchFilter = searchFilter.trim();
                if (searchFilter.length() > 0) {
                    Integer numFilter;
                    try {
                        numFilter = Integer.parseInt(searchFilter);
                    } catch (NumberFormatException nfe) {
                        numFilter = null;
                    }

                    BooleanExpression additionalCriteria = null;
                    if (numFilter != null) {
                        additionalCriteria = addNumberFilters(numFilter);
                    }
                    additionalCriteria = qOr(addStringFilters(searchFilter), additionalCriteria);

                    if (additionalCriteria != null) {
                        exp = qAnd(additionalCriteria, exp);
                        filtered = true;
                    }
                }
            }

            sdList = fetchListByFormAndExpression(advancedForm, exp, originalRecordCount, filtered);
        } else {
            sdList = new DTSDList();
            sdList.setITotalRecords(0);
            sdList.setITotalDisplayRecords(0);
            sdList.setStart(0);
            sdList.setData(new ArrayList<>());
        }
        sdList.setSEcho(advancedForm.getSEcho());

        return sdList;
    }

    private BooleanExpression getAdvancedCellCriteriaExpression(SamplingDataAdvancedForm advancedForm)
    {
        QSamplingEntry qry = QSamplingEntry.samplingEntry;
        BooleanExpression exp = null;
        List<String> sampleTypes = Arrays.asList(advancedForm.getSamplingTypes());
        if (sampleTypes.contains("o")) exp = qOr(qry.otolithSampledInd.eq(true), exp);
        if (sampleTypes.contains("s")) exp = qOr(qry.stomachSampledInd.eq(true), exp);
        if (sampleTypes.contains("c")) exp = qOr(qry.frozenSampledInd.eq(true), exp);
        if (sampleTypes.contains("w")) exp = qOr(qry.weightSampledInd.eq(true), exp);

        if (advancedForm.getSource().equals(SamplingDataAdvancedForm.SourceType.ONE)) {
            exp = qAnd(qry.sampling.samplingCode.eq(advancedForm.getSourceSampleId()), exp);
        } else {
            exp = qAnd(qry.sampling.yearCreated.eq(advancedForm.getYear()), exp);
            exp = qAnd(qry.sampling.speciesId.eq(advancedForm.getSampleSpeciesId()), exp);

            Integer dataSourceId = advancedForm.getDataSourceId();
            if (dataSourceId != null) exp = qAnd(qry.sampling.dataSourceId.eq(dataSourceId), exp);
            Integer bycatchInd = advancedForm.getBycatchInd();
            if (bycatchInd != null) exp = qAnd(qry.sampling.cell.bycatchInd.eq(bycatchInd), exp);
            Integer countryId = advancedForm.getCountryId();
            if (countryId != null) exp = qAnd(queryORsEqual(countryId, Integer.class,
                    qry.sampling.lengthFrequency.country.id, qry.sampling.tripSetSpecies.countryId), exp);
            Integer quarterId = advancedForm.getQuarterId();
            if (quarterId != null) exp = qAnd(queryORsEqual(quarterId, Integer.class,
                    qry.sampling.lengthFrequency.quarter.id, qry.sampling.tripSetSpecies.quarterId), exp);
            Integer nafoDivisionId = advancedForm.getNafoDivisionId();
            if (nafoDivisionId != null) exp = qAnd(queryORsEqual(nafoDivisionId, Integer.class,
                    qry.sampling.lengthFrequency.nafoDivision.id, qry.sampling.tripSetSpecies.nafoDivisionId), exp);
            String unitAreaData = advancedForm.getUnitAreaData();
            if (unitAreaData != null && unitAreaData.trim().length() > 0 && unitAreaData.contains(";")) {
                Integer unitAreaId = Integer.valueOf(unitAreaData.substring(0, unitAreaData.indexOf(";")));
                exp = qAnd(queryORsEqual(unitAreaId, Integer.class,
                        qry.sampling.lengthFrequency.unitArea.id, qry.sampling.tripSetSpecies.unitAreaId), exp);
            }
            Integer lengthCategoryId = advancedForm.getVesselLengthCategoryId();
            if (lengthCategoryId != null)
                exp = qAnd(qry.sampling.lengthFrequency.vessel.lengthCategory.id.eq(lengthCategoryId), exp);
            Integer gearId = advancedForm.getGearId();
            if (gearId != null) exp = qAnd(queryORsEqual(gearId, Integer.class,
                    qry.sampling.lengthFrequency.gear.id, qry.sampling.tripSetSpecies.gearId), exp);
            Float meshSize = advancedForm.getMeshSizeMillimeters();
            if (meshSize != null) exp = qAnd(qry.sampling.lengthFrequency.meshSizeMillimeters.eq(meshSize), exp);
            Integer observerCompanyId = advancedForm.getObserverCompanyId();
            if (observerCompanyId != null)
                exp = qAnd(qry.sampling.tripSetSpecies.observerCompanyId.eq(observerCompanyId), exp);
        }
        return exp;
    }

    private BooleanExpression addNumberFilters(Integer numberFilter)
    {
        QSamplingEntry qry = QSamplingEntry.samplingEntry;
        BooleanExpression numCriteria = qry.samplingData.samplingDataId.eq(numberFilter.longValue());
        numCriteria = qOr(qry.samplingData.storageNbr.eq(numberFilter.longValue()), numCriteria);
        numCriteria = qOr(qry.length.eq(numberFilter), numCriteria);
        return numCriteria;
    }

    private BooleanExpression addStringFilters(String stringFilter)
    {
        QSamplingEntry qry = QSamplingEntry.samplingEntry;
        BooleanExpression stringCriteria = qry.sex.containsIgnoreCase(stringFilter);
        stringCriteria = qOr(qry.maturity.legacyCode.containsIgnoreCase(stringFilter), stringCriteria);
        stringCriteria = qOr(qry.samplingData.tag.eq(stringFilter), stringCriteria);
        stringCriteria = qOr(qry.samplingData.status.englishDescription.containsIgnoreCase(stringFilter), stringCriteria);
        stringCriteria = qOr(qry.samplingData.status.frenchDescription.containsIgnoreCase(stringFilter), stringCriteria);
        return stringCriteria;
    }

    private DTSDList fetchListByFormAndExpression(DTSamplingDataAdvancedForm advancedForm, BooleanExpression exp, Long originalRecordCount, boolean filtered)
    {
        Long recordCount = filtered ? manualCountSamplingEntries(exp) : originalRecordCount;
        DTSDList listWrapper;
        List<OrderSpecifier<?>> sortOrders = new ArrayList<>();
        if (advancedForm.getOrderDefinitions().size() > 0) {
            Map<Integer, Map<String, String>> orderDefs = advancedForm.getOrderDefinitions();
            OrderSpecifier<?> orderSpec;
            for (Integer key : orderDefs.keySet()) {
                orderSpec = SamplingDataOrderSpecifier.getByColumnOrderAndDirection(orderDefs.get(key).get("column"),
                        orderDefs.get(key).get("dir"));
                if (orderSpec != null) sortOrders.add(orderSpec);
            }
        }

        QPageRequest pageRequest;
        Integer page = advancedForm.getStart() > 0 ? advancedForm.getStart() / advancedForm.getLength() : 0;
        pageRequest = new QPageRequest(page, advancedForm.getLength());
        List<SamplingEntry> samplingEntries = manualFindAllSamplingEntries(exp, pageRequest, sortOrders);

        if (advancedForm.getSource().equals(SamplingDataAdvancedForm.SourceType.ONE) && samplingEntries.size() > 0) {
            listWrapper = new DTSDList(new CellHeader(samplingEntries.get(0).getSampling().getCell()),
                    formConvertSamplingData(samplingEntries, true));
        } else {
            listWrapper = new DTSDList(formConvertSamplingData(samplingEntries, true));
        }
        listWrapper.setITotalRecords(originalRecordCount.intValue());
        listWrapper.setITotalDisplayRecords(recordCount.intValue());
        listWrapper.setStart(advancedForm.getStart());

        return listWrapper;
    }

    private Long manualCountSamplingEntries(BooleanExpression exp)
    {
        return customSamplingEntryQuery().where(exp).fetchCount();
    }

    private List<SamplingEntry> manualFindAllSamplingEntries(BooleanExpression exp, QPageRequest pageRequest, List<OrderSpecifier<?>> sortOrders)
    {
        JPAQuery<SamplingEntry> query = customSamplingEntryQuery()
                .where(exp);
        if (sortOrders != null && sortOrders.size() > 0) {
            query.orderBy(sortOrders.toArray(new OrderSpecifier<?>[sortOrders.size()]));
        }
        return query.offset(pageRequest.getOffset()).limit(pageRequest.getPageSize()).fetch();
    }

    private JPAQuery<SamplingEntry> customSamplingEntryQuery()
    {
        JPAQuery<SamplingEntry> query = new JPAQuery<>(entityManager);
        query.from(QSamplingEntry.samplingEntry)
                .innerJoin(QSamplingEntry.samplingEntry.sampling, QSampling.sampling)
                .innerJoin(QSamplingEntry.samplingEntry.samplingData, QSamplingData.samplingData)
                .innerJoin(QSamplingEntry.samplingEntry.sampling.cell, QCell.cell)
                .leftJoin(QSamplingEntry.samplingEntry.maturity, QMaturity.maturity)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.tripSetSpecies, QTripSetSpecies.tripSetSpecies)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency, QLengthFrequency.lengthFrequency)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.country, QCountry.country)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.quarter, QQuarter.quarter)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.nafoDivision, QNafoDivision.nafoDivision)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.unitArea, QUnitArea.unitArea)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.vessel, QVessel.vessel)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.vessel.lengthCategory, QLengthCategory.lengthCategory)
                .leftJoin(QSamplingEntry.samplingEntry.sampling.lengthFrequency.gear, QGear.gear);
        return query;
    }

    public SamplingData fetchById(Long samplingDataId)
    {
        return samplingDataRepository.findById(samplingDataId).orElse(null);
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_SAMPLING_DATA)
    public void updateSamplingDataByForm(SamplingDataForm samplingDataForm)
    {
        SamplingData data = samplingDataRepository.getOne(samplingDataForm.getSamplingDataId());
        BeanUtils.copyProperties(samplingDataForm, data);
        data.setActor(SecurityHelper.getNtPrincipal());
        samplingDataRepository.saveAndFlush(data);
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_SAMPLING_DATA)
    public void patchSamplingData(Long samplingDataId, SamplingDataPatchRequest patchRequest)
    {
        SamplingData recordToPatch = fetchById(samplingDataId);
        if (recordToPatch == null) throw new RuntimeException("[SD Patch] Record not found");
        remapCodesToIds(patchRequest);
        BeanUtils.copyProperties(patchRequest.getData(), recordToPatch, determineIgnoredFields(patchRequest.getFields()));
        recordToPatch.setActor(SecurityHelper.getNtPrincipal());
        samplingDataRepository.saveAndFlush(recordToPatch);
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_SAMPLING_DATA)
    public String markSDForArchive(Long sdId)
    {
        String statusText = "error";
        SamplingData sd = samplingDataRepository.findOne(QSamplingData.samplingData.samplingDataId.eq(sdId)).orElse(null);
        if (sd != null) {
            if (!UNMARKABLE_STATUS_LIST.contains(sd.getStatusId())) {
                sd.setStatusId(SamplingDataStatus.MARK_FOR_ARCHIVE_STS_ID);
                samplingDataRepository.save(sd);
                statusText = lookupService.getSpecificLookupByIdentifier("samplingDataStatus",
                        SamplingDataStatus.MARK_FOR_ARCHIVE_STS_ID).getText();
            } else {
                logger.info("Could not mark SD for archives: incompatible status [" + sdId + "]");
            }
        } else {
            logger.info("Could not mark LF for archives: SD not found [" + sdId + "]");
        }
        return statusText;
    }

    @Override
    public Iterable<SamplingData> findRecords(BooleanExpression[] predicates)
    {
        Iterable<SamplingData> records;
        if (predicates != null && predicates.length > 0) {
            records = samplingDataRepository.findAll(predicates[0]);
        } else {
            records = new ArrayList<>();
        }
        return records;
    }

    @Override
    public Long countRecords(BooleanExpression[] predicates)
    {
        Long count = 0L;
        if (predicates != null && predicates.length > 0) {
            count = samplingDataRepository.count(predicates[0]);
        }
        return count;
    }

    public void saveSamplingData(SamplingData sd)
    {
        samplingDataRepository.save(sd);
    }

    public List<SamplingDataForm> formConvertSamplingData(List<SamplingEntry> samplingEntryList, boolean forList)
    {
        List<SamplingDataForm> formList = new ArrayList<>();
        samplingEntryList.forEach(se -> formList.add(formConvertSamplingData(se.getSamplingData(), se, forList)));
        return formList;
    }

    public SamplingDataForm formConvertSamplingData(SamplingData samplingData, SamplingEntry entry, boolean forList)
    {
        SamplingDataForm form = null;
        if (samplingData != null) {
            form = forList ? new SamplingDataListForm() : new SamplingDataForm();
            BeanUtils.copyProperties(samplingData, form, "samplingEntry");
            BeanUtils.copyProperties(entry, form, "samplingData");
            if (forList) {
                form.setStatusEng(entry.getSamplingData().getStatus().getEnglishDescription());
                form.setStatusFra(entry.getSamplingData().getStatus().getFrenchDescription());
            }
            form.setEntryLength(entry.getLength());
        }
        return form;
    }

    private void remapCodesToIds(SamplingDataPatchRequest patchRequest)
    {
        List<String> modifiedFields = patchRequest.getFields();
        SamplingDataForm data = patchRequest.getData();
        String otolithEdgeCode = data.getOtolithEdgeCode();
        String otolithReliabilityCode = data.getOtolithReliabilityCode();
        String parasiteCode = data.getParasiteCode();
        String primaryStomachContentsCode = data.getPrimaryStomachContentCode();
        String secondaryStomachContentsCode = data.getSecondaryStomachContentCode();

        if (modifiedFields.contains("otolithEdgeCode") && otolithEdgeCode != null && !otolithEdgeCode.trim().equals(""))
            data.setOtolithEdgeId(remapCodeToId("otolithEdges", otolithEdgeCode));
        else data.setOtolithEdgeId(null);

        if (modifiedFields.contains("otolithReliabilityCode") && otolithReliabilityCode != null &&
            !otolithReliabilityCode.trim().equals(""))
            data.setOtolithReliabilityId(remapCodeToId("otolithReliabilities", otolithReliabilityCode));
        else data.setOtolithReliabilityId(null);

        if (modifiedFields.contains("parasiteCode") && parasiteCode != null && !parasiteCode.trim().equals(""))
            data.setParasiteId(remapCodeToId("parasites", parasiteCode));
        else data.setParasiteId(null);

        if (modifiedFields.contains("primaryStomachContentCode") && primaryStomachContentsCode != null &&
            !primaryStomachContentsCode.trim().equals(""))
            data.setPrimaryStomachContentId(remapCodeToId("stomachContents", primaryStomachContentsCode));
        else data.setPrimaryStomachContentId(null);

        if (modifiedFields.contains("secondaryStomachContentCode") && secondaryStomachContentsCode  != null &&
            !secondaryStomachContentsCode.trim().equals(""))
            data.setSecondaryStomachContentId(remapCodeToId("stomachContents", secondaryStomachContentsCode));
        else data.setSecondaryStomachContentId(null);
    }

    private Integer remapCodeToId(String lookupName, String code)
    {
        List<Lookup> lookups = lookupService.getLookupsByKey(lookupName, false);
        List<Lookup> searchedLookups = lookups.stream()
                                              .filter(lk -> lk.getText().startsWith(code+"-"))
                                              .limit(1)
                                              .collect(Collectors.toList());
        if (searchedLookups.size() == 0) throw new RuntimeException("[SD Patch] Code to ID mapping failed");
        return Integer.valueOf(searchedLookups.get(0).getIdentifier().toString());
    }

    private String[] determineIgnoredFields(List<String> modifiedFields)
    {
        if (modifiedFields.contains("otolithEdgeCode"))
            modifiedFields.add("otolithEdgeId");
        if (modifiedFields.contains("otolithReliabilityCode"))
            modifiedFields.add("otolithReliabilityId");
        if (modifiedFields.contains("parasiteCode"))
            modifiedFields.add("parasiteId");
        if (modifiedFields.contains("primaryStomachContentCode"))
            modifiedFields.add("primaryStomachContentId");
        if (modifiedFields.contains("secondaryStomachContentCode"))
            modifiedFields.add("secondaryStomachContentId");

        List<String> classFields = new ArrayList<>();
        Class<?> classToEval = SamplingData.class;
        while (classToEval != Object.class) {
            for (Field field : classToEval.getDeclaredFields()) {
                classFields.add(field.getName());
            }
            classToEval = classToEval.getSuperclass();
        }
        List<String> ignoredFields = classFields.stream()
                                                .filter(cf -> !modifiedFields.contains(cf))
                                                .collect(Collectors.toList());
        return ignoredFields.toArray(new String[ignoredFields.size()]);
    }

    private static final Logger logger = LoggerFactory.getLogger(SamplingDataService.class);
}
