package ca.gc.dfo.psffs.domain.objects;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PSFFS_EXTRACT", indexes = {
        @Index(columnList = "extract_type", name = "EXTRACT_TYPE_INDEX"),
        @Index(columnList = "crt_by", name = "EXTRACT_CRT_BY_INDEX")
})
@Data
public class Extract
{
    @Id
    @Column(name = "extract_id")
    @GeneratedValue(generator = "extractSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "extractSeqGen", sequenceName = "PSFFS_EXTRACT_ID_SEQ", allocationSize = 1)
    private Long extractId;

    @Column(name = "extract_type", length = 100, nullable = false)
    private String type;

    @Column(name = "extract_data")
    @Lob
    private String data;

    @Column(name = "extract_has_data", nullable = false)
    private Boolean hasData;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "crt_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "crt_dt")
    private LocalDate createdDate;

    public void setActor(String actor)
    {
        setCreatedBy(actor);
        setCreatedDate(LocalDate.now());
    }
}
