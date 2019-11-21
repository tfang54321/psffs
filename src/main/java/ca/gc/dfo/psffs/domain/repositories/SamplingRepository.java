package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.Sampling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SamplingRepository extends JpaRepository<Sampling, Long>
{
    List<Sampling> findAllBySamplingCodeEquals(String samplingCode);
}
