package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_MEASURING_TECHNIQUE")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class MeasuringTechnique extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "measuringTechniqueSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "measuringTechniqueSeqGenerator", sequenceName = "CTAB_MEASURING_TECH_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;
}
