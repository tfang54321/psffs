package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.forms.lookups.SamplingRangesForm;
import ca.gc.dfo.psffs.services.CacheService;
import ca.gc.dfo.psffs.services.SettingService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SamplingRangesController
{
    private static final String SAMPLING_RANGES_PATH = TemplateController.SETTINGS_PATH + "/samplingRanges";
    public static final String SAMPLING_RANGES_EDIT_PATH = SAMPLING_RANGES_PATH + "/edit";

    public static final String ENG_SAMPLING_RANGES_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + SAMPLING_RANGES_EDIT_PATH;
    public static final String FRA_SAMPLING_RANGES_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + SAMPLING_RANGES_EDIT_PATH;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SettingService settingService;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_SAMPLING_RANGES_EDIT_PATH, FRA_SAMPLING_RANGES_EDIT_PATH}, method = RequestMethod.GET)
    public String getSamplingRangesEdit(ModelMap model)
    {
        SamplingRangesForm form;
        if (model.containsAttribute("samplingRangesForm")) {
            form = (SamplingRangesForm) model.get("samplingRangesForm");
        } else {
            form = new SamplingRangesForm();

            form.latitudeMax = Integer.valueOf(settingService.getSettingValueByName("MAX_LATITUDE"));
            form.latitudeMin = Integer.valueOf(settingService.getSettingValueByName("MIN_LATITUDE"));
            form.longitudeMax = Integer.valueOf(settingService.getSettingValueByName("MAX_LONGITUDE"));
            form.longitudeMin = Integer.valueOf(settingService.getSettingValueByName("MIN_LONGITUDE"));
            form.meshSizeMax = Integer.valueOf(settingService.getSettingValueByName("MAX_MESH_SIZE"));
            form.meshSizeMin = Integer.valueOf(settingService.getSettingValueByName("MIN_MESH_SIZE"));
            form.depthMax = Integer.valueOf(settingService.getSettingValueByName("MAX_DEPTH"));
            form.depthMin = Integer.valueOf(settingService.getSettingValueByName("MIN_DEPTH"));
    }
        model.addAttribute("samplingRangesForm", form);
        return "lookups/samplingRanges/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_SAMPLING_RANGES_EDIT_PATH, FRA_SAMPLING_RANGES_EDIT_PATH}, method = RequestMethod.POST)
    public String postSamplingRangesEdit(@ModelAttribute("samplingRangesForm") SamplingRangesForm form,
                                         RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";

        try {
            if(form.latitudeMax < form.latitudeMin){
                redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.max_less_than_min_lat");
            }
            else if(form.longitudeMax < form.longitudeMin){
                redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.max_less_than_min_long");
            }
            else if(form.meshSizeMax < form.meshSizeMin){
                redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.max_less_than_min_mesh");
            }
            else if(form.depthMax < form.depthMin){
                redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.max_less_than_min_depth");
            }
            else {
                settingService.updateSetting("MAX_LATITUDE", form.latitudeMax.toString());
                settingService.updateSetting("MIN_LATITUDE", form.latitudeMin.toString());
                settingService.updateSetting("MAX_LONGITUDE", form.longitudeMax.toString());
                settingService.updateSetting("MIN_LONGITUDE", form.longitudeMin.toString());
                settingService.updateSetting("MAX_MESH_SIZE", form.meshSizeMax.toString());
                settingService.updateSetting("MIN_MESH_SIZE", form.meshSizeMin.toString());
                settingService.updateSetting("MAX_DEPTH", form.depthMax.toString());
                settingService.updateSetting("MIN_DEPTH", form.depthMin.toString());

                cacheService.initCacheSessionUUID(request, false);

                redirectAttributes.addFlashAttribute("successMessageKey", "success.sampling_ranges.modified");
            }
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save sampling ranges: " + ex.getMessage(), ex);

            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }

        redirectAttributes.addFlashAttribute("samplingRangesForm", form);
        return "redirect:/" + pageLang + SAMPLING_RANGES_EDIT_PATH;
    }

    private static final Logger logger = LoggerFactory.getLogger(SamplingRangesController.class);
}
