package ca.gc.dfo.psffs.domain.objects.audit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPsffs_RevisionEntity is a Querydsl query type for Psffs_RevisionEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPsffs_RevisionEntity extends EntityPathBase<Psffs_RevisionEntity> {

    private static final long serialVersionUID = 1507502595L;

    public static final QPsffs_RevisionEntity psffs_RevisionEntity = new QPsffs_RevisionEntity("psffs_RevisionEntity");

    public final DateTimePath<java.util.Date> createdDate = createDateTime("createdDate", java.util.Date.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final SetPath<String, StringPath> modifiedEntityNames = this.<String, StringPath>createSet("modifiedEntityNames", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath userAccount = createString("userAccount");

    public final StringPath userFirstName = createString("userFirstName");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final StringPath userLastName = createString("userLastName");

    public QPsffs_RevisionEntity(String variable) {
        super(Psffs_RevisionEntity.class, forVariable(variable));
    }

    public QPsffs_RevisionEntity(Path<? extends Psffs_RevisionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPsffs_RevisionEntity(PathMetadata metadata) {
        super(Psffs_RevisionEntity.class, metadata);
    }

}

