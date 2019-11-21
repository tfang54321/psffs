package ca.gc.dfo.psffs.domain.objects;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "SAMPLING_IDENT_GEN",
       indexes = @Index(columnList = "initials", name = "SMPLNG_IDNT_GEN_INITIALS_INDEX"))
@Data
public class SamplingIdentifierGenerator
{
    @Id
    @GeneratedValue(generator = "samplingIdentGenSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "samplingIdentGenSeqGenerator", sequenceName = "SAMPLING_IDENT_GEN_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "sampling_ident_gen_id")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "initials", length = 5)
    private String initials;

    @Column(name = "identifier")
    private Integer identifier;

    public void incrementIdentifier()
    {
        setIdentifier(getIdentifier() + 1);
    }

    @Override
    public String toString()
    {
        return this.year + "-" + this.initials + "-" + String.format("%4s", ""+this.identifier).replace(' ', '0');
    }
}
