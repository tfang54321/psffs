package ca.gc.dfo.psffs.controllers;

import ca.gc.dfo.psffs.domain.objects.Extract;
import ca.gc.dfo.psffs.export.extract.AbstractExtractRunner;
import ca.gc.dfo.psffs.export.extract.AgeGrowthExtract;
import ca.gc.dfo.psffs.export.extract.LFLSMExtract;
import ca.gc.dfo.psffs.export.extract.LengthFrequencyExtract;
import ca.gc.dfo.psffs.export.report.AbstractReport;
import ca.gc.dfo.psffs.forms.ExecuteReportForm;
import ca.gc.dfo.psffs.json.ExtractList;
import ca.gc.dfo.psffs.services.ExtractService;
import ca.gc.dfo.psffs.services.SamplingDataService;
import ca.gc.dfo.psffs.services.SamplingEntityService;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ServerToolsController
{
    public static final String EXTRACTS_PATH = TemplateController.SERVER_TOOLS_PATH + "/extracts";
    public static final String ENG_EXTRACTS_PATH = PathLocaleChangeInterceptor.ENG_PATH + EXTRACTS_PATH;
    public static final String FRA_EXTRACTS_PATH = PathLocaleChangeInterceptor.FRA_PATH + EXTRACTS_PATH;

    public static final String EXTRACTS_LIST_PATH = EXTRACTS_PATH + "/list";
    public static final String EXTRACT_DOWNLOAD_PATH = EXTRACTS_PATH + "/download";
    public static final String EXTRACT_EXECUTE_PATH = EXTRACTS_PATH + "/execute";

    public static final String REPORTS_PATH = TemplateController.SERVER_TOOLS_PATH + "/reports";
    public static final String ENG_REPORTS_PATH = PathLocaleChangeInterceptor.ENG_PATH + REPORTS_PATH;
    public static final String FRA_REPORTS_PATH = PathLocaleChangeInterceptor.FRA_PATH + REPORTS_PATH;

    public static final String EXECUTE_REPORT_PATH = REPORTS_PATH + "/execute";
    public static final String ENG_EXECUTE_REPORT_PATH = PathLocaleChangeInterceptor.ENG_PATH + EXECUTE_REPORT_PATH;
    public static final String FRA_EXECUTE_REPORT_PATH = PathLocaleChangeInterceptor.FRA_PATH + EXECUTE_REPORT_PATH;

    @Autowired
    private ExtractService extractService;

    @Autowired
    private SamplingEntityService samplingEntityService;

    @Autowired
    private SamplingDataService samplingDataService;

    @RequestMapping(value = {ENG_REPORTS_PATH, FRA_REPORTS_PATH}, method = RequestMethod.GET)
    public String reportsPage(ModelMap model)
    {
        model.addAttribute("reportForm", new ExecuteReportForm());
        return "serverTools/reports";
    }

    @RequestMapping(value = {ENG_EXECUTE_REPORT_PATH, FRA_EXECUTE_REPORT_PATH}, method = RequestMethod.POST)
    public String executeReport(@ModelAttribute("reportForm") ExecuteReportForm reportForm, ModelMap model) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class<?> reportClass = Class.forName(reportForm.getReportType());
        AbstractReport report = (AbstractReport) reportClass.newInstance();
        List<?> data = samplingEntityService.getSamplingTypeEntityDataByReportForm(reportForm);
        report.populateModel(model, reportForm, data);
        return report.getReportView();
    }

    @RequestMapping(value = {ENG_EXTRACTS_PATH, FRA_EXTRACTS_PATH}, method = RequestMethod.GET)
    @PreAuthorize(SecurityHelper.EL_VIEW_EXTRACTS)
    public String extractsPage()
    {
        return "serverTools/extracts";
    }

    @RequestMapping(value = EXTRACTS_LIST_PATH, method = RequestMethod.GET, params = "type", produces = "application/json")
    @PreAuthorize(SecurityHelper.EL_VIEW_EXTRACTS)
    public @ResponseBody ExtractList extractList(@RequestParam("type") String type)
    {
        return extractService.getListByCreatorAndType(SecurityHelper.getNtPrincipal(), type);
    }

    @RequestMapping(value = EXTRACT_DOWNLOAD_PATH, method = RequestMethod.GET, params = "extractId")
    @PreAuthorize(SecurityHelper.EL_VIEW_EXTRACTS)
    public void downloadExtract(@RequestParam("extractId") Long extractId, HttpServletResponse response) throws IllegalArgumentException, IOException
    {
        Extract extract = extractService.getById(extractId);
        if (extract == null) {
            throw new IllegalArgumentException("Cannot download extract, there is no extract with ID: " + extractId);
        }
        String owner = SecurityHelper.getNtPrincipal();
        if (!extract.getCreatedBy().equals(owner)) {
            throw new IllegalArgumentException("Cannot download extract, the logged in user does not match the owner of this extract ID: " + extractId);
        }

        response.setContentType("text/plain");
        response.setHeader("Content-disposition", "attachment; filename=" + extract.getFileName());
        byte[] data = extract.getData().getBytes();
        response.setHeader("Content-Length", ""+data.length);

        OutputStream os = response.getOutputStream();
        InputStream is = new ByteArrayInputStream(data);
        try {
            byte[] byteChunk = new byte[10240]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                os.write(byteChunk, 0, n);
            }
        } finally {
            is.close();

            if (os != null) {
                os.flush();
                os.close();
            }
        }
    }

    @RequestMapping(value = EXTRACT_EXECUTE_PATH, method = RequestMethod.POST, produces = "text/plain")
    @PreAuthorize(SecurityHelper.EL_EXECUTE_EXTRACTS)
    public @ResponseBody String executeExtracts(@RequestParam("type") String type)
    {
        String returnMsg = "success";
        try {
            doExecuteExtract(type);
        } catch (Exception ex) {
            logger.error("An error occurred while attempting an extract execution: " + ex.getMessage(), ex);
            returnMsg = "js.error.server_request_failed";
        }
        return returnMsg;
    }

    private void doExecuteExtract(String type) throws Exception
    {
        String actor = SecurityHelper.getNtPrincipal();
        AbstractExtractRunner extract;
        if (type.equals(LengthFrequencyExtract.class.getName())) {
            extract = new LengthFrequencyExtract(this.extractService,
                    this.samplingEntityService, actor, extractService.getLFExtractPredicate());
        } else if (type.equals(LFLSMExtract.class.getName())) {
            extract = new LFLSMExtract(this.extractService, this.samplingEntityService, actor,
                    extractService.getLFLSMExtractPredicate());
        } else if (type.equals(AgeGrowthExtract.class.getName())) {
            extract = new AgeGrowthExtract(this.extractService, this.samplingDataService, actor,
                    extractService.getSamplingDataExtractPredicate());
        } else {
            throw new IllegalArgumentException("No if handler code block for type passed: " + type);
        }

        extract.execute();
    }

    private static final Logger logger = LoggerFactory.getLogger(ServerToolsController.class);
}
