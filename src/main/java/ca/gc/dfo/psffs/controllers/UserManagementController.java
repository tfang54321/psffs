package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.audit.CommonAuditDto;
import ca.gc.dfo.psffs.domain.objects.security.User;
import ca.gc.dfo.psffs.domain.objects.security.UserRole;
import ca.gc.dfo.psffs.forms.UserManagementForm;
import ca.gc.dfo.psffs.json.UserList;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.psffs.services.SettingService;
import ca.gc.dfo.psffs.services.UserAuditingServiceInterface;
import ca.gc.dfo.psffs.services.UserService;
import ca.gc.dfo.psffs.utility.GeneratePdfReport;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class UserManagementController implements MessageSourceAware {

    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static final String USER_MANAGEMENT_LIST_PATH = TemplateController.USER_MANAGEMENT_PATH + "/list";
    public static final String USER_MANAGEMENT_DATA_PATH = TemplateController.USER_MANAGEMENT_PATH + "/data";
    public static final String USER_MANAGEMENT_CREATE_PATH = TemplateController.USER_MANAGEMENT_PATH + "/create";
    public static final String USER_MANAGEMENT_EDIT_PATH = TemplateController.USER_MANAGEMENT_PATH + "/edit";
    public static final String USER_MANAGEMENT_ACTIVATE_PATH = TemplateController.USER_MANAGEMENT_PATH + "/activate";
    public static final String USER_MANAGEMENT_ROLE_PATH = TemplateController.USER_MANAGEMENT_PATH + "/roles/list";

    public static final String ENG_USER_MANAGEMENT_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_LIST_PATH;
    public static final String FRA_USER_MANAGEMENT_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_LIST_PATH;

    public static final String ENG_USER_MANAGEMENT_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_DATA_PATH;
    public static final String FRA_USER_MANAGEMENT_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_DATA_PATH;

    public static final String ENG_USER_MANAGEMENT_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_CREATE_PATH;
    public static final String FRA_USER_MANAGEMENT_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_CREATE_PATH;

    public static final String ENG_USER_MANAGEMENT_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_EDIT_PATH;
    public static final String FRA_USER_MANAGEMENT_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_EDIT_PATH;

    public static final String ENG_USER_MANAGEMENT_ACTIVATE_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_ACTIVATE_PATH;
    public static final String FRA_USER_MANAGEMENT_ACTIVATE_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_ACTIVATE_PATH;

    public static final String ENG_USER_MANAGEMENT_ROLE_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_ROLE_PATH;
    public static final String FRA_USER_MANAGEMENT_ROLE_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_ROLE_PATH;


    public static final String  USER_MANAGEMENT_SINGLE_REPORT_PATH 			= TemplateController.USER_MANAGEMENT_PATH + "/list/report/single";
    public static final String  USER_MANAGEMENT_ALL_REPORT_PATH 			= TemplateController.USER_MANAGEMENT_PATH + "/reports";
    public static final String  USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH 			= TemplateController.USER_MANAGEMENT_PATH + "/list/report/download/all";
    public static final String  USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH 			= TemplateController.USER_MANAGEMENT_PATH + "/list/report/download/single";


    public static final String  ENG_USER_MANAGEMENT_SINGLE_REPORT_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_SINGLE_REPORT_PATH ;
    public static final String  FRA_USER_MANAGEMENT_SINGLE_REPORT_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_SINGLE_REPORT_PATH ;


    public static final String  ENG_USER_MANAGEMENT_ALL_REPORT_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_ALL_REPORT_PATH ;
    public static final String  FRA_USER_MANAGEMENT_ALL_REPORT_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_ALL_REPORT_PATH ;

    public static final String  ENG_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH ;
    public static final String  FRA_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH ;


    public static final String  ENG_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH ;
    public static final String  FRA_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH ;


    @Autowired
    private UserService userService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private LookupService lookupService;


    @Autowired
    private UserAuditingServiceInterface userAuditService;

    @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_LIST_PATH, FRA_USER_MANAGEMENT_LIST_PATH}, method = RequestMethod.GET)
    public String userManagementList() {
        return "userManagement/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_DATA_PATH, FRA_USER_MANAGEMENT_DATA_PATH}, method = RequestMethod.GET)
    public @ResponseBody UserList userManagementData() {
        return userService.getUserList();
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_CREATE_PATH, FRA_USER_MANAGEMENT_CREATE_PATH}, method = RequestMethod.GET)
    public String userManagementCreate(ModelMap model, Locale locale)
    {
        UserManagementForm uaForm = new UserManagementForm();
        uaForm.setActiveFlag(1);
        model.addAttribute("userManagementForm", uaForm);
        model.addAttribute("infoMessage", messageSource.getMessage("page.user_mgmt.read_only_info", null, locale));
        return "userManagement/create";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_CREATE_PATH, FRA_USER_MANAGEMENT_CREATE_PATH}, method = RequestMethod.POST)
    public String userManagementCreatePost(UserManagementForm uaForm, ModelMap model, RedirectAttributes redirectAttrs, Locale locale)
    {
        String userName = validateUserName(uaForm.getNtPrincipal());
        if(userName.equals("Invalid")){
            model.addAttribute("errorMessage", messageSource.getMessage("page.user_mgmt.invalid_username.format", new Object[] {settingService.getSettingValueByName("USERNAME_SUFFIX")}, locale));
            model.addAttribute("userManagementForm", uaForm);
            return "userManagement/create";
        }
        else {

            if(userService.findUserByUsername(userName) != null){
                model.addAttribute("errorMessage", messageSource.getMessage("page.user_mgmt.invalid_username.exists", new Object[]{userName}, locale));
                model.addAttribute("userManagementForm", uaForm);
                return "userManagement/create";
            }
            else if(userService.findUserByInitials(uaForm.getInitials()) != null){
                model.addAttribute("errorMessage", messageSource.getMessage("page.user_mgmt.invalid_initials.exists", new Object[]{uaForm.getInitials()}, locale));
                model.addAttribute("userManagementForm", uaForm);
                return "userManagement/create";
            }

            uaForm.setNtPrincipal(userName);
            User user = userService.addUserForm(uaForm);

            redirectAttrs.addFlashAttribute("successMessage", messageSource.getMessage("page.user_mgmt.add_success", new Object[]{user.getNtPrincipal()}, locale));
            String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
            return "redirect:/" + pageLang + USER_MANAGEMENT_LIST_PATH;
        }
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_EDIT_PATH, FRA_USER_MANAGEMENT_EDIT_PATH}, method = RequestMethod.GET)
    public String userManagementEdit(@RequestParam Integer id, ModelMap model, Locale locale)
    {
        UserManagementForm uaForm = new UserManagementForm();
        BeanUtils.copyProperties(userService.getUserById(id), uaForm);

        Integer role = null;

        for (UserRole ua:
             uaForm.getUserRoles()) {
            if(!ua.getRole().getRoleName().equals("ROLE_BASE_ACCESS")){
                role = ua.getRole().getRoleId();
            }
        }

        uaForm.setRole(role);
        if(uaForm.getFirstName() == null || uaForm.getFirstName().equals("")) {
            model.addAttribute("infoMessage", messageSource.getMessage("page.user_mgmt.read_only_info", null, locale));
        }
        model.addAttribute("userManagementForm", uaForm);
        return "userManagement/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_EDIT_PATH, FRA_USER_MANAGEMENT_EDIT_PATH}, method = RequestMethod.POST)
    public String userManagementEditPost(UserManagementForm uaForm, ModelMap model, RedirectAttributes redirectAttrs, Locale locale)
    {
        String userName = validateUserName(uaForm.getNtPrincipal());
        if(userName.equals("Invalid")){
            model.addAttribute("errorMessage", messageSource.getMessage("page.user_mgmt.invalid_username.format", new Object[] {settingService.getSettingValueByName("USERNAME_SUFFIX")}, locale));
            model.addAttribute("userManagementForm", uaForm);
            return "userManagement/edit";
        }
        else {
            User userNameTest = userService.findUserByUsername(userName);
            User initialTest = userService.findUserByInitials(uaForm.getInitials());
            if (userNameTest != null && !userNameTest.getUserId().equals(uaForm.getUserId())) {
                model.addAttribute("errorMessage", messageSource.getMessage("page.user_mgmt.invalid_username.exists", new Object[]{userName}, locale));
                model.addAttribute("userManagementForm", uaForm);
                return "userManagement/edit";
            } else if (initialTest != null && !initialTest.getUserId().equals(uaForm.getUserId())) {
                model.addAttribute("errorMessage", messageSource.getMessage("page.user_mgmt.invalid_initials.exists", new Object[]{uaForm.getInitials()}, locale));
                model.addAttribute("userManagementForm", uaForm);
                return "userManagement/edit";
            }

            uaForm.setNtPrincipal(userName);
            User user = userService.editUserForm(uaForm);

            redirectAttrs.addFlashAttribute("successMessage", messageSource.getMessage("page.user_mgmt.edit_success", new Object[]{user.getNtPrincipal()}, locale));
            String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
            return "redirect:/" + pageLang + USER_MANAGEMENT_LIST_PATH;
        }
    }

    @RequestMapping(value = {ENG_USER_MANAGEMENT_ACTIVATE_PATH,FRA_USER_MANAGEMENT_ACTIVATE_PATH}, method = RequestMethod.PATCH, params = {"id"}, produces = "text/plain")
    public @ResponseBody String activateDeactivateUser(@RequestParam("id") Integer id)
    {
        String status = "success";
        try {
            userService.toggleUserActive(id);
        } catch (Exception ex) {
            status = "fail";
            if (!(ex instanceof AccessDeniedException)) {
                logger.error("An error occurred while trying to toggle user active flag: " + ex.getMessage(), ex);
            } else {
                logger.debug("Failed to toggle user active flag: access denied");
            }
        }
        return status;
    }

    @RequestMapping(value = {ENG_USER_MANAGEMENT_ROLE_PATH, FRA_USER_MANAGEMENT_ROLE_PATH}, method = RequestMethod.GET)
    public String userManagementRoles(ModelMap model)
    {
        model.addAttribute("roles", lookupService.getRoleRepository().findAll());
        return "userManagement/roles/list";
    }

    private String validateUserName(String userName){
        String suffix = settingService.getSettingValueByName("USERNAME_SUFFIX");
        if(!userName.endsWith(suffix) && !userName.contains("@")){
            userName = userName + suffix;
        }
        else if(userName.contains("@") && !userName.endsWith(suffix)){
            userName = "Invalid";
        }
        else if(userName.endsWith(suffix) && userName.indexOf('@') == 0){
            userName = "Invalid";
        }
        return userName;
    }



    @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_SINGLE_REPORT_PATH, FRA_USER_MANAGEMENT_SINGLE_REPORT_PATH}, method = RequestMethod.GET)
    public String searchSingleUserReport(@RequestParam String userID, ModelMap model,Locale locale)
    {

        List<CommonAuditDto> auditReport = null;
        if (!StringUtils.isBlank(userID)) {

            auditReport = userAuditService.getUserAuditReport(Optional.of(userID),messageSource,locale);
            if(CollectionUtils.isEmpty(auditReport)) {
                auditReport = new ArrayList<CommonAuditDto>(1);
            }
        }else {
            auditReport = new ArrayList<CommonAuditDto>(1);

        }
        model.addAttribute("userAuditReportList", auditReport);
        model.addAttribute("userID", userID);
        return "audit/singleUserAuditReport";
    }


    @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_ALL_REPORT_PATH, FRA_USER_MANAGEMENT_ALL_REPORT_PATH}, method = RequestMethod.GET)
    public String getAllUserReport(Model model, Locale locale)
    {
        List<CommonAuditDto> auditReport_all = userAuditService.getAuditReportsForAllUsers(messageSource,locale);;
        if(CollectionUtils.isEmpty(auditReport_all)) {
            auditReport_all = new ArrayList<CommonAuditDto>(1);
        }
        model.addAttribute("allUserAuditReport", auditReport_all);
        return "audit/allUserAuditReport";
    }


    @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH,FRA_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH}, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> allUsersAuditReport(Locale locale) {

        List<CommonAuditDto> auditInfo =   userAuditService.getAuditReportsForAllUsers(messageSource,locale);;

        ByteArrayInputStream bis = GeneratePdfReport.allUsersAuditReport(auditInfo,messageSource,locale);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=allUserAuditingReport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

   @PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
    @RequestMapping(value = {ENG_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH,FRA_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH}, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> singleUserAuditReport(@RequestParam String userID,Locale locale) {

        List<CommonAuditDto> auditInfo =   userAuditService.getUserAuditReport(Optional.of(userID),messageSource,locale);;

        ByteArrayInputStream bis = GeneratePdfReport.userAuditReport(userID,auditInfo,messageSource,locale);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=userAuditingReport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
}
