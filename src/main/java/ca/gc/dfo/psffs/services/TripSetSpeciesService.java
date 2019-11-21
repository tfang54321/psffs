package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.*;
import ca.gc.dfo.psffs.domain.objects.lookups.TSSSamplingStatus;
import ca.gc.dfo.psffs.domain.objects.security.User;
import ca.gc.dfo.psffs.domain.repositories.TripSetSpeciesRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.TSSSamplingStatusRepository;
import ca.gc.dfo.psffs.forms.TripSetSpeciesForm;
import ca.gc.dfo.psffs.json.TSSList;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TripSetSpeciesService extends BaseSamplingService
{
    @Autowired
    private TripSetSpeciesRepository tripSetSpeciesRepository;

    @Autowired
    private TSSSamplingStatusRepository tssSamplingStatusRepository;

    private static final List<Integer> UNMARKABLE_STATUS_LIST = Arrays.asList(32, 35);

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_TRIP_SET_SPECIES)
    public TripSetSpecies saveTSS(TripSetSpeciesForm form)
    {
        User user = (User)SecurityHelper.getUserDetails();
        TripSetSpecies tss = getOrInitTSS(form, user);
        if (tss.getCatchDate() != null) {
            tss.getSampling().setYearCreated(tss.getCatchDate().getYear());
        }
        tss = tripSetSpeciesRepository.saveAndFlush(tss);
        boolean resetStorageNumbers = determineAndSetSamplingCell(tss);
        if (form.isEntriesModified() || resetStorageNumbers) {
            createOrUpdateSamplingEntries(form, tss, user, resetStorageNumbers);
        }
        tss = tripSetSpeciesRepository.saveAndFlush(tss);
        return tss;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TRIP_SET_SPECIES)
    public TSSList getListByYear(Integer year)
    {
        LocalDate fromDate = LocalDate.parse(year+"-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate toDate = LocalDate.parse((year+1)+"-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TSSList list = new TSSList();
        list.setTripSetSpeciesList(tripSetSpeciesRepository.queryListByDateRange(fromDate, toDate));
        return list;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_DELETE_TRIP_SET_SPECIES)
    public boolean deleteTSS(String tssId)
    {
        return deleteTSS(getByTSSId(tssId));
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_DELETE_TRIP_SET_SPECIES)
    public boolean deleteTSS(TripSetSpecies tss)
    {
        boolean result;
        try {
            tripSetSpeciesRepository.delete(tss);
            result = true;
        } catch (Exception ex) {
            logger.error("An error occurred while trying to delete TSS: " + tss.getSampling().getSamplingCode(), ex);
            result = false;
        }
        return result;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_TRIP_SET_SPECIES)
    public String markTSSForDistributedArchive(String tssId, Integer newStatusId)
    {
        String statusText = "error";
        TripSetSpecies tss = getByTSSId(tssId);
        if (tss != null) {
            if (!UNMARKABLE_STATUS_LIST.contains(tss.getStatusId())) {
                tss.setStatusId(newStatusId);
                tripSetSpeciesRepository.save(tss);
                TSSSamplingStatus status = tssSamplingStatusRepository.getOne(newStatusId);
                statusText = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                        status.getEnglishDescription() : status.getFrenchDescription();
            } else {
                logger.info("Could not mark TSS for distributed/archives: incompatible status [" + tssId + "]");
            }
        } else {
            logger.info("Could not mark TSS for distributed/archives: TSS not found [" + tssId + "]");
        }
        return statusText;
    }

    private TripSetSpecies getOrInitTSS(TripSetSpeciesForm form, User user)
    {
        String actor = user.getNtPrincipal();
        String tssId = form.getTssId();
        TripSetSpecies tss;
        if (tssId == null) {
            tss = new TripSetSpecies();
            Sampling sampling = new Sampling();
            sampling.setSpeciesId(form.getSampledSpeciesId());
            sampling.setDataSourceId(2);
            Integer yearCreated = LocalDate.now().getYear();
            sampling.setSamplingCode(getNextSampleCode(yearCreated, user.getInitials()));
            sampling.setYearCreated(yearCreated);
            sampling.setSamplingTypeId(31);//TSS
            sampling.setActor(actor);
            sampling.setEntries(new ArrayList<>());
            tss.setSampling(sampling);
            tss.setStatusId(32);
            tss.setActor(actor);
        } else {
            tss = getByTSSId(form.getTssId());
            tss.setActor(actor);
            tss.getSampling().setActor(actor);
        }
        BeanUtils.copyProperties(form, tss, "entries", "statusId");
        convertSpecialData(form, tss);
        if (tss.getStatusId().equals(32) && tss.getCheckedBy() != null && tss.getCheckedBy().trim().length() > 0) {
            tss.setStatusId(33);
        }
        return tss;
    }

    public TripSetSpecies getByTSSId(String tssId)
    {
        List<TripSetSpecies> tripSetSpeciesList = tripSetSpeciesRepository.queryBySampleCode(tssId);
        return tripSetSpeciesList.size() > 0 ? tripSetSpeciesList.get(0) : null;
    }

    public TripSetSpeciesForm getAndFormConvertTSS(String tssId)
    {
        TripSetSpeciesForm form = null;
        TripSetSpecies tss = getByTSSId(tssId);
        if (tss != null) {
            form = new TripSetSpeciesForm();
            BeanUtils.copyProperties(tss, form, "entries");
            revertSpecialData(form, tss);
            if (tss.getSampling() != null) {
                List<SamplingEntry> entries = tss.getSampling().getEntries();

                if (entries != null) {
                    entries = entries.stream()
                                     .filter(e -> e.getActiveFlag().equals(1))
                                     .collect(Collectors.toList());
                    if (entries.size() > 0) {
                        formConvertEntries(form, entries);
                    }
                }
            }
        }
        return form;
    }

    private void formConvertEntries(TripSetSpeciesForm form, List<SamplingEntry> entries)
    {
        Map<String, List<Integer>> entryMap = new HashMap<>();
        Integer lengthGroupMax = form.getLengthGroupMax();
        Integer[] uInitCounts = new Integer[lengthGroupMax+1];
        Integer[] mInitCounts = new Integer[lengthGroupMax+1];
        Integer[] fInitCounts = new Integer[lengthGroupMax+1];
        for (int x = 0; x <= lengthGroupMax; x++) {
            uInitCounts[x] = 0;
            mInitCounts[x] = 0;
            fInitCounts[x] = 0;
        }
        entryMap.put("U", Arrays.asList(uInitCounts));
        entryMap.put("M", Arrays.asList(mInitCounts));
        entryMap.put("F", Arrays.asList(fInitCounts));

        Integer currentCount;
        for (SamplingEntry se : entries) {
            currentCount = entryMap.get(se.getSex()).get(se.getLength());
            entryMap.get(se.getSex()).set(se.getLength(), currentCount + 1);
        }

        form.setU_o(entryMap.get("U").toArray(new Integer[lengthGroupMax+1]));
        form.setM_o(entryMap.get("M").toArray(new Integer[lengthGroupMax+1]));
        form.setF_o(entryMap.get("F").toArray(new Integer[lengthGroupMax+1]));
    }

    private void convertSpecialData(TripSetSpeciesForm form, TripSetSpecies tss)
    {
        String unitAreaData = form.getUnitAreaData();
        String quarterData = form.getQuarterData();
        String checkedByData = form.getCheckedBy();

        if (unitAreaData != null && unitAreaData.trim().length() > 0 && unitAreaData.contains(";"))  {
            tss.setUnitAreaId(
                    getNumberFromStringData(unitAreaData, ";", 0, Integer.class));
        }
        if (quarterData != null && quarterData.trim().length() > 0 && quarterData.contains(";")) {
            tss.setQuarterId(getNumberFromStringData(quarterData, ";", 0, Integer.class));
        }
        if (checkedByData != null && checkedByData.trim().length() > 0 && checkedByData.contains(",")) {
            tss.setCheckedBy(checkedByData.substring(checkedByData.indexOf(",")+1));
        }
    }

    private void revertSpecialData(TripSetSpeciesForm form, TripSetSpecies tss)
    {
        form.setUnitAreaData(getSpecificLookupValueDataByIdentifier("unitAreas", tss.getUnitAreaId()));
        form.setCheckedBy(getSpecificLookupValueDataByValueContains("enteredBys", ","+tss.getCheckedBy()));
    }

    @Override
    protected void populateUniqueCellFields(Cell cell, SamplingTypeEntity samplingTypeEntity)
    {
        TripSetSpecies tripSetSpecies = (TripSetSpecies) samplingTypeEntity;
        cell.setObserverCompanyId(tripSetSpecies.getObserverCompanyId());
    }

    private void createOrUpdateSamplingEntries(TripSetSpeciesForm form, TripSetSpecies tss, User user, boolean resetStorageNumbers)
    {
        String actor = user.getNtPrincipal();
        createOrUpdateSamplingEntriesBySexCode("U", form.getU_o(), tss, actor, resetStorageNumbers);
        createOrUpdateSamplingEntriesBySexCode("M", form.getM_o(), tss, actor, resetStorageNumbers);
        createOrUpdateSamplingEntriesBySexCode("F", form.getF_o(), tss, actor, resetStorageNumbers);
    }

    private void createOrUpdateSamplingEntriesBySexCode(String sexCode, Integer[] otolithCounts, TripSetSpecies tss,
                                                        String actor, boolean resetStorageNumbers)
    {
        Map<Integer, List<SamplingEntry>> applicableEntriesMap = tss.getSampling().getEntries().stream()
                                                                  .filter(e -> e.getSex().equals(sexCode))
                                                                  .collect(Collectors.groupingBy(SamplingEntry::getLength));
        Integer oCount, countDiff;
        List<SamplingEntry> entries;
        AtomicLong latestStorageNumber = new AtomicLong(tss.getSampling().getCell().getLatestStorageNumber());
        int otolithCountsLength = otolithCounts != null && otolithCounts.length > 0 ? otolithCounts.length : 0;
        for (int l = 0; l < otolithCountsLength; l++) {
            oCount = otolithCounts[l];
            entries = applicableEntriesMap.get(l);
            if (oCount > 0) {
                if (!applicableEntriesMap.containsKey(l)) {
                    //Length was never used, create all new entries / data
                    createNewSamplingEntries(sexCode, l, tss, actor, oCount, latestStorageNumber);
                } else {
                    //Length has been used, determine whether to create any new and how many to re-activate
                    if (entries.size() > oCount) {
                        entries.forEach(e ->
                                reInitEntry(e, 0, (resetStorageNumbers ? latestStorageNumber.incrementAndGet() :
                                        e.getSamplingData().getStorageNbr()), actor));
                        for (int c = 0; c < oCount; c++) {
                            entries.get(c).setActiveFlag(1);
                        }
                    } else if (entries.size() < oCount) {
                        entries.forEach(e ->
                                reInitEntry(e, 1, (resetStorageNumbers ? latestStorageNumber.incrementAndGet() :
                                        e.getSamplingData().getStorageNbr()), actor));
                        countDiff = oCount - entries.size();
                        createNewSamplingEntries(sexCode, l, tss, actor, countDiff, latestStorageNumber);
                    } else if (oCount.equals(entries.size()) && resetStorageNumbers) {
                        entries.forEach(e -> reInitEntry(e, e.getActiveFlag(), latestStorageNumber.incrementAndGet(), actor));
                    }
                }
            } else {
                if (entries != null && entries.size() > 0) {
                    entries.forEach(e -> reInitEntry(e, 0, e.getSamplingData().getStorageNbr(), actor));
                }
            }
        }
        tss.getSampling().getCell().setLatestStorageNumber(latestStorageNumber.get());
    }

    private void reInitEntry(SamplingEntry entry, Integer isActive, Long storageNumber, String actor)
    {
        entry.setActiveFlag(isActive);
        entry.setActor(actor);
        entry.getSamplingData().setStorageNbr(storageNumber);
        entry.getSamplingData().setActor(actor);
    }

    private void createNewSamplingEntries(String sexCode, Integer lengthGroup, TripSetSpecies tss, String actor,
                                          Integer numberToCreate, AtomicLong latestStorageNumber)
    {
        SamplingEntry entry;
        SamplingData data;
        List<SamplingEntry> entries = tss.getSampling().getEntries();
        if (entries == null) {
            entries = new ArrayList<>();
        }
        while (numberToCreate > 0) {
            entry = new SamplingEntry();
            entry.setSex(sexCode);
            entry.setLength(lengthGroup);
            entry.setSampling(tss.getSampling());
            entry.setPresOrder(1);
            entry.setActiveFlag(1);
            entry.setSamplings(Arrays.asList("o"));
            entry.setActor(actor);

            data = new SamplingData();
            data.setSampling(tss.getSampling());
            data.setSamplingEntry(entry);
            data.setActor(actor);
            data.setStorageNbr(latestStorageNumber.incrementAndGet());
            entry.setSamplingData(data);

            entries.add(entry);
            numberToCreate--;
        }
        tss.getSampling().setEntries(entries);
    }

    private static final Logger logger = LoggerFactory.getLogger(TripSetSpeciesService.class);
}
