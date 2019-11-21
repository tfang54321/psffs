package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QStomachContent is a Querydsl query type for StomachContent
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QStomachContent extends EntityPathBase<StomachContent> {

    private static final long serialVersionUID = 144590058L;

    public static final QStomachContent stomachContent = new QStomachContent("stomachContent");

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

    public QStomachContent(String variable) {
        super(StomachContent.class, forVariable(variable));
    }

    public QStomachContent(Path<? extends StomachContent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStomachContent(PathMetadata metadata) {
        super(StomachContent.class, metadata);
    }

}

