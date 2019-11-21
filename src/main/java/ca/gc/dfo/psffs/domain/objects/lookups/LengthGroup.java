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
@Table(name = "CTAB_LENGTH_GROUP", indexes = @Index(columnList = "length_unit_id", name = "FKIND_LNGRP_LENGTH_UNIT_ID"))
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@LookupValue(fields = {"id", "lengthUnitId"}, format = "{0};{1}")
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class LengthGroup extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "lengthGroupSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "lengthGroupSeqGenerator", sequenceName = "CTAB_LENGTH_GROUP_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;

    @Column(name = "length_group")
    private Integer lengthGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "length_unit_id", referencedColumnName = "id")
    private LengthUnit lengthUnit;
    @Transient
    private Integer lengthUnitId;

    @PostLoad
    public void postLoadLengthGroup()
    {
        if (lengthUnit != null) setLengthUnitId(lengthUnit.getId());
    }

    @PrePersist
    @PreUpdate
    public void determineDescriptions()
    {
        String edesc = "";
        String fdesc = "";
        if(lengthGroup != null && lengthUnit != null) {
            edesc = String.format(java.util.Locale.US, "%1d", lengthGroup) + " " + lengthUnit.getEnglishDescription();
            fdesc = String.format(java.util.Locale.FRANCE, "%1d", lengthGroup) + " " + lengthUnit.getFrenchDescription();
        }
        setEnglishDescription(edesc);
        setFrenchDescription(fdesc);
    }
}
