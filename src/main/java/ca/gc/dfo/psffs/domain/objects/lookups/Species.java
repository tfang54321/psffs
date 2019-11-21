package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_SPECIES")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText(fields = {"englishDescription", "legacyCode"}, format = "{0} ({1})")
@LookupText(fields = {"frenchDescription", "legacyCode"}, format = "{0} ({1})", lang = LookupText.Language.FRENCH)
public class Species extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "speciesSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "speciesSeqGenerator", sequenceName = "CTAB_SPECIES_ID_SEQ", allocationSize = 1, initialValue = 10000)
    @Column(name = "id")
    private Integer id;

    @Column(name = "scientific_nm")
    private String scientificName;

    @Column(name = "isscaap_cd")
    private Integer isscaapCode;

    @Column(name = "taxonomic_cd")
    private Integer taxonomicCode;

    @Column(name = "nafo_species_cd")
    private String nafoSpeciesCode;
}
