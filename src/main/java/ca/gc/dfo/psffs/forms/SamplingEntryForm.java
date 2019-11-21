package ca.gc.dfo.psffs.forms;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SamplingEntryForm
{
    private Integer length;
    private String sex;
    private String maturityLevel;
    private List<String> samplings;
    private Integer increment;
    private SamplingDataForm samplingData;

    public String[] getFields()
    {
        List<String> fieldList = samplings.stream()
                                          .map(this::mapSamplingToField)
                                          .collect(Collectors.toList());
        return fieldList.toArray(new String[fieldList.size()]);
    }

    private String mapSamplingToField(String sampling)
    {
        String fieldName = this.sex;
        if (!sampling.equals("l")) fieldName += "_"+sampling;
        fieldName += "["+this.length+"]";
        return fieldName;
    }
}
