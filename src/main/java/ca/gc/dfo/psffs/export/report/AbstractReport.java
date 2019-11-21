package ca.gc.dfo.psffs.export.report;

import ca.gc.dfo.psffs.forms.ExecuteReportForm;
import lombok.Data;
import org.springframework.ui.ModelMap;

import java.util.List;

@Data
public abstract class AbstractReport<D>
{
    private String reportNameKey;
    private String reportView;

    public AbstractReport(String reportNameKey, String reportView)
    {
        setReportNameKey(reportNameKey);
        setReportView(reportView);
    }

    public void populateModel(ModelMap model, ExecuteReportForm reportForm, List<D> data)
    {
        model.addAttribute("reportForm", reportForm);
        populateModel(model, data);
    }
    protected abstract void populateModel(ModelMap model, List<D> data);
}
