package ca.gc.dfo.psffs.domain.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "SAMPLING_SETTING", indexes = @Index(columnList = "setting_type"))
@Where(clause = "setting_type='OBSERVER'")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ObserverSetting extends BaseSamplingSetting
{
    //Main
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "observerSettingSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "observerSettingSeqGen", sequenceName = "SAMPLING_SETTING_ID_SEQ", allocationSize = 1, initialValue = 100)
    private Long id;

    @Override
    protected SamplingSettingType chosenType()
    {
        return SamplingSettingType.OBSERVER;
    }

    //All other fields inherited
}
