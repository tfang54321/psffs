package ca.gc.dfo.psffs.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripSettingForm extends AbstractSamplingSettingForm
{
    //Main
    //Inherited

    //Sampling Approach
    private Integer measuringTechniqueId;

    //Per-sample Objectives
    private Integer perNthOtoliths;
    private Integer otolithsDefaultFrequency;
    private Integer sexedOtoliths;
    private Integer perNthStomachs;
    private Integer stomachsDefaultFrequency;
    private Integer sexedStomachs;
    private Integer perNthFrozens;
    private Integer frozensDefaultFrequency;
    private Integer sexedFrozens;
    private Integer perNthWeights;
    private Integer weightsDefaultFrequency;
    private Integer sexedWeights;
}
