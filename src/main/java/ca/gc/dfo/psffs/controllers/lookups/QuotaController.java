package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.*;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.QuotaForm;
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
public class QuotaController extends BaseLookupController
{
    private static final String QUOTA_PATH = TemplateController.SPECIAL_TABLES_PATH + "/quota";
    public static final String QUOTA_LIST_PATH = QUOTA_PATH + "/list";
    public static final String QUOTA_LIST_DATA_PATH = QUOTA_LIST_PATH + "-data";
    public static final String QUOTA_CREATE_PATH = QUOTA_PATH + "/create";
    public static final String QUOTA_EDIT_PATH = QUOTA_PATH + "/edit";
    public static final String QUOTA_ACTIVATE_PATH = QUOTA_PATH + "/activate";

    public static final String ENG_QUOTA_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUOTA_LIST_PATH;
    public static final String FRA_QUOTA_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUOTA_LIST_PATH;
    public static final String ENG_QUOTA_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUOTA_LIST_DATA_PATH;
    public static final String FRA_QUOTA_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUOTA_LIST_DATA_PATH;
    public static final String ENG_QUOTA_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUOTA_CREATE_PATH;
    public static final String FRA_QUOTA_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUOTA_CREATE_PATH;
    public static final String ENG_QUOTA_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUOTA_EDIT_PATH;
    public static final String FRA_QUOTA_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUOTA_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_QUOTA_LIST_PATH, FRA_QUOTA_LIST_PATH}, method = RequestMethod.GET)
    public String getQuotaList(ModelMap model)
    {
        populateCommonListModel(model, QUOTA_LIST_DATA_PATH, QUOTA_EDIT_PATH, QUOTA_ACTIVATE_PATH);
        return "lookups/quota/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_QUOTA_CREATE_PATH, FRA_QUOTA_CREATE_PATH}, method = RequestMethod.GET)
    public String getQuotaCreate(ModelMap model)
    {
        QuotaForm form;
        if (model.containsAttribute("quotaForm")) {
            form = (QuotaForm) model.get("quotaForm");
        } else {
            form = new QuotaForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getVesselRepository()) + 1);
        }
        model.addAttribute("quotaForm", form);
        return "lookups/quota/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_QUOTA_EDIT_PATH, FRA_QUOTA_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getQuotaEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        QuotaForm form;
        if (model.containsAttribute("quotaForm")) {
            form = (QuotaForm) model.get("quotaForm");
        } else {
            form = new QuotaForm();
            Country quota = getLookupService().findLookupById(lookupId, Country.class,
                    getLookupService().getCountryRepository());
            BeanUtils.copyProperties(quota, form);
        }
        model.addAttribute("quotaForm", form);
        return "lookups/quota/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_QUOTA_CREATE_PATH, FRA_QUOTA_CREATE_PATH,
            ENG_QUOTA_EDIT_PATH, FRA_QUOTA_EDIT_PATH}, method = RequestMethod.POST)
    public String postQuotaSave(@ModelAttribute("quotaForm") QuotaForm form,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveQuota(form);
            returnView += QUOTA_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            // Reset the checksum for Vessels so that new offline data will be downloaded.
            getChecksumService().updateChecksumForObjectByEntityClass(Country.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a quota lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += QUOTA_CREATE_PATH;
            } else {
                returnView += QUOTA_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("quotaForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_QUOTA_LIST_DATA_PATH, FRA_QUOTA_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getQuotaListData()
    {
        return getAllQuotaForms();
    }

    @RequestMapping(value = QUOTA_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getCountryRepository(), "countries", request);
    }

    //Private utility methods
    private LookupList getAllQuotaForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<Country> quotas = getLookupService().getAllLookups(Country.class,
                getLookupService().getCountryRepository());
        BaseLookupForm form;
        for (Country v : quotas) {
            form = new QuotaForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveQuota(QuotaForm form) throws InstantiationException, IllegalAccessException
    {
        Country quota;
        if (form.getId() == null) {
            quota = new Country();
        } else {
            quota = getLookupService().findLookupById(form.getId(), Country.class,
                    getLookupService().getCountryRepository());
        }

        getLookupService().saveLookup(form, quota, getLookupService().getCountryRepository(), "countries");
    }

    private static final Logger logger = LoggerFactory.getLogger(QuotaController.class);
}
