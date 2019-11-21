package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.MeasuringTechnique;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasuringTechniqueRepository extends BaseLookupRepository<MeasuringTechnique, Integer>
{
}
