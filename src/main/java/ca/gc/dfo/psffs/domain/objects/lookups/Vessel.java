package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_VESSEL",
       indexes = {
            @Index(columnList = "cfv_side_nbr", name = "VESSEL_CFV_SIDE_NBR_INDEX"),
            @Index(columnList = "tonnage_id", name = "FKIND_VESSEL_TONNAGE_ID"),
            @Index(columnList = "length_category_id", name = "FKIND_VESSEL_LENGTH_CAT_ID")
       })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText(fields = {"englishDescription", "cfvSideNumber"}, format = "{1} ({0})")
@LookupText(fields = {"frenchDescription", "cfvSideNumber"}, format = "{1} ({0})", lang = LookupText.Language.FRENCH)
public class Vessel extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "vesselSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "vesselSeqGenerator", sequenceName = "CTAB_VESSEL_ID_SEQ", allocationSize = 1, initialValue = 15000)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cfv_side_nbr")
    private String cfvSideNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "tonnage_id", referencedColumnName = "id")
    @JsonIgnore
    private Tonnage tonnage;
    @Transient
    private Integer tonnageId;

    @Column(name = "grt")
    private Float grTonnage;

    @Column(name = "vessel_length_meters")
    private Float vesselLengthMeters;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "length_category_id", referencedColumnName = "id")
    @JsonIgnore
    private LengthCategory lengthCategory;
    @Transient
    private Integer lengthCategoryId;

    @PostLoad
    public void postLoad()
    {
        if (lengthCategory != null) {
            setLengthCategoryId(lengthCategory.getId());
        }
        if (tonnage != null) {
            setTonnageId(tonnage.getId());
        }

        super.postLoad();
    }

}
