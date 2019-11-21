package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSamplingEntry is a Querydsl query type for SamplingEntry
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSamplingEntry extends EntityPathBase<SamplingEntry> {

    private static final long serialVersionUID = -835210456L;

    private static final PathInits INITS = new PathInits("*", "sampling.lengthFrequency.sampleSpecies", "sampling.lengthFrequency.directedSpecies", "sampling.lengthFrequency.country", "sampling.lengthFrequency.quarter", "sampling.lengthFrequency.nafoDivision", "sampling.lengthFrequency.unitArea", "sampling.lengthFrequency.vessel", "sampling.lengthFrequency.vessel.lengthCategory", "sampling.lengthFrequency.vessel.fleetSector", "sampling.lengthFrequency.gear", "sampling.cell", "sampling.tripSetSpecies", "samplingData.status");

    public static final QSamplingEntry samplingEntry = new QSamplingEntry("samplingEntry");

    public final BooleanPath activeFlagInd = createBoolean("activeFlagInd");

    public final BooleanPath frozenSampledInd = createBoolean("frozenSampledInd");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> length = createNumber("length", Integer.class);

    public final BooleanPath lengthSampledInd = createBoolean("lengthSampledInd");

    public final ca.gc.dfo.psffs.domain.objects.lookups.QMaturity maturity;

    public final NumberPath<Integer> maturityId = createNumber("maturityId", Integer.class);

    public final StringPath modifiedBy = createString("modifiedBy");

    public final DatePath<java.time.LocalDate> modifiedDate = createDate("modifiedDate", java.time.LocalDate.class);

    public final BooleanPath otolithSampledInd = createBoolean("otolithSampledInd");

    public final NumberPath<Integer> presOrder = createNumber("presOrder", Integer.class);

    public final QSampling sampling;

    public final QSamplingData samplingData;

    public final StringPath sex = createString("sex");

    public final BooleanPath stomachSampledInd = createBoolean("stomachSampledInd");

    public final BooleanPath weightSampledInd = createBoolean("weightSampledInd");

    public QSamplingEntry(String variable) {
        this(SamplingEntry.class, forVariable(variable), INITS);
    }

    public QSamplingEntry(Path<? extends SamplingEntry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSamplingEntry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSamplingEntry(PathMetadata metadata, PathInits inits) {
        this(SamplingEntry.class, metadata, inits);
    }

    public QSamplingEntry(Class<? extends SamplingEntry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.maturity = inits.isInitialized("maturity") ? new ca.gc.dfo.psffs.domain.objects.lookups.QMaturity(forProperty("maturity"), inits.get("maturity")) : null;
        this.sampling = inits.isInitialized("sampling") ? new QSampling(forProperty("sampling"), inits.get("sampling")) : null;
        this.samplingData = inits.isInitialized("samplingData") ? new QSamplingData(forProperty("samplingData"), inits.get("samplingData")) : null;
    }

}

