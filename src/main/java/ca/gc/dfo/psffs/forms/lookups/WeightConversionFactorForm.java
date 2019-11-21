package ca.gc.dfo.psffs.forms.lookups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WeightConversionFactorForm extends BaseLookupForm{

    private Float conversionFactor;

    @JsonIgnore
    private Integer[] speciesIds;

    private String speciesEngDescription;

    private String speciesFraDescription;
}
