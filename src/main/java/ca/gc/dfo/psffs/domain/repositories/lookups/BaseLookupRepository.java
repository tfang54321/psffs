package ca.gc.dfo.psffs.domain.repositories.lookups;

import ca.gc.dfo.psffs.domain.objects.lookups.BaseLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseLookupRepository<T extends BaseLookup, ID> extends JpaRepository<T, ID>
{
    List<T> findAllByActiveFlagIndTrueOrderByOrderAsc();
    List<T> findAllByOrderByOrderAsc();
}
