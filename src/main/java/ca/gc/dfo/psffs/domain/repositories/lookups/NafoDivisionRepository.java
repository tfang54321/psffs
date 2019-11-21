package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.NafoDivision;
import org.springframework.stereotype.Repository;

@Repository
public interface NafoDivisionRepository extends BaseLookupRepository<NafoDivision, Integer>
{
}
