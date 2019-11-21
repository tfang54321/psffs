package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.*;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.SpeciesForm;
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
public class SpeciesController extends BaseLookupController
{
    private static final String SPECIES_PATH = TemplateController.SPECIAL_TABLES_PATH + "/species";
    public static final String SPECIES_LIST_PATH = SPECIES_PATH + "/list";
    public static final String SPECIES_LIST_DATA_PATH = SPECIES_LIST_PATH + "-data";
    public static final String SPECIES_CREATE_PATH = SPECIES_PATH + "/create";
    public static final String SPECIES_EDIT_PATH = SPECIES_PATH + "/edit";
    public static final String SPECIES_ACTIVATE_PATH = SPECIES_PATH + "/activate";

    public static final String ENG_SPECIES_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + SPECIES_LIST_PATH;
    public static final String FRA_SPECIES_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + SPECIES_LIST_PATH;
    public static final String ENG_SPECIES_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + SPECIES_LIST_DATA_PATH;
    public static final String FRA_SPECIES_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + SPECIES_LIST_DATA_PATH;
    public static final String ENG_SPECIES_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + SPECIES_CREATE_PATH;
    public static final String FRA_SPECIES_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + SPECIES_CREATE_PATH;
    public static final String ENG_SPECIES_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + SPECIES_EDIT_PATH;
    public static final String FRA_SPECIES_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + SPECIES_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_SPECIES_LIST_PATH, FRA_SPECIES_LIST_PATH}, method = RequestMethod.GET)
    public String getSpeciesList(ModelMap model)
    {
        populateCommonListModel(model, SPECIES_LIST_DATA_PATH, SPECIES_EDIT_PATH, SPECIES_ACTIVATE_PATH);
        return "lookups/species/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_SPECIES_CREATE_PATH, FRA_SPECIES_CREATE_PATH}, method = RequestMethod.GET)
    public String getSpeciesCreate(ModelMap model)
    {
        SpeciesForm form;
        if (model.containsAttribute("speciesForm")) {
            form = (SpeciesForm) model.get("speciesForm");
        } else {
            form = new SpeciesForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getSpeciesRepository()) + 1);
        }
        model.addAttribute("speciesForm", form);
        return "lookups/species/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_SPECIES_EDIT_PATH, FRA_SPECIES_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getSpeciesEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        SpeciesForm form;
        if (model.containsAttribute("speciesForm")) {
            form = (SpeciesForm) model.get("speciesForm");
        } else {
            form = new SpeciesForm();
            Species species = getLookupService().findLookupById(lookupId, Species.class,
                    getLookupService().getSpeciesRepository());
            BeanUtils.copyProperties(species, form);
        }
        model.addAttribute("speciesForm", form);
        return "lookups/species/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_SPECIES_CREATE_PATH, FRA_SPECIES_CREATE_PATH,
            ENG_SPECIES_EDIT_PATH, FRA_SPECIES_EDIT_PATH}, method = RequestMethod.POST)
    public String postSpeciesSave(@ModelAttribute("speciesForm") SpeciesForm form,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveSpecies(form);
            returnView += SPECIES_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            getChecksumService().updateChecksumForObjectByEntityClass(Species.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a species lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += SPECIES_CREATE_PATH;
            } else {
                returnView += SPECIES_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("speciesForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_SPECIES_LIST_DATA_PATH, FRA_SPECIES_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getSpeciesListData()
    {
        return getAllSpeciesForms();
    }

    @RequestMapping(value = SPECIES_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getSpeciesRepository(), "species", request);
    }

    //Private utility methods
    private LookupList getAllSpeciesForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<Species> species = getLookupService().getAllLookups(Species.class,
                getLookupService().getSpeciesRepository());
        BaseLookupForm form;
        for (Species v : species) {
            form = new SpeciesForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveSpecies(SpeciesForm form) throws InstantiationException, IllegalAccessException
    {
        Species species;
        if (form.getId() == null) {
            species = new Species();
        } else {
            species = getLookupService().findLookupById(form.getId(), Species.class,
                    getLookupService().getSpeciesRepository());
        }

        getLookupService().saveLookup(form, species, getLookupService().getSpeciesRepository(), "species");
    }

    private static final Logger logger = LoggerFactory.getLogger(SpeciesController.class);
}
