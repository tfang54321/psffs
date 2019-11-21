package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVessel is a Querydsl query type for Vessel
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QVessel extends EntityPathBase<Vessel> {

    private static final long serialVersionUID = 982997198L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVessel vessel = new QVessel("vessel");

    public final QBaseLookup _super = new QBaseLookup(this);

    //inherited
    public final BooleanPath activeFlagInd = _super.activeFlagInd;

    public final StringPath cfvSideNumber = createString("cfvSideNumber");

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    //inherited
    public final StringPath englishDescription = _super.englishDescription;

    //inherited
    public final StringPath frenchDescription = _super.frenchDescription;

    public final NumberPath<Float> grTonnage = createNumber("grTonnage", Float.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    //inherited
    public final StringPath legacyCode = _super.legacyCode;

    public final QLengthCategory lengthCategory;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public final QTonnage tonnage;

    public final NumberPath<Float> vesselLengthMeters = createNumber("vesselLengthMeters", Float.class);

    public QVessel(String variable) {
        this(Vessel.class, forVariable(variable), INITS);
    }

    public QVessel(Path<? extends Vessel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVessel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVessel(PathMetadata metadata, PathInits inits) {
        this(Vessel.class, metadata, inits);
    }

    public QVessel(Class<? extends Vessel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lengthCategory = inits.isInitialized("lengthCategory") ? new QLengthCategory(forProperty("lengthCategory")) : null;
        this.tonnage = inits.isInitialized("tonnage") ? new QTonnage(forProperty("tonnage")) : null;
    }

}

