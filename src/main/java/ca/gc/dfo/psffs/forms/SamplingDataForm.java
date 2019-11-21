package ca.gc.dfo.psffs.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SamplingDataForm
{
    private Long samplingDataId;
    //LF ENTRY (READ-ONLY)
    private String sex;
    private String maturityLevel;
    private Integer entryLength;
    //HEADER
    private Long storageNbr;
    private String fieldNbr;
    private String tag;
    private Integer statusId;
    @JsonIgnore
    private String statusEng;
    @JsonIgnore
    private String statusFra;
    //OTOLITH
    private Integer otolithEdgeId;
    private String otolithEdgeCode;
    private Integer otolithReliabilityId;
    private String otolithReliabilityCode;
    private Integer age;
    private Integer eggDiameter;
    //WEIGHT
    private Float roundWt;
    private Float guttedWt;
    private Float ggWt;
    private Integer liverWt;
    private Integer stomachWt;
    private Integer gutsWt;
    private Integer gonadWt;
    //STOMACH
    private Integer fullness;
    private Integer primaryStomachContentId;
    private String primaryStomachContentCode;
    private Integer secondaryStomachContentId;
    private String secondaryStomachContentCode;
    private Integer parasiteId;
    private String parasiteCode;
    private Integer nbrOfParasites;
}
