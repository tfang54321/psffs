package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QOperationalCode is a Querydsl query type for OperationalCode
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QOperationalCode extends EntityPathBase<OperationalCode> {

    private static final long serialVersionUID = 1306706247L;

    public static final QOperationalCode operationalCode = new QOperationalCode("operationalCode");

    public final StringPath englishDescription = createString("englishDescription");

    public final StringPath frenchDescription = createString("frenchDescription");

    public final StringPath legacyCode = createString("legacyCode");

    public final StringPath modifiedBy = createString("modifiedBy");

    public final DatePath<java.time.LocalDate> modifiedDate = createDate("modifiedDate", java.time.LocalDate.class);

    public final NumberPath<Integer> presentationOrder = createNumber("presentationOrder", Integer.class);

    public final StringPath type = createString("type");

    public QOperationalCode(String variable) {
        super(OperationalCode.class, forVariable(variable));
    }

    public QOperationalCode(Path<? extends OperationalCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOperationalCode(PathMetadata metadata) {
        super(OperationalCode.class, metadata);
    }

}

