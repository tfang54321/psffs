package ca.gc.dfo.psffs.forms;

import lombok.Data;

@Data
public abstract class AbstractCellDataForm
{
    //Required cell criteria
    private Integer year;
    private Integer sampleSpeciesId;

    //Cell Criteria
    private Integer dataSourceId;
    private Integer bycatchInd;
    private Integer countryId;//Quota
    private Integer quarterId;
    private Integer nafoDivisionId;
    private String unitAreaData;
    private Integer vesselLengthCategoryId;
    private Integer gearId;
    private Float meshSizeMillimeters;
    private Integer fleetSectorId;
    private Integer observerCompanyId;
}
