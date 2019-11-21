package ca.gc.dfo.psffs.domain.objects.security;

import ca.gc.dfo.psffs.domain.objects.BaseEntity;
import ca.gc.dfo.spring_commons.commons_offline_wet.domain.EAccessUserDetails;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupActiveFlag;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "PSFFS_USER",
       indexes = {@Index(columnList = "nt_principal", name = "PSFFS_USER_PRINCIPAL_INDEX", unique = true),
                  @Index(columnList = "initials", name = "PSFFS_USER_INITIALS_INDEX", unique = true)})
@Data
@ToString(callSuper = true, exclude = "userPreferences")
@EqualsAndHashCode(callSuper = true, exclude = "userPreferences")
@LookupValue(fields = {"partyId", "initials"}, format = "{0},{1}")
@LookupText(fields = {"lastName", "firstName", "initials"}, format = "{0}, {1} ({2})")
public class User extends BaseEntity implements EAccessUserDetails, Serializable
{
    @Id
    @GeneratedValue(generator = "userIdSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userIdSeqGen", sequenceName = "PSFFS_USER_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "user_id")
    private Integer userId;

    @Audited(withModifiedFlag=true)
    @Column(name = "nt_principal", length = 100, nullable = false, unique = true)
    private String ntPrincipal;

    @Column(name = "party_id")
    private Long partyId;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<UserPreference> userPreferences;

    @Audited
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<UserRole> userRoles;

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 75)
    private String lastName;

    @Column(name = "email", length = 150)
    private String email;


    @Audited(withModifiedFlag=true)
    @Column(name = "initials", unique = true, length = 5)
    private String initials;

    @Audited(withModifiedFlag=true)
    @LookupActiveFlag
    @Column(name = "active_flag")
    private Boolean activeFlagInd;

    public String getFullname()
    {
        String lastName = getLastName();
        String firstName = getFirstName();
        List<String> names = new ArrayList<>();
        if (lastName != null && lastName.trim().length() > 0) names.add(lastName);
        if (firstName != null && firstName.trim().length() > 0) names.add(firstName);
        return String.join(", ", names);
    }

    public void setFullname(String fullname) {
        if (fullname != null && fullname.trim().length() > 0) {
            String[] names = fullname.split(",");
            setLastName(names[0].trim());
            if (names.length > 1) {
                setFirstName(names[1].trim());
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userRoles != null && userRoles.size() > 0) {
            userRoles.stream()
                     .filter(UserRole::getActiveFlagInd)
                     .forEach(ur -> authorities.add(new SimpleGrantedAuthority(ur.getRole().getRoleName())));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_BASE_ACCESS"));
        }
        return authorities;
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
