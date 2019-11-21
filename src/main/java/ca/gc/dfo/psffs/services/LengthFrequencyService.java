package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.repositories.SamplingDataRepository;
import ca.gc.dfo.psffs.domain.repositories.SamplingRepository;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.psffs.domain.objects.*;
import ca.gc.dfo.psffs.domain.objects.lookups.*;
import ca.gc.dfo.psffs.domain.repositories.LengthFrequencyRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.*;
import ca.gc.dfo.psffs.exceptions.CellDefinitionNotFoundException;
import ca.gc.dfo.psffs.forms.SamplingEntryForm;
import ca.gc.dfo.psffs.forms.LengthFrequencyForm;
import ca.gc.dfo.psffs.forms.SamplingDataForm;
import ca.gc.dfo.psffs.json.LFList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LengthFrequencyService extends BaseSamplingService
{
    @Autowired
    private LengthFrequencyRepository lengthFrequencyRepository;

    @Autowired
    private SamplingStatusRepository samplingStatusRepository;

    @Autowired
    private PortRepository portRepository;

    @Autowired
    private VesselRepository vesselRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private CatchLocationRepository catchLocationRepository;

    @Autowired
    private QuarterRepository quarterRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private NafoDivisionRepository nafoDivisionRepository;

    @Autowired
    private UnitAreaRepository unitAreaRepository;

    @Autowired
    private SexTypeRepository sexTypeRepository;

    @Autowired
    private LengthGroupRepository lengthGroupRepository;

    @Autowired
    private LengthUnitRepository lengthUnitRepository;

    @Autowired
    private GearRepository gearRepository;

    @Autowired
    private CatchCategoryRepository catchCategoryRepository;

    @Autowired
    private WeightConversionFactorRepository weightConversionFactorRepository;

    @Autowired
    private SampleTypeRepository sampleTypeRepository;

    @Autowired
    private MeasuringTechniqueRepository measuringTechniqueRepository;

    @Autowired
    private MaturityRepository maturityRepository;

    @Autowired
    private SamplingDataRepository samplingDataRepository;

    @Autowired
    private SamplingRepository samplingRepository;

    private static final List<Integer> UNMARKABLE_STATUS_LIST = Arrays.asList(1, 2, 4);

    //PUBLIC API METHODS
    @PreAuthorize(SecurityHelper.EL_VIEW_LENGTH_FREQUENCIES)
    public LFList getListByYear(Integer year)
    {
        LocalDate fromDate = LocalDate.parse(year+"-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate toDate = LocalDate.parse((year+1)+"-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LFList list = new LFList();
        list.setLengthFrequencies(lengthFrequencyRepository.queryListByDateRange(fromDate, toDate));
        return list;
    }

    public LengthFrequency getByLFId(String lfId)
    {
        List<LengthFrequency> queryResult = lengthFrequencyRepository.queryBySampleCode(lfId);
        return queryResult.size() == 1 ? queryResult.get(0) : null;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LENGTH_FREQUENCIES)
    public LengthFrequencyForm getAndFormConvertLF(String lfId)
    {
        LengthFrequencyForm form = null;
        LengthFrequency lf = getByLFId(lfId);
        if (lf != null) {
            form = new LengthFrequencyForm();
            BeanUtils.copyProperties(lf, form, "entries", "vesselId");
            revertSpecialData(form, lf);
            if (lf.getSampling() != null && lf.getSampling().getEntries() != null && lf.getSampling().getEntries().size() > 0) {
                form.setEntries(formConvertEntries(lf.getSampling().getEntries()));
            } else {
                form.setEntries(new SamplingEntryForm[0]);
            }
        }
        return form;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_LENGTH_FREQUENCIES)
    public LengthFrequency syncLF(LengthFrequencyForm form) throws Exception
    {
        String actor = SecurityHelper.getNtPrincipal();
        //Initial creation or fetching of Length Frequency
        LengthFrequency lengthFrequency;
        List<String> ignoreList = new ArrayList<>(Arrays.asList("entries", "vesselId"));
        if (form.getLfId().startsWith("LOCAL-")) {
            lengthFrequency = new LengthFrequency();
            form.setCountsModified(true);
            form.setEntriesModified(true);
            ignoreList.add("id");
        } else {
            lengthFrequency = lengthFrequencyRepository.queryBySampleCode(form.getLfId()).get(0);
        }
        BeanUtils.copyProperties(form, lengthFrequency, ignoreList.toArray(new String[ignoreList.size()]));
        if (form.getVesselId() != null && !form.getVesselId().equals("") && !form.getVesselId().equals("other")) {
            lengthFrequency.setVesselId(Integer.valueOf(form.getVesselId()));
        } else {
            lengthFrequency.setVesselId(null);
        }

        //Convert special data fields back to their normal values (be sure to do the opposite when loading the record: hint lookupService lookup.getIdentifier)
        convertSpecialData(lengthFrequency, form);

        //Re-mapping of relationship IDs to actual objects
        remapRelatedObjects(lengthFrequency);

        if (form.getCountsModified()) {
            //Map the summary length group counts into LengthFrequencyCount objects
            mapLFCounts(lengthFrequency, form, actor);
        }

        //Save base objects (LengthFrequency, Length Frequency Count(s), Sampling)
        lengthFrequency = saveBase(lengthFrequency, actor);

        //Create/Choose cell and determine if a storage number reset is necessary.
        boolean resetAllStorageNumbers = determineAndSetSamplingCell(lengthFrequency);
        lengthFrequencyRepository.save(lengthFrequency);

        //Setting of entries and their sample data to LF if they were modified
        if (form.getEntriesModified() || resetAllStorageNumbers) {
            SamplingEntryForm[] entries = form.getEntries();
            List<SamplingEntry> entryList = lengthFrequency.getSampling().getEntries();
            Map<Integer, SamplingData> entryToDataMap = new HashMap<>();
            if (entryList != null) {
                entryList.forEach(entry -> entry.setActiveFlag(0));
            }
            if (entries != null && entries.length > 0) {
                if (entryList == null) {
                    entryList = new ArrayList<>();
                }
                SamplingEntry entry;
                SamplingData samplingData;
                SamplingData samplingDataToSave;
                Long storageNbr = lengthFrequency.getSampling().getCell().getLatestStorageNumber();
                for (int x = 0; x < entries.length; x++) {
                    if (x+1 <= entryList.size()) {
                        entry = entryList.get(x);
                    } else {
                        entry = new SamplingEntry();
                        entry.setSampling(lengthFrequency.getSampling());
                        entryList.add(entry);
                    }

                    BeanUtils.copyProperties(entries[x], entry, "sampleData");
                    entry.setPresOrder(x + 1);
                    entry.setActor(actor);
                    entry.setActiveFlag(1);
                    if (entries[x].getSamplingData() != null) {
                        if (entry.getSamplingData() != null ) {
                            samplingData = entry.getSamplingData();
                        } else {
                            samplingData = new SamplingData();
                        }
                        BeanUtils.copyProperties(entries[x].getSamplingData(), samplingData);
                        samplingData.setActor(actor);
                        samplingData.setSamplingEntry(entry);
                        samplingDataToSave = new SamplingData();
                        BeanUtils.copyProperties(samplingData, samplingDataToSave, "samplingDataId");
                        if (entry.usesStorageNumber()) {
                            if (samplingDataToSave.getStorageNbr() == null || resetAllStorageNumbers) {
                                storageNbr++;
                                samplingDataToSave.setStorageNbr(storageNbr);
                            }
                        } else {
                            samplingDataToSave.setStorageNbr(null);
                        }
                        entryToDataMap.put(x, samplingDataToSave);
                        entry.setSamplingData(null);
                    } else {
                        entry.setSamplingData(null);
                    }
                }
                lengthFrequency.getSampling().setEntries(entryList);
                lengthFrequency.getSampling().getCell().setLatestStorageNumber(storageNbr);

                //Save entries and entry sample data
                if (lengthFrequency.getSampling().getEntries() != null &&
                        lengthFrequency.getSampling().getEntries().size() > 0) {
                    lengthFrequency = saveEntriesAndData(lengthFrequency, actor);

                    //Re-associate sampling data (had to do this due to a hibernate detach error)
                    SamplingData smpData;
                    SamplingEntry smpEntry;
                    for (int x = 0; x < lengthFrequency.getSampling().getEntries().size(); x++) {
                        smpData = entryToDataMap.get(x);
                        if (smpData != null) {
                            smpEntry = lengthFrequency.getSampling().getEntries().get(x);
                            smpData.setSamplingEntry(smpEntry);
                            smpEntry.setSamplingData(smpData);
                        }
                    }

                    //Save entries again so that they now have their sampling data back.
                    lengthFrequency = saveEntriesAndData(lengthFrequency, actor);
                }
            }
        }

        return lengthFrequency;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MARK_FOR_ARCHIVE_LENGTH_FREQUENCIES)
    public String markLFForArchive(String lfId)
    {
        String statusText = "error";
        LengthFrequency lf = getByLFId(lfId);
        if (lf != null) {
            if (!UNMARKABLE_STATUS_LIST.contains(lf.getStatusId())) {
                SamplingStatus status = samplingStatusRepository.getOne(4);
                lf.setStatus(status);
                lengthFrequencyRepository.save(lf);
                statusText = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                        status.getEnglishDescription() : status.getFrenchDescription();
            } else {
                logger.info("Could not mark LF for archives: incompatible status [" + lfId + "]");
            }
        } else {
            logger.info("Could not mark LF for archives: LF not found [" + lfId + "]");
        }
        return statusText;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_DELETE_LENGTH_FREQUENCIES)
    public boolean delete(String lfId)
    {
        return delete(getByLFId(lfId));
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_DELETE_LENGTH_FREQUENCIES)
    public boolean delete(LengthFrequency lengthFrequency)
    {
        try {

            samplingRepository.delete(lengthFrequency.getSampling());
            samplingRepository.flush();
            lengthFrequencyRepository.delete(lengthFrequency);
        } catch (Exception ex) {
            logger.error("An exception occurred while trying to delete LF: " + lengthFrequency.getId());
            return false;
        }
        return true;
    }

    //PRIVATE UTILITY METHODS
    private SamplingEntryForm[] formConvertEntries(List<SamplingEntry> lfEntries)
    {
        List<SamplingEntryForm> entryForms = new ArrayList<>();
        SamplingEntryForm entryForm;
        SamplingDataForm samplingDataForm;
        if (lfEntries != null && lfEntries.size() > 0) {
            for (SamplingEntry entry : lfEntries) {
                entryForm = new SamplingEntryForm();
                BeanUtils.copyProperties(entry, entryForm, "samplingData");
                if (entry.getSamplingData() != null) {
                    samplingDataForm = new SamplingDataForm();
                    BeanUtils.copyProperties(entry.getSamplingData(), samplingDataForm);
                    entryForm.setSamplingData(samplingDataForm);
                }
                entryForms.add(entryForm);
            }
        }
        return entryForms.toArray(new SamplingEntryForm[entryForms.size()]);
    }

    private LengthFrequency saveBase(LengthFrequency lengthFrequency, String actor)
    {
        lengthFrequency.setActor(actor);
        if (lengthFrequency.getSampling() == null) {
            Sampling sampling = new Sampling();
            sampling.setActor(actor);
            sampling.setSamplingCode(getNextSampleCode(LocalDate.now().getYear(), determineInitials(lengthFrequency)));
            sampling.setDataSourceId(1);//Port sampling
            sampling.setSpeciesId(lengthFrequency.getSampleSpecies().getId());
            sampling.setLocalSamplingCode(lengthFrequency.getLfId());
            sampling.setSamplingTypeId(30);//LF
            sampling.setLengthGroupMin(lengthFrequency.getLengthGroupMin());
            sampling.setLengthGroupMax(lengthFrequency.getLengthGroupMax());
            lengthFrequency.setSampling(sampling);
        }
        String checkedBy = lengthFrequency.getCheckedBy();
        if (lengthFrequency.getStatus() == null) {
            lengthFrequency.setStatus(mapStatusObject(2));
        } else {
            if (lengthFrequency.getStatusId().equals(2) && checkedBy != null && !checkedBy.equals("")) {
                lengthFrequency.setStatus(mapStatusObject(3));
            }
        }
        if (lengthFrequency.getCatchDate() != null) {
            lengthFrequency.getSampling().setYearCreated(lengthFrequency.getCatchDate().getYear());
        }
        lengthFrequency.setEnteredBy(getInitialFieldValue(lengthFrequency.getEnteredBy()));
        lengthFrequency.setVerifiedBy(getInitialFieldValue(lengthFrequency.getVerifiedBy()));
        Integer frequencyCount = lengthFrequencyRepository.countByYear(lengthFrequency.getSampling().getYearCreated());
        lengthFrequency.setFrequencyNumber(frequencyCount+1);
        lengthFrequency = lengthFrequencyRepository.saveAndFlush(lengthFrequency);
        lengthFrequency.postLoad();
        return lengthFrequency;
    }

    @Override
    protected void populateUniqueCellFields(Cell cell, SamplingTypeEntity samplingTypeEntity)
    {
        LengthFrequency lengthFrequency = (LengthFrequency) samplingTypeEntity;
        Vessel vessel = lengthFrequency.getVessel();
        if (vessel != null) {
            cell.setVesselLengthCategoryId(vessel.getLengthCategoryId());
        }
        cell.setMeshSizeMillimeters(lengthFrequency.getMeshSizeMillimeters());
    }

    private LengthFrequency saveEntriesAndData(LengthFrequency lengthFrequency, String actor)
    {
        lengthFrequency.getSampling().getEntries().forEach(entry -> initEntry(lengthFrequency, entry, actor));
        LengthFrequency toReturn = lengthFrequencyRepository.save(lengthFrequency);
        lengthFrequencyRepository.flush();
        toReturn.setLfId(toReturn.getSampling().getSamplingCode());
        return toReturn;
    }

    private String determineInitials(LengthFrequency lengthFrequency)
    {
        String initials;
        String enteredBy = lengthFrequency.getEnteredBy();
        if (!enteredBy.contains(",")) {
            initials = lengthFrequency.getEnteredByOther();
        } else {
            initials = enteredBy.split(",")[1];
        }
        return initials;
    }

    private String getInitialFieldValue(String initialFieldValue)
    {
        String toReturn = initialFieldValue;
        if (toReturn.contains(",")) {
            toReturn = toReturn.split(",")[1];
        }
        return toReturn;
    }

    private void convertSpecialData(LengthFrequency lengthFrequency, LengthFrequencyForm form)
    {
        String unitAreaData = form.getUnitAreaData();
        String conversionFactorData = form.getConversionFactorData();
        String checkedByData = form.getCheckedBy();
        String verifiedByData = form.getVerifiedBy();
        String quarterData = form.getQuarterData();

        if (checkedByData != null && checkedByData.trim().length() > 0) {
            lengthFrequency.setCheckedBy(getInitialFieldValue(checkedByData));
        }
        if (verifiedByData != null && verifiedByData.trim().length() > 0) {
            lengthFrequency.setVerifiedBy(getInitialFieldValue(verifiedByData));
        }
        if (unitAreaData != null && unitAreaData.trim().length() > 0 && unitAreaData.contains(";"))  {
            lengthFrequency.setUnitAreaId(
                    getNumberFromStringData(unitAreaData, ";", 0, Integer.class));
        }
        if (conversionFactorData != null && conversionFactorData.trim().length() > 0 && conversionFactorData.contains(";")) {
            lengthFrequency.setConversionFactorId(
                    getNumberFromStringData(conversionFactorData, ";", 0, Integer.class));
        }
        if (quarterData != null && quarterData.trim().length() > 0 && quarterData.contains(";")) {
            lengthFrequency.setQuarterId(getNumberFromStringData(quarterData, ";", 0, Integer.class));
        }
    }

    private void revertSpecialData(LengthFrequencyForm form, LengthFrequency lengthFrequency)
    {
        form.setUnitAreaData(getSpecificLookupValueDataByIdentifier("unitAreas", lengthFrequency.getUnitAreaId()));
        form.setConversionFactorData(getSpecificLookupValueDataByIdentifier("weightConversionFactors",
                lengthFrequency.getConversionFactorId()));
        form.setCheckedBy(getSpecificLookupValueDataByValueContains("enteredBys", lengthFrequency.getCheckedBy()));
        form.setEnteredBy(getSpecificLookupValueDataByValueContains("enteredBys", lengthFrequency.getEnteredBy()));
        form.setVerifiedBy(getSpecificLookupValueDataByValueContains("enteredBys", lengthFrequency.getVerifiedBy()));
        if (lengthFrequency.getVesselId() == null && lengthFrequency.getOtherVesselDetails() != null &&
            lengthFrequency.getOtherVesselDetails().length() > 0) {
            form.setVesselId("other");
        } else {
            form.setVesselId(lengthFrequency.getVesselId().toString());
        }
    }

    private void initEntry(LengthFrequency lengthFrequency, SamplingEntry entry, String actor)
    {
        entry.setActor(actor);
        String maturityLevel = entry.getMaturityLevel();
        if (maturityLevel != null && maturityLevel.length() > 0) {
            List<Maturity> maturities = maturityRepository.findAllByMaturityLevel(maturityLevel);
            if (maturities != null && maturities.size() > 0) {
                entry.setMaturityId(maturities.get(0).getId());
            }
        }
        entry.setSampling(lengthFrequency.getSampling());
    }

    private void remapRelatedObjects(LengthFrequency lengthFrequency)
    {
        if (detectRelatedObjectChanged(lengthFrequency.getPortOfLanding(), lengthFrequency.getPortOfLandingId()))
            lengthFrequency.setPortOfLanding(mapPortOfLanding(lengthFrequency.getPortOfLandingId()));
        if (detectRelatedObjectChanged(lengthFrequency.getVessel(), lengthFrequency.getVesselId()))
            lengthFrequency.setVessel(mapVessel(lengthFrequency.getVesselId()));
        if (detectRelatedObjectChanged(lengthFrequency.getDirectedSpecies(), lengthFrequency.getDirectedSpeciesId()))
            lengthFrequency.setDirectedSpecies(mapSpecies(lengthFrequency.getDirectedSpeciesId()));
        if (detectRelatedObjectChanged(lengthFrequency.getSampleSpecies(), lengthFrequency.getSampleSpeciesId()))
            lengthFrequency.setSampleSpecies(mapSpecies(lengthFrequency.getSampleSpeciesId()));
        if (detectRelatedObjectChanged(lengthFrequency.getCatchLocation(), lengthFrequency.getCatchLocationId()))
            lengthFrequency.setCatchLocation(mapCatchLocation(lengthFrequency.getCatchLocationId()));
        if (detectRelatedObjectChanged(lengthFrequency.getQuarter(), lengthFrequency.getQuarterId()))
            lengthFrequency.setQuarter(mapQuarter(lengthFrequency.getQuarterId()));
        if (detectRelatedObjectChanged(lengthFrequency.getCountry(), lengthFrequency.getCountryId()))
            lengthFrequency.setCountry(mapCountry(lengthFrequency.getCountryId()));
        if (detectRelatedObjectChanged(lengthFrequency.getNafoDivision(), lengthFrequency.getNafoDivisionId()))
            lengthFrequency.setNafoDivision(mapNafoDivision(lengthFrequency.getNafoDivisionId()));
        if (detectRelatedObjectChanged(lengthFrequency.getUnitArea(), lengthFrequency.getUnitAreaId()))
            lengthFrequency.setUnitArea(mapUnitArea(lengthFrequency.getUnitAreaId()));
        if (detectRelatedObjectChanged(lengthFrequency.getSexType(), lengthFrequency.getSexTypeId()))
            lengthFrequency.setSexType(mapSexType(lengthFrequency.getSexTypeId()));
        if (detectRelatedObjectChanged(lengthFrequency.getLengthGroup(), lengthFrequency.getLengthGroupId()))
            lengthFrequency.setLengthGroup(mapLengthGroup(lengthFrequency.getLengthGroupId()));
        if (detectRelatedObjectChanged(lengthFrequency.getLengthUnit(), lengthFrequency.getLengthUnitId()))
            lengthFrequency.setLengthUnit(mapLengthUnit(lengthFrequency.getLengthUnitId()));
        if (detectRelatedObjectChanged(lengthFrequency.getMeasuringTechnique(), lengthFrequency.getMeasuringTechniqueId()))
            lengthFrequency.setMeasuringTechnique(mapMeasuringTechnique(lengthFrequency.getMeasuringTechniqueId()));
        if (detectRelatedObjectChanged(lengthFrequency.getGear(), lengthFrequency.getGearId()))
            lengthFrequency.setGear(mapGear(lengthFrequency.getGearId()));
        if (detectRelatedObjectChanged(lengthFrequency.getCatchCategory(), lengthFrequency.getCatchCategoryId()))
            lengthFrequency.setCatchCategory(mapCatchCategory(lengthFrequency.getCatchCategoryId()));
        if (detectRelatedObjectChanged(lengthFrequency.getConversionFactor(), lengthFrequency.getConversionFactorId()))
            lengthFrequency.setConversionFactor(mapWeightConversionFactor(lengthFrequency.getConversionFactorId()));
    }

    private void mapLFCounts(LengthFrequency lengthFrequency, LengthFrequencyForm form, String actor)
    {
        List<LengthFrequencyCount> counts = lengthFrequency.getLengthFrequencyCounts();
        if (counts == null) {
            counts = new ArrayList<>();
        } else {
            counts.forEach(count -> count.setCount(0));
        }
        LengthFrequencyCount count;
        String[] sexCodes = new String[] {"U", "M", "F"};
        Map<String, SampleType> sampleTypes = getSampleTypeMap();
        Method getter;
        String getterName = "";
        Integer[] lengthCounts;
        Integer lengthCountLength;
        boolean countExists;
        for (String sexCode : sexCodes) {
            for (String sampleCode : sampleTypes.keySet()) {
                try {
                    getterName = "get"+sexCode+sampleCode;
                    getter = form.getClass().getDeclaredMethod(getterName);
                    if (getter != null) {
                        lengthCounts = (Integer[])getter.invoke(form);
                        if (lengthCounts == null) {
                            lengthCountLength = -1;
                        } else {
                            lengthCountLength = lengthCounts.length;
                        }

                        for (int l = 0; l < lengthCountLength; l++) {
                            if (lengthCounts[l] > 0) {
                                countExists = false;
                                count = new LengthFrequencyCount();
                                count.setSex(sexCode);
                                count.setSampleType(sampleTypes.get(sampleCode));
                                count.setLength(l);
                                if (counts.contains(count)) {
                                    count = counts.get(counts.indexOf(count));
                                    countExists = true;
                                }
                                count.setLengthFrequency(lengthFrequency);
                                count.setActor(actor);
                                count.setCount(lengthCounts[l]);;
                                if (!countExists) counts.add(count);
                            }
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    logger.debug("Exception thrown while trying to fire LF Integer[] count getter method: " +
                            getterName + "()", e);
                }
            }
        }
        lengthFrequency.setLengthFrequencyCounts(counts);
    }

    private Map<String, SampleType> getSampleTypeMap()
    {
        return sampleTypeRepository.findAll().stream()
                                      .collect(Collectors.toMap(st -> st.getLegacyCode().equals("l") ?
                                               "" : "_"+st.getLegacyCode(), st -> st));
    }

    private SamplingStatus mapStatusObject(Integer statusId)
    {
        return samplingStatusRepository.findById(statusId).orElseGet(() -> samplingStatusRepository.getOne(1));
    }

    private SexType mapSexType(Integer sexTypeId)
    {
        return sexTypeId != null ? sexTypeRepository.findById(sexTypeId).orElseGet(() -> sexTypeRepository.getOne(8)) : null;
    }

    private LengthGroup mapLengthGroup(Integer lengthGroupId)
    {
        return lengthGroupId != null ? lengthGroupRepository.findById(lengthGroupId).orElseGet(() -> lengthGroupRepository.getOne(11)) : null;
    }

    private LengthUnit mapLengthUnit(Integer lengthUnitId)
    {
        return lengthUnitId != null ? lengthUnitRepository.findById(lengthUnitId).orElseGet(() -> lengthUnitRepository.getOne(14)) : null;
    }

    private MeasuringTechnique mapMeasuringTechnique(Integer measuringTechniqueId)
    {
        return measuringTechniqueId != null ? processOptional(measuringTechniqueRepository.findById(measuringTechniqueId), MeasuringTechnique.class) : null;
    }

    private Port mapPortOfLanding(Integer portOfLandingId)
    {
        return portOfLandingId != null ? processOptional(portRepository.findById(portOfLandingId), Port.class) : null;
    }

    private Vessel mapVessel(Integer vesselId)
    {
        return vesselId != null ? processOptional(vesselRepository.findById(vesselId), Vessel.class) : null;
    }

    private Species mapSpecies(Integer speciesId)
    {
        return speciesId != null ? processOptional(speciesRepository.findById(speciesId), Species.class) : null;
    }

    private CatchLocation mapCatchLocation(Integer catchLocationId)
    {
        return catchLocationId != null ? processOptional(catchLocationRepository.findById(catchLocationId),
                CatchLocation.class) : null;
    }

    private Quarter mapQuarter(Integer quarterId)
    {
        return quarterId != null ? processOptional(quarterRepository.findById(quarterId), Quarter.class) : null;
    }

    private Country mapCountry(Integer countryId)
    {
        return countryId != null ? processOptional(countryRepository.findById(countryId), Country.class) : null;
    }

    private NafoDivision mapNafoDivision(Integer nafoDivisionId)
    {
        return nafoDivisionId != null ? processOptional(nafoDivisionRepository.findById(nafoDivisionId),
                NafoDivision.class) : null;
    }

    private UnitArea mapUnitArea(Integer unitAreaId)
    {
        return unitAreaId != null ? processOptional(unitAreaRepository.findById(unitAreaId), UnitArea.class) : null;
    }

    private Gear mapGear(Integer gearId)
    {
        return gearId != null ? processOptional(gearRepository.findById(gearId), Gear.class) : null;
    }

    private CatchCategory mapCatchCategory(Integer catchCategoryId)
    {
        return catchCategoryId != null ? processOptional(catchCategoryRepository.findById(catchCategoryId),
                CatchCategory.class) : null;
    }

    private WeightConversionFactor mapWeightConversionFactor(Integer weightConversionFactorId)
    {
        return weightConversionFactorId != null ? weightConversionFactorRepository.findById(
                weightConversionFactorId).orElseGet(() -> weightConversionFactorRepository.getOne(4)) : null;
    }

    private static final Logger logger = LoggerFactory.getLogger(LengthFrequencyService.class);
}
