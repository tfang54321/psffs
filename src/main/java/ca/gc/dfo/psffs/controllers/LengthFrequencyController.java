package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.LengthFrequency;
import ca.gc.dfo.psffs.domain.objects.lookups.SamplingStatus;
import ca.gc.dfo.psffs.exceptions.CellDefinitionNotFoundException;
import ca.gc.dfo.psffs.forms.LengthFrequencyForm;
import ca.gc.dfo.psffs.json.LFList;
import ca.gc.dfo.psffs.json.SyncStatus;
import ca.gc.dfo.psffs.services.LengthFrequencyService;
import ca.gc.dfo.spring_commons.commons_offline_wet.annotations.Offline;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class LengthFrequencyController
{
    public static final String LENGTH_FREQUENCY_LIST_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/list";
    public static final String LENGTH_FREQUENCY_CREATE_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/create";
    public static final String LENGTH_FREQUENCY_EDIT_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/edit";
    public static final String LENGTH_FREQUENCY_LIST_DATA_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/list-data";
    public static final String LENGTH_FREQUENCY_SYNC_DATA_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/sync-data";
    public static final String LENGTH_FREQUENCY_GET_DATA_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/get-data";
    public static final String LENGTH_FREQUENCY_DELETE_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/delete";
    public static final String LENGTH_FREQUENCY_MARK_PATH = TemplateController.LENGTH_FREQUENCY_PATH + "/mark";

    public static final String ENG_LENGTH_FREQUENCY_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            LENGTH_FREQUENCY_LIST_PATH;
    public static final String FRA_LENGTH_FREQUENCY_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            LENGTH_FREQUENCY_LIST_PATH;
    public static final String ENG_LENGTH_FREQUENCY_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            LENGTH_FREQUENCY_CREATE_PATH;
    public static final String FRA_LENGTH_FREQUENCY_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            LENGTH_FREQUENCY_CREATE_PATH;
    public static final String ENG_LENGTH_FREQUENCY_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            LENGTH_FREQUENCY_EDIT_PATH;
    public static final String FRA_LENGTH_FREQUENCY_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            LENGTH_FREQUENCY_EDIT_PATH;
    public static final String ENG_LENGTH_FREQUENCY_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            LENGTH_FREQUENCY_LIST_DATA_PATH;
    public static final String FRA_LENGTH_FREQUENCY_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            LENGTH_FREQUENCY_LIST_DATA_PATH;
    public static final String ENG_LENGTH_FREQUENCY_MARK_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            LENGTH_FREQUENCY_MARK_PATH;
    public static final String FRA_LENGTH_FREQUENCY_MARK_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            LENGTH_FREQUENCY_MARK_PATH;

    @Autowired
    private LengthFrequencyService lengthFrequencyService;

    @Offline
    @RequestMapping(value = {ENG_LENGTH_FREQUENCY_LIST_PATH, FRA_LENGTH_FREQUENCY_LIST_PATH}, method = RequestMethod.GET)
    public String lengthFrequencyList()
    {
        return "lengthFrequency/list";
    }

    @Offline
    @RequestMapping(value = {ENG_LENGTH_FREQUENCY_CREATE_PATH, FRA_LENGTH_FREQUENCY_CREATE_PATH},
            method = RequestMethod.GET)
    public String lengthFrequencyCreate(ModelMap model)
    {
        model.addAttribute("lengthFrequencyForm", new LengthFrequencyForm());
        return "lengthFrequency/create";
    }

    @Offline
    @RequestMapping(value = {ENG_LENGTH_FREQUENCY_EDIT_PATH, FRA_LENGTH_FREQUENCY_EDIT_PATH},
            method = RequestMethod.GET)
    public String lengthFrequencyEdit(ModelMap model)
    {
        model.addAttribute("lengthFrequencyForm", new LengthFrequencyForm());
        return "lengthFrequency/edit";
    }

    @RequestMapping(value = {ENG_LENGTH_FREQUENCY_LIST_DATA_PATH, FRA_LENGTH_FREQUENCY_LIST_DATA_PATH},
                    method = RequestMethod.GET, params = "year", produces = "application/json")
    public @ResponseBody LFList lengthFrequencyListData(@RequestParam("year") Integer year)
    {
        LFList lfList;
        try {
            lfList = lengthFrequencyService.getListByYear(year);
        } catch (Exception ex) {
            lfList = new LFList();
            lfList.setLengthFrequencies(new ArrayList<>());
            if (!(ex instanceof AccessDeniedException)) {
                logger.error("An error occurred while trying to get length frequency list data: " + ex.getMessage(), ex);
            }
        }
        return lfList;
    }

    @RequestMapping(value = LENGTH_FREQUENCY_SYNC_DATA_PATH, method = RequestMethod.POST,
                    produces = "application/json", consumes = "application/json")
    public @ResponseBody SyncStatus lengthFrequencySync(@RequestBody LengthFrequencyForm lf)
    {
        SyncStatus syncStatus = new SyncStatus();
        try {
            LengthFrequency lengthFrequency = lengthFrequencyService.syncLF(lf);
            syncStatus.setStatus(SyncStatus.Status.SUCCESS);
            syncStatus.setId(lengthFrequency.getSampling().getSamplingCode());
        } catch (AccessDeniedException ade) {
            syncStatus.setStatus(SyncStatus.Status.FAIL);
            syncStatus.setErrorKey("js.error.insufficient_privileges");
            logger.debug("Failed to sync LF: access denied");
        } catch (Exception ex) {
            syncStatus.setStatus(SyncStatus.Status.FAIL);
            if (ex instanceof CellDefinitionNotFoundException) {
                syncStatus.setErrorKey("js.error.cell_definition_not_found");
            }
            logger.error("An error occurred while attempting to sync LF ["+ lf.getLfId() + "]: " + ex.getMessage(), ex);
        }
        return syncStatus;
    }

    @RequestMapping(value = LENGTH_FREQUENCY_GET_DATA_PATH, method = RequestMethod.GET, params = "lfId",
                    produces = "application/json")
    public @ResponseBody LengthFrequencyForm getLengthFrequencyData(@RequestParam("lfId") String lfId)
    {
        LengthFrequencyForm form;
        try {
            form = lengthFrequencyService.getAndFormConvertLF(lfId);
        } catch (AccessDeniedException ade) {
            form = null;
        }
        return form;
    }

    @RequestMapping(value = LENGTH_FREQUENCY_DELETE_PATH, method = RequestMethod.DELETE, produces = "text/plain", consumes = "text/plain")
    public @ResponseBody String deleteLengthFrequency(@RequestBody String lfId)
    {
        String status;
        boolean success;
        try {
            success = lengthFrequencyService.delete(lfId);
        } catch (AccessDeniedException ade) {
            success = false;
            logger.debug("Failed to delete LF: access denied");
        } catch (Exception ex) {
            success = false;
            logger.error("An error occurred while trying to delete an LF: " + ex.getMessage(), ex);
        }
        if (success) status = "success";
        else status = "error";
        return status;
    }

    @RequestMapping(value = {ENG_LENGTH_FREQUENCY_MARK_PATH, FRA_LENGTH_FREQUENCY_MARK_PATH}, method = RequestMethod.PATCH,
            produces = "text/plain", consumes  = "text/plain")
    public @ResponseBody String markLengthFrequency(@RequestBody String lfId)
    {
        String result = "";
        try {
             result = lengthFrequencyService.markLFForArchive(lfId);
        }
        catch(AccessDeniedException ade) {
            result = "error";
            logger.info("Failed to Mark LF for archive: Access Denied [" + lfId + "]");
        }

        return result;
    }

    private static final Logger logger = LoggerFactory.getLogger(LengthFrequencyController.class);
}
