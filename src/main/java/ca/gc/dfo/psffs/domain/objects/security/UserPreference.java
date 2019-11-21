package ca.gc.dfo.psffs.domain.objects.security;

import ca.gc.dfo.psffs.domain.objects.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "PSFFS_USER_PREFERENCE", indexes = @Index(columnList = "user_id", name = "FKIND_USER_PREF_USER_ID"))
@Data
@ToString(callSuper = true, exclude = "user")
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class UserPreference extends BaseEntity
{
    public enum PreferenceType
    {
        SAMPLING_DATA_ADVANCED
    }

    @Id
    @GeneratedValue(generator = "userPreferenceSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userPreferenceSeqGen", sequenceName = "PSFFS_USER_PREFERENCE_ID_SEQ",
                        allocationSize = 1, initialValue = 100)
    @Column(name = "user_pref_id")
    private Long userPreferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userId;

    @Column(name = "user_pref_type")
    private String _prefType;

    @Column(name = "user_pref_data", length = 4000)
    private String preferenceData;

    @PreUpdate
    @PrePersist
    public void preSave()
    {
        PreferenceType.valueOf(get_prefType().toUpperCase());
    }

    public void setPreferenceType(PreferenceType preferenceType)
    {
        set_prefType(preferenceType.name());
    }

    public PreferenceType getPreferenceType()
    {
        PreferenceType type;
        try {
            type = PreferenceType.valueOf(get_prefType().toUpperCase());
        } catch (Exception ex) {
            type = null;
            logger.error("An error occurred while trying to derive pref type: " + ex.getMessage(), ex);
        }
        return type;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserPreference.class);
}
