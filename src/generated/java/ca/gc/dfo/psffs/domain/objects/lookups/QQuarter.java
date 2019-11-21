package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QQuarter is a Querydsl query type for Quarter
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QQuarter extends EntityPathBase<Quarter> {

    private static final long serialVersionUID = 707018516L;

    public static final QQuarter quarter = new QQuarter("quarter");

    public final QBaseLookup _super = new QBaseLookup(this);

    //inherited
    public final BooleanPath activeFlagInd = _super.activeFlagInd;

    public final NumberPath<Integer> catchLocationId = createNumber("catchLocationId", Integer.class);

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

    public final NumberPath<Integer> periodFromMonth = createNumber("periodFromMonth", Integer.class);

    public final NumberPath<Integer> periodNumber = createNumber("periodNumber", Integer.class);

    public final NumberPath<Integer> periodToMonth = createNumber("periodToMonth", Integer.class);

    public QQuarter(String variable) {
        super(Quarter.class, forVariable(variable));
    }

    public QQuarter(Path<? extends Quarter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQuarter(PathMetadata metadata) {
        super(Quarter.class, metadata);
    }

}

