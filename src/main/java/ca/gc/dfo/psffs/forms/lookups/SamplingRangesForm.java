package ca.gc.dfo.psffs.forms.lookups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SamplingRangesForm {

    public Integer meshSizeMin;

    public Integer meshSizeMax;

    public Integer latitudeMin;

    public Integer latitudeMax;

    public Integer longitudeMin;

    public Integer longitudeMax;

    public Integer depthMin;

    public Integer depthMax;
}
