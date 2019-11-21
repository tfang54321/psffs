package ca.gc.dfo.psffs.domain.objects.lookups;

import ca.gc.dfo.psffs.domain.objects.BaseEntity;
import ca.gc.dfo.spring_commons.commons_web.annotations.LookupActiveFlag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

@MappedSuperclass
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseLookup extends BaseEntity
{
    @Transient
    private Integer id;

    @Column(name = "legacy_cd", length = 20)
    private String legacyCode;

    @Column(name = "edesc")
    private String englishDescription;

    @Column(name = "fdesc")
    private String frenchDescription;

    @LookupActiveFlag
    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlagInd;

    @Column(name = "presentation_order")
    private Integer order;

    @Transient
    private String description;

    @PostLoad
    public void postLoad()
    {
        setDescription(LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                getEnglishDescription() : getFrenchDescription());
    }

    public abstract Integer getId();
    public abstract void setId(Integer id);

    public void setActiveFlag(Integer activeFlag)
    {
        setActiveFlagInd(activeFlag != null && activeFlag.equals(1));
    }

    public Integer getActiveFlag()
    {
        return this.activeFlagInd ? 1 : 0;
    }
}
