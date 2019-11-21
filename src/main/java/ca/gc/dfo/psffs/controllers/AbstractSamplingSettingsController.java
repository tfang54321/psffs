package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.forms.AbstractSamplingSettingForm;
import ca.gc.dfo.psffs.services.AbstractSamplingSettingService;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

public abstract class AbstractSamplingSettingsController<S extends AbstractSamplingSettingService> implements ApplicationContextAware
{
    private ApplicationContext ctx;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private MessageSource messageSource;

    protected abstract Class<S> chosenService();

    protected abstract String viewName();

    protected abstract String formPath();

    protected AbstractSamplingSettingForm settingsData(Integer year, Integer speciesId, ModelMap model) throws InstantiationException, IllegalAccessException
    {
        AbstractSamplingSettingService settingService = ctx.getBean(chosenService());
        model.addAttribute("years", settingService.getActiveYears());
        AbstractSamplingSettingForm settingForm = settingService.getSettingFormByYearAndSpecies(year, speciesId);
        if(((settingForm.getId() == 1 || settingForm.getId() == 4) && year == 0 && speciesId != null) || (settingForm.getYear() == null && year != 0)){
            //First record in the table contains the default values for Trip Settings
            settingForm.setId(null);
        }
        return settingForm;
    }

    protected String settingsForm(Integer year, Integer speciesId, ModelMap model) throws InstantiationException, IllegalAccessException
    {
        AbstractSamplingSettingService settingService = ctx.getBean(chosenService());
        AbstractSamplingSettingForm settingForm = settingService.getSettingFormByYearAndSpecies(year, speciesId);
        model.addAttribute("settingForm", settingForm);
        model.addAttribute("years", settingService.getActiveYears());
        return viewName();
    }

    protected String settingsSAVE(AbstractSamplingSettingForm settingForm, Boolean setDefault, ModelMap model,
                                  Locale locale, RedirectAttributes redirectAttrs) throws InstantiationException, IllegalAccessException
    {
        AbstractSamplingSettingService settingService = ctx.getBean(chosenService());
        if(settingForm.getSpeciesId() == null){
            settingForm.setYear(0);
        }
        if (setDefault == null) {
            setDefault = false;
        }

        Integer year = settingForm.getYear();
        Integer speciesId = settingForm.getSpeciesId();
        Long savedId = settingForm.getId();
        String appendToSuccess = "";

        settingService.saveSetting(settingForm);
        if(setDefault && settingForm.getSpeciesId() != null){
            AbstractSamplingSettingForm settingForm1 = settingService.getSettingFormByYearAndSpecies(0, settingForm.getSpeciesId());
            Long id = null;

            if(!(settingForm1.getId() != 1 || settingForm1.getId() != 4)) {
                id = settingForm1.getId();
            }
            settingForm1 = settingForm;
            settingForm1.setId(id);
            settingForm1.setYear(null);
            settingService.saveSetting(settingForm1);

            if(settingForm1.getId() != null) {
                appendToSuccess = messageSource.getMessage("js.success.ts.updated"
                        , new Object[]{getSpeciesForSetting(settingForm.getSpeciesId()),
                                messageSource.getMessage("form.dropdown.default_val", null,  locale)}, locale);
            }
            else {
                appendToSuccess = messageSource.getMessage("js.success.ts.created"
                        , new Object[] { getSpeciesForSetting(settingForm.getSpeciesId()),
                                messageSource.getMessage("form.dropdown.default_val", null,  locale)}, locale);
            }
        }
        if(savedId != null){
            //Message for updating the defaults for a species
            if(year == 0 && speciesId != null){
                model.addAttribute("successMessage", messageSource.getMessage("js.success.ts.updated"
                        , new Object[]{getSpeciesForSetting(settingForm.getSpeciesId()),
                                messageSource.getMessage("form.dropdown.default_val", null,  locale)}, locale));
            }
            //Message for updating the system default
            else if(settingForm.getYear() == null && settingForm.getSpeciesId() == null){
                model.addAttribute("successMessage", messageSource.getMessage("js.success.ts.updated_default"
                        , null, locale));
            }
            //Message for updating a standard trip setting
            else {
                model.addAttribute("successMessage", messageSource.getMessage("js.success.ts.updated"
                        , new Object[]{getSpeciesForSetting(speciesId), year.toString()}, locale) + "<br>" + appendToSuccess);
            }
        }
        else {
            //Message for creating the defaults for a species
            if(year == 0){
                model.addAttribute("successMessage", messageSource.getMessage("js.success.ts.created",
                        new Object[] { getSpeciesForSetting(settingForm.getSpeciesId()),
                                messageSource.getMessage("form.dropdown.default_val", null,  locale) }, locale));
            }
            //Message for creating a standard trip setting
            else {
                model.addAttribute("successMessage", messageSource.getMessage("js.success.ts.created",
                        new Object[]{getSpeciesForSetting(speciesId), year.toString()}, locale) + "<br>" + appendToSuccess);
            }
        }

        redirectAttrs.addFlashAttribute("successMessage", model.get("successMessage"));
        String speciesStr = (speciesId == null) ? "" : speciesId.toString();
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        redirectAttrs.addAttribute("year", year);
        redirectAttrs.addAttribute("speciesId", speciesStr);
        return "redirect:/"+pageLang+formPath();
    }

    private String getSpeciesForSetting(Integer speciesId){
        Lookup species = lookupService.getSpecificLookupByIdentifier("species", speciesId);
        return species != null ? species.getText() : "";
    }

    public void setApplicationContext(ApplicationContext context)
    {
        this.ctx = context;
    }
}
