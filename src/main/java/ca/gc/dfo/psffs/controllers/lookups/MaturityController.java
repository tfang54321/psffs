package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.Maturity;
import ca.gc.dfo.psffs.domain.objects.lookups.SexCode;
import ca.gc.dfo.psffs.domain.repositories.lookups.SexCodeRepository;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.MaturityForm;
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
import java.util.stream.Collectors;

@Controller
public class MaturityController extends BaseLookupController
{
    private static final String MATURITY_PATH = TemplateController.SPECIAL_TABLES_PATH + "/maturities";
    public static final String MATURITY_LIST_PATH = MATURITY_PATH + "/list";
    public static final String MATURITY_LIST_DATA_PATH = MATURITY_LIST_PATH + "-data";
    public static final String MATURITY_CREATE_PATH = MATURITY_PATH + "/create";
    public static final String MATURITY_EDIT_PATH = MATURITY_PATH + "/edit";
    public static final String MATURITY_ACTIVATE_PATH = MATURITY_PATH + "/activate";

    public static final String ENG_MATURITY_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + MATURITY_LIST_PATH;
    public static final String FRA_MATURITY_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + MATURITY_LIST_PATH;
    public static final String ENG_MATURITY_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + MATURITY_LIST_DATA_PATH;
    public static final String FRA_MATURITY_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + MATURITY_LIST_DATA_PATH;
    public static final String ENG_MATURITY_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + MATURITY_CREATE_PATH;
    public static final String FRA_MATURITY_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + MATURITY_CREATE_PATH;
    public static final String ENG_MATURITY_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + MATURITY_EDIT_PATH;
    public static final String FRA_MATURITY_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + MATURITY_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_MATURITY_LIST_PATH, FRA_MATURITY_LIST_PATH}, method = RequestMethod.GET)
    public String getMaturityList(ModelMap model)
    {
        populateCommonListModel(model, MATURITY_LIST_DATA_PATH, MATURITY_EDIT_PATH, MATURITY_ACTIVATE_PATH);
        return "lookups/maturity/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_MATURITY_CREATE_PATH, FRA_MATURITY_CREATE_PATH}, method = RequestMethod.GET)
    public String getMaturityCreate(ModelMap model)
    {
        MaturityForm form;
        if (model.containsAttribute("maturityForm")) {
            form = (MaturityForm) model.get("maturityForm");
        } else {
            form = new MaturityForm();
            form.setOrder(getLookupService().getLookupCount(getLookupService().getMaturityRepository()) + 1);
        }
        model.addAttribute("maturityForm", form);
        return "lookups/maturity/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_MATURITY_EDIT_PATH, FRA_MATURITY_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getMaturityEdit(@RequestParam("lId") Integer lookupId, ModelMap model)
    {
        MaturityForm form;
        if (model.containsAttribute("maturityForm")) {
            form = (MaturityForm) model.get("maturityForm");
        } else {
            form = new MaturityForm();
            Maturity maturity = getLookupService().findLookupById(lookupId, Maturity.class,
                    getLookupService().getMaturityRepository());
            BeanUtils.copyProperties(maturity, form);
        }
        model.addAttribute("maturityForm", form);
        return "lookups/maturity/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_MATURITY_CREATE_PATH, FRA_MATURITY_CREATE_PATH,
            ENG_MATURITY_EDIT_PATH, FRA_MATURITY_EDIT_PATH}, method = RequestMethod.POST)
    public String postMaturitySave(@ModelAttribute("maturityForm") MaturityForm form,
                                      RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            saveMaturity(form);
            returnView += MATURITY_LIST_PATH;
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            // Re-init AppCache session UUID so the user will re-download the entire site including this change.
            getCacheService().initCacheSessionUUID(request, false);
            getChecksumService().updateChecksumForObjectByEntityClass(Maturity.class);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a maturity lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += MATURITY_CREATE_PATH;
            } else {
                returnView += MATURITY_EDIT_PATH;
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("maturityForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_MATURITY_LIST_DATA_PATH, FRA_MATURITY_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getMaturityData()
    {
        return getAllMaturityForms();
    }

    @RequestMapping(value = MATURITY_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@RequestParam("id") Integer id,
                                                       @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        return toggleActiveFlag(id, active, getLookupService().getMaturityRepository(), "maturities", request);
    }

    //Private utility methods
    private LookupList getAllMaturityForms()
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<Maturity> maturities = getLookupService().getAllLookups(Maturity.class,
                getLookupService().getMaturityRepository());
        maturities = maturities.stream()
                               .filter(m -> !m.getId().equals(0))
                               .collect(Collectors.toList());
        BaseLookupForm form;
        for (Maturity v : maturities) {
            form = new MaturityForm();
            BeanUtils.copyProperties(v, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private void saveMaturity(MaturityForm form) throws InstantiationException, IllegalAccessException
    {
        Maturity maturity;
        if (form.getId() == null) {
            maturity = new Maturity();
        } else {
            maturity = getLookupService().findLookupById(form.getId(), Maturity.class,
                    getLookupService().getMaturityRepository());
        }

        remapIdsToObjects(form, maturity);

        getLookupService().saveLookup(form, maturity, getLookupService().getMaturityRepository(), "maturities");
    }

    private void remapIdsToObjects(MaturityForm form, Maturity maturity)
    {
        Integer sexCodeId = form.getSexCodeId();
        if (sexCodeId != null) {
            SexCodeRepository sexCodeRepository = getLookupService().getSexCodeRepository();
            SexCode sc = sexCodeRepository.getOne(sexCodeId);
            maturity.setSexCode(sc);
        } else {
            maturity.setSexCode(null);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(MaturityController.class);
}
