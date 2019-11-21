package ca.gc.dfo.psffs.controllers.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.LengthGroup;
import ca.gc.dfo.psffs.domain.repositories.lookups.BaseLookupRepository;
import ca.gc.dfo.psffs.services.CacheService;
import ca.gc.dfo.psffs.services.ChecksumService;
import ca.gc.dfo.psffs.services.LookupService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

@Getter
public class BaseLookupController
{
    @Autowired
    private LookupService lookupService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ChecksumService checksumService;

    protected void populateCommonListModel(ModelMap model, String listDataPath, String editPath, String activatePath)
    {
        String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
        model.addAttribute("listDataTHPath", "/"+pageLang + listDataPath);
        model.addAttribute("editTHPath", "/"+pageLang + editPath);
        model.addAttribute("activateTHPath", activatePath);
    }

    protected String toggleActiveFlag(Integer id, Boolean active, BaseLookupRepository lookupRepo,
                                      String lookupKey, HttpServletRequest request)
    {
        String status;
        boolean success;
        try {
            success = lookupService.toggleLookupActive(id, lookupRepo, lookupKey, active);
        } catch (Exception ex) {
            success = false;
            if (!(ex instanceof AccessDeniedException)) {
                logger.error("An error occurred while trying to toggle lookup active flag: " + ex.getMessage(), ex);
            } else {
                logger.debug("Failed to toggle lookup active flag: access denied");
            }
        }
        if (success) {
            status = "success";
            cacheService.initCacheSessionUUID(request, false);
            checksumService.updateChecksumForObjectByEntityClass(LengthGroup.class);
        } else {
            status = "fail";
        }
        return status;
    }

    private static final Logger logger = LoggerFactory.getLogger(BaseLookupController.class);
}
