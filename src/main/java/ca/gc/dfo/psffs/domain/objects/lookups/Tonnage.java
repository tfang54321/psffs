package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_TONNAGE")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText(fields = {"englishDescription", "legacyCode"}, format = "{0} ({1})")
@LookupText(fields = {"frenchDescription", "legacyCode"}, format = "{0} ({1})", lang = LookupText.Language.FRENCH)
public class Tonnage extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "tonnageSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "tonnageSeqGenerator", sequenceName = "CTAB_TONNAGE_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;
}
