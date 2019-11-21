package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.WeightConversionFactor;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightConversionFactorRepository extends BaseLookupRepository<WeightConversionFactor, Integer>
{
}
