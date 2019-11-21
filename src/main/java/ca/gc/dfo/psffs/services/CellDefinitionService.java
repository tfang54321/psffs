package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.controllers.CellDefinitionsController;
import ca.gc.dfo.psffs.domain.objects.Cell;
import ca.gc.dfo.psffs.domain.objects.CellDefinition;
import ca.gc.dfo.psffs.domain.repositories.CellDefinitionRepository;
import ca.gc.dfo.psffs.domain.repositories.CellRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.CellDefinitionStatusRepository;
import ca.gc.dfo.psffs.forms.CellDefinitionForm;
import ca.gc.dfo.psffs.json.CDList;
import ca.gc.dfo.psffs.json.MigrateStatus;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.BaseService;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CellDefinitionService extends BaseService
{
    @Autowired
    private CellDefinitionRepository cellDefinitionRepository;

    @Autowired
    private CellDefinitionStatusRepository cellDefinitionStatusRepository;

    @Autowired
    private CellRepository cellRepository;

    @Autowired
    private LookupService lookupService;

    public List<CellDefinition> getAllCellDefinitions() {
        return cellDefinitionRepository.findAll();
    }

    public List<CellDefinitionForm> getAllCellDefinitionForms() {
        return getAllCellDefinitions().stream()
                                      .map(this::convertToForm)
                                      .collect(Collectors.toList());
    }

    //CDList constructor fills buttons atrribute with button markup
    public CDList getCDList(){
        return new CDList(getAllCellDefinitionForms());
    }

    public CellDefinition getCellDefinitionById(Long id){
        return cellDefinitionRepository.getCellDefinitionById(id);
    }

    public CellDefinitionForm getCellDefinitionFormById(Long id){
        return convertToForm(getCellDefinitionById(id));
    }

    public CellDefinition getCellDefinitionByYearAndSpeciesId(int year, int speciesId){ return cellDefinitionRepository.getCellDefinitionByYearAndSpeciesId(year, speciesId); }

    public List<CellDefinition> getAllByYear(int year){ return cellDefinitionRepository.getAllByYear(year); }

    public List<CellDefinition> getAllNonHistoricCellDefinitions(){ return cellDefinitionRepository.getCellDefinitionsByStatusId_IdNot(29); }

    public CellDefinition addCellDefinitionForm(CellDefinitionForm cellDefinitionForm){
        CellDefinition cellDef = convertFromForm(cellDefinitionForm);
        cellDef.setStatusId(cellDefinitionStatusRepository.getOne(27));
        cellDef.setActor(SecurityHelper.getNtPrincipal());
        cellDef = cellDefinitionRepository.save(cellDef);

        return cellDef;
    }

    @Transactional
    public CellDefinition saveCellDefinition(CellDefinition cellDef){
        return cellDefinitionRepository.save(cellDef);
    }

    public Boolean containsCells(Long id){
        List<Cell> cellList = cellRepository.findAllByCellDefinitionId(id);
        if(cellList.size() != 0){
            return true;
        }
        else {
            return false;
        }
    }

    @Transactional
    public void editCellDefinitionForm(CellDefinitionForm cellDefinitionForm){
        try{
            CellDefinition cellDef = convertFromForm(cellDefinitionForm);
            cellDef.setStatusId(cellDefinitionRepository.getCellDefinitionById(cellDefinitionForm.getId()).getStatusId());
            cellDef.setMigratedToCellDefId(cellDefinitionRepository.getCellDefinitionById(cellDefinitionForm.getId()).getMigratedToCellDefId());
            cellDef.setActor(SecurityHelper.getNtPrincipal());
            cellDefinitionRepository.save(cellDef);
        }
            catch(Exception e){
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void removeCellDefinition(Long id){
        cellDefinitionRepository.delete(getCellDefinitionById(id));
    }

    public Integer[] getActiveYears(int extraYear){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currYear = calendar.get(Calendar.YEAR) + 1;

        //Minimum year set to 2000, have to use 1999 to fill the list up to then
        int listSize = currYear - 1999;

        Integer[] years = new Integer[listSize];
        Integer[] yearsExtra = new Integer[listSize + 1];

        boolean contExtra = false;
        for(int i = 0; i < listSize; i++){
            years[i] = currYear - i;
            yearsExtra[i] = currYear - i;
            if((currYear - i) == extraYear){
                contExtra = true;
            }
        }
        if(!contExtra && extraYear != -1){
            yearsExtra[listSize] = extraYear;
            return yearsExtra;
        }
        return years;
    }

    @Transactional
    public CellDefinition makeHistoric(CellDefinition cellDef){
        cellDef.setStatusId(cellDefinitionStatusRepository.getOne(29));
        return saveCellDefinition(cellDef);
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_CELL_DEFINITIONS)
    public MigrateStatus migrateCellDefinition(Long cdId, Integer year){
        MigrateStatus status = new MigrateStatus();
        CellDefinition cellDefOld = getCellDefinitionById(cdId);
        CellDefinition cellDef = new CellDefinition();

        BeanUtils.copyProperties(cellDefOld, cellDef);
        cellDef.setYear(year);
        cellDef.setStatusId(cellDefinitionStatusRepository.getOne(27));
        cellDef.setId(null);

        CellDefinition cellDefSpecies = getCellDefinitionByYearAndSpeciesId(year, cellDef.getSpeciesId());

        if (cellDefSpecies != null) {
            status.setStatus(MigrateStatus.Status.FAIL);
            status.setErrorKey("js.error.cd_already_exists");
            status.setErrorArgs(new Object[]{getSpeciesForCellDef(cellDefOld.getSpeciesId()), year});
            cellDefOld.setMigratedToCellDefId(-1L);
            saveCellDefinition(cellDefOld);
        } else {
            CellDefinition returnedCell = saveCellDefinition(cellDef);

            cellDefOld.setMigratedToCellDefId(returnedCell.getId());
            saveCellDefinition(cellDefOld);

            status.setStatus(MigrateStatus.Status.SUCCESS);
            status.setId(returnedCell.getId().toString());
        }

        return status;
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_CELL_DEFINITIONS)
    public String activateCellDefinition(Long id)
    {
        CellDefinition cellDef = getCellDefinitionById(id);

        if(cellDef != null && cellDef.getStatusId() != null){
            cellDef.setStatusId(cellDefinitionStatusRepository.getOne(28));
            saveCellDefinition(cellDef);
            return "SUCCESS";
        }
        else {
            return "ERROR";
        }
    }

    private CellDefinitionForm convertToForm(CellDefinition cellDefinition) {

        CellDefinitionForm cellForm = new CellDefinitionForm();
        cellForm.setId(cellDefinition.getId());
        cellForm.setYear(cellDefinition.getYear());
        cellForm.setSpeciesId(cellDefinition.getSpeciesId());
        cellForm.setDesc(cellDefinition.getDesc());
        cellForm.setStatusId(cellDefinition.getStatusId());
        cellForm.setMigratedToCellDefId(cellDefinition.getMigratedToCellDefId());

        cellForm.setDataSource(cellDefinition.getDataSource());
        cellForm.setByCatch(cellDefinition.getByCatch());
        cellForm.setCountry(cellDefinition.getCountry());
        cellForm.setQuarter(cellDefinition.getQuarter());
        cellForm.setNafoDivision(cellDefinition.getNafoDivision());
        cellForm.setUnitArea(cellDefinition.getUnitArea());
        cellForm.setVesselLengthCat(cellDefinition.getVesselLengthCat());
        cellForm.setGear(cellDefinition.getGear());
        cellForm.setMesh(cellDefinition.getMesh());
        cellForm.setObserverCompany(cellDefinition.getObserverCompany());

        cellForm.setOtolithT(cellDefinition.getOtolithT());
        cellForm.setStomachT(cellDefinition.getStomachT());
        cellForm.setFrozenT(cellDefinition.getFrozenT());
        cellForm.setWeightT(cellDefinition.getWeightT());

        return cellForm;
    }

    private CellDefinition convertFromForm(CellDefinitionForm cellForm) {

        CellDefinition cellDefinition = new CellDefinition();
        cellDefinition.setId(cellForm.getId());
        cellDefinition.setYear(cellForm.getYear());
        cellDefinition.setSpeciesId(cellForm.getSpeciesId());
        cellDefinition.setDesc(cellForm.getDesc());
        cellDefinition.setStatusId(cellForm.getStatusId());
        cellDefinition.setMigratedToCellDefId(cellForm.getMigratedToCellDefId());

        cellDefinition.setDataSource(cellForm.getDataSource());
        cellDefinition.setByCatch(cellForm.getByCatch());
        cellDefinition.setCountry(cellForm.getCountry());
        cellDefinition.setQuarter(cellForm.getQuarter());
        cellDefinition.setNafoDivision(cellForm.getNafoDivision());
        cellDefinition.setUnitArea(cellForm.getUnitArea());
        cellDefinition.setVesselLengthCat(cellForm.getVesselLengthCat());
        cellDefinition.setGear(cellForm.getGear());
        cellDefinition.setMesh(cellForm.getMesh());
        cellDefinition.setObserverCompany(cellForm.getObserverCompany());

        cellDefinition.setOtolithT(cellForm.getOtolithT());
        cellDefinition.setStomachT(cellForm.getStomachT());
        cellDefinition.setFrozenT(cellForm.getFrozenT());
        cellDefinition.setWeightT(cellForm.getWeightT());

        return cellDefinition;
    }

    public String getSpeciesForCellDef(int speciesId)
    {
        List<Lookup> speciesList = lookupService.getLookupsByKey("species");
        String species = "";
        for (Lookup spec:
                speciesList) {
            if(spec.getValue().equals(String.valueOf(speciesId))) {
                species = spec.getText();
            }
        }
        return species;
    }

    private static final Logger logger = LoggerFactory.getLogger(CellDefinitionsController.class);
}
