package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_MATURITY", indexes = @Index(columnList = "sex_code_id", name = "FKIND_MATURITY_SEX_CODE_ID"))
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue(fields = {"legacyCode", "sexCodeCode"}, format = "{0};{1}")
@LookupText(fields = {"legacyCode", "sexCodeCode", "englishDescription"}, format = "{0}-({1}) {2}")
@LookupText(fields = {"legacyCode", "sexCodeCode", "frenchDescription"}, format = "{0}-({1}) {2}", lang = LookupText.Language.FRENCH)
public class Maturity extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "maturitySeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "maturitySeqGenerator", sequenceName = "CTAB_MATURITY_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sex_code_id", referencedColumnName = "id")
    private SexCode sexCode;
    @Transient
    private Integer sexCodeId;
    @Transient
    private String sexCodeEngDescription;
    @Transient
    private String sexCodeFraDescription;
    @Transient
    private String sexCodeCode;

    @PostLoad
    public void postLoadMaturity()
    {
        setSexCode(getSexCode());
    }

    public void setSexCode(SexCode sexCode)
    {
        if (sexCode != null) {
            this.sexCode = sexCode;
            this.sexCodeId = sexCode.getId();
            setSexCodeEngDescription(sexCode.getEnglishDescription());
            setSexCodeFraDescription(sexCode.getFrenchDescription());
            setSexCodeCode(sexCode.getLegacyCode());
        }
    }
}
