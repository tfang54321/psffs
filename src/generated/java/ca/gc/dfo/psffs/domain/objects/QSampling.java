package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSampling is a Querydsl query type for Sampling
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSampling extends EntityPathBase<Sampling> {

    private static final long serialVersionUID = -1529055382L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSampling sampling = new QSampling("sampling");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCell cell;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QDataSource dataSource;

    public final NumberPath<Integer> dataSourceId = createNumber("dataSourceId", Integer.class);

    public final ListPath<SamplingEntry, QSamplingEntry> entries = this.<SamplingEntry, QSamplingEntry>createList("entries", SamplingEntry.class, QSamplingEntry.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLengthFrequency lengthFrequency;

    public final NumberPath<Integer> lengthGroupMax = createNumber("lengthGroupMax", Integer.class);

    public final NumberPath<Integer> lengthGroupMin = createNumber("lengthGroupMin", Integer.class);

    public final StringPath localSamplingCode = createString("localSamplingCode");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final StringPath samplingCode = createString("samplingCode");

    public final ListPath<SamplingData, QSamplingData> samplingDataList = this.<SamplingData, QSamplingData>createList("samplingDataList", SamplingData.class, QSamplingData.class, PathInits.DIRECT2);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSamplingType samplingType;

    public final NumberPath<Integer> samplingTypeId = createNumber("samplingTypeId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSpecies species;

    public final NumberPath<Integer> speciesId = createNumber("speciesId", Integer.class);

    public final QTripSetSpecies tripSetSpecies;

    public final NumberPath<Integer> yearCreated = createNumber("yearCreated", Integer.class);

    public QSampling(String variable) {
        this(Sampling.class, forVariable(variable), INITS);
    }

    public QSampling(Path<? extends Sampling> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSampling(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSampling(PathMetadata metadata, PathInits inits) {
        this(Sampling.class, metadata, inits);
    }

    public QSampling(Class<? extends Sampling> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cell = inits.isInitialized("cell") ? new QCell(forProperty("cell"), inits.get("cell")) : null;
        this.dataSource = inits.isInitialized("dataSource") ? new ca.gc.dfo.psffs.domain.objects.lookups.QDataSource(forProperty("dataSource")) : null;
        this.lengthFrequency = inits.isInitialized("lengthFrequency") ? new QLengthFrequency(forProperty("lengthFrequency"), inits.get("lengthFrequency")) : null;
        this.samplingType = inits.isInitialized("samplingType") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSamplingType(forProperty("samplingType")) : null;
        this.species = inits.isInitialized("species") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSpecies(forProperty("species")) : null;
        this.tripSetSpecies = inits.isInitialized("tripSetSpecies") ? new QTripSetSpecies(forProperty("tripSetSpecies"), inits.get("tripSetSpecies")) : null;
    }

}

