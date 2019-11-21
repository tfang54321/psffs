package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QChecksumForObject is a Querydsl query type for ChecksumForObject
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QChecksumForObject extends EntityPathBase<ChecksumForObject> {

    private static final long serialVersionUID = 277781570L;

    public static final QChecksumForObject checksumForObject = new QChecksumForObject("checksumForObject");

    public final StringPath generatedUUID = createString("generatedUUID");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath objectStoreName = createString("objectStoreName");

    public QChecksumForObject(String variable) {
        super(ChecksumForObject.class, forVariable(variable));
    }

    public QChecksumForObject(Path<? extends ChecksumForObject> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChecksumForObject(PathMetadata metadata) {
        super(ChecksumForObject.class, metadata);
    }

}

