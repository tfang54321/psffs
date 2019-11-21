package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.NafoDivision;
import ca.gc.dfo.psffs.domain.objects.lookups.UnitArea;
import ca.gc.dfo.psffs.domain.repositories.lookups.NafoDivisionRepository;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.UnitAreaForm;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UnitAreaController extends BaseLookupController
{
    private static final String UNIT_AREA_PATH = TemplateController.SPECIAL_TABLES_PATH + "/unitArea";
    public static final String UNIT_AREA_LIST_PATH = UNIT_AREA_PATH + "/list";
    public static final String UNIT_AREA_LIST_DATA_PATH = UNIT_AREA_LIST_PATH + "-data";
    public static final String UNIT_AREA_CREATE_PATH = UNIT_AREA_PATH + "/create";
    public static final String UNIT_AREA_EDIT_PATH = UNIT_AREA_PATH + "/edit";
    public static final String UNIT_AREA_ACTIVATE_PATH = UNIT_AREA_PATH + "/activate";

    public static final String ENG_UNIT_AREA_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + UNIT_AREA_LIST_PATH;
    public static final String FRA_UNIT_AREA_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + UNIT_AREA_LIST_PATH;
    public static final String ENG_UNIT_AREA_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + UNIT_AREA_LIST_DATA_PATH;
    public static final String FRA_UNIT_AREA_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + UNIT_AREA_LIST_DATA_PATH;
    public static final String ENG_UNIT_AREA_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + UNIT_AREA_CREATE_PATH;
    public static final String FRA_UNIT_AREA_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + UNIT_AREA_CREATE_PATH;
    public static final String ENG_UNIT_AREA_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + UNIT_AREA_EDIT_PATH;
    public static final String FRA_UNIT_AREA_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + UNIT_AREA_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_UNIT_AREA_LIST_PATH, FRA_UNIT_AREA_LIST_PATH}, method = RequestMethod.GET)
    public String getUnitAreaList(ModelMap model)
    {
        populateCommonListModel(model, UNIT_AREA_LIST_DATA_PATH, UNIT_AREA_EDIT_PATH, UNIT_AREA_ACTIVATE_PATH);
        return "lookups/unitArea/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_UNIT_AREA_CREATE_PATH, FRA_UNIT_AREA_CREATE_PATH}, method = RequestMethod.GET)
    public String getUnitAreaCreate(ModelMap model)
    {
        UnitAreaForm form;
        if (model.containsAttribute("unitAreaForm")) {
            form = (UnitAreaForm) model.get("unitAreaForm");
        } else {
            form = new UnitAreaForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getUnitAreaRepository()) + 1);
        }
        model.addAttribute("unitAreaForm", form);
        return "lookups/unitArea/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_UNIT_AREA_EDIT_PATH, FRA_UNIT_AREA_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getUnitAreaEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        UnitAreaForm form;
        if (model.containsAttribute("unitAreaForm")) {
            form = (UnitAreaForm) model.get("unitAreaForm");
        } else {
            form = new UnitAreaForm();
            UnitArea unitArea = getLookupService().findLookupById(lookupId, UnitArea.class,
                    getLookupService().getUnitAreaRepository());
            BeanUtils.copyProperties(unitArea, form);
        }
        model.addAttribute("unitAreaForm", form);
        return "lookups/unitArea/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_UNIT_AREA_CREATE_PATH, FRA_UNIT_AREA_CREATE_PATH,
            ENG_UNIT_AREA_EDIT_PATH, FRA_UNIT_AREA_EDIT_PATH}, method = RequestMethod.POST)
    public String postUnitAreaSave(@ModelAttribute("unitAreaForm") UnitAreaForm form,
                                RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveUnitArea(form);
            returnView += UNIT_AREA_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            // Reset the checksum for Vessels so that new offline data will be downloaded.
            getChecksumService().updateChecksumForObjectByEntityClass(UnitArea.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a unit area lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += UNIT_AREA_CREATE_PATH;
            } else {
                returnView += UNIT_AREA_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("unitAreaForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_UNIT_AREA_LIST_DATA_PATH, FRA_UNIT_AREA_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getUnitAreaListData()
    {
        return getAllUnitAreaForms();
    }

    @RequestMapping(value = UNIT_AREA_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getUnitAreaRepository(), "unitAreas", request);
    }

    //Private utility methods
    private LookupList getAllUnitAreaForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<UnitArea> unitAreas = getLookupService().getAllLookups(UnitArea.class,
                getLookupService().getUnitAreaRepository());
        BaseLookupForm form;
        for (UnitArea v : unitAreas) {
            form = new UnitAreaForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveUnitArea(UnitAreaForm form) throws InstantiationException, IllegalAccessException
    {
        UnitArea unitArea;
        if (form.getId() == null) {
            unitArea = new UnitArea();
        } else {
            unitArea = getLookupService().findLookupById(form.getId(), UnitArea.class,
                    getLookupService().getUnitAreaRepository());
        }

        remapIdsToObjects(form, unitArea);

        getLookupService().saveLookup(form, unitArea, getLookupService().getUnitAreaRepository(), "unitAreas");
    }

    private void remapIdsToObjects(UnitAreaForm form, UnitArea unitArea)
    {
        Integer nafoDivId = form.getNafoDivisionId();
            if (nafoDivId != null) {
                NafoDivision nafoDivision = getLookupService().findLookupById(nafoDivId, NafoDivision.class,
                        getLookupService().getGenericLookupRepository(NafoDivisionRepository.class));
                unitArea.setNafoDivision(nafoDivision);
            } else {
                unitArea.setNafoDivision(null);
            }
    }

    private static final Logger logger = LoggerFactory.getLogger(UnitAreaController.class);
}
