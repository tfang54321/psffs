package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "CTAB_WGHT_CONV_FACTOR")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue(fields = {"id", "conversionFactor", "speciesIds"}, format = "{0};{1};{2}")
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class WeightConversionFactor extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "weightConversionFactorSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "weightConversionFactorSeqGenerator", sequenceName = "CTAB_WGHT_CONV_FACTOR_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;

    @Column(name = "conv_factor")
    private Float conversionFactor;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "WGHT_CONV_FCTR_SPECIES",
        joinColumns = @JoinColumn(name = "WGHT_CONV_FACTOR_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "SPECIES_ID", referencedColumnName = "ID"),
        indexes = {
            @Index(columnList = "wght_conv_factor_id", name = "FKIND_WCFSPECIES_WCF_ID"),
            @Index(columnList = "species_id", name = "FKIND_WCFSPECIES_SPECIES_ID")
        }
    )
    private List<Species> species;

    @Transient
    private Integer[] speciesIds;
    @Transient
    private String speciesEngDescription;
    @Transient
    private String speciesFraDescription;


    @PostLoad
    public void postLoadWCF()
    {
        if (species != null && species.size() > 0) {
            setSpeciesIds(species.stream().map(Species::getId).toArray(size -> new Integer[size]));
            setSpeciesEngDescription(species.stream().map(Species::getEnglishDescription).collect(Collectors.joining("/")));
            setSpeciesFraDescription(species.stream().map(Species::getFrenchDescription).collect(Collectors.joining("/")));
        }
    }
}
