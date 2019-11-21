package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.forms.SamplingDataForm;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DTSDList extends SDList
{
    @JsonProperty("_")
    private String sEcho;
    @JsonProperty("recordsTotal")
    private Integer iTotalRecords;
    @JsonProperty("recordsFiltered")
    private Integer iTotalDisplayRecords;
    private Integer start;

    public DTSDList()
    {
        super();
    }
    public DTSDList(List<SamplingDataForm> data)
    {
        super(data);
    }
    public DTSDList(CellHeader cell, List<SamplingDataForm> data)
    {
        super(cell, data);
    }
}
