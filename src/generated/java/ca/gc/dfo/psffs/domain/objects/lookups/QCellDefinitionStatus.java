package ca.gc.dfo.psffs.domain.objects.lookups;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCellDefinitionStatus is a Querydsl query type for CellDefinitionStatus
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCellDefinitionStatus extends EntityPathBase<CellDefinitionStatus> {

    private static final long serialVersionUID = -531366593L;

    public static final QCellDefinitionStatus cellDefinitionStatus = new QCellDefinitionStatus("cellDefinitionStatus");

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

    public QCellDefinitionStatus(String variable) {
        super(CellDefinitionStatus.class, forVariable(variable));
    }

    public QCellDefinitionStatus(Path<? extends CellDefinitionStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCellDefinitionStatus(PathMetadata metadata) {
        super(CellDefinitionStatus.class, metadata);
    }

}

