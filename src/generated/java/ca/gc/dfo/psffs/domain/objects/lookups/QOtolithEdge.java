package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QOtolithEdge is a Querydsl query type for OtolithEdge
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QOtolithEdge extends EntityPathBase<OtolithEdge> {

    private static final long serialVersionUID = 1387454368L;

    public static final QOtolithEdge otolithEdge = new QOtolithEdge("otolithEdge");

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

    public QOtolithEdge(String variable) {
        super(OtolithEdge.class, forVariable(variable));
    }

    public QOtolithEdge(Path<? extends OtolithEdge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOtolithEdge(PathMetadata metadata) {
        super(OtolithEdge.class, metadata);
    }

}

