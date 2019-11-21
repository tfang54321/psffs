package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "CTAB_OBSERVER_COMPANY")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class ObserverCompany extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "observerCompanySeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "observerCompanySeqGenerator", sequenceName = "CTAB_OBSERVER_COMPANY_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "id")
    private Integer id;
}
