package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.*;
import ca.gc.dfo.psffs.domain.repositories.CellDefinitionRepository;
import ca.gc.dfo.psffs.domain.repositories.CellRepository;
import ca.gc.dfo.psffs.domain.repositories.SamplingIdentifierGeneratorRepository;
import ca.gc.dfo.psffs.domain.repositories.SamplingRepository;
import ca.gc.dfo.psffs.exceptions.CellDefinitionNotFoundException;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.BaseService;
import ca.gc.dfo.spring_commons.commons_web.objects.Lookup;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static ca.gc.dfo.psffs.domain.QueryDSLExpressionUtil.qOr;

public abstract class BaseSamplingService extends BaseService
{
    @Autowired
    private SamplingIdentifierGeneratorRepository samplingIdentifierGeneratorRepository;

    @Autowired
    private SamplingRepository samplingRepository;

    @Autowired
    private CellDefinitionRepository cellDefinitionRepository;

    @Autowired
    private CellRepository cellRepository;

    @Autowired
    private LookupService lookupService;

    protected String getNextSampleCode(Integer year, String initials)
    {
        SamplingIdentifierGenerator generator = samplingIdentifierGeneratorRepository.findByYearEqualsAndInitialsEquals(year, initials);
        if (generator == null) {
            generator = new SamplingIdentifierGenerator();
            generator.setYear(year);
            generator.setInitials(initials);
            generator.setIdentifier(0);
            generator = samplingIdentifierGeneratorRepository.saveAndFlush(generator);
        }
        generator.incrementIdentifier();
        String sampleCode = generator.toString();
        while (getBySamplingCode(sampleCode) != null) {
            generator.incrementIdentifier();
            sampleCode = generator.toString();
        }
        samplingIdentifierGeneratorRepository.saveAndFlush(generator);
        return sampleCode;
    }

    protected Sampling getBySamplingCode(String samplingCode)
    {
        Sampling sampling = null;
        List<Sampling> samplingList = samplingRepository.findAllBySamplingCodeEquals(samplingCode);
        if (samplingList.size() > 0) sampling = samplingList.get(0);
        return sampling;
    }

    protected String getSpecificLookupValueDataByIdentifier(String key, Serializable identifier)
    {
        String valueData = null;
        if (key != null && identifier != null) {
            Lookup lookup = lookupService.getSpecificLookupByIdentifier(key, identifier);
            if (lookup != null) {
                valueData = lookup.getValue();
            }
        }
        return valueData;
    }

    protected String getSpecificLookupValueDataByValueContains(String key, String value)
    {
        String valueData = null;
        if (key != null && value != null && value.trim().length() > 0) {
            Lookup lookup = lookupService.getSpecificLookupByValue(key, value, true);
            if (lookup != null) {
                valueData = lookup.getValue();
            }
        }
        return valueData;
    }

    protected boolean determineAndSetSamplingCell(SamplingTypeEntity samplingTypeEntity)
    {
        boolean resetAllStorageNumbers;
        LocalDate createdDate = samplingTypeEntity.getCreatedDate();
        if (createdDate == null) createdDate = LocalDate.now();
        CellDefinition cellDefinition = cellDefinitionRepository.getCellDefinitionByYearAndSpeciesId(createdDate.getYear(),
                samplingTypeEntity.getCellSpeciesId());
        if (cellDefinition == null) {
            throw new CellDefinitionNotFoundException(createdDate.getYear(), samplingTypeEntity.getCellSpeciesId());
        }
        Cell cell = new Cell();
        cell.setCellDefinition(cellDefinition);
        cell.setDataSourceId(samplingTypeEntity.getSampling().getDataSourceId());
        cell.setBycatchInd(samplingTypeEntity.isBycatch() ? 1 : 0);
        cell.setQuotaId(samplingTypeEntity.getCountryId());
        cell.setQuarterId(samplingTypeEntity.getQuarterId());
        cell.setNafoDivisionId(samplingTypeEntity.getNafoDivisionId());
        cell.setUnitAreaId(samplingTypeEntity.getUnitAreaId());
        cell.setGearId(samplingTypeEntity.getGearId());
        populateUniqueCellFields(cell, samplingTypeEntity);

        List<Cell> cellResults = cellRepository.findAllByCellDefinitionIdAndCellCode(cellDefinition.getId(),
                cell.toString());
        if (cellResults.size() >= 1) {
            cell = cellResults.get(0);
            Cell oldCell = samplingTypeEntity.getSampling().getCell();
            if (oldCell != null) {
                resetAllStorageNumbers = !cell.getCellId().equals(oldCell.getCellId());
            } else {
                resetAllStorageNumbers = true;
            }
        } else {
            cell.setLatestStorageNumber(0L);
            resetAllStorageNumbers = true;
        }
        samplingTypeEntity.getSampling().setCell(cell);
        return resetAllStorageNumbers;
    }

    protected void populateUniqueCellFields(Cell cell, SamplingTypeEntity samplingTypeEntity)
    {
        //Default does nothing extra
    }

    protected <N extends Number & Comparable<?>> BooleanExpression queryORsEqual(N equalsNumber, Class<N> numClass, NumberPath<N>... numberPaths)
    {
        BooleanExpression ors = null;
        for (NumberPath<N> numPath : numberPaths) {
            ors = qOr(numPath.eq(numClass.cast(equalsNumber)), ors);
        }
        return ors;
    }
}
