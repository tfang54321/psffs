package ca.gc.dfo.psffs.domain.objects.security;

import ca.gc.dfo.psffs.domain.objects.BaseEntity;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupText;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PSFFS_ROLE")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@LookupValue(fields = "roleId")
@LookupText(fields = "englishRoleTitle")
@LookupText(fields = "frenchRoleTitle", lang = LookupText.Language.FRENCH)
public class Role extends BaseEntity
{


    @Id
    @Column(name = "role_id")
    private Integer roleId;


    @Audited
    @Column(name = "role_nm", nullable = false, unique = true, length = 75)
    private String roleName;

    @Column(name = "en_role_title", nullable = false)
    private String englishRoleTitle;

    @Column(name = "fr_role_title", nullable = false)
    private String frenchRoleTitle;

    @Column(name = "edesc", length = 4000)
    private String englishDescription;

    @Column(name = "fdesc", length = 4000)
    private String frenchDescription;
}
