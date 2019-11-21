package ca.gc.dfo.psffs.forms.lookups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VesselForm extends BaseLookupForm
{
    private String cfvSideNumber;
    @JsonIgnore
    private Float vesselLengthMeters;
    @JsonIgnore
    private Float vesselLengthFeet;
    @JsonIgnore
    private String vesselLengthCategoryData;
    @JsonIgnore
    private Integer tonnageId;
    @JsonIgnore
    private Float grTonnage;
}
