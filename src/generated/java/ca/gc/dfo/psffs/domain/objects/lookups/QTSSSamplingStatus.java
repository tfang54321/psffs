package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTSSSamplingStatus is a Querydsl query type for TSSSamplingStatus
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTSSSamplingStatus extends EntityPathBase<TSSSamplingStatus> {

    private static final long serialVersionUID = 779076629L;

    public static final QTSSSamplingStatus tSSSamplingStatus = new QTSSSamplingStatus("tSSSamplingStatus");

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

    public QTSSSamplingStatus(String variable) {
        super(TSSSamplingStatus.class, forVariable(variable));
    }

    public QTSSSamplingStatus(Path<? extends TSSSamplingStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTSSSamplingStatus(PathMetadata metadata) {
        super(TSSSamplingStatus.class, metadata);
    }

}

