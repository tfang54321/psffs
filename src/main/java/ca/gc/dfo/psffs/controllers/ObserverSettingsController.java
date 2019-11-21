package ca.gc.dfo.psffs.controllers;


import ca.gc.dfo.psffs.forms.ObserverSettingForm;
import ca.gc.dfo.psffs.forms.TripSettingForm;
import ca.gc.dfo.psffs.services.ObserverSettingService;
import ca.gc.dfo.psffs.services.TripSettingService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class ObserverSettingsController extends AbstractSamplingSettingsController<ObserverSettingService>
{
    private static final String OBSERVER_SETTINGS_PATH = "/observerSettings";
    public static final String OBSERVER_SETTINGS_SAVE_PATH = OBSERVER_SETTINGS_PATH + "/save";
    public static final String OBSERVER_SETTINGS_FORM_PATH = OBSERVER_SETTINGS_PATH + "/form";
    public static final String OBSERVER_SETTINGS_DATA_PATH = OBSERVER_SETTINGS_PATH + "/data";

    public static final String ENG_OBSERVER_SETTINGS_SAVE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_SETTINGS_SAVE_PATH;
    public static final String FRA_OBSERVER_SETTINGS_SAVE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_SETTINGS_SAVE_PATH;

    public static final String ENG_OBSERVER_SETTINGS_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_SETTINGS_FORM_PATH;
    public static final String FRA_OBSERVER_SETTINGS_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_SETTINGS_FORM_PATH;

    public static final String ENG_OBSERVER_SETTINGS_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_SETTINGS_DATA_PATH;
    public static final String FRA_OBSERVER_SETTINGS_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_SETTINGS_DATA_PATH;

    @Override
    protected Class<ObserverSettingService> chosenService()
    {
        return ObserverSettingService.class;
    }

    @Override
    protected String viewName()
    {
        return "observer/observerSetting";
    }

    @Override
    protected String formPath()
    {
        return OBSERVER_SETTINGS_FORM_PATH;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_SETTINGS)
    @RequestMapping(value = {ENG_OBSERVER_SETTINGS_DATA_PATH, FRA_OBSERVER_SETTINGS_DATA_PATH}, method = RequestMethod.GET)
    public @ResponseBody ObserverSettingForm observerSettingsData(@RequestParam(value = "year", required = false) Integer year,
                                                          @RequestParam(value = "speciesId", required = false) Integer speciesId,
                                                          ModelMap model) throws InstantiationException, IllegalAccessException
    {
        return (ObserverSettingForm) settingsData(year, speciesId, model);
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_SETTINGS)
    @RequestMapping(value = {ENG_OBSERVER_SETTINGS_LIST_PATH, FRA_OBSERVER_SETTINGS_LIST_PATH}, method = RequestMethod.GET)
    public String observerSettingsForm(@RequestParam(value = "year", required = false) Integer year,
                                   @RequestParam(value = "speciesId", required = false) Integer speciesId,
                                   ModelMap model) throws InstantiationException, IllegalAccessException
    {
        return settingsForm(year, speciesId, model);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_SAMPLING_SETTINGS)
    @RequestMapping(value = {ENG_OBSERVER_SETTINGS_SAVE_PATH, FRA_OBSERVER_SETTINGS_SAVE_PATH}, method = RequestMethod.POST)
    public String observerSettingsSAVE(@ModelAttribute("observerSettingForm") ObserverSettingForm observerSettingForm,
                                   @RequestParam(value = "setDefault", required = false) Boolean setDefault, ModelMap model, Locale locale,
                                   RedirectAttributes redirectAttrs) throws InstantiationException, IllegalAccessException
    {
        return settingsSAVE(observerSettingForm, setDefault, model, locale, redirectAttrs);
    }
}
