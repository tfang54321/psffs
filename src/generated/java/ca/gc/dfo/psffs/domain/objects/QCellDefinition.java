package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCellDefinition is a Querydsl query type for CellDefinition
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCellDefinition extends EntityPathBase<CellDefinition> {

    private static final long serialVersionUID = 764510616L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCellDefinition cellDefinition = new QCellDefinition("cellDefinition");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> byCatch = createNumber("byCatch", Integer.class);

    public final NumberPath<Integer> country = createNumber("country", Integer.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final NumberPath<Integer> dataSource = createNumber("dataSource", Integer.class);

    public final StringPath desc = createString("desc");

    public final NumberPath<Integer> frozenT = createNumber("frozenT", Integer.class);

    public final NumberPath<Integer> gear = createNumber("gear", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> mesh = createNumber("mesh", Integer.class);

    public final NumberPath<Long> migratedToCellDefId = createNumber("migratedToCellDefId", Long.class);

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final NumberPath<Integer> nafoDivision = createNumber("nafoDivision", Integer.class);

    public final NumberPath<Integer> observerCompany = createNumber("observerCompany", Integer.class);

    public final NumberPath<Integer> otolithT = createNumber("otolithT", Integer.class);

    public final NumberPath<Integer> quarter = createNumber("quarter", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QSpecies species;

    public final NumberPath<Integer> speciesId = createNumber("speciesId", Integer.class);

    public final ca.gc.dfo.psffs.domain.objects.lookups.QCellDefinitionStatus statusId;

    public final NumberPath<Integer> stomachT = createNumber("stomachT", Integer.class);

    public final NumberPath<Integer> unitArea = createNumber("unitArea", Integer.class);

    public final NumberPath<Integer> vesselLengthCat = createNumber("vesselLengthCat", Integer.class);

    public final NumberPath<Integer> weightT = createNumber("weightT", Integer.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QCellDefinition(String variable) {
        this(CellDefinition.class, forVariable(variable), INITS);
    }

    public QCellDefinition(Path<? extends CellDefinition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCellDefinition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCellDefinition(PathMetadata metadata, PathInits inits) {
        this(CellDefinition.class, metadata, inits);
    }

    public QCellDefinition(Class<? extends CellDefinition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.species = inits.isInitialized("species") ? new ca.gc.dfo.psffs.domain.objects.lookups.QSpecies(forProperty("species")) : null;
        this.statusId = inits.isInitialized("statusId") ? new ca.gc.dfo.psffs.domain.objects.lookups.QCellDefinitionStatus(forProperty("statusId")) : null;
    }

}

