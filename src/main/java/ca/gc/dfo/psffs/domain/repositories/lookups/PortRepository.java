package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Port;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends BaseLookupRepository<Port, Integer>
{
}
