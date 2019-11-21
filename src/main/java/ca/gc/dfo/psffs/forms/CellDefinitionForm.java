package ca.gc.dfo.psffs.forms;

import ca.gc.dfo.psffs.domain.objects.CellDefinition;
import ca.gc.dfo.psffs.domain.objects.lookups.CellDefinitionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellDefinitionForm {
    //Main
    private Long Id;
    private Integer year;
    private Integer speciesId;
    private String desc;
    private CellDefinitionStatus statusId;
    private String statusStr;
    private Long migratedToCellDefId;

    //Cell Definition Requirements
    private Integer dataSource;
    private Integer byCatch;
    private Integer country;
    private Integer quarter;
    private Integer nafoDivision;
    private Integer unitArea;
    private Integer vesselLengthCat;
    private Integer gear;
    private Integer mesh;
    private Integer observerCompany;

    //Sampling Targets
    private Integer otolithT;
    private Integer stomachT;
    private Integer frozenT;
    private Integer weightT;

    private String speciesName;
    //Buttons Mark-up For List View
    private String buttons;
    private String checkbox;
}
