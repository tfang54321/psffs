package ca.gc.dfo.psffs.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseSamplingSetting extends BaseEntity
{
    public enum SamplingSettingType
    {
        TRIP, OBSERVER
    }

    public BaseSamplingSetting()
    {
        setSettingType(chosenType().name());
    }

    protected abstract @NotNull SamplingSettingType chosenType();

    //Main
    @Column(name = "crt_year")
    @Nullable
    @JsonIgnore
    private Integer year;

    @Column(name = "species_id")
    @Nullable
    @JsonIgnore
    private Integer speciesId;

    @Column(name = "setting_type", nullable = false)
    private String settingType;

    @Column(name = "description")
    private String description;

    //Sampling Approach
    @Column(name = "measuring_technique_id")
    private Integer measuringTechniqueId;

    @Column(name = "length_group_id")
    private Integer lengthGroupId;

    @Column(name = "length_unit_id")
    private Integer lengthUnitId;

    @Column(name = "length_group_min_range")
    private Integer lengthGroupMin;

    @Column(name = "length_group_max_range")
    private Integer lengthGroupMax;

    //Per-sample Objectives
    @Column(name = "per_nth_otoliths")
    private Integer perNthOtoliths;

    @Column(name = "otoliths_def_freq")
    private Integer otolithsDefaultFrequency;

    @Column(name = "sexed_otoliths_ind")
    private Boolean sexedOtolithsInd;

    @Column(name = "per_nth_stomachs")
    private Integer perNthStomachs;

    @Column(name = "stomachs_def_freq")
    private Integer stomachsDefaultFrequency;

    @Column(name = "sexed_stomachs_ind")
    private Boolean sexedStomachsInd;

    @Column(name = "per_nth_frozens")
    private Integer perNthFrozens;

    @Column(name = "frozens_def_freq")
    private Integer frozensDefaultFrequency;

    @Column(name = "sexed_frozens_ind")
    private Boolean sexedFrozensInd;

    @Column(name = "per_nth_weights")
    private Integer perNthWeights;

    @Column(name = "weights_def_freq")
    private Integer weightsDefaultFrequency;

    @Column(name = "sexed_weights_ind")
    private Boolean sexedWeightsInd;

    public void setSexedOtoliths(Integer sexedOtoliths)
    {
        setSexedOtolithsInd(sexedOtoliths != null && sexedOtoliths.equals(1));
    }

    public Integer getSexedOtoliths()
    {
        return this.sexedOtolithsInd != null && this.sexedOtolithsInd ? 1 : 0;
    }

    public void setSexedStomachs(Integer sexedStomachs)
    {
        setSexedStomachsInd(sexedStomachs != null && sexedStomachs.equals(1));
    }

    public Integer getSexedStomachs()
    {
        return this.sexedStomachsInd != null && this.sexedStomachsInd ? 1 : 0;
    }

    public void setSexedFrozens(Integer sexedFrozens)
    {
        setSexedFrozensInd(sexedFrozens != null && sexedFrozens.equals(1));
    }

    public Integer getSexedFrozens()
    {
        return this.sexedFrozensInd != null && this.sexedFrozensInd ? 1 : 0;
    }

    public void setSexedWeights(Integer sexedWeights)
    {
        setSexedWeightsInd(sexedWeights != null && sexedWeights.equals(1));
    }

    public Integer getSexedWeights()
    {
        return this.sexedWeightsInd != null && this.sexedWeightsInd ? 1 : 0;
    }

    public abstract Long getId();

    public abstract void setId(Long id);

    public Integer getYearKey()
    {
        return this.year != null ? this.year : -1;
    }

    public Integer getSpeciesKey()
    {
        return this.speciesId != null ? this.speciesId : -1;
    }
}
