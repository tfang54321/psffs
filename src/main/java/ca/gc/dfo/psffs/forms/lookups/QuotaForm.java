package ca.gc.dfo.psffs.forms.lookups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuotaForm extends BaseLookupForm{
    @Column(name = "nafo_country_cd")
    private String nafoCountryCode;

}
