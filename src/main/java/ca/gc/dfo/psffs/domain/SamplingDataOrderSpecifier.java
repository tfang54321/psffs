package ca.gc.dfo.psffs.domain;

import ca.gc.dfo.psffs.domain.objects.QSamplingEntry;
import com.querydsl.core.types.OrderSpecifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SamplingDataOrderSpecifier
{
    STORAGE_NBR_ASC("1", "asc", QSamplingEntry.samplingEntry.samplingData.storageNbr.asc()),
    STORAGE_NBR_DESC("1", "desc", QSamplingEntry.samplingEntry.samplingData.storageNbr.desc()),
    TAG_ASC("2", "asc", QSamplingEntry.samplingEntry.samplingData.tag.asc()),
    TAG_DESC("2", "desc", QSamplingEntry.samplingEntry.samplingData.tag.desc()),
    SEX_ASC("3", "asc", QSamplingEntry.samplingEntry.sex.asc()),
    SEX_DESC("3", "desc", QSamplingEntry.samplingEntry.sex.desc()),
    MATURITY_ASC("4", "asc", QSamplingEntry.samplingEntry.maturity.legacyCode.asc()),
    MATURITY_DESC("4", "desc", QSamplingEntry.samplingEntry.maturity.legacyCode.desc()),
    LENGTH_ASC("5", "asc", QSamplingEntry.samplingEntry.length.asc()),
    LENGTH_DESC("5", "asc", QSamplingEntry.samplingEntry.length.desc());

    SamplingDataOrderSpecifier(String columnOrder, String direction, OrderSpecifier<?> orderSpecifier)
    {
        this.COLUMN_ORDER = columnOrder;
        this.DIRECTION = direction;
        this.ORDER_SPECIFIER = orderSpecifier;
    }

    public String COLUMN_ORDER;
    public String DIRECTION;
    public OrderSpecifier<?> ORDER_SPECIFIER;

    public static OrderSpecifier<?> getByColumnOrderAndDirection(String columnOrder, String direction)
    {
        OrderSpecifier<?> specifier = null;
        final String dir = direction != null ? direction.toLowerCase() : "asc";
        List<SamplingDataOrderSpecifier> specifierList = Arrays.stream(SamplingDataOrderSpecifier.values())
                      .filter(sdos -> sdos.COLUMN_ORDER.equals(columnOrder) && sdos.DIRECTION.equals(dir))
                      .limit(1)
                      .collect(Collectors.toList());
        if (specifierList.size() > 0) {
            specifier = specifierList.get(0).ORDER_SPECIFIER;
        }
        return specifier;
    }
}
