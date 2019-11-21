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
@Where(clause = "code_type='SAMPLING_TYPE'")
@LookupValue
@LookupText(fields = {"englishDescription", "legacyCode"}, format = "{0}({1})")
@LookupText(fields = {"frenchDescription", "legacyCode"}, format = "{0}({1})", lang = LookupText.Language.FRENCH)
public class SamplingType extends OperationalCode
{
    @Id
    @Column(name = "id")
    private Integer id;
}
