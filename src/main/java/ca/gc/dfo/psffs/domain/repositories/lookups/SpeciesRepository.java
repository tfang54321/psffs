package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Species;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository extends BaseLookupRepository<Species, Integer>
{
}
