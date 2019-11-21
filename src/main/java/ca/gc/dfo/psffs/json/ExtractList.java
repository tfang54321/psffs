package ca.gc.dfo.psffs.json;

import lombok.Data;

import java.util.List;

@Data
public class ExtractList
{
    private List<ExtractListItem> extracts;

    public ExtractList() {}

    public ExtractList(List<ExtractListItem> extracts)
    {
        setExtracts(extracts);
    }
}
