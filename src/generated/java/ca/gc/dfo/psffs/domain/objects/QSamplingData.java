package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSamplingData is a Querydsl query type for SamplingData
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSamplingData extends EntityPathBase<SamplingData> {

    private static final long serialVersionUID = 1774130740L;

    private static final PathInits INITS = new PathInits("*", "sampling.lengthFrequency.sampleSpecies", "sampling.lengthFrequency.directedSpecies", "sampling.lengthFrequency.country", "sampling.lengthFrequency.quarter", "sampling.lengthFrequency.nafoDivision", "sampling.lengthFrequency.unitArea", "sampling.lengthFrequency.vessel", "sampling.lengthFrequency.vessel.lengthCategory", "sampling.lengthFrequency.vessel.fleetSector", "sampling.lengthFrequency.gear", "sampling.cell", "sampling.tripSetSpecies");

    public static final QSamplingData samplingData = new QSamplingData("samplingData");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final NumberPath<Integer> eggDiameter = createNumber("eggDiameter", Integer.class);

    public final StringPath fieldNbr = createString("fieldNbr");

    public final NumberPath<Integer> fullness = createNumber("fullness", Integer.class);

    public final NumberPath<Float> ggWt = createNumber("ggWt", Float.class);

    public final NumberPath<Integer> gonadWt = createNumber("gonadWt", Integer.class);

    public final NumberPath<Integer> gutsWt = createNumber("gutsWt", Integer.class);

    public final NumberPath<Float> guttedWt = createNumber("guttedWt", Float.class);

    public final NumberPath<Integer> liverWt = createNumber("liverWt", Integer.class);

    public final StringPath modifiedBy = createString("modifiedBy");

    public final DatePath<java.time.LocalDate> modifiedDate = createDate("modifiedDate", java.time.LocalDate.class);

    public final NumberPath<Integer> nbrOfParasites = createNumber("nbrOfParasites", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QOtolithEdge otolithEdge;

    public final NumberPath<Integer> otolithEdgeId = createNumber("otolithEdgeId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QOtolithReliability otolithReliability;

    public final NumberPath<Integer> otolithReliabilityId = createNumber("otolithReliabilityId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QParasite parasite;

    public final NumberPath<Integer> parasiteId = createNumber("parasiteId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QStomachContent primaryStomachContent;

    public final NumberPath<Integer> primaryStomachContentId = createNumber("primaryStomachContentId", Integer.class);

    public final NumberPath<Float> roundWt = createNumber("roundWt", Float.class);

    public final QSampling sampling;

    public final NumberPath<Long> samplingDataId = createNumber("samplingDataId", Long.class);

    public final QSamplingEntry samplingEntry;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QStomachContent secondaryStomachContent;

    public final NumberPath<Integer> secondaryStomachContentId = createNumber("secondaryStomachContentId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSamplingDataStatus status;

    public final NumberPath<Integer> statusId = createNumber("statusId", Integer.class);

    public final NumberPath<Integer> stomachWt = createNumber("stomachWt", Integer.class);

    public final NumberPath<Long> storageNbr = createNumber("storageNbr", Long.class);

    public final StringPath tag = createString("tag");

    public QSamplingData(String variable) {
        this(SamplingData.class, forVariable(variable), INITS);
    }

    public QSamplingData(Path<? extends SamplingData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSamplingData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSamplingData(PathMetadata metadata, PathInits inits) {
        this(SamplingData.class, metadata, inits);
    }

    public QSamplingData(Class<? extends SamplingData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.otolithEdge = inits.isInitialized("otolithEdge") ? new ca.gc.dfo.psffs.domain.objects.lookups.QOtolithEdge(forProperty("otolithEdge")) : null;
        this.otolithReliability = inits.isInitialized("otolithReliability") ? new ca.gc.dfo.psffs.domain.objects.lookups.QOtolithReliability(forProperty("otolithReliability")) : null;
        this.parasite = inits.isInitialized("parasite") ? new ca.gc.dfo.psffs.domain.objects.lookups.QParasite(forProperty("parasite")) : null;
        this.primaryStomachContent = inits.isInitialized("primaryStomachContent") ? new ca.gc.dfo.psffs.domain.objects.lookups.QStomachContent(forProperty("primaryStomachContent")) : null;
        this.sampling = inits.isInitialized("sampling") ? new QSampling(forProperty("sampling"), inits.get("sampling")) : null;
        this.samplingEntry = inits.isInitialized("samplingEntry") ? new QSamplingEntry(forProperty("samplingEntry"), inits.get("samplingEntry")) : null;
        this.secondaryStomachContent = inits.isInitialized("secondaryStomachContent") ? new ca.gc.dfo.psffs.domain.objects.lookups.QStomachContent(forProperty("secondaryStomachContent")) : null;
        this.status = inits.isInitialized("status") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSamplingDataStatus(forProperty("status")) : null;
    }

}

