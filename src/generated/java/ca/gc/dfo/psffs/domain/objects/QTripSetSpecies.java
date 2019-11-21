package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTripSetSpecies is a Querydsl query type for TripSetSpecies
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTripSetSpecies extends EntityPathBase<TripSetSpecies> {

    private static final long serialVersionUID = -59790910L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTripSetSpecies tripSetSpecies = new QTripSetSpecies("tripSetSpecies");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCatchCategory catchCategory;

    public final NumberPath<Integer> catchCategoryId = createNumber("catchCategoryId", Integer.class);

    public final DatePath<java.time.LocalDate> catchDate = createDate("catchDate", java.time.LocalDate.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCatchLocation catchLocation;

    public final NumberPath<Integer> catchLocationId = createNumber("catchLocationId", Integer.class);

    public final StringPath checkedBy = createString("checkedBy");

    public final StringPath comments = createString("comments");

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCountry country;

    public final NumberPath<Integer> countryId = createNumber("countryId", Integer.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final NumberPath<Float> depthMeters = createNumber("depthMeters", Float.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSpecies directedSpecies;

    public final NumberPath<Integer> directedSpeciesId = createNumber("directedSpeciesId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QGear gear;

    public final NumberPath<Integer> gearId = createNumber("gearId", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QLengthGroup lengthGroup;

    public final NumberPath<Integer> lengthGroupId = createNumber("lengthGroupId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QLengthUnit lengthUnit;

    public final NumberPath<Integer> lengthUnitId = createNumber("lengthUnitId", Integer.class);

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QNafoDivision nafoDivision;

    public final NumberPath<Integer> nafoDivisionId = createNumber("nafoDivisionId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QObserverCompany observerCompany;

    public final NumberPath<Integer> observerCompanyId = createNumber("observerCompanyId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QQuarter quarter;

    public final NumberPath<Integer> quarterId = createNumber("quarterId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSpecies sampledSpecies;

    public final NumberPath<Integer> sampledSpeciesId = createNumber("sampledSpeciesId", Integer.class);

    public final QSampling sampling;

    public final NumberPath<Integer> setNumber = createNumber("setNumber", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QTSSSamplingStatus status;

    public final NumberPath<Integer> statusId = createNumber("statusId", Integer.class);

    public final NumberPath<Integer> tripNumber = createNumber("tripNumber", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QUnitArea unitArea;

    public final NumberPath<Integer> unitAreaId = createNumber("unitAreaId", Integer.class);

    public QTripSetSpecies(String variable) {
        this(TripSetSpecies.class, forVariable(variable), INITS);
    }

    public QTripSetSpecies(Path<? extends TripSetSpecies> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTripSetSpecies(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTripSetSpecies(PathMetadata metadata, PathInits inits) {
        this(TripSetSpecies.class, metadata, inits);
    }

    public QTripSetSpecies(Class<? extends TripSetSpecies> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.catchCategory = inits.isInitialized("catchCategory") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCatchCategory(forProperty("catchCategory")) : null;
        this.catchLocation = inits.isInitialized("catchLocation") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCatchLocation(forProperty("catchLocation")) : null;
        this.country = inits.isInitialized("country") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCountry(forProperty("country")) : null;
        this.directedSpecies = inits.isInitialized("directedSpecies") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSpecies(forProperty("directedSpecies")) : null;
        this.gear = inits.isInitialized("gear") ? new ca.gc.dfo.psffs.domain.objects.lookups.QGear(forProperty("gear")) : null;
        this.lengthGroup = inits.isInitialized("lengthGroup") ? new ca.gc.dfo.psffs.domain.objects.lookups.QLengthGroup(forProperty("lengthGroup"), inits.get("lengthGroup")) : null;
        this.lengthUnit = inits.isInitialized("lengthUnit") ? new ca.gc.dfo.psffs.domain.objects.lookups.QLengthUnit(forProperty("lengthUnit")) : null;
        this.nafoDivision = inits.isInitialized("nafoDivision") ? new ca.gc.dfo.psffs.domain.objects.lookups.QNafoDivision(forProperty("nafoDivision")) : null;
        this.observerCompany = inits.isInitialized("observerCompany") ? new ca.gc.dfo.psffs.domain.objects.lookups.QObserverCompany(forProperty("observerCompany")) : null;
        this.quarter = inits.isInitialized("quarter") ? new ca.gc.dfo.psffs.domain.objects.lookups.QQuarter(forProperty("quarter")) : null;
        this.sampledSpecies = inits.isInitialized("sampledSpecies") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSpecies(forProperty("sampledSpecies")) : null;
        this.sampling = inits.isInitialized("sampling") ? new QSampling(forProperty("sampling"), inits.get("sampling")) : null;
        this.status = inits.isInitialized("status") ? new ca.gc.dfo.psffs.domain.objects.lookups.QTSSSamplingStatus(forProperty("status")) : null;
        this.unitArea = inits.isInitialized("unitArea") ? new ca.gc.dfo.psffs.domain.objects.lookups.QUnitArea(forProperty("unitArea"), inits.get("unitArea")) : null;
    }

}

