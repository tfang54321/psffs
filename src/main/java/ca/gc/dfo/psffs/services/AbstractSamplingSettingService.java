package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.BaseSamplingSetting;
import ca.gc.dfo.psffs.domain.repositories.BaseSamplingSettingRepository;
import ca.gc.dfo.psffs.forms.AbstractSamplingSettingForm;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

public abstract class AbstractSamplingSettingService<E extends BaseSamplingSetting, R extends BaseSamplingSettingRepository, F extends AbstractSamplingSettingForm> extends BaseService implements ApplicationContextAware
{
    private ApplicationContext ctx;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private ChecksumService checksumService;

    protected abstract Class<R> chosenRepository();

    protected abstract Class<E> chosenEntity();

    protected abstract Class<F> chosenForm();

    public List<E> getAllSettings()
    {
        BaseSamplingSettingRepository<E> repo = ctx.getBean(chosenRepository());
        return repo.findAll();
    }

    public List<Integer> getAllSettingYears() {
        List<E> settingList = getAllSettings();
        List<Integer> intList = new ArrayList<>();
        for (E setting : settingList) {
            if(setting.getYear() != null) {
                intList.add(setting.getYear());
            }
        }
        return intList;
    }

    public E getSettingById(Long id)
    {
        BaseSamplingSettingRepository<E> repo = ctx.getBean(chosenRepository());
        return repo.getById(id);
    }

    public E getSettingByYearAndSpecies(Integer year, Integer speciesId){
        BaseSamplingSettingRepository<E> repo = ctx.getBean(chosenRepository());
        E setting = repo.getByYearAndSpeciesId(year, speciesId);
        if(setting == null){
            setting = repo.getByYearAndSpeciesId(null,speciesId);

            if(setting == null){
                setting = repo.getByYearAndSpeciesId(null,null);
            }
        }
        return setting;
    }

    public F getSettingFormByYearAndSpecies(Integer year, Integer speciesId) throws InstantiationException, IllegalAccessException
    {
        return convertToForm(getSettingByYearAndSpecies(year, speciesId));
    }

    public void saveSetting(F settingForm) throws InstantiationException, IllegalAccessException
    {
        E setting = chosenEntity().newInstance();
        if(settingForm.getYear() != null) {
            if (settingForm.getYear() == 0) {
                settingForm.setYear(null);
            }
        }
        setting.setActor(SecurityHelper.getNtPrincipal());
        BaseSamplingSettingRepository<E> repo = ctx.getBean(chosenRepository());
        repo.save(convertFromForm(settingForm, setting));

        checksumService.updateChecksumForObjectByEntityClass(chosenEntity());
    }

    public int[] getActiveYears(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int nextYear = calendar.get(Calendar.YEAR) + 1;

        List<Integer> intList = getAllSettingYears();

        int listSize = 3 + intList.size();

        final int[] years = new int[listSize];

        for(int i = 0; i < listSize; i++){
            if(i == 0){
                years[i] = 0;
            }
            else if(i == 1 || i == 2) {
                years[i] = (nextYear + 1) - i;
            }
            else {
                years[i] = intList.get(i - 3);
            }
        }

        return Arrays.stream(years)
                .filter(i -> i == 0 || i == nextYear || i == nextYear - 1 || intList.contains(i))
                .distinct()
                .toArray();
    }

    private F convertToForm(E setting) throws InstantiationException, IllegalAccessException
    {
        F settingForm = chosenForm().newInstance();

        BeanUtils.copyProperties(setting, settingForm, "lengthGroupId");
        Integer lengthGroupId = setting.getLengthGroupId();
        if (lengthGroupId != null) {
            settingForm.setLengthGroupData(lookupService.getSpecificLookupByIdentifier("lengthGroups",
                    lengthGroupId).getValue());
        }

        return settingForm;
    }

    private E convertFromForm(F settingForm, E setting) {
        BeanUtils.copyProperties(settingForm, setting, "lengthGroupData");
        String lengthGroupData = settingForm.getLengthGroupData();
        if (lengthGroupData != null && lengthGroupData.trim().length() > 0 && lengthGroupData.contains(";")) {
            setting.setLengthGroupId(getNumberFromStringData(lengthGroupData, ";", 0, Integer.class));
        }

        return setting;
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
    {
        this.ctx = context;
    }
}
