package ca.gc.dfo.psffs.domain.objects.security;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPreference is a Querydsl query type for UserPreference
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUserPreference extends EntityPathBase<UserPreference> {

    private static final long serialVersionUID = 1988800977L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPreference userPreference = new QUserPreference("userPreference");

    public final ca.gc.dfo.psffs.domain.objects.QBaseEntity _super = new ca.gc.dfo.psffs.domain.objects.QBaseEntity(this);

    public final StringPath _prefType = createString("_prefType");

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final StringPath preferenceData = createString("preferenceData");

    public final QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Long> userPreferenceId = createNumber("userPreferenceId", Long.class);

    public QUserPreference(String variable) {
        this(UserPreference.class, forVariable(variable), INITS);
    }

    public QUserPreference(Path<? extends UserPreference> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPreference(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPreference(PathMetadata metadata, PathInits inits) {
        this(UserPreference.class, metadata, inits);
    }

    public QUserPreference(Class<? extends UserPreference> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

