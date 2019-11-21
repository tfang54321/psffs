package ca.gc.dfo.psffs.domain.objects;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSystemSetting is a Querydsl query type for SystemSetting
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSystemSetting extends EntityPathBase<SystemSetting> {

    private static final long serialVersionUID = -772947074L;

    public static final QSystemSetting systemSetting = new QSystemSetting("systemSetting");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DatePath<java.time.LocalDate> createdDate = _super.createdDate;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DatePath<java.time.LocalDate> modifiedDate = _super.modifiedDate;

    public final StringPath settingName = createString("settingName");

    public final StringPath settingValue = createString("settingValue");

    public QSystemSetting(String variable) {
        super(SystemSetting.class, forVariable(variable));
    }

    public QSystemSetting(Path<? extends SystemSetting> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSystemSetting(PathMetadata metadata) {
        super(SystemSetting.class, metadata);
    }

}

