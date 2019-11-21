package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.controllers.ChecksumObjectController;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.AbstractCacheService;
import org.springframework.stereotype.Service;

@Service
public class CacheService extends AbstractCacheService
{
    public CacheService()
    {
        //Provide additional paths for the CacheService to ignore
        super(ChecksumObjectController.CHECKSUM_OBJECTS_PATH);
    }
}
