package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.LengthCategory;
import org.springframework.stereotype.Repository;

@Repository
public interface LengthCategoryRepository extends BaseLookupRepository<LengthCategory, Integer>
{
}
