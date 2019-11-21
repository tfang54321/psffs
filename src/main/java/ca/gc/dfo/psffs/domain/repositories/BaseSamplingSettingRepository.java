package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.BaseSamplingSetting;
import ca.gc.dfo.psffs.domain.objects.TripSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface BaseSamplingSettingRepository<T extends BaseSamplingSetting> extends JpaRepository<T, Long>
{
    T getById(Long id);

    T getByYearAndSpeciesId(Integer year, Integer species);
}
