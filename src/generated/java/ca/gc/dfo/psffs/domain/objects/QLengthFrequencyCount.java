package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLengthFrequencyCount is a Querydsl query type for LengthFrequencyCount
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLengthFrequencyCount extends EntityPathBase<LengthFrequencyCount> {

    private static final long serialVersionUID = 761810076L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLengthFrequencyCount lengthFrequencyCount = new QLengthFrequencyCount("lengthFrequencyCount");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> length = createNumber("length", Integer.class);

    public final QLengthFrequency lengthFrequency;

    public final StringPath modifiedBy = createString("modifiedBy");

    public final DatePath<java.time.LocalDate> modifiedDate = createDate("modifiedDate", java.time.LocalDate.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSampleType sampleType;

    public final StringPath sex = createString("sex");

    public QLengthFrequencyCount(String variable) {
        this(LengthFrequencyCount.class, forVariable(variable), INITS);
    }

    public QLengthFrequencyCount(Path<? extends LengthFrequencyCount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLengthFrequencyCount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLengthFrequencyCount(PathMetadata metadata, PathInits inits) {
        this(LengthFrequencyCount.class, metadata, inits);
    }

    public QLengthFrequencyCount(Class<? extends LengthFrequencyCount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lengthFrequency = inits.isInitialized("lengthFrequency") ? new QLengthFrequency(forProperty("lengthFrequency"), inits.get("lengthFrequency")) : null;
        this.sampleType = inits.isInitialized("sampleType") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSampleType(forProperty("sampleType")) : null;
    }

}

