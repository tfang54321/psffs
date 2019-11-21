package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CTAB_QUARTER", indexes = @Index(columnList = "catch_location_id", name = "FKIND_QUARTER_CATCH_LOC_ID"))
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue(fields = {"id", "months"}, format = "{0};{1}")
@LookupText(fields = {"englishDescription", "periodNumber"}, format = "{0} ({1})")
@LookupText(fields = {"frenchDescription", "periodNumber"}, format = "{0} ({1})", lang = LookupText.Language.FRENCH)
public class Quarter extends BaseLookup
{
    @Id
    @GeneratedValue(generator = "quarterSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "quarterSeqGenerator", sequenceName = "CTAB_QUARTER_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "id")
    private Integer id;

    @Column(name = "period_nbr")
    private Integer periodNumber;

    @Column(name = "period_from_mth")
    private Integer periodFromMonth;

    @Column(name = "period_to_mth")
    private Integer periodToMonth;

    @Column(name = "catch_location_id")
    private Integer catchLocationId;
    @Transient
    private String catchLocationString;

    @Transient
    private Integer[] months;

    @PostLoad
    public void afterFetch()
    {
        List<Integer> monthList = new ArrayList<>();
        for (int x = periodFromMonth; x <= periodToMonth; x++) {
            monthList.add(x);
        }
        setMonths(monthList.toArray(new Integer[0]));
    }
}
