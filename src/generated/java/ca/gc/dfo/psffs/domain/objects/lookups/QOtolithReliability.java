package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QOtolithReliability is a Querydsl query type for OtolithReliability
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QOtolithReliability extends EntityPathBase<OtolithReliability> {

    private static final long serialVersionUID = 1964205303L;

    public static final QOtolithReliability otolithReliability = new QOtolithReliability("otolithReliability");

    public final QOperationalCode _super = new QOperationalCode(this);

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
    public final NumberPath<Integer> presentationOrder = _super.presentationOrder;

    //inherited
    public final StringPath type = _super.type;

    public QOtolithReliability(String variable) {
        super(OtolithReliability.class, forVariable(variable));
    }

    public QOtolithReliability(Path<? extends OtolithReliability> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOtolithReliability(PathMetadata metadata) {
        super(OtolithReliability.class, metadata);
    }

}

