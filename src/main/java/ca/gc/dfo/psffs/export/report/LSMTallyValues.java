package ca.gc.dfo.psffs.export.report;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Comparator;

@Data
public class LSMTallyValues implements Comparable<LSMTallyValues>
{
    private String maturityCode;
    private String description;

    private Integer length1;
    private Integer length2;
    private Integer length3;
    private Integer length4;
    private Integer length5;
    private Integer length6;
    private Integer length7;
    private Integer length8;
    private Integer length9;
    private Integer length10;
    private Integer length11;
    private Integer length12;
    private Integer length13;
    private Integer length14;
    private Integer length15;

    public LSMTallyValues(String maturityCode, String description)
    {
        setMaturityCode(maturityCode);
        setDescription(description);
        setLength1(0);
        setLength2(0);
        setLength3(0);
        setLength4(0);
        setLength5(0);
        setLength6(0);
        setLength7(0);
        setLength8(0);
        setLength9(0);
        setLength10(0);
        setLength11(0);
        setLength12(0);
        setLength13(0);
        setLength14(0);
        setLength15(0);
    }

    public Integer getMaturityCodeInt()
    {
        Integer matCodeInt = 0;
        String maturityCode = getMaturityCode();
        if (maturityCode != null && maturityCode.trim().length() > 0) {
            try {
                matCodeInt = Integer.parseInt(maturityCode);
            } catch (NumberFormatException nfe) {
                matCodeInt = 0;
            }
        }
        return matCodeInt;
    }

    public Integer getLengthSum()
    {
        return length1+length2+length3+length4+length5+length6+length7+length8+length9+length10+
                length11+length12+length13+length14+length15*-1;
    }

    public void setLengthByIndex(int index, Integer value)
    {
        Method setter;
        try {
            setter = this.getClass().getDeclaredMethod("setLength" + index, Integer.class);
        } catch (NoSuchMethodException nsme) {
            //Do nothing, set attempt fails
            setter = null;
        }

        if (setter != null) {
            try {
                setter.invoke(this, value);
            } catch (Exception ex) {
                //Do nothing, setter failed
            }
        }
    }

    public String getLength1Str() {return !this.length1.equals(0) ? this.length1.toString() : "";}
    public String getLength2Str() {return !this.length2.equals(0) ? this.length2.toString() : "";}
    public String getLength3Str() {return !this.length3.equals(0) ? this.length3.toString() : "";}
    public String getLength4Str() {return !this.length4.equals(0) ? this.length4.toString() : "";}
    public String getLength5Str() {return !this.length5.equals(0) ? this.length5.toString() : "";}
    public String getLength6Str() {return !this.length6.equals(0) ? this.length6.toString() : "";}
    public String getLength7Str() {return !this.length7.equals(0) ? this.length7.toString() : "";}
    public String getLength8Str() {return !this.length8.equals(0) ? this.length8.toString() : "";}
    public String getLength9Str() {return !this.length9.equals(0) ? this.length9.toString() : "";}
    public String getLength10Str() {return !this.length10.equals(0) ? this.length10.toString() : "";}
    public String getLength11Str() {return !this.length11.equals(0) ? this.length11.toString() : "";}
    public String getLength12Str() {return !this.length12.equals(0) ? this.length12.toString() : "";}
    public String getLength13Str() {return !this.length13.equals(0) ? this.length13.toString() : "";}
    public String getLength14Str() {return !this.length14.equals(0) ? this.length14.toString() : "";}
    public String getLength15Str() {return !this.length15.equals(0) ? this.length15.toString() : "";}

    public int compareTo(LSMTallyValues ltv)
    {
        return Comparator.comparingInt(LSMTallyValues::getMaturityCodeInt)
                         .thenComparingInt(LSMTallyValues::getLengthSum)
                         .compare(ltv, this);
    }
}
