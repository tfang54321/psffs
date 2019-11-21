package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.SampleType;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SMPLNG_LENGTH_FREQUENCY_COUNT", indexes = {
        @Index(columnList = "length_frequency_id", name = "FKIND_SMP_LF_CNT_LF_ID"),
        @Index(columnList = "sample_type_id", name = "FKIND_SMP_LF_CNT_SMP_TYPE_ID")
})
@Data
public class LengthFrequencyCount
{
    @Id
    @GeneratedValue(generator = "lfCountSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "lfCountSeqGenerator", sequenceName = "SMPLNG_LENGTH_FREQ_CNT_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "lf_count_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "length_frequency_id", referencedColumnName = "length_frequency_id", nullable = false)
    private LengthFrequency lengthFrequency;

    @Column(name = "length_group", nullable = false)
    private Integer length;

    @Column(name = "sex_cd")
    private String sex;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sample_type_id", referencedColumnName = "id")
    private SampleType sampleType;

    @Column(name = "lf_count")
    private Integer count;

    @Column(name = "mod_by", length = 100)
    private String modifiedBy;

    @Column(name = "mod_dt")
    private LocalDate modifiedDate;

    public void setActor(String actor)
    {
        setModifiedBy(actor);
        setModifiedDate(LocalDate.now());
    }

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        if (this.sex != null && this.sex.trim().length() > 0) b.append(this.sex);
        if (this.sampleType != null && this.sampleType.getLegacyCode() != null) {
            b.append("_")
             .append(this.sampleType.getLegacyCode());
        }
        if (this.length != null) {
            b.append("[")
             .append(this.length)
             .append("]");
        }
        return b.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }
}
