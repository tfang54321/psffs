package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SMPLNG_TRIP_SET_SPECIES", indexes = {
        @Index(columnList = "sampling_id", name = "FKIND_SMP_TSS_SAMPLING_ID"),
        @Index(columnList = "trip_nbr", name = "SMP_TSS_TRIP_NBR_INDEX"),
        @Index(columnList = "set_nbr", name = "SMP_TSS_SET_NBR_INDEX"),
        @Index(columnList = "sampled_species_id", name = "FKIND_SMP_TSS_SMP_SPECIES_ID"),
        @Index(columnList = "tss_sampling_status_id", name = "FKIND_SMP_TSS_SMP_STATUS_ID"),
        @Index(columnList = "observer_company_id", name = "FKIND_SMP_TSS_OBSV_COMP_ID"),
        @Index(columnList = "directed_species_id", name = "FKIND_SMP_TSS_DIR_SPECIES_ID"),
        @Index(columnList = "catch_category_id", name = "FKIND_SMP_TSS_CATCH_CAT_ID"),
        @Index(columnList = "catch_location_id", name = "FKIND_SMP_TSS_CATCH_LOC_ID"),
        @Index(columnList = "quarter_id", name = "FKIND_SMP_TSS_QUARTER_ID"),
        @Index(columnList = "country_id", name = "FKIND_SMP_TSS_COUNTRY_ID"),
        @Index(columnList = "length_unit_id", name = "FKIND_SMP_TSS_LEN_UNIT_ID"),
        @Index(columnList = "length_group_id", name = "FKIND_SMP_TSS_LEN_GRP_ID"),
        @Index(columnList = "gear_id", name = "FKIND_SMP_TSS_GEAR_ID"),
        @Index(columnList = "nafo_division_id", name = "FKIND_SMP_TSS_NAFO_DIV_ID"),
        @Index(columnList = "unit_area_id", name = "FKIND_SMP_TSS_UNIT_AREA_ID")
})
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TripSetSpecies extends BaseEntity implements SamplingTypeEntity
{
    @Id
    @GeneratedValue(generator = "tripSetSpeciesSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "tripSetSpeciesSeqGenerator", sequenceName = "SMPLNG_TRIP_SET_SPECIES_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "trip_set_species_id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampling_id", referencedColumnName = "sampling_id")
    private Sampling sampling;
    @Transient
    private String tssId;

    @Column(name = "trip_nbr", nullable = false)
    private Integer tripNumber;

    @Column(name = "set_nbr", nullable = false)
    private Integer setNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampled_species_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Species sampledSpecies;
    @Column(name = "sampled_species_id")
    private Integer sampledSpeciesId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "tss_sampling_status_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TSSSamplingStatus status;
    @Column(name = "tss_sampling_status_id", nullable = false)
    private Integer statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "observer_company_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ObserverCompany observerCompany;
    @Column(name = "observer_company_id")
    private Integer observerCompanyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "directed_species_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Species directedSpecies;
    @Column(name = "directed_species_id")
    private Integer directedSpeciesId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "catch_category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CatchCategory catchCategory;
    @Column(name = "catch_category_id")
    private Integer catchCategoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "catch_location_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CatchLocation catchLocation;
    @Column(name = "catch_location_id")
    private Integer catchLocationId;

    @Column(name = "catch_dt", nullable = false)
    private LocalDate catchDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "quarter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Quarter quarter;
    @Column(name = "quarter_id")
    private Integer quarterId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "country_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Country country;
    @Column(name = "country_id")
    private Integer countryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "length_unit_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LengthUnit lengthUnit;
    @Column(name = "length_unit_id")
    private Integer lengthUnitId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "length_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LengthGroup lengthGroup;
    @Column(name = "length_group_id")
    private Integer lengthGroupId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "gear_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Gear gear;
    @Column(name = "gear_id")
    private Integer gearId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "nafo_division_id", referencedColumnName = "id", insertable = false, updatable = false)
    private NafoDivision nafoDivision;
    @Column(name = "nafo_division_id")
    private Integer nafoDivisionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "unit_area_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UnitArea unitArea;
    @Column(name = "unit_area_id")
    private Integer unitAreaId;

    @Column(name = "depth_meters")
    private Float depthMeters;

    @Column(name = "comments", length = 4000)
    private String comments;

    @Column(name = "checked_by", length = 5)
    private String checkedBy;

    @Transient
    private Integer numberOfOtoliths;

    @Transient
    private Integer lengthGroupMin;

    @Transient
    private Integer lengthGroupMax;

    @PostLoad
    public void postLoad()
    {
        Sampling sampling = this.getSampling();
        if (sampling != null && sampling.getSamplingTypeId().equals(31)) {
            setNumberOfOtoliths(getSampling().getEntries().size());
            setTssId(sampling.getSamplingCode());
            setLengthGroupMin(sampling.getLengthGroupMin());
            setLengthGroupMax(sampling.getLengthGroupMax());
        }
    }

    @Override
    public Integer getCellSpeciesId()
    {
        return getSampledSpeciesId();
    }

    @Override
    public boolean isBycatch()
    {
        boolean answer = false;
        Integer sampleSpeciesId = getSampledSpeciesId();
        Integer directedSpeciesId = getDirectedSpeciesId();
        if (sampleSpeciesId != null && directedSpeciesId != null) {
            if (!sampleSpeciesId.equals(directedSpeciesId)) answer = true;
        }
        return answer;
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
