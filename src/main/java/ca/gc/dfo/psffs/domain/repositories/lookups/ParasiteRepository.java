package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Parasite;
import org.springframework.stereotype.Repository;

@Repository
public interface ParasiteRepository extends BaseLookupRepository<Parasite, Integer>
{
}
