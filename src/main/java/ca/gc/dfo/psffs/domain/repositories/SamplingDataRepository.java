package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.SamplingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SamplingDataRepository extends JpaRepository<SamplingData, Long>, JpaSpecificationExecutor<SamplingData>, QuerydslPredicateExecutor<SamplingData>
{
}
