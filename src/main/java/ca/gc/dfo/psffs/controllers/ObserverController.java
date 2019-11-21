package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.ObserverSetting;
import ca.gc.dfo.psffs.domain.objects.TripSetSpecies;
import ca.gc.dfo.psffs.exceptions.CellDefinitionNotFoundException;
import ca.gc.dfo.psffs.forms.TripSetSpeciesForm;
import ca.gc.dfo.psffs.json.SyncStatus;
import ca.gc.dfo.psffs.json.TSSList;
import ca.gc.dfo.psffs.services.ObserverSettingService;
import ca.gc.dfo.psffs.services.TripSetSpeciesService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
public class ObserverController
{
    public static final String OBSERVER_LIST_PATH = TemplateController.OBSERVER_PATH + "/list";
    public static final String OBSERVER_CREATE_PATH = TemplateController.OBSERVER_PATH + "/create";
    public static final String OBSERVER_LIST_DATA_PATH = OBSERVER_LIST_PATH + "/data";
    public static final String OBSERVER_EDIT_PATH = TemplateController.OBSERVER_PATH + "/edit";
    public static final String OBSERVER_EDIT_DATA_PATH = TemplateController.OBSERVER_PATH + "/form";
    public static final String OBSERVER_DELETE_PATH = TemplateController.OBSERVER_PATH + "/delete";
    public static final String OBSERVER_MARK_PATH = TemplateController.OBSERVER_PATH + "/mark";

    public static final String ENG_OBSERVER_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_LIST_PATH;
    public static final String FRA_OBSERVER_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_LIST_PATH;
    public static final String ENG_OBSERVER_LIST_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_LIST_DATA_PATH;
    public static final String FRA_OBSERVER_LIST_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_LIST_DATA_PATH;
    public static final String ENG_OBSERVER_CREATE_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_CREATE_PATH;
    public static final String FRA_OBSERVER_CREATE_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_CREATE_PATH;
    public static final String ENG_OBSERVER_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_EDIT_PATH;
    public static final String FRA_OBSERVER_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_EDIT_PATH;
    public static final String ENG_OBSERVER_MARK_PATH = PathLocaleChangeInterceptor.ENG_PATH +
            OBSERVER_MARK_PATH;
    public static final String FRA_OBSERVER_MARK_PATH = PathLocaleChangeInterceptor.FRA_PATH +
            OBSERVER_MARK_PATH;

    @Autowired
    private TripSetSpeciesService tripSetSpeciesService;

    @Autowired
    private ObserverSettingService observerSettingService;

    @PreAuthorize(SecurityHelper.EL_VIEW_TRIP_SET_SPECIES)
    @RequestMapping(value = {ENG_OBSERVER_LIST_PATH, FRA_OBSERVER_LIST_PATH}, method = RequestMethod.GET)
    public String observerSamplingList()
    {
        return "observer/list";
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TRIP_SET_SPECIES)
    @RequestMapping(value = {ENG_OBSERVER_CREATE_PATH, FRA_OBSERVER_CREATE_PATH},
            method = RequestMethod.GET)
    public String observerSamplingCreate(ModelMap model)
    {
        TripSetSpeciesForm form = new TripSetSpeciesForm();
        model.addAttribute("tripSetSpeciesForm", form);
        return "observer/create";
    }

