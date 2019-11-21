package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseLookup is a Querydsl query type for BaseLookup
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBaseLookup extends EntityPathBase<BaseLookup> {

    private static final long serialVersionUID = -2066635197L;

    public static final QBaseLookup baseLookup = new QBaseLookup("baseLookup");

    public final ca.gc.dfo.psffs.domain.objects.QBaseEntity _super = new ca.gc.dfo.psffs.domain.objects.QBaseEntity(this);

    public final BooleanPath activeFlagInd = createBoolean("activeFlagInd");

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final StringPath englishDescription = createString("englishDescription");

    public final StringPath frenchDescription = createString("frenchDescription");

    public final StringPath legacyCode = createString("legacyCode");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    public QBaseLookup(String variable) {
        super(BaseLookup.class, forVariable(variable));
    }

    public QBaseLookup(Path<? extends BaseLookup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseLookup(PathMetadata metadata) {
        super(BaseLookup.class, metadata);
    }

}

