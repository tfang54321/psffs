package ca.gc.dfo.psffs.export.report;

import lombok.Data;

import java.util.Comparator;

@Data
public class LengthGroupKey implements Comparable<LengthGroupKey>
{
    public enum LengthUnitEnum {
        LENGTH_UNIT_14("cm"),
        LENGTH_UNIT_13("mm"),
        LENGTH_UNIT_12("&mu;m");

        LengthUnitEnum(String unit)
        {
            this.UNIT = unit;
        }

        public String UNIT;
    }

    private Integer lengthGroup;
    private String lengthUnit;

    public LengthGroupKey(Integer lengthGroup, String lengthUnit)
    {
        setLengthGroup(lengthGroup);
        setLengthUnit(lengthUnit);
    }

    @Override
    public int compareTo(LengthGroupKey other)
    {
        return Comparator.comparing(LengthGroupKey::getLengthUnit)
                         .thenComparingInt(LengthGroupKey::getLengthGroup)
                         .compare(this, other);
    }
}
