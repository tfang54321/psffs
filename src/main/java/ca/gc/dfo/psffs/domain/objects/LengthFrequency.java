package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "SMPLNG_LENGTH_FREQUENCY", indexes = {
        @Index(columnList = "sampling_id", name = "FKIND_SMP_LF_SAMPLING_ID"),
        @Index(columnList = "sampling_status_id", name = "FKIND_SMP_LF_STATUS_ID"),
        @Index(columnList = "port_of_landing_id", name = "FKIND_SMP_LF_PORT_ID"),
        @Index(columnList = "vessel_id", name = "FKIND_SMP_LF_VESSEL_ID"),
        @Index(columnList = "directed_species_id", name = "FKIND_SMP_LF_DIR_SPECIES_ID"),
        @Index(columnList = "sample_species_id", name = "FKIND_SMP_LF_SMP_SPECIES_ID"),
        @Index(columnList = "catch_location_id", name = "FKIND_SMP_LF_CATCH_LOC_ID"),
        @Index(columnList = "quarter_id", name = "FKIND_SMP_LF_QUARTER_ID"),
        @Index(columnList = "country_id", name = "FKIND_SMP_LF_COUNTRY_ID"),
        @Index(columnList = "nafo_division_id", name = "FKIND_SMP_LF_NAFO_DIV_ID"),
        @Index(columnList = "unit_area_id", name = "FKIND_SMP_LF_UNIT_AREA_ID"),
        @Index(columnList = "sex_type_id", name = "FKIND_SMP_LF_SEX_TYPE_ID"),
        @Index(columnList = "length_group_id", name = "FKIND_SMP_LF_LENGTH_GROUP_ID"),
        @Index(columnList = "length_unit_id", name = "FKIND_SMP_LF_LENGTH_UNIT_ID"),
        @Index(columnList = "measuring_technique_id", name = "FKIND_SMP_LF_MEAS_TECH_ID"),
        @Index(columnList = "gear_id", name = "FKIND_SMP_LF_GEAR_ID"),
        @Index(columnList = "catch_category_id", name = "FKIND_SMP_LF_CATCH_CAT_ID"),
        @Index(columnList = "wght_conv_factor_id", name = "FKIND_SMP_LF_WHT_CNV_FCTR_ID")
})
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LengthFrequency extends BaseEntity implements SamplingTypeEntity
{
    //HEADER
    @Id
    @GeneratedValue(generator = "lengthFrequencySeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "lengthFrequencySeqGenerator", sequenceName = "SMPLNG_LENGTH_FREQUENCY_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "length_frequency_id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampling_id", referencedColumnName = "sampling_id")
    private Sampling sampling;
    @Transient
    private String lfId;
    @Transient
    private String localLfId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampling_status_id", referencedColumnName = "id", nullable = false)
    private SamplingStatus status;
    @Transient
    private Integer statusId;

    @Column(name = "frequency_nbr", nullable = false)
    private Integer frequencyNumber;

    @Column(name = "catch_dt", nullable = false)
    private LocalDate catchDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "port_of_landing_id", referencedColumnName = "id")
    private Port portOfLanding;
    @Transient
    private Integer portOfLandingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "vessel_id", referencedColumnName = "id")
    private Vessel vessel;
    @Transient
    private Integer vesselId;

    @Column(name = "other_vessel_details")
    private String otherVesselDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "directed_species_id", referencedColumnName = "id")
    private Species directedSpecies;
    @Transient
    private Integer directedSpeciesId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sample_species_id", referencedColumnName = "id")
    private Species sampleSpecies;
    @Transient
    private Integer sampleSpeciesId;

    @Column(name = "entered_by", nullable = false, length = 5)
    private String enteredBy;

    @Column(name = "entered_by_other", length = 5)
    private String enteredByOther;

    //CATCH TIME AND LOCATION
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "catch_location_id", referencedColumnName = "id")
    private CatchLocation catchLocation;
    @Transient
    private Integer catchLocationId;

    @Column(name = "catch_time")
    private LocalTime catchTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "quarter_id", referencedColumnName = "id")
    private Quarter quarter;
    @Transient
    private Integer quarterId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Country country;
    @Transient
    private Integer countryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "nafo_division_id", referencedColumnName = "id")
    private NafoDivision nafoDivision;
    @Transient
    private Integer nafoDivisionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "unit_area_id", referencedColumnName = "id")
    private UnitArea unitArea;
    @Transient
    private Integer unitAreaId;

    @Column(name = "longitude")
    private Integer longitude;

    @Column(name = "latitude")
    private Integer latitude;

    //SAMPLING REQUIREMENTS
    @Column(name = "req_nbr_otoliths")
    private Integer reqOtoliths;

    @Column(name = "req_nbr_stomachs")
    private Integer reqStomachs;

    @Column(name = "req_nbr_frozens")
    private Integer reqFrozens;

    @Column(name = "req_nbr_weights")
    private Integer reqWeights;

    @Column(name = "per_nth_otoliths")
    private Integer perNthOtoliths;

    @Column(name = "per_nth_stomachs")
    private Integer perNthStomachs;

    @Column(name = "per_nth_frozens")
    private Integer perNthFrozens;

    @Column(name = "per_nth_weights")
    private Integer perNthWeights;

    @Column(name = "sexed_otoliths_ind")
    private Integer sexedOtoliths;

    @Column(name = "sexed_stomachs_ind")
    private Integer sexedStomachs;

    @Column(name = "sexed_frozens_ind")
    private Integer sexedFrozens;

    @Column(name = "sexed_weights_ind")
    private Integer sexedWeights;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sex_type_id", referencedColumnName = "id")
    private SexType sexType;
    @Transient
    private Integer sexTypeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "length_group_id", referencedColumnName = "id")
    private LengthGroup lengthGroup;
    @Transient
    private Integer lengthGroupId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "length_unit_id", referencedColumnName = "id")
    private LengthUnit lengthUnit;
    @Transient
    private Integer lengthUnitId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "measuring_technique_id", referencedColumnName = "id")
    private MeasuringTechnique measuringTechnique;
    @Transient
    private Integer measuringTechniqueId;

    //VESSEL DETAILS
    @Column(name = "vessel_comments", length = 4000)
    private String vesselComments;

    //CATCH DETAILS
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "gear_id", referencedColumnName = "id")
    private Gear gear;
    @Transient
    private Integer gearId;

    @Column(name = "gear_amount_nbr")
    private Integer gearAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "catch_category_id", referencedColumnName = "id")
    private CatchCategory catchCategory;
    @Transient
    private Integer catchCategoryId;

    @Column(name = "mesh_size_mm")
    private Float meshSizeMillimeters;

    @Column(name = "depth_meters")
    private Float depthMeters;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "wght_conv_factor_id", referencedColumnName = "id")
    private WeightConversionFactor conversionFactor;
    @Transient
    private Integer conversionFactorId;

    @Column(name = "turnout_wght_kg")
    private Float turnoutWeightKilograms;

    @Column(name = "sample_wght_kg")
    private Float sampleWeightKilograms;

    @Column(name = "soak_time_hrs")
    private Float soakTimeHours;

    @Column(name = "catch_dtls_comments", length = 4000)
    private String catchDetailsComments;

    //REVIEW
    @Column(name = "lf_comments", length = 4000)
    private String comments;

    @Column(name = "verified_by", length = 5)
    private String verifiedBy;

    @Column(name = "checked_by", length = 5)
    private String checkedBy;

    //LENGTH FREQUENCY SUMMARY
    @Transient
    private Integer lengthGroupMin;

    @Transient
    private Integer lengthGroupMax;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "lengthFrequency")
    @JsonIgnore
    private List<LengthFrequencyCount> lengthFrequencyCounts;

    @Transient
    private Integer[] u;
    @Transient
    private Integer[] m;
    @Transient
    private Integer[] f;

    @Transient
    private Integer[] u_o;
    @Transient
    private Integer[] u_s;
    @Transient
    private Integer[] u_c;
    @Transient
    private Integer[] u_w;

    @Transient
    private Integer[] m_o;
    @Transient
    private Integer[] m_s;
    @Transient
    private Integer[] m_c;
    @Transient
    private Integer[] m_w;

    @Transient
    private Integer[] f_o;
    @Transient
    private Integer[] f_s;
    @Transient
    private Integer[] f_c;
    @Transient
    private Integer[] f_w;

    @PostLoad
    public void postLoad()
    {
        Sampling sampling = getSampling();
        if (sampling != null && sampling.getSamplingTypeId().equals(30)) {
            setLfId(sampling.getSamplingCode());
            setLocalLfId(sampling.getLocalSamplingCode());
            setLengthGroupMin(sampling.getLengthGroupMin());
            setLengthGroupMax(sampling.getLengthGroupMax());

            setStatusId(getStatus().getId());
            Port portOfLanding = getPortOfLanding();
            setPortOfLandingId(portOfLanding != null ? portOfLanding.getId() : null);
            Vessel vessel = getVessel();
            setVesselId(vessel != null ? vessel.getId() : null);
            setDirectedSpeciesId(getDirectedSpecies().getId());
            setSampleSpeciesId(getSampleSpecies().getId());
            CatchLocation catchLocation = getCatchLocation();
            setCatchLocationId(catchLocation != null ? catchLocation.getId() : null);
            Country country = getCountry();
            setCountryId(country != null ? country.getId() : null);
            Quarter quarter = getQuarter();
            setQuarterId(quarter != null ? quarter.getId() : null);
            NafoDivision nafoDivision = getNafoDivision();
            setNafoDivisionId(nafoDivision != null ? nafoDivision.getId() : null);
            UnitArea unitArea = getUnitArea();
            setUnitAreaId(unitArea != null ? unitArea.getId() : null);
            SexType sexType = getSexType();
            setSexTypeId(sexType != null ? sexType.getId() : null);
            LengthGroup lengthGroup = getLengthGroup();
            setLengthGroupId(lengthGroup != null ? lengthGroup.getId() : null);
            LengthUnit lengthUnit = getLengthUnit();
            setLengthUnitId(lengthUnit != null ? lengthUnit.getId() : null);
            MeasuringTechnique measuringTechnique = getMeasuringTechnique();
            setMeasuringTechniqueId(measuringTechnique != null ? measuringTechnique.getId() : null);
            Gear gear = getGear();
            setGearId(gear != null ? gear.getId() : null);
            CatchCategory catchCategory = this.getCatchCategory();
            setCatchCategoryId(catchCategory != null ? catchCategory.getId() : null);
            WeightConversionFactor weightConversionFactor = getConversionFactor();
            setConversionFactorId(weightConversionFactor != null ? weightConversionFactor.getId() : null);

            setU(getCountsBySampleTypeIdAndSexCode(21, "U"));
            setM(getCountsBySampleTypeIdAndSexCode(21, "M"));
            setF(getCountsBySampleTypeIdAndSexCode(21, "F"));

            setU_o(getCountsBySampleTypeIdAndSexCode(22, "U"));
            setU_s(getCountsBySampleTypeIdAndSexCode(23, "U"));
            setU_c(getCountsBySampleTypeIdAndSexCode(24, "U"));
            setU_w(getCountsBySampleTypeIdAndSexCode(25, "U"));

            setM_o(getCountsBySampleTypeIdAndSexCode(22, "M"));
            setM_s(getCountsBySampleTypeIdAndSexCode(23, "M"));
            setM_c(getCountsBySampleTypeIdAndSexCode(24, "M"));
            setM_w(getCountsBySampleTypeIdAndSexCode(25, "M"));

            setF_o(getCountsBySampleTypeIdAndSexCode(22, "F"));
            setF_s(getCountsBySampleTypeIdAndSexCode(23, "F"));
            setF_c(getCountsBySampleTypeIdAndSexCode(24, "F"));
            setF_w(getCountsBySampleTypeIdAndSexCode(25, "F"));
        }
    }

    private Integer[] getCountsBySampleTypeIdAndSexCode(Integer sampleTypeId, String sexCode)
    {
        List<Integer> counts = new ArrayList<>();
        List<LengthFrequencyCount> lfcList = this.lengthFrequencyCounts.stream()
                .filter(lfc -> lfc.getSex().toUpperCase().equals(sexCode))
                .filter(lfc -> lfc.getSampleType().getId().equals(sampleTypeId))
                .collect(Collectors.toList());
        while (counts.size() <= lengthGroupMax) counts.add(0);
        lfcList.forEach(lfc -> counts.set(lfc.getLength(), lfc.getCount()));
        return counts.toArray(new Integer[counts.size()]);
    }

    @Override
    public Integer getCellSpeciesId()
    {
        return getSampleSpeciesId();
    }

    @Override
    public boolean isBycatch()
    {
        boolean answer = false;
        Integer sampleSpeciesId = getSampleSpeciesId();
        Integer directedSpeciesId = getDirectedSpeciesId();
        if (sampleSpeciesId != null && directedSpeciesId != null) {
            if (!sampleSpeciesId.equals(directedSpeciesId)) answer = true;
        }
        return answer;
    }

    public void setStatus(SamplingStatus status)
    {
        this.status = status;
        setStatusId(status.getId());
    }

    public void setPortOfLanding(Port portOfLanding)
    {
        this.portOfLanding = portOfLanding;
        setPortOfLandingId(portOfLanding != null ? portOfLanding.getId() : null);
    }

    public void setVessel(Vessel vessel)
    {
        this.vessel = vessel;
        setVesselId(vessel != null ? vessel.getId() : null);
    }

    public void setDirectedSpecies(Species directedSpecies)
    {
        this.directedSpecies = directedSpecies;
        setDirectedSpeciesId(directedSpecies.getId());
    }

    public void setSampleSpecies(Species sampleSpecies)
    {
        this.sampleSpecies = sampleSpecies;
        setSampleSpeciesId(sampleSpecies.getId());
    }

    public void setCatchLocation(CatchLocation catchLocation)
    {
        this.catchLocation = catchLocation;
        setCatchLocationId(catchLocation != null ? catchLocation.getId() : null);
    }

    public void setCountry(Country country)
    {
        this.country = country;
        setCountryId(country != null ? country.getId() : null);
    }

    public void setQuarter(Quarter quarter)
    {
        this.quarter = quarter;
        setQuarterId(quarter != null ? quarter.getId() : null);
    }

    public void setNafoDivision(NafoDivision nafoDivision)
    {
        this.nafoDivision = nafoDivision;
        setNafoDivisionId(nafoDivision != null ? nafoDivision.getId() : null);
    }

    public void setUnitArea(UnitArea unitArea)
    {
        this.unitArea = unitArea;
        setUnitAreaId(unitArea != null ? unitArea.getId() : null);
    }

    public void setSexType(SexType sexType)
    {
        this.sexType = sexType;
        setSexTypeId(sexType != null ? sexType.getId() : null);
    }

    public void setLengthGroup(LengthGroup lengthGroup)
    {
        this.lengthGroup = lengthGroup;
        setLengthGroupId(lengthGroup != null ? lengthGroup.getId() : null);
    }

    public void setLengthUnit(LengthUnit lengthUnit)
    {
        this.lengthUnit = lengthUnit;
        setLengthUnitId(lengthUnit != null ? lengthUnit.getId() : null);
    }

    public void setGear(Gear gear)
    {
        this.gear = gear;
        setGearId(gear != null ? gear.getId() : null);
    }

    public void setCatchCategory(CatchCategory catchCategory)
    {
        this.catchCategory = catchCategory;
        setCatchCategoryId(catchCategory != null ? catchCategory.getId() : null);
    }

    public void setConversionFactor(WeightConversionFactor conversionFactor)
    {
        this.conversionFactor = conversionFactor;
        setConversionFactorId(conversionFactor != null ? conversionFactor.getId() : null);
    }

    public void setLengthGroupMin(Integer lengthGroupMin)
    {
        this.lengthGroupMin = lengthGroupMin;
        if (this.getSampling() != null) {
            this.getSampling().setLengthGroupMin(lengthGroupMin);
        }
    }

    public void setLengthGroupMax(Integer lengthGroupMax)
    {
        this.lengthGroupMax = lengthGroupMax;
        if (this.getSampling() != null) {
            this.getSampling().setLengthGroupMax(lengthGroupMax);
        }
    }
}
