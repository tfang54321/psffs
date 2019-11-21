package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLengthUnit is a Querydsl query type for LengthUnit
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLengthUnit extends EntityPathBase<LengthUnit> {

    private static final long serialVersionUID = 117166114L;

    public static final QLengthUnit lengthUnit = new QLengthUnit("lengthUnit");

    public final QOperationalCode _super = new QOperationalCode(this);

    //inherited
    public final StringPath englishDescription = _super.englishDescription;

    //inherited
    public final StringPath frenchDescription = _super.frenchDescription;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    //inherited
    public final StringPath legacyCode = _super.legacyCode;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> presentationOrder = _super.presentationOrder;

    //inherited
    public final StringPath type = _super.type;

    public QLengthUnit(String variable) {
        super(LengthUnit.class, forVariable(variable));
    }

    public QLengthUnit(Path<? extends LengthUnit> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLengthUnit(PathMetadata metadata) {
        super(LengthUnit.class, metadata);
    }

}

