package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCatchLocation is a Querydsl query type for CatchLocation
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCatchLocation extends EntityPathBase<CatchLocation> {

    private static final long serialVersionUID = -655510024L;

    public static final QCatchLocation catchLocation = new QCatchLocation("catchLocation");

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

    public QCatchLocation(String variable) {
        super(CatchLocation.class, forVariable(variable));
    }

    public QCatchLocation(Path<? extends CatchLocation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCatchLocation(PathMetadata metadata) {
        super(CatchLocation.class, metadata);
    }

}

