package ca.gc.dfo.psffs.controllers;


import ca.gc.dfo.psffs.forms.TripSettingForm;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.psffs.services.TripSettingService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class TripSettingsController extends AbstractSamplingSettingsController<TripSettingService>
{
    public static final String TRIP_SETTINGS_SAVE_PATH = TemplateController.TRIP_SETTINGS_PATH + "/save";
    public static final String TRIP_SETTINGS_FORM_PATH = TemplateController.TRIP_SETTINGS_PATH + "/form";
    public static final String TRIP_SETTINGS_DATA_PATH = TemplateController.TRIP_SETTINGS_PATH + "/data";

    public static final String ENG_TRIP_SETTINGS_SAVE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            TRIP_SETTINGS_SAVE_PATH;
    public static final String FRA_TRIP_SETTINGS_SAVE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            TRIP_SETTINGS_SAVE_PATH;

    public static final String ENG_TRIP_SETTINGS_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            TRIP_SETTINGS_FORM_PATH;
    public static final String FRA_TRIP_SETTINGS_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            TRIP_SETTINGS_FORM_PATH;

    public static final String ENG_TRIP_SETTINGS_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            TRIP_SETTINGS_DATA_PATH;
    public static final String FRA_TRIP_SETTINGS_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            TRIP_SETTINGS_DATA_PATH;

    @Override
    protected Class<TripSettingService> chosenService()
    {
        return TripSettingService.class;
    }

    @Override
    protected String viewName()
    {
        return "lengthFrequency/tripSetting";
    }

    @Override
    protected String formPath()
    {
        return TRIP_SETTINGS_FORM_PATH;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_SETTINGS)
    @RequestMapping(value = {ENG_TRIP_SETTINGS_DATA_PATH, FRA_TRIP_SETTINGS_DATA_PATH}, method = RequestMethod.GET)
    public @ResponseBody TripSettingForm tripSettingsData(@RequestParam(value = "year", required = false) Integer year,
                                                          @RequestParam(value = "speciesId", required = false) Integer speciesId,
                                                          ModelMap model) throws InstantiationException, IllegalAccessException
    {
        return (TripSettingForm)settingsData(year, speciesId, model);
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_SETTINGS)
    @RequestMapping(value = {ENG_TRIP_SETTINGS_LIST_PATH, FRA_TRIP_SETTINGS_LIST_PATH}, method = RequestMethod.GET)
    public String tripSettingsForm(@RequestParam(value = "year", required = false) Integer year,
                                   @RequestParam(value = "speciesId", required = false) Integer speciesId,
                                   ModelMap model) throws InstantiationException, IllegalAccessException
    {
        return settingsForm(year, speciesId, model);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_SAMPLING_SETTINGS)
    @RequestMapping(value = {ENG_TRIP_SETTINGS_SAVE_PATH, FRA_TRIP_SETTINGS_SAVE_PATH}, method = RequestMethod.POST)
    public String tripSettingsSAVE(@ModelAttribute("tripSettingForm") TripSettingForm tripSettingForm,
                                   @RequestParam(value = "setDefault", required = false) Boolean setDefault, ModelMap model, Locale locale,
                                   RedirectAttributes redirectAttrs) throws InstantiationException, IllegalAccessException
    {
        return settingsSAVE(tripSettingForm, setDefault, model, locale, redirectAttrs);
    }
}
