package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Vessel;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselRepository extends BaseLookupRepository<Vessel, Integer>
{
}
