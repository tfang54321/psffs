/**
 * 
 */
package ca.gc.dfo.psffs.domain.objects.audit;


import ca.gc.dfo.psffs.listener.Psffs_RevisionListener;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@Entity
@Table(name = "AUDIT_REVINFO")
@RevisionEntity(Psffs_RevisionListener.class)
@SequenceGenerator(name = "PK", sequenceName = "REVINFO_SQ", allocationSize = 1)
public class Psffs_RevisionEntity implements Serializable {

    private static final long serialVersionUID = 3775550420286576001L;
    private String userFirstName;
    private String userLastName;
    private String userAccount;
    private Integer userId;

    @RevisionTimestamp
    private Date createdDate;


    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK")
    @Column(name = "ID")
    private int id;


 

   

    public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    @ElementCollection
    @JoinTable(name = "AUDIT_REVCHANGES", joinColumns = {@JoinColumn(name = "REV")})
    @Column(name = "ENTITYNAME")
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;
    
 

    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}



