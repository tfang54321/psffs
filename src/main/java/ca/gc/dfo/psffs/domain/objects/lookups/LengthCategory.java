package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_LENGTH_CATEGORY")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue(fields = {"id", "minMeters", "maxMeters"}, format = "{0};{1};{2}")
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class LengthCategory extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "lengthCategorySeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "lengthCategorySeqGenerator", sequenceName = "CTAB_LENGTH_CATEGORY_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;

    @Column(name = "min_meters")
    private Float minMeters;

    @Column(name = "max_meters")
    private Float maxMeters;

    @PrePersist
    @PreUpdate
    public void determineDescriptions()
    {
        String edesc = "";
        String fdesc = "";
        if (minMeters != null && maxMeters != null) {
            Float minInches = minMeters * 39.37f;
            Float maxInches = maxMeters * 39.37f;
            Integer minFeet = Float.valueOf(minInches / 12f).intValue();
            Integer maxFeet = Float.valueOf(maxInches / 12f).intValue();
            Integer minInchesInt = Float.valueOf(minInches % 12f).intValue();
            Integer maxInchesInt = Float.valueOf(maxInches % 12f).intValue();
            StringBuilder de = new StringBuilder();
            de.append(minFeet).append("'");
            if (!minInchesInt.equals(0)) de.append(minInchesInt).append("''");
            de.append(" - ");
            de.append(maxFeet).append("'");
            if (!maxInchesInt.equals(0)) de.append(maxInchesInt).append("''");
            de.append(" (");
            StringBuilder df = new StringBuilder(de);
            de.append(String.format(java.util.Locale.US,"%.1f", minMeters)).append(" - ");
            df.append(String.format(java.util.Locale.FRANCE,"%.1f", minMeters)).append(" - ");
            de.append(String.format(java.util.Locale.US,"%.1f", maxMeters)).append(" m)");
            df.append(String.format(java.util.Locale.FRANCE,"%.1f", maxMeters)).append(" m)");
            edesc = de.toString();
            fdesc = df.toString();
        }
        setEnglishDescription(edesc);
        setFrenchDescription(fdesc);
    }
}
