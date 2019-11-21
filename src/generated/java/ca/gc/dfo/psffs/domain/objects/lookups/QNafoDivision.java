package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QNafoDivision is a Querydsl query type for NafoDivision
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNafoDivision extends EntityPathBase<NafoDivision> {

    private static final long serialVersionUID = -441682143L;

    public static final QNafoDivision nafoDivision = new QNafoDivision("nafoDivision");

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

    public QNafoDivision(String variable) {
        super(NafoDivision.class, forVariable(variable));
    }

    public QNafoDivision(Path<? extends NafoDivision> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNafoDivision(PathMetadata metadata) {
        super(NafoDivision.class, metadata);
    }

}

