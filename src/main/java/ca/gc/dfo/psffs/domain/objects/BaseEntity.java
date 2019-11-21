package ca.gc.dfo.psffs.domain.objects;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
@Data
public abstract class BaseEntity
{
    @Column(name = "crt_by", length = 100, nullable = false)
    private String createdBy;

    @Column(name = "crt_dt", nullable = false)
    private LocalDate createdDate;

    @Column(name = "mod_by", length = 100)
    private String modifiedBy;

    @Column(name = "mod_dt")
    private LocalDate modifiedDate;

    public void setActor(String actor)
    {
        setModifiedBy(actor);
        setModifiedDate(LocalDate.now());

        if (createdDate == null) {
            setCreatedBy(actor);
            setCreatedDate(LocalDate.now());
        }
    }
}
