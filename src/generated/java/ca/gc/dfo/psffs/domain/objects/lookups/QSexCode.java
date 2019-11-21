package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSexCode is a Querydsl query type for SexCode
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSexCode extends EntityPathBase<SexCode> {

    private static final long serialVersionUID = 2043795419L;

    public static final QSexCode sexCode = new QSexCode("sexCode");

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

    public QSexCode(String variable) {
        super(SexCode.class, forVariable(variable));
    }

    public QSexCode(Path<? extends SexCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSexCode(PathMetadata metadata) {
        super(SexCode.class, metadata);
    }

}

