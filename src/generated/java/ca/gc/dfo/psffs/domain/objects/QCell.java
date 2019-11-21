package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCell is a Querydsl query type for Cell
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCell extends EntityPathBase<Cell> {

    private static final long serialVersionUID = 1492675173L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCell cell = new QCell("cell");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> bycatchInd = createNumber("bycatchInd", Integer.class);

    public final StringPath cellCode = createString("cellCode");

    public final QCellDefinition cellDefinition;

    public final NumberPath<Long> cellId = createNumber("cellId", Long.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCountry country;

    public final NumberPath<Integer> countryId = createNumber("countryId", Integer.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final ca.gc.dfo.psffs.domain.objects.lookups.QDataSource dataSource;

    public final NumberPath<Integer> dataSourceId = createNumber("dataSourceId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QGear gear;

    public final NumberPath<Integer> gearId = createNumber("gearId", Integer.class);

    public final NumberPath<Long> latestStorageNumber = createNumber("latestStorageNumber", Long.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QLengthCategory lengthCategory;

    public final NumberPath<Float> meshSizeMillimeters = createNumber("meshSizeMillimeters", Float.class);

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

    public final ca.gc.dfo.psffs.domain.objects.lookups.QUnitArea unitArea;

    public final NumberPath<Integer> unitAreaId = createNumber("unitAreaId", Integer.class);

    public final NumberPath<Integer> vesselLengthCategoryId = createNumber("vesselLengthCategoryId", Integer.class);

    public QCell(String variable) {
        this(Cell.class, forVariable(variable), INITS);
    }

    public QCell(Path<? extends Cell> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCell(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCell(PathMetadata metadata, PathInits inits) {
        this(Cell.class, metadata, inits);
    }

    public QCell(Class<? extends Cell> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cellDefinition = inits.isInitialized("cellDefinition") ? new QCellDefinition(forProperty("cellDefinition"), inits.get("cellDefinition")) : null;
        this.country = inits.isInitialized("country") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCountry(forProperty("country")) : null;
        this.dataSource = inits.isInitialized("dataSource") ? new ca.gc.dfo.psffs.domain.objects.lookups.QDataSource(forProperty("dataSource")) : null;
        this.gear = inits.isInitialized("gear") ? new ca.gc.dfo.psffs.domain.objects.lookups.QGear(forProperty("gear")) : null;
        this.lengthCategory = inits.isInitialized("lengthCategory") ? new ca.gc.dfo.psffs.domain.objects.lookups.QLengthCategory(forProperty("lengthCategory")) : null;
        this.nafoDivision = inits.isInitialized("nafoDivision") ? new ca.gc.dfo.psffs.domain.objects.lookups.QNafoDivision(forProperty("nafoDivision")) : null;
        this.observerCompany = inits.isInitialized("observerCompany") ? new ca.gc.dfo.psffs.domain.objects.lookups.QObserverCompany(forProperty("observerCompany")) : null;
        this.quarter = inits.isInitialized("quarter") ? new ca.gc.dfo.psffs.domain.objects.lookups.QQuarter(forProperty("quarter")) : null;
        this.unitArea = inits.isInitialized("unitArea") ? new ca.gc.dfo.psffs.domain.objects.lookups.QUnitArea(forProperty("unitArea"), inits.get("unitArea")) : null;
    }

}

