package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMeasuringTechnique is a Querydsl query type for MeasuringTechnique
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMeasuringTechnique extends EntityPathBase<MeasuringTechnique> {

    private static final long serialVersionUID = 839419021L;

    public static final QMeasuringTechnique measuringTechnique = new QMeasuringTechnique("measuringTechnique");

    public final QBaseLookup _super = new QBaseLookup(this);

    //inherited
    public final BooleanPath activeFlagInd = _super.activeFlagInd;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    //inherited
    public final StringPath englishDescription = _super.englishDescription;

    //inherited
    public final StringPath frenchDescription = _super.frenchDescription;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    //inherited
    public final StringPath legacyCode = _super.legacyCode;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public QMeasuringTechnique(String variable) {
        super(MeasuringTechnique.class, forVariable(variable));
    }

    public QMeasuringTechnique(Path<? extends MeasuringTechnique> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMeasuringTechnique(PathMetadata metadata) {
        super(MeasuringTechnique.class, metadata);
    }

}

