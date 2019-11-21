package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.Maturity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryInit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SAMPLING_ENTRY", indexes = {
        @Index(columnList = "sex_cd", name = "SAMPLING_ENTRY_SEX_CD_INDEX"),
        @Index(columnList = "length_group", name = "SAMPLING_ENTRY_LEN_GRP_INDEX"),
        @Index(columnList = "maturity_id", name = "FKIND_SMP_ENTRY_MATURITY_ID"),
        @Index(columnList = "sampling_data_id", name = "FKIND_SMP_ENTRY_SMP_DATA_ID")
})
@Data
@ToString(exclude = "sampling")
@EqualsAndHashCode(exclude = "sampling")
public class SamplingEntry
{
    @Id
    @GeneratedValue(generator = "samplingEntrySeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "samplingEntrySeqGenerator", sequenceName = "SAMPLING_ENTRY_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "smp_entry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sampling_id", referencedColumnName = "sampling_id")
    @JsonIgnore
    @QueryInit({
            "lengthFrequency.sampleSpecies", "lengthFrequency.directedSpecies", "lengthFrequency.country",
            "lengthFrequency.quarter", "lengthFrequency.nafoDivision", "lengthFrequency.unitArea",
            "lengthFrequency.vessel", "lengthFrequency.vessel.lengthCategory", "lengthFrequency.vessel.fleetSector",
            "lengthFrequency.gear", "cell", "tripSetSpecies"
    })
    private Sampling sampling;

    @Column(name = "length_group", nullable = false)
    private Integer length;

    @Column(name = "sex_cd", length = 1, nullable = false)
    private String sex;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "maturity_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Maturity maturity;
    @Column(name = "maturity_id", nullable = false)
    @ColumnDefault("0")
    private Integer maturityId;
    @Transient
    private String maturityLevel;

    @Column(name = "length_sampled_ind")
    @JsonIgnore
    private Boolean lengthSampledInd;

    @Column(name = "otolith_sampled_ind")
    @JsonIgnore
    private Boolean otolithSampledInd;

    @Column(name = "stomach_sampled_ind")
    @JsonIgnore
    private Boolean stomachSampledInd;

    @Column(name = "frozen_sampled_ind")
    @JsonIgnore
    private Boolean frozenSampledInd;

    @Column(name = "weight_sampled_ind")
    @JsonIgnore
    private Boolean weightSampledInd;

    @Column(name = "active_flag", nullable = false)
    @JsonIgnore
    private Boolean activeFlagInd;

    @Column(name = "mod_by", length = 100)
    private String modifiedBy;

    @Column(name = "mod_dt")
    private LocalDate modifiedDate;

    @Column(name = "presentation_order")
    private Integer presOrder;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampling_data_id", referencedColumnName = "sampling_data_id")
    @QueryInit("status")
    private SamplingData samplingData;

    @PostLoad
    public void postLoad()
    {
        Maturity maturity = getMaturity();
        if (maturity != null) setMaturityLevel(maturity.getLegacyCode());
    }

    @PreUpdate
    @PrePersist
    public void prePersist()
    {
        if (maturityId == null) maturityId = 0;
    }

    public List<String> getSamplings()
    {
        List<String> samplingList = new ArrayList<>();
        if (lengthSampledInd != null && lengthSampledInd) samplingList.add("l");
        if (otolithSampledInd != null && otolithSampledInd) samplingList.add("o");
        if (stomachSampledInd != null && stomachSampledInd) samplingList.add("s");
        if (frozenSampledInd != null && frozenSampledInd) samplingList.add("c");
        if (weightSampledInd != null && weightSampledInd) samplingList.add("w");
        return samplingList;
    }

    public void setSamplings(List<String> samplings)
    {
        if (samplings != null && samplings.size() > 0) {
            setLengthSampledInd(samplings.contains("l"));
            setOtolithSampledInd(samplings.contains("o"));
            setStomachSampledInd(samplings.contains("s"));
            setFrozenSampledInd(samplings.contains("c"));
            setWeightSampledInd(samplings.contains("w"));
        }
    }

    public boolean usesStorageNumber()
    {
        List<String> samplings = getSamplings();
        return samplings.contains("o") || samplings.contains("s") || samplings.contains("c") || samplings.contains("w");
    }

    public Integer getIncrement()
    {
        return 1;
    }

    public void setActor(String actor)
    {
        setModifiedBy(actor);
        setModifiedDate(LocalDate.now());
        if (samplingData != null) {
            samplingData.setActor(actor);
        }
    }

    public void setActiveFlag(Integer activeFlag)
    {
        setActiveFlagInd(activeFlag != null && activeFlag.equals(1));
    }

    public Integer getActiveFlag()
    {
        return this.activeFlagInd ? 1 : 0;
    }
}
