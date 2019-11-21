package ca.gc.dfo.psffs.forms;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class LengthFrequencyForm
{
    //HEADER
    private Long id;
    private String lfId;
    private Integer statusId;
    private LocalDate catchDate;
    private Integer portOfLandingId;
    private String vesselId;
    private String otherVesselDetails;
    private Integer directedSpeciesId;
    private Integer sampleSpeciesId;
    private String enteredBy;
    private String enteredByOther;
    private String localLfId;

    //CATCH TIME AND LOCATION
    private Integer catchLocationId;
    private String quarterData;
    private LocalTime catchTime;
    private Integer countryId;
    private Integer nafoDivisionId;
    private String unitAreaData;
    private Integer longitude;
    private Integer latitude;

    //SAMPLING REQUIREMENTS
    private Integer reqOtoliths;
    private Integer reqStomachs;
    private Integer reqFrozens;
    private Integer reqWeights;
    private Integer perNthOtoliths;
    private Integer perNthStomachs;
    private Integer perNthFrozens;
    private Integer perNthWeights;
    private Integer sexedOtoliths;
    private Integer sexedStomachs;
    private Integer sexedFrozens;
    private Integer sexedWeights;
    private Integer sexTypeId;
    private Integer lengthGroupId;
    private Integer lengthUnitId;
    private Integer measuringTechniqueId;

    //VESSEL DETAILS
    private String vesselComments;

    //CATCH DETAILS
    private Integer gearId;
    private Integer gearAmount;
    private Integer catchCategoryId;
    private Float meshSizeMillimeters;
    private Float depthMeters;
    private String conversionFactorData;
    private Float turnoutWeightKilograms;
    private Float sampleWeightKilograms;
    private Float soakTimeHours;
    private String catchDetailsComments;

    //REVIEW
    private String comments;
    private String verifiedBy;
    private String checkedBy;

    //LENGTH FREQUENCY SUMMARY
    private Integer lengthGroupMin;
    private Integer lengthGroupMax;
    private Integer[] m; //m
    private Integer[] f; //f
    private Integer[] u; //u

    private Integer[] m_o; //m otolith
    private Integer[] m_s; //m stomach
    private Integer[] m_c; //m frozen
    private Integer[] m_w; //m weight

    private Integer[] f_o; //f otolith
    private Integer[] f_s; //f stomach
    private Integer[] f_c; //f frozen
    private Integer[] f_w; //f weight

    private Integer[] u_o; //u otolith
    private Integer[] u_s; //u stomach
    private Integer[] u_c; //u frozen
    private Integer[] u_w; //u weight
    private Boolean countsModified;
    private Boolean entriesModified;

    private SamplingEntryForm[] entries;

    public LengthFrequencyForm()
    {
        setStatusId(1);
        setCountsModified(false);
        setEntriesModified(false);
    }
}
