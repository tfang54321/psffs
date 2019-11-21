package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.TripSetting;
import ca.gc.dfo.psffs.domain.repositories.TripSettingRepository;
import ca.gc.dfo.psffs.forms.TripSettingForm;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TripSettingService extends AbstractSamplingSettingService<TripSetting, TripSettingRepository, TripSettingForm>
{
    @Override
    protected Class<TripSetting> chosenEntity()
    {
        return TripSetting.class;
    }

    @Override
    protected Class<TripSettingRepository> chosenRepository()
    {
        return TripSettingRepository.class;
    }

    @Override
    protected Class<TripSettingForm> chosenForm()
    {
        return TripSettingForm.class;
    }
}
