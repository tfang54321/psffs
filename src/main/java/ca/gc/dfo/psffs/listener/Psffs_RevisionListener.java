package ca.gc.dfo.psffs.listener;


import ca.gc.dfo.psffs.domain.objects.audit.Psffs_RevisionEntity;
import ca.gc.dfo.psffs.domain.objects.security.User;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionListener;
import org.hibernate.envers.RevisionType;

import java.io.Serializable;


/**
 *
 */
public class Psffs_RevisionListener implements RevisionListener, EntityTrackingRevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        Psffs_RevisionEntity revision = (Psffs_RevisionEntity) revisionEntity;
      User userDetails = (User) SecurityHelper.getUserDetails();
        if (userDetails != null) {
            revision.setUserAccount(userDetails.getFullname());
            revision.setUserFirstName(userDetails.getFirstName());
            revision.setUserLastName(userDetails.getLastName());
            revision.setUserId(userDetails.getUserId());

        } else {
            revision.setUserAccount("SYSTEM");
            revision.setUserFirstName("SYSTEM");
            revision.setUserLastName("SYSTEM");
        }
    }

    @Override
    public void entityChanged(Class entityClass, String entityName,
                              Serializable entityId, RevisionType revisionType,
                              Object revisionEntity) {
//        System.out.println("monitor changed entitu....");
    }
}
