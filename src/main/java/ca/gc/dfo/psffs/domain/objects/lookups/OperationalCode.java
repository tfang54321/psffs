package ca.gc.dfo.psffs.domain.objects.lookups;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
@Data
public abstract class OperationalCode
{
    @Column(name = "legacy_cd")
    private String legacyCode;

    @Column(name = "edesc")
    private String englishDescription;

    @Column(name = "fdesc")
    private String frenchDescription;

    @Column(name = "code_type")
    private String type;

    @Column(name = "presentation_order")
    private Integer presentationOrder;

    @Column(name = "mod_by", length = 100)
    private String modifiedBy;

    @Column(name = "mod_dt")
    private LocalDate modifiedDate;
}
