/**
 *
 */
package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.audit.CommonAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author FANGW
 *
 */
@Service
public class AuditServiceImpl implements UserAuditingServiceInterface {



	static final String queryParameter = "userId";
	static final String queryAuditForSingleUser = "SELECT "
			+ "userAudit.rev ,"   //0
			+ "userAudit.revtype ,"   //1
			+ "userAudit.nt_principal ,"   //2
			+ "userAudit.nt_principal_mod ,"   //3
			+ "userAudit.user_id ,"//4
			+ "userAudit.active_flag,"  //5
			+ "userAudit.active_flag_ind_mod,"   //6
			+"userAudit.initials,"   //7
			+"userAudit.initials_mod,"   //8
			+ "revInfo.created_date ,"   //9
			+ "revInfo.user_Account"   //10
			+ " FROM PSFFS_USER_AUD userAudit,AUDIT_REVINFO revInfo "
			+ " WHERE userAudit.user_id = :userId and userAudit.rev= revInfo.id   order by revInfo.id desc";

	static final String queryAuditForSingleUserForAll = "SELECT "
			+ "userAudit.rev ,"   //0
			+ "userAudit.revtype ,"   //1
			+ "userAudit.nt_principal ,"   //2
			+ "userAudit.nt_principal_mod ,"   //3
			+ "userAudit.user_id ,"//4
			+ "userAudit.active_flag,"  //5
			+ "userAudit.active_flag_ind_mod,"   //6
			+"userAudit.initials,"   //7
			+"userAudit.initials_mod,"   //8
			+ "revInfo.created_date ,"   //9
			+ "revInfo.user_Account"   //10
			+ " FROM PSFFS_USER_AUD userAudit,AUDIT_REVINFO revInfo "
			+ " WHERE  userAudit.rev= revInfo.id   order by revInfo.id desc";

	static final String queryAuditForSingleUserRole = "SELECT "
			+ "userRoleAud.rev ,"
			+ "userRoleAud.revtype ,"
			+ "userRoleAud.user_id ,"
			+ "userRoleAud.role_id , "
			+ "userRoleAud.role_mod ,"
			+ "revInfo.created_date ,"
			+ "revInfo.user_Account"


			+ " FROM AUDIT_REVINFO revInfo,PSFFS_USER_ROLE_AUD userRoleAud "
			+ " WHERE userRoleAud.user_id = :userId and userRoleAud.rev= revInfo.id    order by revInfo.id desc";

	static final String queryAuditForSingleUserRoleForAll= "SELECT "
			+ "userRoleAud.rev ,"
			+ "userRoleAud.revtype ,"
			+ "userRoleAud.user_id ,"
			+ "userRoleAud.role_id , "
			+ "userRoleAud.role_mod ,"
			+ "revInfo.created_date ,"
			+ "revInfo.user_Account"


			+ " FROM AUDIT_REVINFO revInfo,PSFFS_USER_ROLE_AUD userRoleAud "
			+ " WHERE userRoleAud.rev= revInfo.id    order by revInfo.id desc";

	@Autowired
	private EntityManager entityManager;


	@Override
	@Transactional(readOnly = true)
	public List<CommonAuditDto> getAuditReportsForAllUsers(MessageSource messageSource, Locale locale) {
		List<CommonAuditDto> userAuditFromUserAUD = this.getUserAuditReportFromUserAUD(null,messageSource,locale,queryAuditForSingleUserForAll);
		List<CommonAuditDto> userAuditFromUserRoleAUD = this.getUserAuditReportFromUserRoleAUD(null,messageSource,locale,queryAuditForSingleUserForAll);

		userAuditFromUserAUD.addAll(userAuditFromUserRoleAUD);

		Collections.sort(userAuditFromUserAUD);
		Collections.reverse(userAuditFromUserAUD);
		return userAuditFromUserAUD;//it include all user audit now.
	}

