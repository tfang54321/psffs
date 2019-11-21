package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseSamplingSetting is a Querydsl query type for BaseSamplingSetting
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBaseSamplingSetting extends EntityPathBase<BaseSamplingSetting> {

    private static final long serialVersionUID = 638263797L;

    public static final QBaseSamplingSetting baseSamplingSetting = new QBaseSamplingSetting("baseSamplingSetting");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> frozensDefaultFrequency = createNumber("frozensDefaultFrequency", Integer.class);

    public final NumberPath<Integer> lengthGroupId = createNumber("lengthGroupId", Integer.class);

    public final NumberPath<Integer> lengthGroupMax = createNumber("lengthGroupMax", Integer.class);

    public final NumberPath<Integer> lengthGroupMin = createNumber("lengthGroupMin", Integer.class);

    public final NumberPath<Integer> lengthUnitId = createNumber("lengthUnitId", Integer.class);

    public final NumberPath<Integer> measuringTechniqueId = createNumber("measuringTechniqueId", Integer.class);

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final NumberPath<Integer> otolithsDefaultFrequency = createNumber("otolithsDefaultFrequency", Integer.class);

    public final NumberPath<Integer> perNthFrozens = createNumber("perNthFrozens", Integer.class);

    public final NumberPath<Integer> perNthOtoliths = createNumber("perNthOtoliths", Integer.class);

    public final NumberPath<Integer> perNthStomachs = createNumber("perNthStomachs", Integer.class);

    public final NumberPath<Integer> perNthWeights = createNumber("perNthWeights", Integer.class);

    public final StringPath settingType = createString("settingType");

    public final BooleanPath sexedFrozensInd = createBoolean("sexedFrozensInd");

    public final BooleanPath sexedOtolithsInd = createBoolean("sexedOtolithsInd");

    public final BooleanPath sexedStomachsInd = createBoolean("sexedStomachsInd");

    public final BooleanPath sexedWeightsInd = createBoolean("sexedWeightsInd");

    public final NumberPath<Integer> speciesId = createNumber("speciesId", Integer.class);

    public final NumberPath<Integer> stomachsDefaultFrequency = createNumber("stomachsDefaultFrequency", Integer.class);

    public final NumberPath<Integer> weightsDefaultFrequency = createNumber("weightsDefaultFrequency", Integer.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QBaseSamplingSetting(String variable) {
        super(BaseSamplingSetting.class, forVariable(variable));
    }

    public QBaseSamplingSetting(Path<? extends BaseSamplingSetting> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseSamplingSetting(PathMetadata metadata) {
        super(BaseSamplingSetting.class, metadata);
    }

}

