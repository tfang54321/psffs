package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PSFFS_OPERATIONAL_CODE")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Where(clause = "code_type='SAMPLING_DATA_STATUS'")
@LookupValue
@LookupText
@LookupText(fields = "frenchDescription", lang = LookupText.Language.FRENCH)
public class SamplingDataStatus extends OperationalCode
{
    public static final Integer ENTERED_STS_ID = 36;
    public static final Integer COMPLETED_STS_ID = 37;
    public static final Integer MARK_FOR_ARCHIVE_STS_ID = 38;
    public static final Integer ARCHIVED_STS_ID = 39;

    @Id
    @Column(name = "id")
    private Integer id;
}
