package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.CellDefinition;
import ca.gc.dfo.psffs.forms.CellDefinitionForm;
import ca.gc.dfo.psffs.json.CDList;
import ca.gc.dfo.psffs.json.MigrateStatus;
import ca.gc.dfo.psffs.json.SyncStatus;
import ca.gc.dfo.psffs.services.CellDefinitionService;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Controller
public class CellDefinitionsController implements MessageSourceAware
{
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static final String CELL_DEFINITIONS_LIST_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/list";
    public static final String CELL_DEFINITIONS_DATA_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/data";
    public static final String CELL_DEFINITIONS_CREATE_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/create";
    public static final String CELL_DEFINITIONS_EDIT_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/edit";
    public static final String CELL_DEFINITIONS_REMOVE_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/remove";
    public static final String CELL_DEFINITIONS_MIGRATE_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/migrate";
    public static final String CELL_DEFINITIONS_ACTIVE_PATH = TemplateController.CELL_DEFINITIONS_PATH + "/active";

    public static final String ENG_CELL_DEFINITIONS_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_LIST_PATH;
    public static final String FRA_CELL_DEFINITIONS_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_LIST_PATH;

    public static final String ENG_CELL_DEFINITIONS_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_DATA_PATH;
    public static final String FRA_CELL_DEFINITIONS_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_DATA_PATH;

    public static final String ENG_CELL_DEFINITIONS_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_CREATE_PATH;
    public static final String FRA_CELL_DEFINITIONS_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_CREATE_PATH;

    public static final String ENG_CELL_DEFINITIONS_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_EDIT_PATH;
    public static final String FRA_CELL_DEFINITIONS_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_EDIT_PATH;

    public static final String ENG_CELL_DEFINITIONS_REMOVE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_REMOVE_PATH;
    public static final String FRA_CELL_DEFINITIONS_REMOVE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_REMOVE_PATH;

    public static final String ENG_CELL_DEFINITIONS_MIGRATE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_MIGRATE_PATH;
    public static final String FRA_CELL_DEFINITIONS_MIGRATE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_MIGRATE_PATH;

    public static final String ENG_CELL_DEFINITIONS_ACTIVE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            CELL_DEFINITIONS_ACTIVE_PATH;
    public static final String FRA_CELL_DEFINITIONS_ACTIVE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            CELL_DEFINITIONS_ACTIVE_PATH;

    @Autowired
    private CellDefinitionService cellDefinitionService;
    @Autowired
    private LookupService lookupService;

