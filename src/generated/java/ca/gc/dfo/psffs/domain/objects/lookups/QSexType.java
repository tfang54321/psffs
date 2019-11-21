package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSexType is a Querydsl query type for SexType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSexType extends EntityPathBase<SexType> {

    private static final long serialVersionUID = 2044311848L;

    public static final QSexType sexType = new QSexType("sexType");

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

    public QSexType(String variable) {
        super(SexType.class, forVariable(variable));
    }

    public QSexType(Path<? extends SexType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSexType(PathMetadata metadata) {
        super(SexType.class, metadata);
    }

}

