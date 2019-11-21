package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.domain.objects.ChecksumForObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ChecksumObjectResponse
{
    public enum Status
    {
        OUT_OF_DATE, UP_TO_DATE
    }

    @JsonIgnore
    private ChecksumForObject checksum;
    private Status status;
    private List<?> objects;

    public String getObjectStoreName()
    {
        return this.checksum != null ? this.checksum.getObjectStoreName().toLowerCase() : "";
    }

    public String getChecksumUUID()
    {
        return this.checksum != null ? this.checksum.getGeneratedUUID() : "";
    }
}
