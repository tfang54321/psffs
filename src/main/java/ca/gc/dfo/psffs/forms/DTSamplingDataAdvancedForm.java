package ca.gc.dfo.psffs.forms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DTSamplingDataAdvancedForm extends SamplingDataAdvancedForm
{
    //Set as "_" from datatables get request
    private String sEcho;
    //"column" ex: column[0][name]
    private Map<Integer, Map<String, String>> columnDefinitions;
    //"sKeyword" ex: search[value]="Hello World!"
    private String searchValue;
    //"iDisplayStart"
    private Integer start;
    //"iDisplayLength"
    private Integer length;
    //"iSortingCols" ex: order[0][column]="1" / order[0][dir]="asc"
    private Map<Integer, Map<String, String>> orderDefinitions;
    //"iColumns" ex: size of column definitions
    private Integer iColumns;

    public DTSamplingDataAdvancedForm()
    {
        super();
        setColumnDefinitions(new HashMap<>());
        setOrderDefinitions(new HashMap<>());
    }

    public Integer getiColumns()
    {
        return iColumns != null ? iColumns : (columnDefinitions != null ? columnDefinitions.size() : 0);
    }
}
