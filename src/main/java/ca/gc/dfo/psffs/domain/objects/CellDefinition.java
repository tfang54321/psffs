package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.CellDefinitionStatus;
import ca.gc.dfo.psffs.domain.objects.lookups.Species;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
//Initial value for sequence is set to 100 since there are 2 auto generated values in the table that do no use the sequence
@SequenceGenerator(name = "cellDefSeqGen", sequenceName = "CELL_DEFINITION_ID_SEQ", allocationSize = 1, initialValue = 100)
@Table(name = "CELL_DEFINITION", indexes = {
        @Index(columnList = "species_id", name = "FKIND_CELL_DEF_SPECIES_ID"),
        @Index(columnList = "status_id", name = "FKIND_CELL_DEF_STATUS_ID"),
})
@Data
@ToString(callSuper = true, exclude = {"species"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CellDefinition extends BaseEntity
{
    //Main
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "cellDefSeqGen", strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "crt_year")
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Species species;
    @Column(name = "species_id")
    private Integer speciesId;

    @Column(name = "description", length = 1000)
    private String desc;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private CellDefinitionStatus statusId;

    @Column(name = "migrated_to_cell_def_id")
    private Long migratedToCellDefId;

    //Cell Definition Requirements
    @Column(name = "data_source_ind")
    private Integer dataSource;

    @Column(name = "by_catch_ind")
    private Integer byCatch;

    @Column(name = "country_ind")
    private Integer country;

    @Column(name = "quarter_ind")
    private Integer quarter;

    @Column(name = "nafo_div_ind")
    private Integer nafoDivision;

    @Column(name = "unit_area_ind")
    private Integer unitArea;

    @Column(name = "vessel_length_cat_ind")
    private Integer vesselLengthCat;

    @Column(name = "gear_ind")
    private Integer gear;

    @Column(name = "mesh_size_ind")
    private Integer mesh;

    @Column(name = "observer_company_ind")
    private Integer observerCompany;

    //Sampling Targets
    @Column(name = "smp_target_nbr_otolith")
    private Integer otolithT;

    @Column(name = "smp_target_nbr_stomach")
    private Integer stomachT;

    @Column(name = "smp_target_nbr_frozen")
    private Integer frozenT;

    @Column(name = "smp_target_nbr_weight")
    private Integer weightT;
}
