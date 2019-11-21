package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.Species;
import ca.gc.dfo.psffs.domain.objects.lookups.WeightConversionFactor;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.WeightConversionFactorForm;
import ca.gc.dfo.psffs.json.LookupList;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WeightConversionFactorController extends BaseLookupController
{
    private static final String WCF_PATH = TemplateController.SPECIAL_TABLES_PATH + "/weightConversionFactors";
    public static final String WCF_LIST_PATH = WCF_PATH + "/list";
    public static final String WCF_LIST_DATA_PATH = WCF_LIST_PATH + "-data";
    public static final String WCF_CREATE_PATH = WCF_PATH + "/create";
    public static final String WCF_EDIT_PATH = WCF_PATH + "/edit";
    public static final String WCF_ACTIVATE_PATH = WCF_PATH + "/activate";

    public static final String ENG_WCF_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + WCF_LIST_PATH;
    public static final String FRA_WCF_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + WCF_LIST_PATH;
    public static final String ENG_WCF_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + WCF_LIST_DATA_PATH;
    public static final String FRA_WCF_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + WCF_LIST_DATA_PATH;
    public static final String ENG_WCF_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + WCF_CREATE_PATH;
    public static final String FRA_WCF_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + WCF_CREATE_PATH;
    public static final String ENG_WCF_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + WCF_EDIT_PATH;
    public static final String FRA_WCF_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + WCF_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_WCF_LIST_PATH, FRA_WCF_LIST_PATH}, method = RequestMethod.GET)
    public String getWeightConversionFactorList(ModelMap model)
    {
        populateCommonListModel(model, WCF_LIST_DATA_PATH, WCF_EDIT_PATH, WCF_ACTIVATE_PATH);
        return "lookups/weightConversionFactor/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_WCF_CREATE_PATH, FRA_WCF_CREATE_PATH}, method = RequestMethod.GET)
    public String getWeightConversionFactorCreate(ModelMap model)
    {
        WeightConversionFactorForm form;
        if (model.containsAttribute("wcfForm")) {
            form = (WeightConversionFactorForm) model.get("wcfForm");
        } else {
            form = new WeightConversionFactorForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getWeightConversionFactorRepository())+1);
        }
        model.addAttribute("wcfForm", form);
        return "lookups/weightConversionFactor/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_WCF_EDIT_PATH, FRA_WCF_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getWeightConversionFactorEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        WeightConversionFactorForm form;
        if (model.containsAttribute("wcfForm")) {
            form = (WeightConversionFactorForm) model.get("wcfForm");
        } else {
            form = new WeightConversionFactorForm();
            WeightConversionFactor wcf = getLookupService().findLookupById(lookupId, WeightConversionFactor.class,
                    getLookupService().getWeightConversionFactorRepository());
            BeanUtils.copyProperties(wcf, form);
        }
        model.addAttribute("wcfForm", form);
        return "lookups/weightConversionFactor/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_WCF_CREATE_PATH, FRA_WCF_CREATE_PATH,
            ENG_WCF_EDIT_PATH, FRA_WCF_EDIT_PATH}, method = RequestMethod.POST)
    public String postWeightConversionFactorSave(@ModelAttribute("wcfForm") WeightConversionFactorForm form,
                                RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveWeightConversionFactor(form);
            returnView += WCF_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            // Reset the checksum for Vessels so that new offline data will be downloaded.
            getChecksumService().updateChecksumForObjectByEntityClass(WeightConversionFactor.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a weight conversion factor lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += WCF_CREATE_PATH;
            } else {
                returnView += WCF_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("wcfForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_WCF_LIST_DATA_PATH, FRA_WCF_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getWeightConversionFactorListData()
    {
        return getAllWeightConversionFactorForms();
    }

    @RequestMapping(value = WCF_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getWeightConversionFactorRepository(),
                "weightConversionFactors", request);
    }

    //Private utility methods
    private LookupList getAllWeightConversionFactorForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<WeightConversionFactor> wcf = getLookupService().getAllLookups(WeightConversionFactor.class,
                getLookupService().getWeightConversionFactorRepository());
        BaseLookupForm form;
        for (WeightConversionFactor v : wcf) {
            form = new WeightConversionFactorForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveWeightConversionFactor(WeightConversionFactorForm form) throws InstantiationException, IllegalAccessException
    {
        WeightConversionFactor wcf;
        if (form.getId() == null) {
            wcf = new WeightConversionFactor();
        } else {
            wcf = getLookupService().findLookupById(form.getId(), WeightConversionFactor.class,
                    getLookupService().getWeightConversionFactorRepository());
        }

        remapIdsToObjects(form, wcf);

        getLookupService().saveLookup(form, wcf, getLookupService().getWeightConversionFactorRepository(),
                "weightConversionFactors");
    }

    private void remapIdsToObjects(WeightConversionFactorForm form, WeightConversionFactor wcf)
    {
        Integer[] speciesIds = form.getSpeciesIds();
        if (speciesIds != null) {
            List<Species> speciesArr = new ArrayList<>();
            for (Integer speciesId:
                 speciesIds) {
                Species sp = getLookupService().findLookupById(speciesId, Species.class,
                        getLookupService().getSpeciesRepository());
                speciesArr.add(sp);
            }
            wcf.setSpecies(speciesArr);
        } else {
            wcf.setSpecies(null);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(WeightConversionFactorController.class);
}
