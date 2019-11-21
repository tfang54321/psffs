package ca.gc.dfo.psffs.domain.objects;

import ca.gc.dfo.psffs.domain.objects.lookups.*;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "SAMPLING_CELL", indexes = {
        @Index(columnList = "cell_cd", name = "SAMPLING_CELL_CD_INDEX", unique = true),
        @Index(columnList = "cell_definition_id", name = "FKIND_SMP_CELL_CELL_DEF_ID"),
        @Index(columnList = "data_source_id", name = "FKIND_SMP_CELL_DATASOURCE_ID"),
        @Index(columnList = "country_id", name = "FKIND_SMP_CELL_COUNTRY_ID"),
        @Index(columnList = "quarter_id", name = "FKIND_SMP_CELL_QUARTER_ID"),
        @Index(columnList = "nafo_division_id", name = "FKIND_SMP_CELL_NAFO_DIV_ID"),
        @Index(columnList = "unit_area_id", name = "FKIND_SMP_CELL_UNIT_AREA_ID"),
        @Index(columnList = "vessel_length_category_id", name = "FKIND_SMP_CELL_LENG_CAT_ID"),
        @Index(columnList = "gear_id", name = "FKIND_SMP_CELL_GEAR_ID"),
        @Index(columnList = "observer_company_id", name = "FKIND_SMP_CELL_OBSV_COMP_ID")
})
@Getter
@Setter
public class Cell extends BaseEntity
{
    @Id
    @GeneratedValue(generator = "cellSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "cellSeqGen", sequenceName = "SAMPLING_CELL_ID_SEQ", allocationSize = 1, initialValue = 100)
    @Column(name = "sampling_cell_id")
    private Long cellId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "cell_definition_id", referencedColumnName = "id", nullable = false)
    private CellDefinition cellDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DataSource dataSource;
    @Column(name = "data_source_id")
    private Integer dataSourceId;

    @Column(name = "bycatch_ind")
    private Integer bycatchInd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Country country;
    @Column(name = "country_id")
    private Integer countryId;//Quota ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quarter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Quarter quarter;
    @Column(name = "quarter_id")
    private Integer quarterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nafo_division_id", referencedColumnName = "id", insertable = false, updatable = false)
    private NafoDivision nafoDivision;
    @Column(name = "nafo_division_id")
    private Integer nafoDivisionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_area_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UnitArea unitArea;
    @Column(name = "unit_area_id")
    private Integer unitAreaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vessel_length_category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LengthCategory lengthCategory;
    @Column(name = "vessel_length_category_id")
    private Integer vesselLengthCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gear_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Gear gear;
    @Column(name = "gear_id")
    private Integer gearId;

    @Column(name = "mesh_size_mm")
    private Float meshSizeMillimeters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observer_company_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ObserverCompany observerCompany;
    @Column(name = "observer_company_id")
    private Integer observerCompanyId;

    @Column(name = "latest_storage_nbr", nullable = false)
    private Long latestStorageNumber;

    @Column(name = "cell_cd", nullable = false, unique = true, length = 500)
    private String cellCode;

    @PrePersist
    @PreUpdate
    public void resetCellCode()
    {
        setCellCode(null);
        setCellCode(this.toString());
        setActor(SecurityHelper.getNtPrincipal());
    }

    @Override
    public String toString()
    {
        String cellCode = getCellCode();
        CellDefinition cellDef = getCellDefinition();
        if (cellCode == null || cellCode.trim().length() < 1 && cellDef != null) {
            List<String> cellCriteriaList = new ArrayList<>();
            appendCellCriteria(cellDef.getYear(), true, cellCriteriaList);
            appendCellCriteria(cellDef.getSpeciesId(), true, cellCriteriaList);
            appendCellCriteria(getDataSourceId(), cellDef.getDataSource() >= 1, cellCriteriaList);
            appendCellCriteria(getBycatchInd(), cellDef.getByCatch() >= 1, cellCriteriaList);
            appendCellCriteria(getQuotaId(), cellDef.getCountry() >= 1, cellCriteriaList);
            appendCellCriteria(getQuarterId(), cellDef.getQuarter() >= 1, cellCriteriaList);
            appendCellCriteria(getNafoDivisionId(), cellDef.getNafoDivision() >= 1, cellCriteriaList);
            appendCellCriteria(getUnitAreaId(), cellDef.getUnitArea() >= 1, cellCriteriaList);
            appendCellCriteria(getVesselLengthCategoryId(), cellDef.getVesselLengthCat() >= 1, cellCriteriaList);
            appendCellCriteria(getGearId(), cellDef.getGear() >= 1, cellCriteriaList);
            appendCellCriteria(getMeshSizeMillimeters(), cellDef.getMesh() >= 1, cellCriteriaList);
            appendCellCriteria(getObserverCompanyId(), cellDef.getObserverCompany() >= 1, cellCriteriaList);

            cellCode = String.join("|", cellCriteriaList);
        }
        return cellCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Cell && this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }

    public Integer getQuotaId()
    {
        return getCountryId();
    }

    public void setQuotaId(Integer quotaId)
    {
        setCountryId(quotaId);
    }

    private void appendCellCriteria(Serializable criteria, boolean isRequirement, List<String> cellCriteriaList)
    {
        String cellCriteria = "-";
        if (criteria != null && isRequirement) {
            if (criteria instanceof Float) cellCriteria = String.format(Locale.US, "%.2f", (Float)criteria);
            else if (criteria instanceof Double) cellCriteria = String.format(Locale.US, "%.2f", (Double)criteria);
            else cellCriteria = criteria.toString();
        }
        cellCriteriaList.add(cellCriteria);
    }
}
