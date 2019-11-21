package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.SamplingEntry;
import ca.gc.dfo.psffs.forms.DTSamplingDataAdvancedForm;
import ca.gc.dfo.psffs.json.*;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.psffs.domain.objects.SamplingData;
import ca.gc.dfo.psffs.domain.objects.security.UserPreference;
import ca.gc.dfo.psffs.forms.SamplingDataAdvancedForm;
import ca.gc.dfo.psffs.forms.SamplingDataForm;
import ca.gc.dfo.psffs.forms.SamplingDataListForm;
import ca.gc.dfo.psffs.services.SamplingDataService;
import ca.gc.dfo.psffs.services.UserService;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class SamplingDataController
{
    private static final String SAMPLING_DATA_PATH = "/samplingData";
    public static final String SAMPLING_DATA_LIST_PATH = SAMPLING_DATA_PATH + "/list";
    public static final String SAMPLING_DATA_DATA_PATH = SAMPLING_DATA_PATH + "/data";
    public static final String SAMPLING_DATA_EDIT_PATH = SAMPLING_DATA_PATH + "/edit";
    public static final String SAMPLING_DATA_FORM_PATH = SAMPLING_DATA_PATH + "/form";
    public static final String SAMPLING_DATA_PATCH_PATH = SAMPLING_DATA_PATH + "/patch";
    public static final String SAMPLING_DATA_MARK_PATH = SAMPLING_DATA_PATH + "/mark";

    public static final String ENG_SAMPLING_DATA_LIST_PATH = PathLocaleChangeInterceptor.ENG_PATH + SAMPLING_DATA_LIST_PATH;
    public static final String FRA_SAMPLING_DATA_LIST_PATH = PathLocaleChangeInterceptor.FRA_PATH + SAMPLING_DATA_LIST_PATH;
    public static final String ENG_SAMPLING_DATA_DATA_PATH = PathLocaleChangeInterceptor.ENG_PATH + SAMPLING_DATA_DATA_PATH;
    public static final String FRA_SAMPLING_DATA_DATA_PATH = PathLocaleChangeInterceptor.FRA_PATH + SAMPLING_DATA_DATA_PATH;
    public static final String ENG_SAMPLING_DATA_EDIT_PATH = PathLocaleChangeInterceptor.ENG_PATH + SAMPLING_DATA_EDIT_PATH;
    public static final String FRA_SAMPLING_DATA_EDIT_PATH = PathLocaleChangeInterceptor.FRA_PATH + SAMPLING_DATA_EDIT_PATH;
    public static final String ENG_SAMPLING_DATA_MARK_PATH = PathLocaleChangeInterceptor.ENG_PATH + SAMPLING_DATA_MARK_PATH;
    public static final String FRA_SAMPLING_DATA_MARK_PATH = PathLocaleChangeInterceptor.FRA_PATH + SAMPLING_DATA_MARK_PATH;

    @Autowired
    private SamplingDataService samplingDataService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_DATA)
    @RequestMapping(value = {ENG_SAMPLING_DATA_LIST_PATH, FRA_SAMPLING_DATA_LIST_PATH}, method = RequestMethod.GET)
    public String getSamplingDataList(ModelMap model, @RequestParam(value = "sId", required = false) String samplingCode)
    {
        UserPreference sdAdvancedPreferences = userService.getUserPreferenceByNtPrincipal(
                UserPreference.PreferenceType.SAMPLING_DATA_ADVANCED, SecurityHelper.getNtPrincipal());
        SamplingDataAdvancedForm samplingDataAdvancedForm;
        if (sdAdvancedPreferences != null) {
            try {
                samplingDataAdvancedForm = objectMapper.readValue(sdAdvancedPreferences.getPreferenceData(),
                        SamplingDataAdvancedForm.class);
            } catch (Exception ex) {
                logger.error("An error occurred while trying to parse JSON into SamplingDataAdvancedForm: " +
                        ex.getMessage(), ex);
                samplingDataAdvancedForm = new SamplingDataAdvancedForm();
            }
        } else {
            samplingDataAdvancedForm = new SamplingDataAdvancedForm();
        }

        if (samplingCode != null && samplingCode.trim().length() > 0) {
            samplingDataAdvancedForm.setSource(SamplingDataAdvancedForm.SourceType.ONE);
            samplingDataAdvancedForm.setSourceSampleId(samplingCode);
        }

        model.addAttribute("samplingDataAdvancedForm", samplingDataAdvancedForm);
        return "samplingData/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_DATA)
    @RequestMapping(value = {ENG_SAMPLING_DATA_DATA_PATH, FRA_SAMPLING_DATA_DATA_PATH}, method = RequestMethod.GET,
                    produces = "application/json")
    public @ResponseBody DTSDList getSamplingDataData(
            @ModelAttribute("samplingDataAdvancedForm") DTSamplingDataAdvancedForm advancedForm,
            HttpServletRequest request)
    {
        String searchValue = request.getParameter("search[value]");
        if (searchValue != null) {
            advancedForm.setSearchValue(searchValue);
        }
        String sEcho = request.getParameter("_");
        if (sEcho != null) {
            advancedForm.setSEcho(sEcho);
        }
        parseMultiDimensionalParameter(advancedForm.getColumnDefinitions(), "column", request);
        parseMultiDimensionalParameter(advancedForm.getOrderDefinitions(), "order", request);

        if (advancedForm.getSavePreferences()) {
            String ntPrincipal = SecurityHelper.getNtPrincipal();
            String prefData = null;
            try {
                SamplingDataAdvancedForm baseForm = new SamplingDataAdvancedForm();
                List<String> ignoreList = Arrays.stream(DTSamplingDataAdvancedForm.class.getDeclaredFields())
                                            .map(Field::getName)
                                            .collect(Collectors.toList());
                BeanUtils.copyProperties(advancedForm, baseForm, ignoreList.toArray(new String[ignoreList.size()]));
                prefData = objectMapper.writeValueAsString(baseForm);
            } catch (JsonProcessingException jpe) {
                logger.error("Error occurred while attempting to write advanced form as JSON: " + jpe.getMessage(), jpe);
            }
            if (prefData != null) {
                userService.saveUserPreferenceData(UserPreference.PreferenceType.SAMPLING_DATA_ADVANCED, ntPrincipal, prefData);
            }
        }

        return samplingDataService.fetchDataByAdvancedForm(advancedForm);
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_DATA)
    @RequestMapping(value = {ENG_SAMPLING_DATA_EDIT_PATH, FRA_SAMPLING_DATA_EDIT_PATH}, method = RequestMethod.GET,
                    params = "sdId")
    public String getSamplingDataEdit()
    {
        return "samplingData/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_SAMPLING_DATA)
    @RequestMapping(value = SAMPLING_DATA_FORM_PATH, method = RequestMethod.GET, params = "sdId",
                    produces = "application/json")
    public @ResponseBody SamplingDataForm getSamplingDataForm(@RequestParam("sdId") Long sdId)
    {
        SamplingData samplingData = samplingDataService.fetchById(sdId);
        return samplingDataService.formConvertSamplingData(samplingData, samplingData.getSamplingEntry(), false);
    }

    @RequestMapping(value = SAMPLING_DATA_FORM_PATH, method = RequestMethod.POST, produces = "application/json",
                    consumes = "application/json")
    public @ResponseBody SyncStatus saveSamplingDataForm(@RequestBody SamplingDataForm samplingDataForm)
    {
        SyncStatus status = new SyncStatus();
        try {
            samplingDataService.updateSamplingDataByForm(samplingDataForm);
            status.setId(samplingDataForm.getSamplingDataId().toString());
            status.setStatus(SyncStatus.Status.SUCCESS);
        } catch (AccessDeniedException ade) {
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.insufficient_privileges");
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save sampling data form: " + ex.getMessage(), ex);
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.serverDB_operation_failed");
        }
        return status;
    }

    @RequestMapping(value = SAMPLING_DATA_PATCH_PATH, method = RequestMethod.PATCH, produces = "application/json",
                    consumes = "application/json", params = "sdId")
    public @ResponseBody SyncStatus patchSamplingData(@RequestParam("sdId") Long sdId,
                                                      @RequestBody SamplingDataPatchRequest patchRequest)
    {
        SyncStatus status = new SyncStatus();
        try {
            samplingDataService.patchSamplingData(sdId, patchRequest);
            status.setStatus(SyncStatus.Status.SUCCESS);
            status.setId(sdId.toString());
        } catch (AccessDeniedException ade) {
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.insufficient_privileges");
        } catch (Exception ex) {
            logger.error("An error occurred while an attempt to patch sampling data was made: " + ex.getMessage(), ex);
            status.setStatus(SyncStatus.Status.FAIL);
            status.setErrorKey("js.error.serverDB_operation_failed");
        }
        return status;
    }

    @RequestMapping(value = {ENG_SAMPLING_DATA_MARK_PATH, FRA_SAMPLING_DATA_MARK_PATH}, method = RequestMethod.PATCH,
            produces = "text/plain", consumes  = "text/plain")
    public @ResponseBody String markSamplingData(@RequestBody String sdId)
    {
        String result = "";
        try {
            result = samplingDataService.markSDForArchive(Long.valueOf(sdId));
        }
        catch(AccessDeniedException ade) {
            result = "error";
            logger.info("Failed to Mark SD for archive: Access Denied [" + sdId + "]");
        }

        return result;
    }

    private void parseMultiDimensionalParameter(Map<Integer, Map<String, String>> paramMap, String startsWith,
                                                HttpServletRequest request)
    {
        List<String> applicableParams = Collections.list(request.getParameterNames())
                                                    .stream()
                                                    .filter(n -> n.startsWith(startsWith))
                                                    .collect(Collectors.toList());
        Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
        Matcher m;
        Integer index;
        String key, value;
        for (String applicableParam : applicableParams) {
            key = null;
            index = null;
            m = p.matcher(applicableParam);
            if (m.find()) {
                index = Integer.valueOf(m.group(1));
                if (m.find()) {
                    key = m.group(1);
                }
            }
            if (index != null && key != null) {
                value = request.getParameter(applicableParam);
                if (!paramMap.containsKey(index)) {
                    paramMap.put(index, new HashMap<>());
                }
                paramMap.get(index).put(key, value);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SamplingDataController.class);
}
