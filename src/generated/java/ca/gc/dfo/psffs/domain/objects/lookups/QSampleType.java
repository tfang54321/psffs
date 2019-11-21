package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSampleType is a Querydsl query type for SampleType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSampleType extends EntityPathBase<SampleType> {

    private static final long serialVersionUID = -900612452L;

    public static final QSampleType sampleType = new QSampleType("sampleType");

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

    public QSampleType(String variable) {
        super(SampleType.class, forVariable(variable));
    }

    public QSampleType(Path<? extends SampleType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSampleType(PathMetadata metadata) {
        super(SampleType.class, metadata);
    }

}

