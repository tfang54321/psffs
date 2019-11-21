package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSamplingDataStatus is a Querydsl query type for SamplingDataStatus
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSamplingDataStatus extends EntityPathBase<SamplingDataStatus> {

    private static final long serialVersionUID = 2066746907L;

    public static final QSamplingDataStatus samplingDataStatus = new QSamplingDataStatus("samplingDataStatus");

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

    public QSamplingDataStatus(String variable) {
        super(SamplingDataStatus.class, forVariable(variable));
    }

    public QSamplingDataStatus(Path<? extends SamplingDataStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSamplingDataStatus(PathMetadata metadata) {
        super(SamplingDataStatus.class, metadata);
    }

}

