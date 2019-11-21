package ca.gc.dfo.psffs.json;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChecksumObjectsResponse
{
    private List<ChecksumObjectResponse> responses;

    public ChecksumObjectsResponse()
    {
        this.responses = new ArrayList<>();
    }
}
