package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSpecies is a Querydsl query type for Species
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSpecies extends EntityPathBase<Species> {

    private static final long serialVersionUID = -1952850524L;

    public static final QSpecies species = new QSpecies("species");

    public final QBaseLookup _super = new QBaseLookup(this);

    //inherited
    public final BooleanPath activeFlagInd = _super.activeFlagInd;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    //inherited
    public final StringPath englishDescription = _super.englishDescription;

    //inherited
    public final StringPath frenchDescription = _super.frenchDescription;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> isscaapCode = createNumber("isscaapCode", Integer.class);

    //inherited
    public final StringPath legacyCode = _super.legacyCode;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final StringPath nafoSpeciesCode = createString("nafoSpeciesCode");

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public final StringPath scientificName = createString("scientificName");

    public final NumberPath<Integer> taxonomicCode = createNumber("taxonomicCode", Integer.class);

    public QSpecies(String variable) {
        super(Species.class, forVariable(variable));
    }

    public QSpecies(Path<? extends Species> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSpecies(PathMetadata metadata) {
        super(Species.class, metadata);
    }

}

