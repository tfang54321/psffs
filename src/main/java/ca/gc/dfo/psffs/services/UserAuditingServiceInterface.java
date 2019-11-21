package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.audit.CommonAuditDto;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


/**
 * @author FANGW
 */
public interface UserAuditingServiceInterface {
    public List<CommonAuditDto> getAuditReportsForAllUsers(MessageSource messageSource, Locale locale);

    public List<CommonAuditDto> getUserAuditReport(Optional<String> userId, MessageSource messageSource, Locale locale);

    public List<Integer> getAllUserID();
}
