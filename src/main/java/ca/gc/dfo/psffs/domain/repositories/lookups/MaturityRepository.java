package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.Maturity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaturityRepository extends BaseLookupRepository<Maturity, Integer>
{
    @Query("select m from Maturity m where m.legacyCode=:maturityLevel")
    List<Maturity> findAllByMaturityLevel(@Param("maturityLevel") String maturityLevel);

    List<Maturity> findAllBySexCodeIsNotNullOrderByOrderAsc();
}
