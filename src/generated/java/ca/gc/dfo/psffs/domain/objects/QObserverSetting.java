package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QObserverSetting is a Querydsl query type for ObserverSetting
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QObserverSetting extends EntityPathBase<ObserverSetting> {

    private static final long serialVersionUID = -695204425L;

    public static final QObserverSetting observerSetting = new QObserverSetting("observerSetting");

    public final QBaseSamplingSetting _super = new QBaseSamplingSetting(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    //inherited
    public final StringPath description = _super.description;

    //inherited
    public final NumberPath<Integer> frozensDefaultFrequency = _super.frozensDefaultFrequency;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final NumberPath<Integer> lengthGroupId = _super.lengthGroupId;

    //inherited
    public final NumberPath<Integer> lengthGroupMax = _super.lengthGroupMax;

    //inherited
    public final NumberPath<Integer> lengthGroupMin = _super.lengthGroupMin;

    //inherited
    public final NumberPath<Integer> lengthUnitId = _super.lengthUnitId;

    //inherited
    public final NumberPath<Integer> measuringTechniqueId = _super.measuringTechniqueId;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> otolithsDefaultFrequency = _super.otolithsDefaultFrequency;

    //inherited
    public final NumberPath<Integer> perNthFrozens = _super.perNthFrozens;

    //inherited
    public final NumberPath<Integer> perNthOtoliths = _super.perNthOtoliths;

    //inherited
    public final NumberPath<Integer> perNthStomachs = _super.perNthStomachs;

    //inherited
    public final NumberPath<Integer> perNthWeights = _super.perNthWeights;

    //inherited
    public final StringPath settingType = _super.settingType;

    //inherited
    public final BooleanPath sexedFrozensInd = _super.sexedFrozensInd;

    //inherited
    public final BooleanPath sexedOtolithsInd = _super.sexedOtolithsInd;

    //inherited
    public final BooleanPath sexedStomachsInd = _super.sexedStomachsInd;

    //inherited
    public final BooleanPath sexedWeightsInd = _super.sexedWeightsInd;

    //inherited
    public final NumberPath<Integer> speciesId = _super.speciesId;

    //inherited
    public final NumberPath<Integer> stomachsDefaultFrequency = _super.stomachsDefaultFrequency;

    //inherited
    public final NumberPath<Integer> weightsDefaultFrequency = _super.weightsDefaultFrequency;

    //inherited
    public final NumberPath<Integer> year = _super.year;

    public QObserverSetting(String variable) {
        super(ObserverSetting.class, forVariable(variable));
    }

    public QObserverSetting(Path<? extends ObserverSetting> path) {
        super(path.getType(), path.getMetadata());
    }

    public QObserverSetting(PathMetadata metadata) {
        super(ObserverSetting.class, metadata);
    }

}

