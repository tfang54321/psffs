package ca.gc.dfo.psffs.forms;

import ca.gc.dfo.psffs.domain.objects.security.UserPreference;
import ca.gc.dfo.psffs.domain.objects.security.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserManagementForm {

    private Integer userId;

    private String ntPrincipal;

    private Long partyId;

    private List<UserPreference> userPreferences;

    private List<UserRole> userRoles;

    private Integer role;

    private String firstName;

    private String lastName;

    private String email;

    private String initials;

    private Integer activeFlag;

    public void setNtPrincipal(String ntPrincipal)
    {
        this.ntPrincipal = ntPrincipal != null ? ntPrincipal.replaceAll("\\s", "") : "";
    }

    public void setInitials(String initials)
    {
        this.initials = initials != null ? initials.toUpperCase().replaceAll("\\s", "") : "";
    }
}
