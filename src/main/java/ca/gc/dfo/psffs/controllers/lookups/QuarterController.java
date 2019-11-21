package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.controllers.TemplateController;
import ca.gc.dfo.psffs.domain.objects.lookups.CatchLocation;
import ca.gc.dfo.psffs.domain.objects.lookups.Quarter;
import ca.gc.dfo.psffs.domain.repositories.lookups.CatchLocationRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.QuarterRepository;
import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import ca.gc.dfo.psffs.forms.lookups.GroupedQuarterForm;
import ca.gc.dfo.psffs.forms.lookups.QuarterForm;
import ca.gc.dfo.psffs.json.LookupList;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class QuarterController extends BaseLookupController implements MessageSourceAware
{
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static final String QUARTER_PATH = TemplateController.SPECIAL_TABLES_PATH + "/quarters";
    public static final String QUARTER_LIST_PATH = QUARTER_PATH + "/list";
    public static final String QUARTER_LIST_DATA_PATH = QUARTER_LIST_PATH + "-data";
    public static final String QUARTER_REPLACE_PATH = QUARTER_PATH + "/replace";

    public static final String ENG_QUARTER_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUARTER_LIST_PATH;
    public static final String FRA_QUARTER_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUARTER_LIST_PATH;
    public static final String ENG_QUARTER_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUARTER_LIST_DATA_PATH;
    public static final String FRA_QUARTER_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUARTER_LIST_DATA_PATH;
    public static final String ENG_QUARTER_REPLACE_PATH = PathLocaleChangeInterceptor.ENG_PATH + QUARTER_REPLACE_PATH;
    public static final String FRA_QUARTER_REPLACE_PATH = PathLocaleChangeInterceptor.FRA_PATH + QUARTER_REPLACE_PATH;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_QUARTER_LIST_PATH, FRA_QUARTER_LIST_PATH}, method = RequestMethod.GET)
    public String getQuarterList(ModelMap model)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        model.addAttribute("listDataTHPath", "/"+pageLang+ QUARTER_LIST_DATA_PATH);
        return "lookups/quarter/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_QUARTER_REPLACE_PATH, FRA_QUARTER_REPLACE_PATH}, method = RequestMethod.GET)
    public String getQuarterReplace(ModelMap model, Locale locale)
    {
        GroupedQuarterForm form = new GroupedQuarterForm();
        form.setPeriodNumber1(1);
        form.setPeriodNumber2(2);
        form.setPeriodNumber3(3);
        form.setPeriodNumber4(4);
        if(!model.containsAttribute("groupedQuarterForm")) {
            model.addAttribute("groupedQuarterForm", form);
        }
        return "lookups/quarter/replace";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @RequestMapping(value = {ENG_QUARTER_LIST_DATA_PATH, FRA_QUARTER_LIST_DATA_PATH},
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LookupList getQuarterData(Locale locale)
    {
        return getAllQuarterForms(locale);
    }

    //Private utility methods
    private LookupList getAllQuarterForms(Locale locale)
    {
        List<BaseLookupForm> baseLookupForms = new ArrayList<>();
        List<Quarter> quarters = getLookupService().getAllLookups(Quarter.class,
                getLookupService().getQuarterRepository());
        BaseLookupForm form;
        for (Quarter v : quarters) {
            if(v.getActiveFlag() == 1) {
                form = new QuarterForm();
                v.setCatchLocationString(getCatchLocationStringById(v.getCatchLocationId(), locale));
                BeanUtils.copyProperties(v, form);
                baseLookupForms.add(form);
            }
        }
        return new LookupList(baseLookupForms);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @RequestMapping(value = {ENG_QUARTER_REPLACE_PATH, FRA_QUARTER_REPLACE_PATH}, method = RequestMethod.POST)
    public String postQuarterSave(@ModelAttribute("groupedQuarterForm") GroupedQuarterForm form,
                                   RedirectAttributes redirectAttributes, HttpServletRequest request, Locale locale)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        String returnView = "redirect:/"+pageLang;
        try {
            String validateMessage = validateGroupedQuarterForm(form, locale);
            if(validateMessage.equals("")) {
                saveQuarter(form);
                returnView += QUARTER_LIST_PATH;
                redirectAttributes.addFlashAttribute("successMessageKey", "success.code_tables.modified");
                // Re-init AppCache session UUID so the user will re-download the entire site including this change.
                getCacheService().initCacheSessionUUID(request, false);
                getChecksumService().updateChecksumForObjectByEntityClass(Quarter.class);
            }
            else {
                returnView += QUARTER_REPLACE_PATH;
                redirectAttributes.addFlashAttribute("errorMessage", validateMessage);
                redirectAttributes.addFlashAttribute("groupedQuarterForm", form);
                redirectAttributes.addFlashAttribute("stopJSDisable", "true");
            }
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save the quarters lookups: " + ex.getMessage(), ex);
            returnView += QUARTER_REPLACE_PATH;
            redirectAttributes.addFlashAttribute("groupedQuarterForm", form);
            redirectAttributes.addFlashAttribute("errorMessageKey", "js.error.serverDB_operation_failed");
        }
        return returnView;
    }

    //return an empty string if valid, otherwise returns the error message
    private String validateGroupedQuarterForm(GroupedQuarterForm form, Locale locale){
        String isValid = "";
        int toMonth1 = form.getPeriodToMonth1();
        int fromMonth1 = form.getPeriodFromMonth1();
        int toMonth2 = form.getPeriodToMonth2();
        int fromMonth2 = form.getPeriodFromMonth2();
        int toMonth3 = form.getPeriodToMonth3();
        int fromMonth3 = form.getPeriodFromMonth3();
        int toMonth4 = form.getPeriodToMonth4();
        int fromMonth4 = form.getPeriodFromMonth4();
        boolean[] monthsUsed = new boolean[12];

        if(fromMonth1 != -1 && toMonth1 != -1) {
            if(fromMonth1 <= toMonth1) {
                for (int x = fromMonth1 - 1; x < toMonth1; x++) {
                    monthsUsed[x] = true;
                }
            }
            else {
                for (int x = fromMonth1 - 1; x < 12; x++) {
                    monthsUsed[x] = true;
                }

                for (int x = 0; x < toMonth1; x++) {
                    monthsUsed[x] = true;
                }
            }
        }
        if(fromMonth2 != -1 && toMonth2 != -1) {
            boolean q2Complete = false;
            int y = fromMonth2 - 1;
            while(!q2Complete){
                if(y == toMonth2 - 1) {
                    q2Complete = true;
                }
                if (monthsUsed[y]) {
                    if(isValid.equals("")) {
                        isValid = messageSource.getMessage("js.error.quarter.overlap", new Object[] {"second"}, locale);
                    }
                }
                monthsUsed[y] = true;
                if(y == 11){
                    y = 0;
                }
                else {
                    y++;
                }
            }
        }
        if(fromMonth3 != -1 && toMonth3 != -1) {
            boolean q3Complete = false;
            int z = fromMonth3 - 1;
            while(!q3Complete){
                if(z == toMonth3 - 1) {
                    q3Complete = true;
                }
                if (monthsUsed[z]) {
                    if(isValid.equals("")) {
                        isValid = messageSource.getMessage("js.error.quarter.overlap", new Object[] {"third"}, locale);
                    }
                }
                monthsUsed[z] = true;
                if(z == 11){
                    z = 0;
                }
                else {
                    z++;
                }
            }
        }
        if(fromMonth4 != -1 && toMonth4 != -1) {
            boolean q4Complete = false;
            int k = fromMonth4 - 1;
            while(!q4Complete){
                if(k == toMonth4 - 1){
                    q4Complete = true;
                }
                if (monthsUsed[k]) {
                    if(isValid.equals("")) {
                        isValid = messageSource.getMessage("js.error.quarter.overlap", new Object[] {"fourth"}, locale);
                    }
                }
                monthsUsed[k] = true;
                if(k == 11){
                    k = 0;
                }
                else {
                    k++;
                }
            }
        }

        DateFormatSymbols french_dfs = new DateFormatSymbols(Locale.FRENCH);
        String[] fra_mon = french_dfs.getMonths();

        DateFormatSymbols english_dfs = new DateFormatSymbols();
        String[] eng_mon = english_dfs.getMonths();

        String monthsForErrFra = "";
        String monthsForErrEng = "";
        if(isValid.equals("")) {
            for(int j = 0; j < monthsUsed.length; j++){
                if(!monthsUsed[j]){
                    monthsForErrFra += fra_mon[j] + ", ";
                    monthsForErrEng += eng_mon[j] + ", ";
                    isValid = "0";
                }
            }
        }

        if(isValid.equals("0")){
            if(locale.getLanguage().equals("en")){
                isValid = messageSource.getMessage("js.error.quarter.all_months", new Object[] {monthsForErrEng.substring(0, monthsForErrEng.length() - 2)}, locale);
            }
            else {
                isValid = messageSource.getMessage("js.error.quarter.all_months", new Object[] {monthsForErrFra.substring(0, monthsForErrFra.length() - 2)}, locale);
            }

        }

        return isValid;
    }

    private void saveQuarter(GroupedQuarterForm form) throws InstantiationException, IllegalAccessException
    {
        Quarter quarterOne = new Quarter();
        Quarter quarterTwo = new Quarter();
        Quarter quarterThree = new Quarter();
        Quarter quarterFour = new Quarter();
        QuarterRepository quarterRepository = getLookupService().getQuarterRepository();
        List<Quarter> quarters = quarterRepository.findAllByCatchLocationIdEqualsOrderByOrderAsc(form.getCatchLocationId());

        for (Quarter quarter:
             quarters) {
            quarter.setActiveFlag(0);
            quarter.setModifiedDate(LocalDate.now());
            quarter.setActor(SecurityHelper.getNtPrincipal());
            quarterRepository.save(quarter);
        }

        quarterOne.setId(null);
        quarterOne.setPeriodFromMonth(form.getPeriodFromMonth1());
        quarterOne.setPeriodToMonth(form.getPeriodToMonth1());
        quarterOne.setPeriodNumber(form.getPeriodNumber1());
        quarterOne.setCatchLocationId(form.getCatchLocationId());
        quarterOne.setActiveFlag(1);
        quarterOne.setOrder(1);
        setQuarterDescriptions(quarterOne);

        quarterOne.setActor(SecurityHelper.getNtPrincipal());
        quarterRepository.save(quarterOne);

        quarterTwo.setPeriodFromMonth(form.getPeriodFromMonth2());
        quarterTwo.setPeriodToMonth(form.getPeriodToMonth2());
        quarterTwo.setPeriodNumber(form.getPeriodNumber2());
        quarterTwo.setCatchLocationId(form.getCatchLocationId());
        quarterTwo.setActiveFlag(1);
        quarterTwo.setOrder(2);
        setQuarterDescriptions(quarterTwo);

        quarterTwo.setActor(SecurityHelper.getNtPrincipal());
        quarterRepository.save(quarterTwo);

        quarterThree.setId(null);
        quarterThree.setPeriodFromMonth(form.getPeriodFromMonth3());
        quarterThree.setPeriodToMonth(form.getPeriodToMonth3());
        quarterThree.setPeriodNumber(form.getPeriodNumber3());
        quarterThree.setCatchLocationId(form.getCatchLocationId());
        quarterThree.setActiveFlag(1);
        quarterThree.setOrder(3);
        setQuarterDescriptions(quarterThree);

        quarterThree.setActor(SecurityHelper.getNtPrincipal());
        quarterRepository.save(quarterThree);

        quarterFour.setId(null);
        quarterFour.setPeriodFromMonth(form.getPeriodFromMonth4());
        quarterFour.setPeriodToMonth(form.getPeriodToMonth4());
        quarterFour.setPeriodNumber(form.getPeriodNumber4());
        quarterFour.setCatchLocationId(form.getCatchLocationId());
        quarterFour.setActiveFlag(1);
        quarterFour.setOrder(4);
        setQuarterDescriptions(quarterFour);

        quarterFour.setActor(SecurityHelper.getNtPrincipal());
        quarterRepository.save(quarterFour);
    }

    private String getCatchLocationStringById(int catchLocationId, Locale locale){
            CatchLocationRepository catchLocationRepository = getLookupService().getCatchLocationRepository();
            CatchLocation cl = catchLocationRepository.getOne(catchLocationId);
            if(locale.getLanguage().equals("en")) {
                return cl.getEnglishDescription();
            }
            else {
                return cl.getFrenchDescription();
            }
    }

    private void setQuarterDescriptions(Quarter quarter){

        DateFormatSymbols french_dfs = new DateFormatSymbols(Locale.FRENCH);
        String[] fr_months = french_dfs.getShortMonths();

        DateFormatSymbols english_dfs = new DateFormatSymbols();
        String[] eng_months = english_dfs.getShortMonths();

        quarter.setEnglishDescription((eng_months[quarter.getPeriodFromMonth()-1] + "-" + eng_months[quarter.getPeriodToMonth()-1]).replace(".",""));
        quarter.setFrenchDescription((fr_months[quarter.getPeriodFromMonth()-1] + "-" + fr_months[quarter.getPeriodToMonth()-1]).replace(".",""));
    }

    private static final Logger logger = LoggerFactory.getLogger(QuarterController.class);
}
