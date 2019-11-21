package ca.gc.dfo.psffs.forms;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TripSetSpeciesForm
{
    private Long id;
    private String tssId;
    private Integer tripNumber;
    private Integer setNumber;
    private Integer sampledSpeciesId;
    private Integer statusId;
    private Integer observerCompanyId;
    private Integer directedSpeciesId;
    private Integer catchCategoryId;
    private Integer catchLocationId;
    private LocalDate catchDate;
    private String quarterData;
    private Integer countryId;
    private Integer lengthUnitId;
    private Integer lengthGroupId;
    private Integer gearId;
    private Integer nafoDivisionId;
    private String unitAreaData;
    private Float depthMeters;

    private String comments;
    private String checkedBy;

    private Integer lengthGroupMin;
    private Integer lengthGroupMax;

    private Integer[] u_o;
    private Integer[] m_o;
    private Integer[] f_o;

    private Boolean entriesModified;

    public Boolean isEntriesModified()
    {
        return this.entriesModified != null ? this.entriesModified : false;
    }

    public TripSetSpeciesForm()
    {
        setEntriesModified(false);
    }
}
