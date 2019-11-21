package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWeightConversionFactor is a Querydsl query type for WeightConversionFactor
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWeightConversionFactor extends EntityPathBase<WeightConversionFactor> {

    private static final long serialVersionUID = 1180376885L;

    public static final QWeightConversionFactor weightConversionFactor = new QWeightConversionFactor("weightConversionFactor");

    public final QBaseLookup _super = new QBaseLookup(this);

    //inherited
    public final BooleanPath activeFlagInd = _super.activeFlagInd;

    public final NumberPath<Float> conversionFactor = createNumber("conversionFactor", Float.class);

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

    public final ListPath<Species, QSpecies> species = this.<Species, QSpecies>createList("species", Species.class, QSpecies.class, PathInits.DIRECT2);

    public QWeightConversionFactor(String variable) {
        super(WeightConversionFactor.class, forVariable(variable));
    }

    public QWeightConversionFactor(Path<? extends WeightConversionFactor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeightConversionFactor(PathMetadata metadata) {
        super(WeightConversionFactor.class, metadata);
    }

}

