package ca.gc.dfo.psffs.json;

import lombok.Data;

import java.util.List;

@Data
public class TSSList
{
    private List<TSSListItem> tripSetSpeciesList;
}
