package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLengthCategory is a Querydsl query type for LengthCategory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLengthCategory extends EntityPathBase<LengthCategory> {

    private static final long serialVersionUID = -1693502308L;

    public static final QLengthCategory lengthCategory = new QLengthCategory("lengthCategory");

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

    //inherited
    public final StringPath legacyCode = _super.legacyCode;

    public final NumberPath<Float> maxMeters = createNumber("maxMeters", Float.class);

    public final NumberPath<Float> minMeters = createNumber("minMeters", Float.class);

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public QLengthCategory(String variable) {
        super(LengthCategory.class, forVariable(variable));
    }

    public QLengthCategory(Path<? extends LengthCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLengthCategory(PathMetadata metadata) {
        super(LengthCategory.class, metadata);
    }

}

