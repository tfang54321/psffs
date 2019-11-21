package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QExtract is a Querydsl query type for Extract
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QExtract extends EntityPathBase<Extract> {

    private static final long serialVersionUID = 521320606L;

    public static final QExtract extract = new QExtract("extract");

    public final StringPath createdBy = createString("createdBy");

    public final DatePath<java.time.LocalDate> createdDate = createDate("createdDate", java.time.LocalDate.class);

    public final StringPath data = createString("data");

    public final NumberPath<Long> extractId = createNumber("extractId", Long.class);

    public final StringPath fileName = createString("fileName");

    public final BooleanPath hasData = createBoolean("hasData");

    public final StringPath type = createString("type");

    public QExtract(String variable) {
        super(Extract.class, forVariable(variable));
    }

    public QExtract(Path<? extends Extract> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExtract(PathMetadata metadata) {
        super(Extract.class, metadata);
    }

}

