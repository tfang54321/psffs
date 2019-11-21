package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.DataSource;
import ca.gc.dfo.psffs.domain.objects.lookups.SamplingType;
import ca.gc.dfo.psffs.domain.objects.lookups.Species;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "SAMPLING", indexes = {
        @Index(columnList = "sampling_cd", name = "SAMPLING_CD_INDEX", unique = true),
        @Index(columnList = "sampling_type_id", name = "FKIND_SMP_SAMPLING_TYPE_ID"),
        @Index(columnList = "species_id", name = "FKIND_SMP_SPECIES_ID"),
        @Index(columnList = "data_source_id", name = "FKIND_SMP_DATA_SOURCE_ID"),
        @Index(columnList = "sampling_cell_id", name = "FKIND_SMP_SMP_CELL_ID")
})
@Getter
@Setter
public class Sampling extends BaseEntity
{
    @Id
    @GeneratedValue(generator = "samplingSeqGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "samplingSeqGenerator", sequenceName = "SAMPLING_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "sampling_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sampling_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SamplingType samplingType;
    @Column(name = "sampling_type_id", nullable = false)
    private Integer samplingTypeId;

    @Column(name = "sampling_cd", unique = true, nullable = false)
    private String samplingCode; //YEAR(YYYY)-INITIAL(II)-NUMBER(NNNN)

    @Column(name = "local_sampling_cd")
    private String localSamplingCode;

    @Column(name = "year_crt", nullable = false)
    private Integer yearCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Species species;
    @Column(name = "species_id", nullable = false)
    private Integer speciesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DataSource dataSource;
    @Column(name = "data_source_id", nullable = false)
    private Integer dataSourceId;

    @Column(name = "length_group_min", nullable = false)
    private Integer lengthGroupMin;

    @Column(name = "length_group_max", nullable = false)
    private Integer lengthGroupMax;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "sampling")
    private LengthFrequency lengthFrequency;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "sampling")
    private TripSetSpecies tripSetSpecies;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "sampling")
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("presOrder ASC")
    private List<SamplingEntry> entries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sampling")
    private List<SamplingData> samplingDataList;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "sampling_cell_id", referencedColumnName = "sampling_cell_id", nullable = true)
    private Cell cell;

    @PrePersist
    @PreUpdate
    public void prePersist()
    {
        if (getYearCreated() == null) {
            setYearCreated(LocalDate.now().getYear());
        }
    }

    public Integer getSamplingTypeId()
    {
        return this.samplingTypeId != null ? samplingTypeId : -1;
    }
}
