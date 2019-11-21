package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.forms.SamplingDataForm;
import lombok.Data;

import java.util.List;

@Data
public class SamplingDataPatchRequest
{
    private List<String> fields;
    private SamplingDataForm data;
}
