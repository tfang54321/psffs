package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.psffs.domain.objects.SystemSetting;
import ca.gc.dfo.psffs.domain.repositories.SystemSettingRepository;
import ca.gc.dfo.spring_commons.commons_web.api.Populator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingService implements Populator
{
    private Map<String, SystemSetting> settings;

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    public SettingService()
    {
        this.settings = new HashMap<>();
    }

    @Override
    public void populate()
    {
        this.settings = systemSettingRepository.findAll().stream()
                                                            .collect(Collectors.toMap(s->s.getSettingName(),s->s));
    }

    public Map<String, SystemSetting> getSettings()
    {
        return this.settings;
    }

    public String getSettingValueByName(String name)
    {
        String value = null;
        if (this.settings.containsKey(name)) {
            value = this.settings.get(name).getSettingValue();
        }
        return value;
    }

    public boolean updateSetting(String name, String value)
    {
        boolean success = true;
        SystemSetting setting = systemSettingRepository.findBySettingNameEquals(name);
        if (setting != null) {
            String actor = SecurityHelper.getNtPrincipal();
            setting.setActor(actor);
            setting.setSettingValue(value);

            try {
                systemSettingRepository.save(setting);
                this.settings.put(name, setting);
            } catch (Exception ex) {
                success = false;
            }
        }
        return success;
    }
}
