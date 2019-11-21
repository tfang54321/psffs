package ca.gc.dfo.psffs.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class ExtractListItem
{
    @JsonIgnore
    private final Long extractId;
    @JsonIgnore
    private final String type;
    private final LocalDate createdDate;
    @JsonIgnore
    private final String fileName;

    public String getDownloadLink()
    {
        return "<a href=\"javascript: extracts.downloadExtract(" + extractId + ")\">" + fileName + "</a>";
    }

    public String getSimpleType()
    {
        return type != null ? type.substring(type.lastIndexOf(".") + 1) : "";
    }
}
