package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.LengthCategory;
import ca.gc.dfo.psffs.domain.objects.lookups.LengthGroup;
import ca.gc.dfo.psffs.domain.objects.lookups.LengthUnit;
import ca.gc.dfo.psffs.domain.objects.lookups.Vessel;
import ca.gc.dfo.psffs.domain.repositories.lookups.LengthUnitRepository;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.LengthCategoryForm;
import ca.gc.dfo.psffs.forms.lookups.LengthGroupForm;
import ca.gc.dfo.psffs.forms.lookups.VesselForm;
import ca.gc.dfo.psffs.json.LookupList;
import ca.gc.dfo.psffs.services.CacheService;
import ca.gc.dfo.psffs.services.ChecksumService;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LengthGroupController extends BaseLookupController
{
    private static final String LENGTH_GROUP_PATH = TemplateController.SPECIAL_TABLES_PATH + "/lengthGroups";
    public static final String LENGTH_GROUP_LIST_PATH = LENGTH_GROUP_PATH + "/list";
    public static final String LENGTH_GROUP_LIST_DATA_PATH = LENGTH_GROUP_LIST_PATH + "-data";
    public static final String LENGTH_GROUP_CREATE_PATH = LENGTH_GROUP_PATH + "/create";
    public static final String LENGTH_GROUP_EDIT_PATH = LENGTH_GROUP_PATH + "/edit";
    public static final String LENGTH_GROUP_ACTIVATE_PATH = LENGTH_GROUP_PATH + "/activate";

    public static final String ENG_LENGTH_GROUP_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_GROUP_LIST_PATH;
    public static final String FRA_LENGTH_GROUP_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_GROUP_LIST_PATH;
    public static final String ENG_LENGTH_GROUP_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_GROUP_LIST_DATA_PATH;
    public static final String FRA_LENGTH_GROUP_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_GROUP_LIST_DATA_PATH;
    public static final String ENG_LENGTH_GROUP_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_GROUP_CREATE_PATH;
    public static final String FRA_LENGTH_GROUP_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_GROUP_CREATE_PATH;
    public static final String ENG_LENGTH_GROUP_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_GROUP_EDIT_PATH;
    public static final String FRA_LENGTH_GROUP_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_GROUP_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_GROUP_LIST_PATH, FRA_LENGTH_GROUP_LIST_PATH}, method = RequestMethod.GET)
    public String getLengthGroupList(ModelMap model)
    {
        populateCommonListModel(model, LENGTH_GROUP_LIST_DATA_PATH, LENGTH_GROUP_EDIT_PATH, LENGTH_GROUP_ACTIVATE_PATH);
        return "lookups/lengthGroup/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_GROUP_CREATE_PATH, FRA_LENGTH_GROUP_CREATE_PATH}, method = RequestMethod.GET)
    public String getLengthGroupCreate(ModelMap model)
    {
        LengthGroupForm form;
        if (model.containsAttribute("lengthGroupForm")) {
            form = (LengthGroupForm) model.get("lengthGroupForm");
        } else {
            form = new LengthGroupForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getLengthGroupRepository()) + 1);
        }
        model.addAttribute("lengthGroupForm", form);
        return "lookups/lengthGroup/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_GROUP_EDIT_PATH, FRA_LENGTH_GROUP_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getLengthGroupEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        LengthGroupForm form;
        if (model.containsAttribute("lengthGroupForm")) {
            form = (LengthGroupForm) model.get("lengthGroupForm");
        } else {
            form = new LengthGroupForm();
            LengthGroup lengthGroup = getLookupService().findLookupById(lookupId, LengthGroup.class,
                    getLookupService().getLengthGroupRepository());
            BeanUtils.copyProperties(lengthGroup, form);
        }
        model.addAttribute("lengthGroupForm", form);
        return "lookups/lengthGroup/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_GROUP_CREATE_PATH, FRA_LENGTH_GROUP_CREATE_PATH,
            ENG_LENGTH_GROUP_EDIT_PATH, FRA_LENGTH_GROUP_EDIT_PATH}, method = RequestMethod.POST)
    public String postLengthGroupSave(@ModelAttribute("lengthGroupForm") LengthGroupForm form,
                                    RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveLengthGroup(form);
            returnView += LENGTH_GROUP_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            getChecksumService().updateChecksumForObjectByEntityClass(LengthGroup.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a length group lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += LENGTH_GROUP_CREATE_PATH;
            } else {
                returnView += LENGTH_GROUP_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("lengthGroupForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_LENGTH_GROUP_LIST_DATA_PATH, FRA_LENGTH_GROUP_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getLengthGroupListData()
    {
        return getAllLengthGroupForms();
    }

    @RequestMapping(value = LENGTH_GROUP_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getLengthGroupRepository(), "lengthGroups", request);
    }

    //Private utility methods
    private LookupList getAllLengthGroupForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<LengthGroup> lengthGroups = getLookupService().getAllLookups(LengthGroup.class,
                getLookupService().getLengthGroupRepository());
        BaseLookupForm form;
        for (LengthGroup v : lengthGroups) {
            form = new LengthGroupForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveLengthGroup(LengthGroupForm form) throws InstantiationException, IllegalAccessException
    {
        LengthGroup lengthGroup;
        if (form.getId() == null) {
            lengthGroup = new LengthGroup();
        } else {
            lengthGroup = getLookupService().findLookupById(form.getId(), LengthGroup.class,
                    getLookupService().getLengthGroupRepository());
        }
        remapIdsToObjects(form, lengthGroup);

        BeanUtils.copyProperties(form, lengthGroup);

        lengthGroup.determineDescriptions();

        form.setEnglishDescription(lengthGroup.getEnglishDescription());
        form.setFrenchDescription(lengthGroup.getFrenchDescription());

        getLookupService().saveLookup(form, lengthGroup, getLookupService().getLengthGroupRepository(),
                "lengthGroups");
    }

    private void remapIdsToObjects(LengthGroupForm form, LengthGroup lengthGroup) {
        Integer lengthUnitId = form.getLengthUnitId();
        if (lengthUnitId != null) {
            LengthUnitRepository lengthUnitRepository = getLookupService().getLengthUnitRepository();
            LengthUnit lc = lengthUnitRepository.getOne(lengthUnitId);
            lengthGroup.setLengthUnit(lc);
        } else {
            lengthGroup.setLengthUnit(null);
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(LengthGroupController.class);

}
