package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Tonnage;
import org.springframework.stereotype.Repository;

@Repository
public interface TonnageRepository extends BaseLookupRepository<Tonnage, Integer>
{
}
