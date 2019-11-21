package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSamplingType is a Querydsl query type for SamplingType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSamplingType extends EntityPathBase<SamplingType> {

    private static final long serialVersionUID = 2076798041L;

    public static final QSamplingType samplingType = new QSamplingType("samplingType");

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

    public QSamplingType(String variable) {
        super(SamplingType.class, forVariable(variable));
    }

    public QSamplingType(Path<? extends SamplingType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSamplingType(PathMetadata metadata) {
        super(SamplingType.class, metadata);
    }

}

