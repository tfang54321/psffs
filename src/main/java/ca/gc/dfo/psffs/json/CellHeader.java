package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.domain.objects.Cell;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;

@Data
public class CellHeader
{
    private Integer year;
    private String species;
    private String dataSource;
    private String bycatch;
    private String country;
    private String quarter;
    private String nafoDivision;
    private String unitArea;
    private String lengthCategory;
    private String gear;
    private Float meshSize;
    private String observerCompany;

    public CellHeader(Cell cell)
    {
        boolean isEnglish = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en");
        if (cell.getCellDefinition() != null) {
            setYear(cell.getCellDefinition().getYear());
            if (cell.getCellDefinition().getSpecies() != null) setSpecies(isEnglish ?
                    cell.getCellDefinition().getSpecies().getEnglishDescription() :
                    cell.getCellDefinition().getSpecies().getFrenchDescription());
        }
        if (cell.getDataSource() != null) setDataSource(isEnglish ? cell.getDataSource().getEnglishDescription() :
                cell.getDataSource().getFrenchDescription());
        if (cell.getBycatchInd() != null) setBycatch(isEnglish ? (cell.getBycatchInd().equals(1) ? "Yes" : "No")
                : (cell.getBycatchInd().equals(1) ? "Oui" : "Non"));
        if (cell.getCountry() != null) setCountry(isEnglish ? cell.getCountry().getDescription() :
                cell.getCountry().getFrenchDescription());
        if (cell.getNafoDivision() != null) setNafoDivision(isEnglish ? cell.getNafoDivision().getEnglishDescription() :
                cell.getNafoDivision().getFrenchDescription());
        if (cell.getUnitArea() != null) setUnitArea(isEnglish ? cell.getUnitArea().getEnglishDescription() :
                cell.getUnitArea().getFrenchDescription());
        if (cell.getLengthCategory() != null) setLengthCategory(isEnglish ? cell.getLengthCategory().getEnglishDescription() :
                cell.getLengthCategory().getFrenchDescription());
        if (cell.getGear() != null) setGear(isEnglish ? cell.getGear().getEnglishDescription() :
                cell.getGear().getFrenchDescription());
        if (cell.getMeshSizeMillimeters() != null) setMeshSize(cell.getMeshSizeMillimeters());
        if (cell.getObserverCompany() != null) setObserverCompany(isEnglish ? cell.getObserverCompany().getEnglishDescription() :
                cell.getObserverCompany().getFrenchDescription());
    }
}
