package ca.gc.dfo.psffs.domain.objects.security;

import ca.gc.dfo.psffs.domain.objects.BaseEntity;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "PSFFS_USER_ROLE", indexes = {
        @Index(columnList = "user_id", name = "FKIND_USER_ROLE_USER_ID"),
        @Index(columnList = "role_id", name = "FKIND_USER_ROLE_ROLE_ID")
})
@Data
@ToString(callSuper = true, exclude = "user")
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class UserRole extends BaseEntity
{
    @Id
    @GeneratedValue(generator = "userRoleIdSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userRoleIdSeqGen", sequenceName = "PSFFS_USER_ROLE_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "user_role_id")
    private Long userRoleId;



    @Audited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;


    @Audited(withModifiedFlag=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    private Role role;



    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlagInd;

    @PreUpdate
    @PrePersist
    public void preSave()
    {
        setActor(SecurityHelper.getNtPrincipal());

        if (getActiveFlagInd() == null) {
            setActiveFlagInd(true);
        }
    }

    public void setActiveFlag(Integer activeFlag)
    {
        setActiveFlagInd(activeFlag != null && activeFlag.equals(1));
    }

    public Integer getActiveFlag()
    {
        return this.activeFlagInd != null && this.activeFlagInd ? 1 : 0;
    }
}
