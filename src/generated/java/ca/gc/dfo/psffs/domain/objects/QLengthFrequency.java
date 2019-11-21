package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLengthFrequency is a Querydsl query type for LengthFrequency
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLengthFrequency extends EntityPathBase<LengthFrequency> {

    private static final long serialVersionUID = 2073607219L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLengthFrequency lengthFrequency = new QLengthFrequency("lengthFrequency");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCatchCategory catchCategory;

    public final DatePath<java.time.LocalDate> catchDate = createDate("catchDate", java.time.LocalDate.class);

    public final StringPath catchDetailsComments = createString("catchDetailsComments");

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCatchLocation catchLocation;

    public final TimePath<java.time.LocalTime> catchTime = createTime("catchTime", java.time.LocalTime.class);

    public final StringPath checkedBy = createString("checkedBy");

    public final StringPath comments = createString("comments");

    public final ca.gc.dfo.psffs.domain.objects.lookups.QWeightConversionFactor conversionFactor;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCountry country;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final NumberPath<Float> depthMeters = createNumber("depthMeters", Float.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSpecies directedSpecies;

    public final StringPath enteredBy = createString("enteredBy");

    public final StringPath enteredByOther = createString("enteredByOther");

    public final NumberPath<Integer> frequencyNumber = createNumber("frequencyNumber", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QGear gear;

    public final NumberPath<Integer> gearAmount = createNumber("gearAmount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> latitude = createNumber("latitude", Integer.class);

    public final ListPath<LengthFrequencyCount, QLengthFrequencyCount> lengthFrequencyCounts = this.<LengthFrequencyCount, QLengthFrequencyCount>createList("lengthFrequencyCounts", LengthFrequencyCount.class, QLengthFrequencyCount.class, PathInits.DIRECT2);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QLengthGroup lengthGroup;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QLengthUnit lengthUnit;

    public final NumberPath<Integer> longitude = createNumber("longitude", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QMeasuringTechnique measuringTechnique;

    public final NumberPath<Float> meshSizeMillimeters = createNumber("meshSizeMillimeters", Float.class);

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QNafoDivision nafoDivision;

    public final StringPath otherVesselDetails = createString("otherVesselDetails");

    public final NumberPath<Integer> perNthFrozens = createNumber("perNthFrozens", Integer.class);

    public final NumberPath<Integer> perNthOtoliths = createNumber("perNthOtoliths", Integer.class);

    public final NumberPath<Integer> perNthStomachs = createNumber("perNthStomachs", Integer.class);

    public final NumberPath<Integer> perNthWeights = createNumber("perNthWeights", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QPort portOfLanding;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QQuarter quarter;

    public final NumberPath<Integer> reqFrozens = createNumber("reqFrozens", Integer.class);

    public final NumberPath<Integer> reqOtoliths = createNumber("reqOtoliths", Integer.class);

    public final NumberPath<Integer> reqStomachs = createNumber("reqStomachs", Integer.class);

    public final NumberPath<Integer> reqWeights = createNumber("reqWeights", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSpecies sampleSpecies;

    public final NumberPath<Float> sampleWeightKilograms = createNumber("sampleWeightKilograms", Float.class);

    public final QSampling sampling;

    public final NumberPath<Integer> sexedFrozens = createNumber("sexedFrozens", Integer.class);

    public final NumberPath<Integer> sexedOtoliths = createNumber("sexedOtoliths", Integer.class);

    public final NumberPath<Integer> sexedStomachs = createNumber("sexedStomachs", Integer.class);

    public final NumberPath<Integer> sexedWeights = createNumber("sexedWeights", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSexType sexType;

    public final NumberPath<Float> soakTimeHours = createNumber("soakTimeHours", Float.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSamplingStatus status;

    public final NumberPath<Float> turnoutWeightKilograms = createNumber("turnoutWeightKilograms", Float.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QUnitArea unitArea;

    public final StringPath verifiedBy = createString("verifiedBy");

    public final ca.gc.dfo.psffs.domain.objects.lookups.QVessel vessel;

    public final StringPath vesselComments = createString("vesselComments");

    public QLengthFrequency(String variable) {
        this(LengthFrequency.class, forVariable(variable), INITS);
    }

    public QLengthFrequency(Path<? extends LengthFrequency> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLengthFrequency(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLengthFrequency(PathMetadata metadata, PathInits inits) {
        this(LengthFrequency.class, metadata, inits);
    }

    public QLengthFrequency(Class<? extends LengthFrequency> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.catchCategory = inits.isInitialized("catchCategory") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCatchCategory(forProperty("catchCategory")) : null;
        this.catchLocation = inits.isInitialized("catchLocation") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCatchLocation(forProperty("catchLocation")) : null;
        this.conversionFactor = inits.isInitialized("conversionFactor") ? new ca.gc.dfo.psffs.domain.objects.lookups.QWeightConversionFactor(forProperty("conversionFactor")) : null;
        this.country = inits.isInitialized("country") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCountry(forProperty("country")) : null;
        this.directedSpecies = inits.isInitialized("directedSpecies") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSpecies(forProperty("directedSpecies")) : null;
        this.gear = inits.isInitialized("gear") ? new ca.gc.dfo.psffs.domain.objects.lookups.QGear(forProperty("gear")) : null;
        this.lengthGroup = inits.isInitialized("lengthGroup") ? new ca.gc.dfo.psffs.domain.objects.lookups.QLengthGroup(forProperty("lengthGroup"), inits.get("lengthGroup")) : null;
        this.lengthUnit = inits.isInitialized("lengthUnit") ? new ca.gc.dfo.psffs.domain.objects.lookups.QLengthUnit(forProperty("lengthUnit")) : null;
        this.measuringTechnique = inits.isInitialized("measuringTechnique") ? new ca.gc.dfo.psffs.domain.objects.lookups.QMeasuringTechnique(forProperty("measuringTechnique")) : null;
        this.nafoDivision = inits.isInitialized("nafoDivision") ? new ca.gc.dfo.psffs.domain.objects.lookups.QNafoDivision(forProperty("nafoDivision")) : null;
        this.portOfLanding = inits.isInitialized("portOfLanding") ? new ca.gc.dfo.psffs.domain.objects.lookups.QPort(forProperty("portOfLanding")) : null;
        this.quarter = inits.isInitialized("quarter") ? new ca.gc.dfo.psffs.domain.objects.lookups.QQuarter(forProperty("quarter")) : null;
        this.sampleSpecies = inits.isInitialized("sampleSpecies") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSpecies(forProperty("sampleSpecies")) : null;
        this.sampling = inits.isInitialized("sampling") ? new QSampling(forProperty("sampling"), inits.get("sampling")) : null;
        this.sexType = inits.isInitialized("sexType") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSexType(forProperty("sexType")) : null;
        this.status = inits.isInitialized("status") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSamplingStatus(forProperty("status")) : null;
        this.unitArea = inits.isInitialized("unitArea") ? new ca.gc.dfo.psffs.domain.objects.lookups.QUnitArea(forProperty("unitArea"), inits.get("unitArea")) : null;
        this.vessel = inits.isInitialized("vessel") ? new ca.gc.dfo.psffs.domain.objects.lookups.QVessel(forProperty("vessel"), inits.get("vessel")) : null;
    }

}

