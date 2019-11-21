package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.ObserverSetting;
import ca.gc.dfo.psffs.domain.objects.TripSetting;
import ca.gc.dfo.psffs.domain.repositories.ObserverSettingRepository;
import ca.gc.dfo.psffs.domain.repositories.TripSettingRepository;
import ca.gc.dfo.psffs.forms.ObserverSettingForm;
import ca.gc.dfo.psffs.forms.TripSettingForm;
import org.springframework.stereotype.Service;

@Service
public class ObserverSettingService extends AbstractSamplingSettingService<ObserverSetting, ObserverSettingRepository, ObserverSettingForm>
{
    @Override
    protected Class<ObserverSetting> chosenEntity()
    {
        return ObserverSetting.class;
    }

    @Override
    protected Class<ObserverSettingRepository> chosenRepository()
    {
        return ObserverSettingRepository.class;
    }

    @Override
    protected Class<ObserverSettingForm> chosenForm()
    {
        return ObserverSettingForm.class;
    }
}
