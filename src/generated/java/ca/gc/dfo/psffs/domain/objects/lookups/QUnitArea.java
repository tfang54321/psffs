package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUnitArea is a Querydsl query type for UnitArea
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUnitArea extends EntityPathBase<UnitArea> {

    private static final long serialVersionUID = 1430826185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUnitArea unitArea = new QUnitArea("unitArea");

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

    public final QNafoDivision nafoDivision;

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public QUnitArea(String variable) {
        this(UnitArea.class, forVariable(variable), INITS);
    }

    public QUnitArea(Path<? extends UnitArea> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUnitArea(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUnitArea(PathMetadata metadata, PathInits inits) {
        this(UnitArea.class, metadata, inits);
    }

    public QUnitArea(Class<? extends UnitArea> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.nafoDivision = inits.isInitialized("nafoDivision") ? new QNafoDivision(forProperty("nafoDivision")) : null;
    }

}

