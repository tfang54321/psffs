package ca.gc.dfo.psffs.domain.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "PSFFS_SYS_SETTING",
       indexes = @Index(columnList = "setting_nm", name="PSFFS_SYS_SETTING_NM_INDEX", unique = true))
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SystemSetting extends BaseEntity
{
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "setting_nm", unique = true, length = 100)
    private String settingName;

    @Column(name = "setting_val", length = 4000)
    private String settingValue;
}
