package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.CatchCategory;
import org.springframework.stereotype.Repository;

@Repository
public interface CatchCategoryRepository extends BaseLookupRepository<CatchCategory, Integer>
{
}
