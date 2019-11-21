package ca.gc.dfo.psffs.forms.lookups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LengthGroupForm extends BaseLookupForm
{
    private Integer lengthGroup;

    private Integer lengthUnit;
//    private LengthUnit lengthUnit
    private Integer lengthUnitId;
}
