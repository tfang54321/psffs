package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.TripSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripSettingRepository extends BaseSamplingSettingRepository<TripSetting>
{
}
