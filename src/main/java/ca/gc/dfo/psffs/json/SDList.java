package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.forms.SamplingDataForm;
import lombok.Data;

import java.util.List;

@Data
public class SDList
{
    private CellHeader cell;
    private List<SamplingDataForm> data;

    public SDList() {}
    public SDList(List<SamplingDataForm> data)
    {
        setData(data);
    }
    public SDList(CellHeader cell, List<SamplingDataForm> data)
    {
        this(data);
        setCell(cell);
    }
}
