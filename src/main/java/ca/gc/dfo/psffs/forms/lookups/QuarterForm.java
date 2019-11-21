package ca.gc.dfo.psffs.forms.lookups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuarterForm extends BaseLookupForm{

    private Integer periodNumber;

    private Integer periodFromMonth;

    private Integer periodToMonth;

    private Integer catchLocationId;

    private String catchLocationString;
}
