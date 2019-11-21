package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCatchCategory is a Querydsl query type for CatchCategory
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCatchCategory extends EntityPathBase<CatchCategory> {

    private static final long serialVersionUID = 1788924737L;

    public static final QCatchCategory catchCategory = new QCatchCategory("catchCategory");

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

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    //inherited
    public final NumberPath<Integer> order = _super.order;

    public QCatchCategory(String variable) {
        super(CatchCategory.class, forVariable(variable));
    }

    public QCatchCategory(Path<? extends CatchCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCatchCategory(PathMetadata metadata) {
        super(CatchCategory.class, metadata);
    }

}

