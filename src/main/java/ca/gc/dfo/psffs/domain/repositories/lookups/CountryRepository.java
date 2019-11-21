package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Country;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends BaseLookupRepository<Country, Integer>
{
}
