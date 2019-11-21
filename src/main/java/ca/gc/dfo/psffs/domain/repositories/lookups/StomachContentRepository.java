package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.StomachContent;
import org.springframework.stereotype.Repository;

@Repository
public interface StomachContentRepository extends BaseLookupRepository<StomachContent, Integer>
{
}
