package ca.gc.dfo.psffs.json;

import lombok.Data;

@Data
public class MigrateStatus {

    public enum Status
    {
        SUCCESS, FAIL
    }

    private Status status;
    private String id;
    private String errorKey;
    private Object[] errorArgs;
}
