package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Gear;
import org.springframework.stereotype.Repository;

@Repository
public interface GearRepository extends BaseLookupRepository<Gear, Integer>
{
}
