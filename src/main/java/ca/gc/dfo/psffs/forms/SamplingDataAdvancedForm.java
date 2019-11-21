package ca.gc.dfo.psffs.forms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SamplingDataAdvancedForm extends AbstractCellDataForm
{
    public enum SourceType
    {
        ALL, ONE
    }

    //Source
    private SourceType source;
    private String sourceSampleId;

    //Cell header data comes from super class

    //Filters
    private String[] samplingTypes;

    //Fields
    private String fields;

    //Fetch & Save
    private Boolean savePreferences;

    public SamplingDataAdvancedForm()
    {
        setSamplingTypes(new String[] {"o","s","c","w"});
        setFields("none");
        setSource(SourceType.ALL);
    }

    public Boolean getSavePreferences()
    {
        return this.savePreferences != null ? this.savePreferences : false;
    }
}
