package ca.gc.dfo.psffs.forms.lookups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UnitAreaForm extends BaseLookupForm{

    @JsonIgnore
    private Integer nafoDivisionId;

    private String nafoDivisionCode;

    private String nafoDivisionEngDescription;
    private String nafoDivisionFraDescription;
}
