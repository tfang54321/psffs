package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.LengthCategory;
import ca.gc.dfo.psffs.domain.objects.lookups.Tonnage;
import ca.gc.dfo.psffs.domain.objects.lookups.Vessel;
import ca.gc.dfo.psffs.domain.repositories.lookups.TonnageRepository;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.VesselForm;
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
public class VesselController extends BaseLookupController
{
    private static final String VESSEL_PATH = TemplateController.SPECIAL_TABLES_PATH + "/vessels";
    public static final String VESSEL_LIST_PATH = VESSEL_PATH + "/list";
    public static final String VESSEL_LIST_DATA_PATH = VESSEL_LIST_PATH + "-data";
    public static final String VESSEL_CREATE_PATH = VESSEL_PATH + "/create";
    public static final String VESSEL_EDIT_PATH = VESSEL_PATH + "/edit";
    public static final String VESSEL_ACTIVATE_PATH = VESSEL_PATH + "/activate";

    public static final String ENG_VESSEL_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + VESSEL_LIST_PATH;
    public static final String FRA_VESSEL_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + VESSEL_LIST_PATH;
    public static final String ENG_VESSEL_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + VESSEL_LIST_DATA_PATH;
    public static final String FRA_VESSEL_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + VESSEL_LIST_DATA_PATH;
    public static final String ENG_VESSEL_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + VESSEL_CREATE_PATH;
    public static final String FRA_VESSEL_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + VESSEL_CREATE_PATH;
    public static final String ENG_VESSEL_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + VESSEL_EDIT_PATH;
    public static final String FRA_VESSEL_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + VESSEL_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_VESSEL_LIST_PATH, FRA_VESSEL_LIST_PATH}, method = RequestMethod.GET)
    public String getVesselList(ModelMap model)
    {
        populateCommonListModel(model, VESSEL_LIST_DATA_PATH, VESSEL_EDIT_PATH, VESSEL_ACTIVATE_PATH);
        return "lookups/vessel/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_VESSEL_CREATE_PATH, FRA_VESSEL_CREATE_PATH}, method = RequestMethod.GET)
    public String getVesselCreate(ModelMap model)
    {
        VesselForm form;
        if (model.containsAttribute("vesselForm")) {
            form = (VesselForm) model.get("vesselForm");
        } else {
            form = new VesselForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getVesselRepository()) + 1);
        }
        model.addAttribute("vesselForm", form);
        return "lookups/vessel/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_VESSEL_EDIT_PATH, FRA_VESSEL_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getVesselEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        VesselForm form;
        if (model.containsAttribute("vesselForm")) {
            form = (VesselForm) model.get("vesselForm");
        } else {
            form = new VesselForm();
            Vessel vessel = getLookupService().findLookupById(lookupId, Vessel.class,
                    getLookupService().getVesselRepository());
            BeanUtils.copyProperties(vessel, form);
            if (vessel.getLengthCategory() != null) {
                form.setVesselLengthCategoryData(getLookupService().getSpecificLookupByIdentifier("lengthCategories",
                        vessel.getLengthCategoryId()).getValue());
            }
        }
        model.addAttribute("vesselForm", form);
        return "lookups/vessel/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_VESSEL_CREATE_PATH, FRA_VESSEL_CREATE_PATH,
            ENG_VESSEL_EDIT_PATH, FRA_VESSEL_EDIT_PATH}, method = RequestMethod.POST)
    public String postVesselSave(@ModelAttribute("vesselForm") VesselForm form,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveVessel(form);
            returnView += VESSEL_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            // Reset the checksum for Vessels so that new offline data will be downloaded.
            getChecksumService().updateChecksumForObjectByEntityClass(Vessel.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a vessel lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += VESSEL_CREATE_PATH;
            } else {
                returnView += VESSEL_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("vesselForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_VESSEL_LIST_DATA_PATH, FRA_VESSEL_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getVesselListData()
    {
        return getAllVesselForms();
    }

    @RequestMapping(value = VESSEL_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getVesselRepository(), "vessels", request);
    }

    //Private utility methods
    private LookupList getAllVesselForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<Vessel> vessels = getLookupService().getAllLookups(Vessel.class, getLookupService().getVesselRepository());
        BaseLookupForm form;
        for (Vessel v : vessels) {
            form = new VesselForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveVessel(VesselForm form) throws InstantiationException, IllegalAccessException
    {
        Vessel vessel;
        if (form.getId() == null) {
            vessel = new Vessel();
        } else {
            vessel = getLookupService().findLookupById(form.getId(), Vessel.class,
                    getLookupService().getVesselRepository());
        }

        remapIdsToObjects(form, vessel);

        getLookupService().saveLookup(form, vessel, getLookupService().getVesselRepository(), "vessels");
    }

    private void remapIdsToObjects(VesselForm form, Vessel vessel)
    {
        String vesselLengthCategoryData = form.getVesselLengthCategoryData();
        if (!vesselLengthCategoryData.equals("")) {
            Integer lcId = Integer.valueOf(vesselLengthCategoryData.split(";")[0]);
            LengthCategory lc = getLookupService().findLookupById(lcId, LengthCategory.class,
                    getLookupService().getLengthCategoryRepository());
            vessel.setLengthCategory(lc);
        } else {
            vessel.setLengthCategory(null);
        }

        Integer tonnageId = form.getTonnageId();
        if (tonnageId != null) {
            Tonnage tonnage = getLookupService().findLookupById(tonnageId, Tonnage.class,
                    getLookupService().getGenericLookupRepository(TonnageRepository.class));
            vessel.setTonnage(tonnage);
        } else {
            vessel.setTonnage(null);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(VesselController.class);
}
