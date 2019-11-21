package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.security.User;
import ca.gc.dfo.psffs.domain.objects.security.UserRole;
import ca.gc.dfo.psffs.forms.AccountForm;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.annotations.Offline;
import ca.gc.dfo.spring_commons.commons_offline_wet.controllers.AbstractTemplateController;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TemplateController extends AbstractTemplateController
{
    public static final String CELL_DEFINITIONS_PATH = "/cellDefinitions";
    public static final String ENG_CELL_DEFINITIONS_PATH = PathLocaleChangeInterceptor.ENG_PATH + CELL_DEFINITIONS_PATH;
    public static final String FRA_CELL_DEFINITIONS_PATH = PathLocaleChangeInterceptor.FRA_PATH + CELL_DEFINITIONS_PATH;

    public static final String LENGTH_FREQUENCY_PATH = "/lengthFrequency";
    public static final String ENG_LENGTH_FREQUENCY_PATH = PathLocaleChangeInterceptor.ENG_PATH + LENGTH_FREQUENCY_PATH;
    public static final String FRA_LENGTH_FREQUENCY_PATH = PathLocaleChangeInterceptor.FRA_PATH + LENGTH_FREQUENCY_PATH;

    public static final String TRIP_SETTINGS_PATH = LENGTH_FREQUENCY_PATH + "/tripSettings";
    public static final String ENG_TRIP_SETTINGS_PATH = PathLocaleChangeInterceptor.ENG_PATH + TRIP_SETTINGS_PATH;
    public static final String FRA_TRIP_SETTINGS_PATH = PathLocaleChangeInterceptor.FRA_PATH + TRIP_SETTINGS_PATH;

    public static final String OBSERVER_PATH = "/observer";
    public static final String ENG_OBSERVER_PATH = PathLocaleChangeInterceptor.ENG_PATH + OBSERVER_PATH;
    public static final String FRA_OBSERVER_PATH = PathLocaleChangeInterceptor.FRA_PATH + OBSERVER_PATH;

    public static final String SAMPLINGS_PATH = "/samplings";
    public static final String ENG_SAMPLINGS_PATH = PathLocaleChangeInterceptor.ENG_PATH + SAMPLINGS_PATH;
    public static final String FRA_SAMPLINGS_PATH = PathLocaleChangeInterceptor.FRA_PATH + SAMPLINGS_PATH;

    public static final String SETTINGS_PATH = "/settings";
    public static final String ENG_SETTINGS_PATH = PathLocaleChangeInterceptor.ENG_PATH + SETTINGS_PATH;
    public static final String FRA_SETTINGS_PATH = PathLocaleChangeInterceptor.FRA_PATH + SETTINGS_PATH;

    public static final String USER_MANAGEMENT_PATH = "/userManagement";
    public static final String ENG_USER_MANAGEMENT_PATH = PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_PATH;
    public static final String FRA_USER_MANAGEMENT_PATH = PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_PATH;

    public static final String CODE_TABLES_PATH = "/codeTables";
    public static final String ENG_CODE_TABLES_PATH = PathLocaleChangeInterceptor.ENG_PATH + CODE_TABLES_PATH;
    public static final String FRA_CODE_TABLES_PATH = PathLocaleChangeInterceptor.FRA_PATH + CODE_TABLES_PATH;

    public static final String SPECIAL_TABLES_PATH = "/specialTables";
    public static final String ENG_SPECIAL_TABLES_PATH = PathLocaleChangeInterceptor.ENG_PATH + SPECIAL_TABLES_PATH;
    public static final String FRA_SPECIAL_TABLES_PATH = PathLocaleChangeInterceptor.FRA_PATH + SPECIAL_TABLES_PATH;

    public static final String SERVER_TOOLS_PATH = "/serverTools";
    public static final String ENG_SERVER_TOOLS_PATH = PathLocaleChangeInterceptor.ENG_PATH + SERVER_TOOLS_PATH;
    public static final String FRA_SERVER_TOOLS_PATH = PathLocaleChangeInterceptor.FRA_PATH + SERVER_TOOLS_PATH;

    public static final String MYACCOUNT_PATH = "/account";
    public static final String ENG_MYACCOUNT_PATH = PathLocaleChangeInterceptor.ENG_PATH + MYACCOUNT_PATH;
    public static final String FRA_MYACCOUNT_PATH = PathLocaleChangeInterceptor.FRA_PATH + MYACCOUNT_PATH;

    public static final String HELP_PATH = "/help";
    public static final String ENG_HELP_PATH = PathLocaleChangeInterceptor.ENG_PATH + HELP_PATH;
    public static final String FRA_HELP_PATH = PathLocaleChangeInterceptor.FRA_PATH + HELP_PATH;

    @Offline
    @RequestMapping(method = RequestMethod.GET, value = {
            ENG_CELL_DEFINITIONS_PATH, FRA_CELL_DEFINITIONS_PATH,
            ENG_LENGTH_FREQUENCY_PATH, FRA_LENGTH_FREQUENCY_PATH, ENG_OBSERVER_PATH, FRA_OBSERVER_PATH,
            ENG_SAMPLINGS_PATH, FRA_SAMPLINGS_PATH, ENG_SETTINGS_PATH, FRA_SETTINGS_PATH, ENG_USER_MANAGEMENT_PATH,
            FRA_USER_MANAGEMENT_PATH, ENG_CODE_TABLES_PATH, FRA_CODE_TABLES_PATH, ENG_SPECIAL_TABLES_PATH,
            FRA_SPECIAL_TABLES_PATH, ENG_SERVER_TOOLS_PATH, FRA_SERVER_TOOLS_PATH
    })
    public String specificPanelNodes(ModelMap model, HttpServletRequest request)
    {
        return panelNodes(model, request);
    }

    @RequestMapping(value = {ENG_MYACCOUNT_PATH, FRA_MYACCOUNT_PATH}, method = RequestMethod.GET)
    public String getMyAccount(ModelMap model)
    {
        AccountForm account = new AccountForm();
        User user = (User)SecurityHelper.getUserDetails();
        account.setUsername(user.getUsername());
        account.setFullname(user.getFullname());
        account.setInitials(user.getInitials());
        account.setEmail(user.getEmail());
        String language = LocaleContextHolder.getLocale().getLanguage().toLowerCase();
        List<String> roles = user.getUserRoles().stream()
                                .map(UserRole::getRole)
                                .map(r -> language.equals("fr") ? r.getFrenchRoleTitle() : r.getEnglishRoleTitle())
                                .collect(Collectors.toList());
        account.setRoles(roles);
        model.addAttribute("account", account);
        return "account";
    }

    @RequestMapping(value = {ENG_HELP_PATH, FRA_HELP_PATH}, method = RequestMethod.GET)
    public String getHelp()
    {
        return "help";
    }
}
