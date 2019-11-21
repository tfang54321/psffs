package ca.gc.dfo.psffs.domain.objects.security;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = -2042257151L;

    public static final QRole role = new QRole("role");

    public final ca.gc.dfo.psffs.domain.objects.QBaseEntity _super = new ca.gc.dfo.psffs.domain.objects.QBaseEntity(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final StringPath englishDescription = createString("englishDescription");

    public final StringPath englishRoleTitle = createString("englishRoleTitle");

    public final StringPath frenchDescription = createString("frenchDescription");

    public final StringPath frenchRoleTitle = createString("frenchRoleTitle");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

    public final StringPath roleName = createString("roleName");

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

