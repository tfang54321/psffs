package ca.gc.dfo.psffs.forms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExecuteReportForm extends AbstractCellDataForm
{
    private String _csrf;
    private String reportType;

    public static ExecuteReportForm newBlankInstance()
    {
        return new ExecuteReportForm();
    }
}