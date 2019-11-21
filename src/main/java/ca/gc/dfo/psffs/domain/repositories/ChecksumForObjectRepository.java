package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.ChecksumForObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecksumForObjectRepository extends JpaRepository<ChecksumForObject, Integer>
{
    ChecksumForObject findByObjectStoreNameEquals(String objectStoreName);
}
