package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.*;
import com.querydsl.core.annotations.QueryInit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SAMPLING_DATA", indexes = {
        @Index(columnList = "field_nbr", name = "SAMPLING_DATA_FIELD_NBR_INDEX"),
        @Index(columnList = "sampling_id", name = "FKIND_SMP_DATA_SMPLNG_ID"),
        @Index(columnList = "otolith_edge_id", name = "FKIND_SMP_DATA_OTO_EDGE_ID"),
        @Index(columnList = "otolith_reliability_id", name = "FKIND_SMP_DATA_OTO_RLBTY_ID"),
        @Index(columnList = "primary_stomach_content_id", name = "FKIND_SMP_DATA_PRM_STOM_CNT_ID"),
        @Index(columnList = "secondary_stomach_content_id", name = "FKIND_SMP_DATA_SCN_STOM_CNT_ID"),
        @Index(columnList = "parasite_id", name = "FKIND_SMP_DATA_PARASITE_ID"),
        @Index(columnList = "sampling_data_sts_id", name = "FKIND_SMP_DATA_STS_ID")
})
@Data
@ToString(exclude = {"samplingEntry", "sampling"})
@EqualsAndHashCode(exclude = {"samplingEntry", "sampling"})
public class SamplingData
{
    @Id
    @GeneratedValue(generator = "samplingDataSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "samplingDataSeqGenerator", sequenceName = "SAMPLING_DATA_ID_SEQ", allocationSize = 1, initialValue = 1000)
    @Column(name = "sampling_data_id")
    private Long samplingDataId;

    @Column(name = "storage_nbr")
    private Long storageNbr;

    @Column(name = "field_nbr")
    private String fieldNbr;

    @Column(name = "tag_nbr", length = 10)
    private String tag;

    @Column(name = "sampling_data_sts_id")
    private Integer statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampling_data_sts_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SamplingDataStatus status;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "samplingData")
    private SamplingEntry samplingEntry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sampling_id", referencedColumnName = "sampling_id")
    @QueryInit({
            "lengthFrequency.sampleSpecies", "lengthFrequency.directedSpecies", "lengthFrequency.country",
            "lengthFrequency.quarter", "lengthFrequency.nafoDivision", "lengthFrequency.unitArea",
            "lengthFrequency.vessel", "lengthFrequency.vessel.lengthCategory", "lengthFrequency.vessel.fleetSector",
            "lengthFrequency.gear", "cell", "tripSetSpecies"
    })
    private Sampling sampling;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "otolith_edge_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OtolithEdge otolithEdge;
    @Column(name = "otolith_edge_id")
    private Integer otolithEdgeId;
    @Transient
    private String otolithEdgeCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "otolith_reliability_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OtolithReliability otolithReliability;
    @Column(name = "otolith_reliability_id")
    private Integer otolithReliabilityId;
    @Transient
    private String otolithReliabilityCode;

    @Column(name = "age")
    private Integer age;

    @Column(name = "egg_diameter_mm")
    private Integer eggDiameter;

    @Column(name = "round_weight")
    private Float roundWt;

    @Column(name = "gutted_weight")
    private Float guttedWt;

    @Column(name = "gutted_gilled_weight")
    private Float ggWt;

    @Column(name = "liver_weight")
    private Integer liverWt;

    @Column(name = "stomach_weight")
    private Integer stomachWt;

    @Column(name = "guts_weight")
    private Integer gutsWt;

    @Column(name = "gonad_weight")
    private Integer gonadWt;

    @Column(name = "stomach_fullness")
    private Integer fullness;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "primary_stomach_content_id", referencedColumnName = "id", insertable = false, updatable = false)
    private StomachContent primaryStomachContent;
    @Column(name = "primary_stomach_content_id")
    private Integer primaryStomachContentId;
    @Transient
    private String primaryStomachContentCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "secondary_stomach_content_id", referencedColumnName = "id", insertable = false, updatable = false)
    private StomachContent secondaryStomachContent;
    @Column(name = "secondary_stomach_content_id")
    private Integer secondaryStomachContentId;
    @Transient
    private String secondaryStomachContentCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "parasite_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Parasite parasite;
    @Column(name = "parasite_id")
    private Integer parasiteId;
    @Transient
    private String parasiteCode;

    @Column(name = "nbr_of_parasites")
    private Integer nbrOfParasites;

    @Column(name = "mod_by", length = 100)
    private String modifiedBy;

    @Column(name = "mod_dt")
    private LocalDate modifiedDate;

    @PostLoad
    public void postLoad()
    {
        OtolithEdge otolithEdge = getOtolithEdge();
        setOtolithEdgeCode(otolithEdge != null ? otolithEdge.getLegacyCode() : null);
        OtolithReliability otolithReliability = getOtolithReliability();
        setOtolithReliabilityCode(otolithReliability != null ? otolithReliability.getLegacyCode() : null);
        StomachContent primaryStomachContent = getPrimaryStomachContent();
        setPrimaryStomachContentCode(primaryStomachContent != null ? primaryStomachContent.getLegacyCode() : null);
        StomachContent secondaryStomachContent = getSecondaryStomachContent();
        setSecondaryStomachContentCode(secondaryStomachContent != null ? secondaryStomachContent.getLegacyCode() : null);
        Parasite parasite = getParasite();
        setParasiteCode(parasite != null ? parasite.getLegacyCode() : null);
    }

    @PrePersist
    @PreUpdate
    public void setDefaults()
    {
        if (this.statusId == null) {
            this.statusId = SamplingDataStatus.ENTERED_STS_ID;
        }
    }

    public void setActor(String actor)
    {
        setModifiedBy(actor);
        setModifiedDate(LocalDate.now());
    }
}
