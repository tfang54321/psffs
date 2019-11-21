package ca.gc.dfo.psffs.json;

import lombok.Data;

@Data
public class SyncStatus
{
    public enum Status
    {
        SUCCESS, FAIL
    }

    private Status status;
    private String id;
    private String errorKey;

    //Implemented for backwards compatibility
    public String getLfId()
    {
        return this.id;
    }
}
