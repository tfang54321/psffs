package ca.gc.dfo.psffs.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractSamplingSettingForm
{
    //Main
    private Long id;
    private Integer year;
    private Integer speciesId;
    private String description;

    //Sampling Approach
    private String lengthGroupData;
    private Integer lengthUnitId;
    private Integer lengthGroupMin;
    private Integer lengthGroupMax;
}
