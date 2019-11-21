package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.repositories.security.RoleRepository;
import ca.gc.dfo.psffs.export.extract.AbstractExtractRunner;
import ca.gc.dfo.psffs.export.report.AbstractReport;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.psffs.domain.objects.lookups.*;
import ca.gc.dfo.psffs.domain.repositories.security.UserRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.*;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.spring_commons.commons_web.objects.BlankLookup;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import ca.gc.dfo.spring_commons.commons_web.services.AbstractLookupService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class LookupService extends AbstractLookupService implements ApplicationContextAware
{
    public enum GenericLookup
    {
        //CODE TABLES
        CATCH_CATEGORY("", "catchCategories", CatchCategoryRepository.class, CatchCategory.class),
        CONDITION_LANDED("", "conditionLandeds", ConditionLandedRepository.class, ConditionLanded.class),
        DATA_SOURCE("", "dataSources", DataSourceRepository.class, DataSource.class),
        GEAR("field.code", "gears", GearRepository.class, Gear.class),
        MEASURING_TECHNIQUE("", "measuringTechniques", MeasuringTechniqueRepository.class, MeasuringTechnique.class),
        NAFO_DIVISION("field.division_code", "nafoDivisions", NafoDivisionRepository.class, NafoDivision.class),
        OBSERVER_COMPANY("", "observerCompanies", ObserverCompanyRepository.class, ObserverCompany.class),
        OTOLITH_EDGE("field.code", "otolithEdges", OtolithEdgeRepository.class, OtolithEdge.class),
        PARASITE("field.code", "parasites", ParasiteRepository.class, Parasite.class),
        PORT("field.code", "ports", PortRepository.class, Port.class),
        STOMACH_CONTENT("field.code", "stomachContents", StomachContentRepository.class, StomachContent.class),
        TONNAGE("field.code", "tonnages", TonnageRepository.class, Tonnage.class);

        GenericLookup(String legacyCodeLabelKey, String codeAttr, Class<? extends BaseLookupRepository> repoBeanClass,
                      Class<? extends BaseLookup> entityClass)
        {
            this(legacyCodeLabelKey, codeAttr, "getId", repoBeanClass, entityClass);
        }

        GenericLookup(String legacyCodeLabelKey, String codeAttr, String identifierGetter,
                      Class<? extends BaseLookupRepository> repoBeanClass, Class<? extends BaseLookup> entityClass)
        {
            this.LEGACY_CODE_LABEL_KEY = legacyCodeLabelKey;
            this.CODE_ATTR = codeAttr;
            this.IDENTIFIER_GETTER = identifierGetter;
            this.REPO_BEAN_CLASS = repoBeanClass;
            this.ENTITY_CLASS = entityClass;
        }

        public String LEGACY_CODE_LABEL_KEY;
        public String CODE_ATTR;
        public String IDENTIFIER_GETTER;
        public Class<? extends BaseLookupRepository> REPO_BEAN_CLASS;
        public Class<? extends BaseLookup> ENTITY_CLASS;
    }

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final VesselRepository vesselRepository;

    private final SpeciesRepository speciesRepository;

    private final QuarterRepository quarterRepository;

    private final CountryRepository countryRepository;

    private final UnitAreaRepository unitAreaRepository;

    private final SamplingStatusRepository samplingStatusRepository;

    private final TSSSamplingStatusRepository tssSamplingStatusRepository;

    private final SamplingDataStatusRepository samplingDataStatusRepository;

    private final SexTypeRepository sexTypeRepository;

    private final LengthGroupRepository lengthGroupRepository;

    private final SexCodeRepository sexCodeRepository;

    private final LengthUnitRepository lengthUnitRepository;

    private final OtolithReliabilityRepository otolithReliabilityRepository;

    private final SampleTypeRepository sampleTypeRepository;

    private final CatchLocationRepository catchLocationRepository;

    private final LengthCategoryRepository lengthCategoryRepository;

    private final WeightConversionFactorRepository weightConversionFactorRepository;

    private final MaturityRepository maturityRepository;

    private final CellDefinitionStatusRepository cellDefinitionStatusRepository;

    private final MessageSource messageSource;

    private ApplicationContext applicationContext;

    @Override
    public void populate()
    {
        try {
            // USER MANAGEMENT
            addLookups("enteredBys", "getId", userRepository.findAllByOrderByInitialsAsc());
            appendToLookups("enteredBys", new BlankLookup("other", "Not listed", "Non list&eacute;"));
            addLookups("roles", "getId", roleRepository.findAllExcept("ROLE_BASE_ACCESS"));

            // SPECIAL TABLES
            addLookups("vessels", "getId", vesselRepository.findAllByOrderByOrderAsc());
            appendToLookups("vessels", new BlankLookup("other", "Not listed", "Non list&eacute;"));
            getSpecificLookupByValue("vessels", "other").setIdentifier(-1);
            addLookups("species", "getId", speciesRepository.findAllByOrderByOrderAsc());
            addLookups("offshoreQuarters", "getId", quarterRepository.findAllByCatchLocationIdEqualsOrderByOrderAsc(6));
            addLookups("inshoreQuarters", "getId", quarterRepository.findAllByCatchLocationIdEqualsOrderByOrderAsc(7));
            addLookups("lengthCategories", "getId", lengthCategoryRepository.findAllByOrderByOrderAsc());
            addLookups("maturities", "getId", maturityRepository.findAllBySexCodeIsNotNullOrderByOrderAsc());
            addLookups("unitAreas", "getId", unitAreaRepository.findAllByOrderByOrderAsc());
            addLookups("countries", "getId", countryRepository.findAllByOrderByOrderAsc());
            addLookups("weightConversionFactors", "getId", weightConversionFactorRepository.findAllByOrderByOrderAsc());
            addLookups("lengthGroups", "getId", lengthGroupRepository.findAllByOrderByOrderAsc());

            // CODE TABLES (GENERIC)
            BaseLookupRepository<? extends BaseLookup, Integer> baseRepo;
            for (GenericLookup gl : GenericLookup.values()) {
                baseRepo = gl.REPO_BEAN_CLASS.cast(applicationContext.getBean(gl.REPO_BEAN_CLASS));
                addLookups(gl.CODE_ATTR, gl.IDENTIFIER_GETTER, baseRepo.findAllByOrderByOrderAsc());
            }

            // OPERATIONAL CODES
            addLookups("samplingStatus", "getId", samplingStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("catchLocations", "getId", catchLocationRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("sexTypes", "getId", sexTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("lengthUnits", "getId", lengthUnitRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("otolithReliabilities", "getId", otolithReliabilityRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("sexCodes", "getId", sexCodeRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("sampleTypes", "getId", sampleTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("cellDefinitionStatus", "getId", cellDefinitionStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("tssSamplingStatus", "getId", tssSamplingStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));
            addLookups("samplingDataStatus", "getId", samplingDataStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "presentationOrder")));

            // STATICALLY POPULATED
            addLookups("years", populateYears());
            addLookups("yesOrNo", populateYesOrNo());
            addLookups("months", populateMonths());
            addLookups("extractTypes", populateExtractTypes());
            addLookups("reportTypes", populateReportTypes());
        } catch (Exception ex) {
            String error = "An error occurred while trying to populate lookups: " + ex.getMessage();
            logger.error(error, ex);
            throw new RuntimeException(error, ex);
        }
    }

    public List<? extends BaseLookup> getAllGenericLookups(GenericLookup gl)
    {
        BaseLookupRepository<? extends BaseLookup, Integer> baseRepo = applicationContext.getBean(gl.REPO_BEAN_CLASS);
        return getAllLookups(BaseLookup.class, baseRepo);
    }

    public <E extends BaseLookup> List<E> getAllLookups(Class<E> entityClass, BaseLookupRepository baseLookupRepo)
    {
        List<E> lookupList = new ArrayList<>();
        lookupList.addAll(baseLookupRepo.findAll());
        return lookupList;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    public Boolean toggleLookupActive(GenericLookup gl, Integer id, Boolean active)
    {
        Boolean result;
        BaseLookupRepository<BaseLookup, Integer> repo = this.applicationContext.getBean(gl.REPO_BEAN_CLASS);
        if (id != null) {
            result = toggleLookupActive(id, repo, gl.CODE_ATTR, active);
        } else {
            result = false;
        }
        return result;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    public Boolean toggleLookupActive(Integer id, BaseLookupRepository lookupRepo, String lookupKey, Boolean active)
    {
        boolean result = true;
        BaseLookup baseLookup = (BaseLookup)lookupRepo.getOne(id);
        baseLookup.setActiveFlag(active ? 1 : 0);
        try {
            lookupRepo.save(baseLookup);
        } catch (Exception ex) {
            logger.error("An error occurred when attempting to activate/deactivate lookup: " + ex.getMessage(), ex);
            result = false;
        }
        if (result) {
            Lookup lookup = getSpecificLookupByIdentifier(lookupKey, id);
            if (lookup != null) {
                lookup.setActive(active);
            }
        }
        return result;
    }

    public Integer getLookupCount(GenericLookup gl)
    {
        BaseLookupRepository baseRepo = this.applicationContext.getBean(gl.REPO_BEAN_CLASS);
        return getLookupCount(baseRepo);
    }

    public Integer getLookupCount(BaseLookupRepository baseLookupRepository)
    {
        return Long.valueOf(baseLookupRepository.count()).intValue();
    }

    @Transactional
    public void saveLookup(BaseLookupForm form, GenericLookup gl, String... ignoreProperties) throws InstantiationException, IllegalAccessException
    {
        BaseLookupRepository repo = this.applicationContext.getBean(gl.REPO_BEAN_CLASS);
        BaseLookup baseLookup;
        if (form.getId() == null) {
            baseLookup = gl.ENTITY_CLASS.newInstance();
        } else {
            baseLookup = (BaseLookup)repo.getOne(form.getId());
        }
        saveLookup(form, baseLookup, repo, gl.CODE_ATTR, ignoreProperties);
    }

    @Transactional
    public void saveLookup(BaseLookupForm form, BaseLookup baseLookup, BaseLookupRepository repo, String lookupKey, String... ignoreProperties) throws InstantiationException, IllegalAccessException
    {
        BeanUtils.copyProperties(form, baseLookup, ignoreProperties);
        baseLookup.setActor(SecurityHelper.getNtPrincipal());
        baseLookup = (BaseLookup) repo.save(baseLookup);

        Lookup existingLookup = getSpecificLookupByIdentifier(lookupKey, baseLookup.getId());
        if(existingLookup == null){
            appendToLookups(lookupKey, "getId", baseLookup);
        }
        else {
            BaseLookup copyOfLookup = (BaseLookup) Hibernate.unproxy(baseLookup);
            Lookup editedLookup = Lookup.convert(copyOfLookup);
            editedLookup.setIdentifier(baseLookup.getId());
            BeanUtils.copyProperties(editedLookup, existingLookup, "locale");
        }
    }

    public BaseLookup findLookupById(Integer id, GenericLookup gl)
    {
        BaseLookupRepository<BaseLookup, Integer> repo = this.applicationContext.getBean(gl.REPO_BEAN_CLASS);
        return findLookupById(id, gl.ENTITY_CLASS, repo);
    }

    public <T extends BaseLookup> T findLookupById(Integer id, Class<T> lookupEntityClass, BaseLookupRepository repo)
    {
        return lookupEntityClass.cast(repo.getOne(id));
    }

    public GenericLookup determineGenericLookup(String pathCode)
    {
        return Arrays.stream(GenericLookup.values())
                     .filter(gl -> gl.CODE_ATTR.equals(pathCode))
                     .limit(1)
                     .collect(Collectors.toList()).get(0);
    }

    public <T extends BaseLookupRepository> T getGenericLookupRepository(Class<T> lookupRepositoryClass)
    {
        return this.applicationContext.getBean(lookupRepositoryClass);
    }

    //PRIVATE UTILITY METHODS

    private List<BlankLookup> populateYears()
    {
        List<BlankLookup> yearList = new ArrayList<>();
        Integer currentYear = LocalDate.now().getYear();
        String currentYearStr = currentYear.toString();
        int minusYears = 80;
        BlankLookup year = new BlankLookup(currentYearStr, currentYearStr, currentYearStr);
        yearList.add(year);
        for (int y = (currentYear - 1); y > (currentYear - minusYears); y--) {
            currentYearStr = ""+y;
            year = new BlankLookup(currentYearStr, currentYearStr, currentYearStr);
            yearList.add(year);
        }
        return yearList;
    }

    private List<BlankLookup> populateYesOrNo()
    {
        List<BlankLookup> yesOrNoList = new ArrayList<>();
        yesOrNoList.add(new BlankLookup("1", "Yes", "Oui"));
        yesOrNoList.add(new BlankLookup("0", "No", "Non"));
        return yesOrNoList;
    }

    private List<BlankLookup> populateMonths()
    {
        DateFormatSymbols french_dfs = new DateFormatSymbols(Locale.FRENCH);
        String[] fr_months = french_dfs.getMonths();

        DateFormatSymbols english_dfs = new DateFormatSymbols();
        String[] eng_months = english_dfs.getMonths();

        List<BlankLookup> monthList = new ArrayList<>();

        for (int x = 1; x < fr_months.length; x++){
            monthList.add(new BlankLookup(String.valueOf(x), eng_months[x-1], fr_months[x-1]));
        }
        return monthList;
    }

    private List<BlankLookup> populateExtractTypes()
    {
        List<BlankLookup> extractTypes = new ArrayList<>();
        Reflections reflections = new Reflections("ca.gc.dfo.psffs.export");
        Set<Class<? extends AbstractExtractRunner>> runners = reflections.getSubTypesOf(AbstractExtractRunner.class);
        for (Class<? extends AbstractExtractRunner> runner : runners) {
            extractTypes.add(new BlankLookup(runner.getName(), runner.getSimpleName(), runner.getSimpleName()));
        }
        return extractTypes;
    }

    private List<BlankLookup> populateReportTypes() throws Exception
    {
        List<BlankLookup> reportTypes = new ArrayList<>();
        Reflections reflections = new Reflections("ca.gc.dfo.psffs.export");
        Set<Class<? extends AbstractReport>> reports = reflections.getSubTypesOf(AbstractReport.class);
        AbstractReport<?> instance;
        for (Class<? extends AbstractReport> report : reports) {
            instance = report.newInstance();
            reportTypes.add(new BlankLookup(report.getName(), messageSource.getMessage(
                    instance.getReportNameKey(), null, Locale.ENGLISH), messageSource.getMessage(
                            instance.getReportNameKey(), null, Locale.FRENCH)));
        }
        return reportTypes;
    }

    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);
}