	@Override
	public List<CommonAuditDto> getUserAuditReport(Optional<String> userId, MessageSource messageSource,Locale locale) {
		List<CommonAuditDto> userAuditFromUserAUD = this.getUserAuditReportFromUserAUD(userId,messageSource,locale,queryAuditForSingleUser);
		List<CommonAuditDto> userAuditFromUserRoleAUD = this.getUserAuditReportFromUserRoleAUD(userId,messageSource,locale,queryAuditForSingleUserRole);

		userAuditFromUserAUD.addAll(userAuditFromUserRoleAUD);

		Collections.sort(userAuditFromUserAUD);
		Collections.reverse(userAuditFromUserAUD);
		 return userAuditFromUserAUD;//it include all user audit now.


	}

	private List<CommonAuditDto>  parserAuditRecordsFromUserAUD(Query query,MessageSource messageSource, Locale locale) {

		List<Object[]> auditList = query.getResultList();
		List<CommonAuditDto> auditDTOs = new ArrayList<CommonAuditDto>();
		CommonAuditDto auditDto = null;

		String actionName = null;
		if(!CollectionUtils.isEmpty(auditList)) {
			for(Object[] temp :auditList) {

				if (temp[1].equals(new BigDecimal(0))) {
					actionName = messageSource.getMessage("i18n.audit.report.action.add", null, locale);
				}

				if (temp[3].equals(new BigDecimal(1))) {//user name modified
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					auditDto.setNewValue(String.valueOf( temp[2]));
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[9]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[9]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[10]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.name", null, locale));
					auditDto.setUserID(String.valueOf( temp[4]));
					auditDTOs.add(auditDto);
				}

				if (temp[6].equals(new BigDecimal(1))) {//user activate status
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					if("1".equals(String.valueOf( temp[5]))){
						auditDto.setNewValue(messageSource.getMessage("i18n.user.status.activate", null, locale));
					}else {
						auditDto.setNewValue(messageSource.getMessage("i18n.user.status.inactivate", null, locale));
					}
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[9]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[9]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[10]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.status", null, locale));
					auditDto.setUserID(String.valueOf( temp[4]));
					auditDTOs.add(auditDto);
				}



				if (temp[8].equals(new BigDecimal(1))) {//user initial
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					if("1".equals(String.valueOf( temp[7]))){
						auditDto.setNewValue(messageSource.getMessage("i18n.user.name.initials", null, locale));
					}else {
						auditDto.setNewValue(messageSource.getMessage("i18n.user.name.initials", null, locale));
					}
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[9]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[9]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[10]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.initials", null, locale));
					auditDto.setUserID(String.valueOf( temp[4]));
					auditDTOs.add(auditDto);
				}
			}
		}
		return auditDTOs;
	}

	private List<CommonAuditDto>  parserAuditRecordsFromUserRoleAUD(Query query,MessageSource messageSource, Locale locale) {

		List<Object[]> auditList = query.getResultList();
		List<CommonAuditDto> auditDTOs = new ArrayList<CommonAuditDto>();
		CommonAuditDto auditDto = null;

		String actionName = null;
		if(!CollectionUtils.isEmpty(auditList)) {
			for(Object[] temp :auditList) {

				if (temp[1].equals(new BigDecimal(0))) {
					actionName = messageSource.getMessage("i18n.audit.report.action.add", null, locale);
				}

				if (temp[4].equals(new BigDecimal(1))) {//user role
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					auditDto.setNewValue(String.valueOf( temp[3]));
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[5]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[5]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[6]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.role", null, locale));
					auditDto.setUserID(String.valueOf( temp[2]));
					auditDTOs.add(auditDto);
				}
			}
		}
		return auditDTOs;
	}

	public String convertToCurrentTimeZone(String date) {
		String converted_date = "";
		try {


			//commoent out for now
//			DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
//			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//			Date tempDate = utcFormat.parse(date);
//
//			DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
//			currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

			DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");

			Date tempDate = currentTFormat.parse(date);

			converted_date =  currentTFormat.format(tempDate);
		}catch (Exception e){ e.printStackTrace();}

		return converted_date;
	}


	//get the current time zone

