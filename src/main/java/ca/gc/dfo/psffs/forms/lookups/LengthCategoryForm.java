package ca.gc.dfo.psffs.forms.lookups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LengthCategoryForm extends BaseLookupForm{

    @Column(name = "min_meters")
    private Float minMeters;

    @Column(name = "max_meters")
    private Float maxMeters;
}
