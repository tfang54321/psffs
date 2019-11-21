package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLengthGroup is a Querydsl query type for LengthGroup
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLengthGroup extends EntityPathBase<LengthGroup> {

    private static final long serialVersionUID = -675621983L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLengthGroup lengthGroup1 = new QLengthGroup("lengthGroup1");

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

    public final NumberPath<Integer> lengthGroup = createNumber("lengthGroup", Integer.class);

    public final QLengthUnit lengthUnit;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public QLengthGroup(String variable) {
        this(LengthGroup.class, forVariable(variable), INITS);
    }

    public QLengthGroup(Path<? extends LengthGroup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLengthGroup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLengthGroup(PathMetadata metadata, PathInits inits) {
        this(LengthGroup.class, metadata, inits);
    }

    public QLengthGroup(Class<? extends LengthGroup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lengthUnit = inits.isInitialized("lengthUnit") ? new QLengthUnit(forProperty("lengthUnit")) : null;
    }

}

