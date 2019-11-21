package ca.gc.dfo.psffs.domain.objects.security;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2042164138L;

    public static final QUser user = new QUser("user");

    public final ca.gc.dfo.psffs.domain.objects.QBaseEntity _super = new ca.gc.dfo.psffs.domain.objects.QBaseEntity(this);

    public final BooleanPath activeFlagInd = createBoolean("activeFlagInd");

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    public final StringPath initials = createString("initials");

    public final StringPath lastName = createString("lastName");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final StringPath ntPrincipal = createString("ntPrincipal");

    public final NumberPath<Long> partyId = createNumber("partyId", Long.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final ListPath<UserPreference, QUserPreference> userPreferences = this.<UserPreference, QUserPreference>createList("userPreferences", UserPreference.class, QUserPreference.class, PathInits.DIRECT2);

    public final ListPath<UserRole, QUserRole> userRoles = this.<UserRole, QUserRole>createList("userRoles", UserRole.class, QUserRole.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

