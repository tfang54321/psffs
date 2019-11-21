package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_CONDITION_LANDED")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class ConditionLanded extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "conditionLandedSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "conditionLandedSeqGenerator", sequenceName = "CTAB_CONDITION_LANDED_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;
}
