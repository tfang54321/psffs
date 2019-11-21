package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.SexType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SexTypeRepository extends JpaRepository<SexType, Integer>
{
}
