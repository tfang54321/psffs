package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.forms.lookups.BaseLookupForm;
import lombok.Data;

import java.util.List;

@Data
public class LookupList
{
    private List<? extends BaseLookupForm> data;

    public LookupList() {}
    public LookupList(List<? extends BaseLookupForm> data)
    {
        setData(data);
    }
}
