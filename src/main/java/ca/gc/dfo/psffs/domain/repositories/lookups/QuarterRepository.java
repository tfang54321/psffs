package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Quarter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuarterRepository extends BaseLookupRepository<Quarter, Integer>
{
    @Query("select q from Quarter q where q.catchLocationId=:catchLocationId and q.activeFlagInd=1")
    List<Quarter> findAllByCatchLocationIdEqualsOrderByOrderAsc(Integer catchLocationId);
}
