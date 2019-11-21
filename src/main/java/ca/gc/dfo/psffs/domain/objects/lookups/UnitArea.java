package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_UNIT_AREA", indexes = @Index(columnList = "nafo_division_id", name = "FKIND_UNIT_AREA_NAFO_DIV_ID"))
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue(fields = {"id", "nafoDivisionId"}, format = "{0};{1}")
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class UnitArea extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "unitAreaSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "unitAreaSeqGenerator", sequenceName = "CTAB_UNIT_AREA_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "nafo_division_id", referencedColumnName = "id")
    private NafoDivision nafoDivision;
    @Transient
    private Integer nafoDivisionId;
    @Transient
    private String nafoDivisionCode;
    @Transient
    private String nafoDivisionEngDescription;
    @Transient
    private String nafoDivisionFraDescription;

    @PostLoad
    public void postLoadUnitArea()
    {
        setNafoDivisionId(getNafoDivision());
    }

    public void setNafoDivisionId(NafoDivision nafoDivision)
    {
        if (nafoDivision != null) {
            this.nafoDivisionId = nafoDivision.getId();
            setNafoDivisionEngDescription(nafoDivision.getEnglishDescription());
            setNafoDivisionFraDescription(nafoDivision.getFrenchDescription());
            setNafoDivisionCode(nafoDivision.getLegacyCode());
        }
    }
}
