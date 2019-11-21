package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.LengthCategory;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.LengthCategoryForm;
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
public class LengthCategoryController extends BaseLookupController
{
    private static final String LENGTH_CAT_PATH = TemplateController.SPECIAL_TABLES_PATH + "/lengthCategories";
    public static final String LENGTH_CAT_LIST_PATH = LENGTH_CAT_PATH + "/list";
    public static final String LENGTH_CAT_LIST_DATA_PATH = LENGTH_CAT_LIST_PATH + "-data";
    public static final String LENGTH_CAT_CREATE_PATH = LENGTH_CAT_PATH + "/create";
    public static final String LENGTH_CAT_EDIT_PATH = LENGTH_CAT_PATH + "/edit";
    public static final String LENGTH_CAT_ACTIVATE_PATH = LENGTH_CAT_PATH + "/activate";

    public static final String ENG_LENGTH_CAT_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_CAT_LIST_PATH;
    public static final String FRA_LENGTH_CAT_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_CAT_LIST_PATH;
    public static final String ENG_LENGTH_CAT_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_CAT_LIST_DATA_PATH;
    public static final String FRA_LENGTH_CAT_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_CAT_LIST_DATA_PATH;
    public static final String ENG_LENGTH_CAT_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_CAT_CREATE_PATH;
    public static final String FRA_LENGTH_CAT_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_CAT_CREATE_PATH;
    public static final String ENG_LENGTH_CAT_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_CAT_EDIT_PATH;
    public static final String FRA_LENGTH_CAT_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_CAT_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_CAT_LIST_PATH, FRA_LENGTH_CAT_LIST_PATH}, method = RequestMethod.GET)
    public String getLengthCatList(ModelMap model)
    {
        populateCommonListModel(model, LENGTH_CAT_LIST_DATA_PATH, LENGTH_CAT_EDIT_PATH, LENGTH_CAT_ACTIVATE_PATH);
        return "lookups/lengthCategories/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_CAT_CREATE_PATH, FRA_LENGTH_CAT_CREATE_PATH}, method = RequestMethod.GET)
    public String getLengthCatCreate(ModelMap model)
    {
        LengthCategoryForm form;
        if (model.containsAttribute("lengthCategoryForm")) {
            form = (LengthCategoryForm) model.get("lengthCategoryForm");
        } else {
            form = new LengthCategoryForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getLengthCategoryRepository()) + 1);
        }
        model.addAttribute("lengthCategoryForm", form);
        return "lookups/lengthCategories/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_CAT_EDIT_PATH, FRA_LENGTH_CAT_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getLengthCatEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        LengthCategoryForm form;
        if (model.containsAttribute("lengthCategoryForm")) {
            form = (LengthCategoryForm) model.get("lengthCategoryForm");
        } else {
            form = new LengthCategoryForm();
            LengthCategory lengthCategory = getLookupService().findLookupById(lookupId, LengthCategory.class,
                    getLookupService().getLengthCategoryRepository());
            BeanUtils.copyProperties(lengthCategory, form);
        }
        model.addAttribute("lengthCategoryForm", form);
        return "lookups/lengthCategories/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_CAT_CREATE_PATH, FRA_LENGTH_CAT_CREATE_PATH,
            ENG_LENGTH_CAT_EDIT_PATH, FRA_LENGTH_CAT_EDIT_PATH}, method = RequestMethod.POST)
    public String postLengthCatSave(@ModelAttribute("lengthCategoryForm") LengthCategoryForm form,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            if(form.getMaxMeters() >= form.getMinMeters()){
                saveLengthCat(form);
                returnView += LENGTH_CAT_LIST_PATH;
                redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
                // Re-init AppCache session UUID so the user will re-download the entire site including this change.
                getCacheService().initCacheSessionUUID(request, false);
                // Reset the checksum for Length Category so that new offline data will be downloaded.
                getChecksumService().updateChecksumForObjectByEntityClass(LengthCategory.class);
            }
            else {
                if (request.getServletPath().contains("create")) {
                    returnView += LENGTH_CAT_CREATE_PATH;
                } else {
                    returnView += LENGTH_CAT_EDIT_PATH;
                    redirectAttributes.addAttribute("lId", form.getId());
                }
                redirectAttributes.addFlashAttribute("lengthCategoryForm", form);
                redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.max_less_than_min_meters");
            }
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a length category lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += LENGTH_CAT_CREATE_PATH;
            } else {
                returnView += LENGTH_CAT_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("lengthCategoryForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_CAT_LIST_DATA_PATH, FRA_LENGTH_CAT_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getLengthCatListData()
    {
        return getAllLengthCatForms();
    }

    @RequestMapping(value = LENGTH_CAT_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getLengthCategoryRepository(), "lengthCategories", request);
    }

    //Private utility methods
    private LookupList getAllLengthCatForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<LengthCategory> lengthCats = getLookupService().getAllLookups(LengthCategory.class,
                getLookupService().getLengthCategoryRepository());
        BaseLookupForm form;
        for (LengthCategory v : lengthCats) {
            form = new LengthCategoryForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveLengthCat(LengthCategoryForm form) throws InstantiationException, IllegalAccessException
    {
        LengthCategory lengthCategory;
        if (form.getId() == null) {
            lengthCategory = new LengthCategory();
        } else {
            lengthCategory = getLookupService().findLookupById(form.getId(), LengthCategory.class,
                    getLookupService().getLengthCategoryRepository());
        }

        BeanUtils.copyProperties(form, lengthCategory);

        lengthCategory.determineDescriptions();

        form.setEnglishDescription(lengthCategory.getEnglishDescription());
        form.setFrenchDescription(lengthCategory.getFrenchDescription());

        getLookupService().saveLookup(form, lengthCategory, getLookupService().getLengthCategoryRepository(),
                "lengthCategories");
    }

    private static final Logger logger = LoggerFactory.getLogger(LengthCategoryController.class);
}
