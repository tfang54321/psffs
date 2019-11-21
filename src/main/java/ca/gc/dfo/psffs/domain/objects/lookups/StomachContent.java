package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_STOMACH_CONTENT")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText(fields = {"legacyCode", "englishDescription"}, format = "{0}-{1}")
@LookupText(fields = {"legacyCode", "frenchDescription"}, format = "{0}-{1}", lang = LookupText.Language.FRENCH)
public class StomachContent extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "stomachContentSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "stomachContentSeqGenerator", sequenceName = "CTAB_STOMACH_CONTENT_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;
}
