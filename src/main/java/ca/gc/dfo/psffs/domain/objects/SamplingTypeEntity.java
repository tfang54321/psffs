package ca.gc.dfo.psffs.domain.objects;

import java.time.LocalDate;

public interface SamplingTypeEntity
{
    LocalDate getCreatedDate();
    Integer getCellSpeciesId();
    Sampling getSampling();
    boolean isBycatch();
    Integer getCountryId();
    Integer getQuarterId();
    Integer getNafoDivisionId();
    Integer getUnitAreaId();
    Integer getGearId();
}
