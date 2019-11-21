package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSamplingStatus is a Querydsl query type for SamplingStatus
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSamplingStatus extends EntityPathBase<SamplingStatus> {

    private static final long serialVersionUID = -1390550703L;

    public static final QSamplingStatus samplingStatus = new QSamplingStatus("samplingStatus");

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

    public QSamplingStatus(String variable) {
        super(SamplingStatus.class, forVariable(variable));
    }

    public QSamplingStatus(Path<? extends SamplingStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSamplingStatus(PathMetadata metadata) {
        super(SamplingStatus.class, metadata);
    }

}

