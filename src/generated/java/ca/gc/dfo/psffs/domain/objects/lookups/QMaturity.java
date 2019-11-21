package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMaturity is a Querydsl query type for Maturity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMaturity extends EntityPathBase<Maturity> {

    private static final long serialVersionUID = 2037895209L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMaturity maturity = new QMaturity("maturity");

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

    public final QSexCode sexCode;

    public QMaturity(String variable) {
        this(Maturity.class, forVariable(variable), INITS);
    }

    public QMaturity(Path<? extends Maturity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMaturity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMaturity(PathMetadata metadata, PathInits inits) {
        this(Maturity.class, metadata, inits);
    }

    public QMaturity(Class<? extends Maturity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sexCode = inits.isInitialized("sexCode") ? new QSexCode(forProperty("sexCode")) : null;
    }

}

