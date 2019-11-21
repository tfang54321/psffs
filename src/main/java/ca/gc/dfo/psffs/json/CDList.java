package ca.gc.dfo.psffs.json;

import ca.gc.dfo.psffs.forms.CellDefinitionForm;
import lombok.Data;

import java.util.List;

@Data
public class CDList {
    private List<CellDefinitionForm> data;

    public CDList(){
    }
    public CDList(List<CellDefinitionForm> cellDefinitionForms){
        for (CellDefinitionForm celldef:
             cellDefinitionForms) {
            String disableDel = "";
            if(celldef.getStatusId().getId() == 28 || celldef.getStatusId().getId() == 29){
                disableDel = "disabled";
            }
            celldef.setButtons("<a href='javascript: cellDefinition.goToEdit(" + celldef.getId() + ")' title='Edit' class='btn btn-link cell-btn large-buttons'><span class='glyphicon glyphicon-edit'></span></a><a href='javascript: cellDefinition.removeCellDef(" + celldef.getId() + ")' " + disableDel + " title='Remove' class='btn btn-link cell-btn large-buttons'><span class='glyphicon glyphicon-trash'></span></a>");
            celldef.setCheckbox("<input type='checkbox' class='cd-list-checkbox list-checkbox' value='" + celldef.getId() + "' />");
        }
        setData(cellDefinitionForms);
    }
}
