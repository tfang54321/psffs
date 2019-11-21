package ca.gc.dfo.psffs.json;

import lombok.Data;

@Data
public class ChecksumObjectRequest
{
    private String objectStoreName;
    private String checksumUUID;
}
