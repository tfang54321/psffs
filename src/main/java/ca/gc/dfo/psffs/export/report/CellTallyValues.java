package ca.gc.dfo.psffs.export.report;

import lombok.Data;

@Data
public class CellTallyValues
{
    private Integer male;
    private Integer female;
    private Integer unsexed;

    private Integer maleOtoliths;
    private Integer femaleOtoliths;
    private Integer unsexedOtoliths;

    private Integer maleWeights;
    private Integer femaleWeights;
    private Integer unsexedWeights;

    private Integer maleStomachs;
    private Integer femaleStomachs;
    private Integer unsexedStomachs;

    private Integer frozens;

    public CellTallyValues()
    {
        setMale(0);
        setFemale(0);
        setUnsexed(0);

        setMaleOtoliths(0);
        setFemaleOtoliths(0);
        setUnsexedOtoliths(0);

        setMaleWeights(0);
        setFemaleWeights(0);
        setUnsexedWeights(0);

        setMaleStomachs(0);
        setFemaleStomachs(0);
        setUnsexedStomachs(0);

        setFrozens(0);
    }

    public void incrementMale(int by) {setMale(getMale() + by);}
    public void incrementFemale(int by) {setFemale(getFemale() + by);}
    public void incrementUnsexed(int by) {setUnsexed(getUnsexed() + by);}

    public void incrementMaleOtoliths(int by) {setMaleOtoliths(getMaleOtoliths() + by);}
    public void incrementFemaleOtoliths(int by) {setFemaleOtoliths(getFemaleOtoliths() + by);}
    public void incrementUnsexedOtoliths(int by) {setUnsexedOtoliths(getUnsexedOtoliths() + by);}

    public void incrementMaleWeights(int by) {setMaleWeights(getMaleWeights() + by);}
    public void incrementFemaleWeights(int by) {setFemaleWeights(getFemaleWeights() + by);}
    public void incrementUnsexedWeights(int by) {setUnsexedWeights(getUnsexedWeights() + by);}

    public void incrementMaleStomachs(int by) {setMaleStomachs(getMaleStomachs() + by);}
    public void incrementFemaleStomachs(int by) {setFemaleStomachs(getFemaleStomachs() + by);}
    public void incrementUnsexedStomachs(int by) {setUnsexedStomachs(getUnsexedStomachs() + by);}

    public void incrementFrozens(int by) {setFrozens(getFrozens() + by);}

    public String getMaleStr() {return !this.male.equals(0) ? this.male.toString() : "";}
    public String getFemaleStr() {return !this.female.equals(0) ? this.female.toString() : "";}
    public String getUnsexedStr() {return !this.unsexed.equals(0) ? this.unsexed.toString() : "";}

    public String getMaleOtolithsStr() {return !this.maleOtoliths.equals(0) ? this.maleOtoliths.toString() : "";}
    public String getFemaleOtolithsStr() {return !this.femaleOtoliths.equals(0) ? this.femaleOtoliths.toString() : "";}
    public String getUnsexedOtolithsStr() {return !this.unsexedOtoliths.equals(0) ? this.unsexedOtoliths.toString() : "";}

    public String getMaleWeightsStr() {return !this.maleWeights.equals(0) ? this.maleWeights.toString() : "";}
    public String getFemaleWeightsStr() {return !this.femaleWeights.equals(0) ? this.femaleWeights.toString() : "";}
    public String getUnsexedWeightsStr() {return !this.unsexedWeights.equals(0) ? this.unsexedWeights.toString() : "";}

    public String getMaleStomachsStr() {return !this.maleStomachs.equals(0) ? this.maleStomachs.toString() : "";}
    public String getFemaleStomachsStr() {return !this.femaleStomachs.equals(0) ? this.femaleStomachs.toString() : "";}
    public String getUnsexedStomachsStr() {return !this.unsexedStomachs.equals(0) ? this.unsexedStomachs.toString() : "";}

    public String getFrozensStr() {return this.frozens != 0 ? this.frozens.toString() : "";}
}
