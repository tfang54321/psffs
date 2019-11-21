package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.SamplingIdentifierGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SamplingIdentifierGeneratorRepository extends JpaRepository<SamplingIdentifierGenerator, Long>
{
    SamplingIdentifierGenerator findByYearEqualsAndInitialsEquals(Integer year, String initials);
}
