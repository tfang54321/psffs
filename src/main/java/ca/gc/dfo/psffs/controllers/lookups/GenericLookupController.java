package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.BaseLookup;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.json.LookupList;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
public class GenericLookupController extends BaseLookupController
{
    private static final String GENERIC_PATH = "/codeTables/{codeAttr}";
    public static final String GENERIC_LOOKUP_LIST_PATH = GENERIC_PATH + "/list";
    public static final String GENERIC_LOOKUP_LIST_DATA_PATH = GENERIC_LOOKUP_LIST_PATH + "-data";
    public static final String GENERIC_LOOKUP_CREATE_PATH = GENERIC_PATH + "/create";
    public static final String GENERIC_LOOKUP_EDIT_PATH = GENERIC_PATH + "/edit";
    public static final String GENERIC_LOOKUP_ACTIVATE_PATH = GENERIC_PATH + "/activate";

    public static final String ENG_GENERIC_LOOKUP_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + GENERIC_LOOKUP_LIST_PATH;
    public static final String FRA_GENERIC_LOOKUP_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + GENERIC_LOOKUP_LIST_PATH;
    public static final String ENG_GENERIC_LOOKUP_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + GENERIC_LOOKUP_LIST_DATA_PATH;
    public static final String FRA_GENERIC_LOOKUP_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + GENERIC_LOOKUP_LIST_DATA_PATH;
    public static final String ENG_GENERIC_LOOKUP_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + GENERIC_LOOKUP_CREATE_PATH;
    public static final String FRA_GENERIC_LOOKUP_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + GENERIC_LOOKUP_CREATE_PATH;
    public static final String ENG_GENERIC_LOOKUP_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + GENERIC_LOOKUP_EDIT_PATH;
    public static final String FRA_GENERIC_LOOKUP_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + GENERIC_LOOKUP_EDIT_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_GENERIC_LOOKUP_LIST_PATH, FRA_GENERIC_LOOKUP_LIST_PATH}, method = RequestMethod.GET)
    public String getGenericLookupList(@PathVariable("codeAttr") String codeAttr, ModelMap model)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        LookupService.GenericLookup gl = getLookupService().determineGenericLookup(codeAttr);
        model.addAttribute("listDataTHPath", "/"+pageLang+GENERIC_LOOKUP_LIST_DATA_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR));
        model.addAttribute("editTHPath", "/"+pageLang+GENERIC_LOOKUP_EDIT_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR));
        model.addAttribute("activateTHPath", GENERIC_LOOKUP_ACTIVATE_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR));
        injectCommonModelAttributes(model, gl, pageLang);
        return "lookups/generic/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_GENERIC_LOOKUP_CREATE_PATH, FRA_GENERIC_LOOKUP_CREATE_PATH}, method = RequestMethod.GET)
    public String getGenericLookupCreate(@PathVariable("codeAttr") String codeAttr, ModelMap model)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        LookupService.GenericLookup gl = getLookupService().determineGenericLookup(codeAttr);
        BaseLookupForm form;
        if (model.containsAttribute("baseLookupForm")) {
            form = (BaseLookupForm) model.get("baseLookupForm");
        } else {
            form = new BaseLookupForm();
            form.setOrder(getLookupService().getLookupCount(gl) + 1);
        }
        model.addAttribute("baseLookupForm", form);
        injectCommonModelAttributes(model, gl, pageLang);
        return "lookups/generic/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_GENERIC_LOOKUP_EDIT_PATH, FRA_GENERIC_LOOKUP_EDIT_PATH}, method = RequestMethod.GET, params = "lId")
    public String getGenericLookupEdit(@PathVariable("codeAttr") String codeAttr, @RequestParam("lId") Integer lookupId,
                                       ModelMap model)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        LookupService.GenericLookup gl = getLookupService().determineGenericLookup(codeAttr);
        BaseLookupForm form;
        if (model.containsAttribute("baseLookupForm")) {
            form = (BaseLookupForm) model.get("baseLookupForm");
        } else {
            form = new BaseLookupForm();
            BaseLookup baseLookup = getLookupService().findLookupById(lookupId, gl);
            BeanUtils.copyProperties(baseLookup, form);
        }
        model.addAttribute("baseLookupForm", form);
        model.addAttribute("editTHPath", "/" + pageLang +
                GENERIC_LOOKUP_EDIT_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR) + "(lId=${" + lookupId + "})");
        injectCommonModelAttributes(model, gl, pageLang);
        return "lookups/generic/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_GENERIC_LOOKUP_CREATE_PATH, FRA_GENERIC_LOOKUP_CREATE_PATH,
                            ENG_GENERIC_LOOKUP_EDIT_PATH, FRA_GENERIC_LOOKUP_EDIT_PATH}, method = RequestMethod.POST)
    public String postGenericLookupSave(@PathVariable("codeAttr") String codeAttr,
                                          @ModelAttribute("baseLookupForm") BaseLookupForm form,
                                          RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            LookupService.GenericLookup gl = getLookupService().determineGenericLookup(codeAttr);
            getLookupService().saveLookup(form, gl);
            returnView += GENERIC_LOOKUP_LIST_PATH.replaceAll("\\{codeAttr}", codeAttr);
            redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
            getCacheService().initCacheSessionUUID(request, false);
            getChecksumService().updateChecksumForObjectByEntityClass(gl.ENTITY_CLASS);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save a generic lookup: " + ex.getMessage(), ex);
            if (request.getServletPath().contains("create")) {
                returnView += GENERIC_LOOKUP_CREATE_PATH.replaceAll("\\{codeAttr}", codeAttr);
            } else {
                returnView += GENERIC_LOOKUP_EDIT_PATH.replaceAll("\\{codeAttr}", codeAttr);
                redirectAttributes.addAttribute("lId", form.getId());
            }
            redirectAttributes.addFlashAttribute("baseLookupForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_GENERIC_LOOKUP_LIST_DATA_PATH, FRA_GENERIC_LOOKUP_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getGenericLookupListData(@PathVariable("codeAttr") String codeAttr)
    {
        LookupService.GenericLookup gl = getLookupService().determineGenericLookup(codeAttr);
        return getAllGenericLookupForms(gl);
    }

    @RequestMapping(value = GENERIC_LOOKUP_ACTIVATE_PATH, method = RequestMethod.PATCH, params = {"id","active"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateCode(@PathVariable("codeAttr") String codeAttr,
            @RequestParam("id") Integer id, @RequestParam("active") Boolean active, HttpServletRequest request)
    {
        String status;
        LookupService.GenericLookup gl = getLookupService().determineGenericLookup(codeAttr);
        boolean success;
        try {
            success = getLookupService().toggleLookupActive(gl, id, active);
        } catch (Exception ex) {
            success = false;
            if (!(ex instanceof AccessDeniedException)) {
                logger.error("An error occurred while trying to toggle active flag: " + ex.getMessage(), ex);
            } else {
                logger.debug("Failed to toggle active flag: access denied");
            }
        }
        if (success) {
            status = "success";
            getCacheService().initCacheSessionUUID(request, false);
            getChecksumService().updateChecksumForObjectByEntityClass(gl.ENTITY_CLASS);
        } else {
            status = "fail";
        }
        return status;
    }

    //Private utility methods
    private void injectCommonModelAttributes(ModelMap model, LookupService.GenericLookup gl, String pageLang)
    {
        model.addAttribute("createTHPath", "/"+pageLang+GENERIC_LOOKUP_CREATE_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR));
        model.addAttribute("glEnum", gl);
    }

    private LookupList getAllGenericLookupForms(LookupService.GenericLookup gl)
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<? extends BaseLookup> baseLookups = getLookupService().getAllGenericLookups(gl);
        BaseLookupForm form;
        for (BaseLookup bl : baseLookups) {
            form = new BaseLookupForm();
            BeanUtils.copyProperties(bl, form);
            baseLookupForms.add(form);
        }
        return new LookupList(baseLookupForms);
    }

    private static final Logger logger = LoggerFactory.getLogger(GenericLookupController.class);
}
