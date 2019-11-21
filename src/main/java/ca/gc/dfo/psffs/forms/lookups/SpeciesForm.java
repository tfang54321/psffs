package ca.gc.dfo.psffs.forms.lookups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SpeciesForm extends BaseLookupForm{

    private String scientificName;

    private Integer isscaapCode;

    private Integer taxonomicCode;

    private String nafoSpeciesCode;
}