	public String getCurrentTimeZone(){
		TimeZone tz = Calendar.getInstance().getTimeZone();
		//		System.out.println(tz.getDisplayName());
		return tz.getID();
	}
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getAllUserID() {

		String queryAuditForSingleUserFromUserAudit = "SELECT distinct userAudit.user_id FROM PSFFS_USER_AUD userAudit order by userAudit.user_id asc  ";
		String queryAuditForSingleUserFromUserRoleAudit = "SELECT distinct userAudit.user_id FROM PSFFS_USER_ROLE_AUD userAudit order by userAudit.user_id asc  ";

		Query query = entityManager.createNativeQuery(queryAuditForSingleUserFromUserAudit);
		List<Object[]> allUserIdsFromUsrAudit = query.getResultList();

		query = entityManager.createNativeQuery(queryAuditForSingleUserFromUserRoleAudit);
		List<Object[]> allUserIdsFromUserRoldAudit = query.getResultList();


		List<Integer> userIds = null;
		if(!CollectionUtils.isEmpty(allUserIdsFromUsrAudit)) {

			userIds = new ArrayList<Integer>(allUserIdsFromUsrAudit.size());
			for(Object  temp :allUserIdsFromUsrAudit) {
				userIds.add(Integer.valueOf(String.valueOf(temp)));
			}
		}

		if(!CollectionUtils.isEmpty(allUserIdsFromUserRoldAudit)) {

			if(CollectionUtils.isEmpty(userIds))
					userIds = new ArrayList<Integer>(allUserIdsFromUserRoldAudit.size());

			for(Object  temp :allUserIdsFromUserRoldAudit) {
				userIds.add(Integer.valueOf(String.valueOf(temp)));
			}
		}
		return userIds;
	}


	@Transactional(readOnly = true)
	protected List<CommonAuditDto> getUserAuditReportFromUserAUD(Optional<String>   userId, MessageSource messageSource, Locale locale,String queryString) {

		Query query = null;
		if(userId != null) {
			query = entityManager.createNativeQuery(queryString);
			query.setParameter(queryParameter, userId.get());
		}else{
			query = entityManager.createNativeQuery(queryString);
		}

		return  this.parserAuditRecordsFromUserAUD(query, messageSource, locale);
	}




	@Transactional(readOnly = true)
	protected List<CommonAuditDto> getUserAuditReportFromUserRoleAUD(Optional<String>   userId,  MessageSource messageSource, Locale locale,String queryString) {
		String queryParameter = "userId";

		Query query = null;
		if(userId != null) {
			query = entityManager.createNativeQuery(queryString);
			query.setParameter(queryParameter, userId.get());
		}else{
			query = entityManager.createNativeQuery(queryString);
		}

		return  this.parserAuditRecordsFromUserRoleAUD(query, messageSource, locale);
	}



	private List<CommonAuditDto>  parserAuditRecords(Query query,MessageSource messageSource, Locale locale) {

		List<Object[]> auditList = query.getResultList();
		List<CommonAuditDto> auditDTOs = new ArrayList<CommonAuditDto>();
		CommonAuditDto auditDto = null;

		String actionName = null;
		if(!CollectionUtils.isEmpty(auditList)) {
			for(Object[] temp :auditList) {

				if (((BigDecimal)temp[1]).equals(new BigDecimal(0))) {
					actionName = messageSource.getMessage("i18n.audit.report.action.add", null, locale);
				}
				if (((BigDecimal)temp[5]).equals(new BigDecimal(1)))  {//role modified
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					auditDto.setNewValue(String.valueOf( temp[8]));
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[7]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.role", null, locale));
					auditDto.setUserID(String.valueOf( temp[9]));
					auditDTOs.add(auditDto);
				}
				if (((BigDecimal)temp[3]).equals(new BigDecimal(1))) {//user name modified
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					auditDto.setNewValue(String.valueOf( temp[2]));
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[7]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.name", null, locale));
					auditDto.setUserID(String.valueOf( temp[9]));
					auditDTOs.add(auditDto);
				}

				if (((BigDecimal)temp[11]).equals(new BigDecimal(1))) {//user activate status
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					if("1".equals(String.valueOf( temp[10]))){
						auditDto.setNewValue(messageSource.getMessage("i18n.user.status.activate", null, locale));
					}else {
						auditDto.setNewValue(messageSource.getMessage("i18n.user.status.inactivate", null, locale));
					}
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[7]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.status", null, locale));
					auditDto.setUserID(String.valueOf( temp[9]));
					auditDTOs.add(auditDto);
				}
			}
		}
		return auditDTOs;
	}

}