    @RequestMapping(value = OBSERVER_CREATE_PATH, method = RequestMethod.POST, params = "save",
                    produces = "application/json", consumes = "application/json")
    public @ResponseBody SyncStatus observerSamplingSave(@RequestBody TripSetSpeciesForm form)
    {
        SyncStatus status = new SyncStatus();
        try {
            //If record is new
            if (form.getLengthGroupMin() == null && form.getLengthGroupMax() == null) {
                ObserverSetting settings = observerSettingService.getSettingByYearAndSpecies(form.getCatchDate().getYear(),
                        form.getSampledSpeciesId());
                if (settings != null) {
                    form.setLengthGroupMin(settings.getLengthGroupMin());
                    form.setLengthGroupMax(settings.getLengthGroupMax());
                    form.setLengthUnitId(settings.getLengthUnitId());
                    form.setLengthGroupId(settings.getLengthGroupId());
                } else {
                    form.setLengthGroupMin(0);
                    form.setLengthGroupMax(150);
                    form.setLengthUnitId(14);
                    form.setLengthGroupId(4);
                }
            }

            TripSetSpecies tss = tripSetSpeciesService.saveTSS(form);
            status.setStatus(SyncStatus.Status.SUCCESS);
            status.setId(tss.getSampling().getSamplingCode());
        } catch (AccessDeniedException ade) {
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.insufficient_privileges");
            logger.debug("Failed to save trip set species: access denied");
        } catch (CellDefinitionNotFoundException cdnf) {
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.cell_definition_not_found_plain");
            logger.error("An error occurred while trying to save a trip set species: " + cdnf.getMessage());
        } catch (Exception ex) {
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.serverDB_operation_failed");
            logger.error("An error occurred while trying to save a trip set species: " + ex.getMessage(), ex);
        }
        return status;
    }

    @RequestMapping(value = {ENG_OBSERVER_LIST_DATA_PATH, FRA_OBSERVER_LIST_DATA_PATH},
            method = RequestMethod.GET, params = "year", produces = "application/json")
    public @ResponseBody TSSList observerSamplingListData(@RequestParam("year") Integer year)
    {
        TSSList tssList;

        try {
            tssList = tripSetSpeciesService.getListByYear(year);
        } catch (Exception ex) {
            tssList = new TSSList();
            tssList.setTripSetSpeciesList(new ArrayList<>());
            if (!(ex instanceof AccessDeniedException)) {
                logger.error("An error occurred while trying to get list of TSS: " + ex.getMessage(), ex);
            }
        }

        return tssList;
    }

    @RequestMapping(value = OBSERVER_DELETE_PATH, method = RequestMethod.DELETE, produces = "text/plain", consumes = "text/plain")
    public @ResponseBody String deleteObserverSampling(@RequestBody String tssId)
    {
        String status;
        boolean success;

        try {
            success = tripSetSpeciesService.deleteTSS(tssId);
        } catch (Exception ex) {
            success = false;
            if (!(ex instanceof AccessDeniedException)) {
                logger.error("An error occurred while trying to delete trip set species: " + ex.getMessage(), ex);
            } else {
                logger.debug("Failed to delete TSS: access denied");
            }
        }
        if (success) status = "success";
        else status = "error";
        return status;
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TRIP_SET_SPECIES)
    @RequestMapping(value = {ENG_OBSERVER_EDIT_PATH, FRA_OBSERVER_EDIT_PATH}, method = RequestMethod.GET)
    public String editObserverSampling(ModelMap model)
    {
        model.addAttribute("tripSetSpeciesForm", new TripSetSpeciesForm());
        return "observer/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TRIP_SET_SPECIES)
    @RequestMapping(value = OBSERVER_EDIT_DATA_PATH, method = RequestMethod.GET, params = "tssId",
            produces = "application/json")
    public @ResponseBody TripSetSpeciesForm getLengthFrequencyData(@RequestParam("tssId") String tssId)
    {
        return tripSetSpeciesService.getAndFormConvertTSS(tssId);
    }

    @RequestMapping(value = {ENG_OBSERVER_MARK_PATH, FRA_OBSERVER_MARK_PATH}, method = RequestMethod.PATCH,
            produces = "text/plain", consumes  = "text/plain")
    public @ResponseBody String markTripSetSpecies(@RequestBody String statusSpecs)
    {
        String[] specs = statusSpecs.split(",");
        return tripSetSpeciesService.markTSSForDistributedArchive(specs[0], Integer.valueOf(specs[1]));
    }

    private static final Logger logger = LoggerFactory.getLogger(ObserverController.class);
}
