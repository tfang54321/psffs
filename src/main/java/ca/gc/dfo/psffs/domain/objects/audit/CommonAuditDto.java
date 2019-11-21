package ca.gc.dfo.psffs.domain.objects.audit;
import java.lang.Comparable;
public class CommonAuditDto implements Comparable<CommonAuditDto> {


    /**
     * @author FANGW
     *
     */
    private static final long serialVersionUID = 4370266473930771037L;
    private String rev;
    public String getRev() {
        return rev;
    }
    public void setRev(String rev) {
        this.rev = rev;
    }
    private String auditDate;
    private String auditTime;
    private String userName;
    private String elementName_audit;
    private String actionName;
    private String newValue;
    private String userID;


    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getElementName_audit() {
        return elementName_audit;
    }
    public void setElementName_audit(String elementName_audit) {
        this.elementName_audit = elementName_audit;
    }
    public String getActionName() {
        return actionName;
    }
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
    public String getNewValue() {
        return newValue;
    }
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    public String getAuditDate() {
        return auditDate;
    }
    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }
    public String getAuditTime() {
        return auditTime;
    }
    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public int getColumnCount() {

        return getClass().getDeclaredFields().length;
    }

    @Override
    public int compareTo(CommonAuditDto auditDto) {
        // let's sort the employee based on age in ascending order
        // returns a negative integer, zero, or a positive integer as this
        // employee id
        // is less than, equal to, or greater than the specified object.
        return Integer.parseInt(this.rev) - Integer.parseInt(auditDto.rev);
    }

    @Override
    public String toString() {
        return "auditDto : " + rev + " - " + auditDate + " - " + auditTime + " - "
                + newValue + "n";
    }
}
