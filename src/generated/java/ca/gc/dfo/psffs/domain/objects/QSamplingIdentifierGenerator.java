package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSamplingIdentifierGenerator is a Querydsl query type for SamplingIdentifierGenerator
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSamplingIdentifierGenerator extends EntityPathBase<SamplingIdentifierGenerator> {

    private static final long serialVersionUID = 42070112L;

    public static final QSamplingIdentifierGenerator samplingIdentifierGenerator = new QSamplingIdentifierGenerator("samplingIdentifierGenerator");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> identifier = createNumber("identifier", Integer.class);

    public final StringPath initials = createString("initials");

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QSamplingIdentifierGenerator(String variable) {
        super(SamplingIdentifierGenerator.class, forVariable(variable));
    }

    public QSamplingIdentifierGenerator(Path<? extends SamplingIdentifierGenerator> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSamplingIdentifierGenerator(PathMetadata metadata) {
        super(SamplingIdentifierGenerator.class, metadata);
    }

}

