package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.SexCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SexCodeRepository extends JpaRepository<SexCode, Integer>
{
}
