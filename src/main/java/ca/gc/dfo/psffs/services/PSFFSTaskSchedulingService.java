package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.CellDefinition;
import ca.gc.dfo.psffs.json.MigrateStatus;
import ca.gc.dfo.spring_commons.commons_web.services.TaskSchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;


@Service
public class PSFFSTaskSchedulingService extends TaskSchedulingService
{
    @Autowired
    public CellDefinitionService cellDefinitionService;

    @Autowired
    public LookupService lookupService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ExtractService extractService;

    //@Scheduled(cron = "")
    //public void methodName() {//unitOfWork}

    //See the following URL for information on valid cron statements
    //https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html

    //Cron for each minute for testing
    //@Scheduled(cron = "0 */1 * * * *")

    @Scheduled(cron = "0 30 0 * * ?")
    public void expireOldExtracts()
    {
        extractService.expireOldExtracts();
    }

    //Cron for first day of new year at 11pm
    @Scheduled(cron = "0 0 23 1 1 ?")
    public void cellDefinitionMigrateCurrentYear(){

        Integer currYear = Calendar.getInstance().get(Calendar.YEAR);
        List<CellDefinition> cellDefinitions = cellDefinitionService.getAllByYear(currYear - 1);
        try{
            for (CellDefinition cellDef:
                    cellDefinitions) {
                if(cellDef.getYear() == currYear - 1){
                        performMigration(cellDef, currYear);
                    }
                }
        }
        catch(Exception e){
            logger.error("An error occurred while migrating Cell Definitions");
        }
    }

    //Cron for each minute for testing
    //@Scheduled(cron = "0 */1 * * * *")
    //Cron for each day at 11pm
    @Scheduled(cron = "0 0 23 * * ?")
    public void cellDefinitionNightlyMigration(){

        List<CellDefinition> cellDefinitions = cellDefinitionService.getAllNonHistoricCellDefinitions();

        try {
            for (CellDefinition cellDef :
                    cellDefinitions) {
                Integer currYear = Calendar.getInstance().get(Calendar.YEAR);
                if (cellDef.getMigratedToCellDefId() == null) {
                    if (cellDef.getYear() < currYear) {
                        performMigration(cellDef, currYear);
                    }
                } else if (cellDef.getMigratedToCellDefId().equals(cellDef.getId())) {
                    performMigration(cellDef, currYear);
                }
                //If Cell Definition from an older year is still labelled as active then this changes it to historic
                else if (cellDef.getStatusId().getId() == 28 && cellDef.getYear() < currYear) {
                    try {
                        cellDefinitionService.makeHistoric(cellDef);
                    }
                    catch(Exception e){
                        logger.error("An error occurred while setting the status of Cell Definition " + cellDef.getId() + " to historic");
                    }
                }
            }
        }
        catch(Exception e){
            logger.error("An error occurred during nightly migration of Cell Definitions");
        }
    }

    private void performMigration(CellDefinition cellDef, Integer year){
        MigrateStatus status = cellDefinitionService.migrateCellDefinition(cellDef.getId(), year);

        if(status.getStatus() == MigrateStatus.Status.SUCCESS){
            try {
                CellDefinition postMigrate = cellDefinitionService.getCellDefinitionById(cellDef.getId());
                CellDefinition result = cellDefinitionService.makeHistoric(postMigrate);
                logger.info("Cell definition " + result.getId() + " successfully migrated");
            }
            catch(Exception e){
                logger.error("An error occurred while setting the status of Cell Definition " + cellDef.getId() + " to historic");
            }
        }
        else if(status.getErrorKey() != null && status.getErrorKey().equals("ALREADY_EXISTS")){
            try {
                CellDefinition postMigrate = cellDefinitionService.getCellDefinitionById(cellDef.getId());
                CellDefinition result = cellDefinitionService.makeHistoric(postMigrate);

                logger.error("Cell Definition " + cellDef.getId() + ": " + messageSource.getMessage("js.error.cd_already_exists"
                        , new Object[]{cellDefinitionService.getSpeciesForCellDef(cellDef.getSpeciesId()), year}, LocaleContextHolder.getLocale()));
            }
            catch(Exception e){
                logger.error("An error occurred while setting the status of Cell Definition " + cellDef.getId() + " to historic");
            }
        }
        else {
            logger.error("An error occurred while migrating Cell Definition " + cellDef.getId());
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(PSFFSTaskSchedulingService.class);
}