    @PreAuthorize(SecurityHelper.EL_VIEW_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_LIST_PATH, FRA_CELL_DEFINITIONS_LIST_PATH}, method = RequestMethod.GET)
    public String cellDefinitionList()
    {
        return "cellDefinition/list";
    }

    @RequestMapping(value = {ENG_CELL_DEFINITIONS_MIGRATE_PATH, FRA_CELL_DEFINITIONS_MIGRATE_PATH}, method = RequestMethod.GET)
    public @ResponseBody MigrateStatus cellDefinitionMigrate(Long cdId, Integer year)
    {
        MigrateStatus status = new MigrateStatus();
        status.setStatus(MigrateStatus.Status.FAIL);

        try {
            status = cellDefinitionService.migrateCellDefinition(cdId, year);
        } catch (AccessDeniedException ade) {
            status.setErrorKey("js.error.insufficient_privileges");
        } catch (Exception ex) {
            status.setErrorKey("js.error.serverDB_operation_failed");
            logger.error("An error occurred while trying to migrate a cell definition: " + ex.getMessage(), ex);
        }

        return status;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_DATA_PATH, FRA_CELL_DEFINITIONS_DATA_PATH}, method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody CDList celldefinitionData(Locale locale)
    {
        CDList cdList = cellDefinitionService.getCDList();
        List<Lookup> speciesList = lookupService.getLookupsByKey("species");

        for (CellDefinitionForm celldef:
                cdList.getData()) {
            for (Lookup species:
                    speciesList) {
                if(species.getValue().equals(celldef.getSpeciesId().toString())) {
                    celldef.setSpeciesName(species.getText());
                }
            }
            celldef.setStatusStr(locale.getLanguage().toLowerCase().equals("en") ? celldef.getStatusId().getEnglishDescription() : celldef.getStatusId().getFrenchDescription());
        }
        return cdList;
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_CREATE_PATH, FRA_CELL_DEFINITIONS_CREATE_PATH}, method = RequestMethod.GET)
    public String cellDefinitionCreate(ModelMap model)
    {
        model.addAttribute("cellDefinitionForm", new CellDefinitionForm());
        model.addAttribute("years", cellDefinitionService.getActiveYears(-1));
        return "cellDefinition/create";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_CREATE_PATH, FRA_CELL_DEFINITIONS_CREATE_PATH}, method = RequestMethod.POST)
    public String cellDefinitionCreate(@Valid CellDefinitionForm cellDefinitionForm, BindingResult bindingResult, ModelMap model, Locale locale, RedirectAttributes redirectAttributes)
    {
        int year = cellDefinitionForm.getYear();
        int speciesId = cellDefinitionForm.getSpeciesId();
        String species = "";

        CellDefinition cellDefinition = cellDefinitionService.getCellDefinitionByYearAndSpeciesId(year, speciesId);
        if(cellDefinition != null){

            species = cellDefinitionService.getSpeciesForCellDef(speciesId);

            model.addAttribute("uniqueCellDef", messageSource.getMessage("js.error.cd_already_exists", new Object[] {species, String.valueOf(year) }, locale));
        }

        if(!bindingResult.hasErrors() && cellDefinition == null){
            cellDefinition = cellDefinitionService.addCellDefinitionForm(cellDefinitionForm);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("js.success.cd.created",new Object[] { cellDefinition.getId() }, locale));
            String pageLang =locale.getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
            return "redirect:/" + pageLang + CELL_DEFINITIONS_LIST_PATH;
        }

        model.addAttribute("years", cellDefinitionService.getActiveYears(cellDefinitionForm.getYear()));
        return "cellDefinition/create";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_EDIT_PATH, FRA_CELL_DEFINITIONS_EDIT_PATH}, method = RequestMethod.GET)
    public String cellDefinitionEdit(@RequestParam Long id, ModelMap model, Locale locale)
    {
        CellDefinitionForm cellDefinitionForm = cellDefinitionService.getCellDefinitionFormById(id);
        cellDefinitionForm.setStatusStr(locale.getLanguage().toLowerCase().equals("en") ? cellDefinitionForm.getStatusId().getEnglishDescription() : cellDefinitionForm.getStatusId().getFrenchDescription());
        if(cellDefinitionForm.getStatusId().getId() == 29){
            model.addAttribute("makeFormReadOnly", true);
            model.addAttribute("readOnlyMessage", messageSource.getMessage("page.cell_definition.readonly_message.historic", null, locale));
        }

        if(cellDefinitionService.containsCells(cellDefinitionForm.getId())){
            model.addAttribute("makeFormReadOnly", true);
            model.addAttribute("readOnlyMessage", messageSource.getMessage("page.cell_definition.readonly_message.cell", null, locale));
        }

        if(cellDefinitionForm.getStatusId().getId() == 27 && cellDefinitionForm.getYear() == Calendar.getInstance().get(Calendar.YEAR)){
            model.addAttribute("markActive", true);
            model.addAttribute("editId", cellDefinitionForm.getId());
        }

        model.addAttribute("cellDefinitionForm", cellDefinitionForm);
        model.addAttribute("years", cellDefinitionService.getActiveYears(cellDefinitionForm.getYear()));
        return "cellDefinition/edit";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_EDIT_PATH, FRA_CELL_DEFINITIONS_EDIT_PATH}, method = RequestMethod.POST)
    public String cellDefinitionEdit(@Valid CellDefinitionForm cellDefinitionForm, BindingResult bindingResult, ModelMap model, Locale locale, RedirectAttributes redirectAttributes)
    {
        int year = cellDefinitionForm.getYear();
        int speciesId = cellDefinitionForm.getSpeciesId();
        String species = "";

        CellDefinition cellDefinition = cellDefinitionService.getCellDefinitionByYearAndSpeciesId(year, speciesId);
        if(!bindingResult.hasErrors() && (cellDefinition == null || cellDefinition.getId().equals(cellDefinitionForm.getId()))){
            cellDefinitionService.editCellDefinitionForm(cellDefinitionForm);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("js.success.cd.updated", new Object[]{cellDefinitionForm.getId()}, locale));
            String pageLang = locale.getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
            return "redirect:/" + pageLang + CELL_DEFINITIONS_LIST_PATH;
        }
        if(cellDefinition != null && !cellDefinition.getId().equals(cellDefinitionForm.getId())){

            species = cellDefinitionService.getSpeciesForCellDef(speciesId);

            model.addAttribute("uniqueCellDef", messageSource.getMessage("js.error.cd_already_exists", new Object[] {species, String.valueOf(year) }, locale));
        }

        model.addAttribute("years", cellDefinitionService.getActiveYears(year));

        return "cellDefinition/edit";
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_CELL_DEFINITIONS)
    @RequestMapping(value = {ENG_CELL_DEFINITIONS_REMOVE_PATH, FRA_CELL_DEFINITIONS_REMOVE_PATH}, method = RequestMethod.GET)
    public String cellDefinitionRemove(Long id, ModelMap model, Locale locale, RedirectAttributes redirectAttributes) {
        if(cellDefinitionService.containsCells(id)){
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("js.error.cd.remove", new Object[]{id}, locale));
        }
        else {
            cellDefinitionService.removeCellDefinition(id);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("js.success.cd.removed", new Object[]{id}, locale));
        }

        String pageLang = locale.getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        return "redirect:/" + pageLang + CELL_DEFINITIONS_LIST_PATH;
    }

    @RequestMapping(value = {ENG_CELL_DEFINITIONS_ACTIVE_PATH, FRA_CELL_DEFINITIONS_ACTIVE_PATH},
            method = RequestMethod.PATCH, produces = "text/plain")
    public @ResponseBody String cellDefinitionActivate(Long id)
    {
        String retVal;
        try {
            retVal = cellDefinitionService.activateCellDefinition(id);
        } catch (AccessDeniedException ex) {
            retVal = "ERROR";
            logger.debug("Failed to activate cell definition: access denied");
        } catch (Exception ex) {
            retVal = "ERROR";
            logger.error("An error occurred while trying to activate a cell definition: " + ex.getMessage(), ex);
        }
        return retVal;
    }

    private static final Logger logger = LoggerFactory.getLogger(CellDefinitionsController.class);
}
