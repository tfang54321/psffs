package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_OTOLITH_EDGE")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText(fields = {"legacyCode", "englishDescription"}, format = "{0}-{1}")
@LookupText(fields = {"legacyCode", "frenchDescription"}, format = "{0}-{1}", lang = LookupText.Language.FRENCH)
public class OtolithEdge extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "otolithEdgeSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "otolithEdgeSeqGenerator", sequenceName = "CTAB_OTOLITH_EDGE_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;
}
