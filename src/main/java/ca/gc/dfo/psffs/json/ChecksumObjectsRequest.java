package ca.gc.dfo.psffs.json;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChecksumObjectsRequest
{
    private List<ChecksumObjectRequest> requests;

    public ChecksumObjectsRequest()
    {
        this.requests = new ArrayList<>();
    }
}
