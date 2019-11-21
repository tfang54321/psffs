package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.*;
import ca.gc.dfo.psffs.domain.objects.lookups.SamplingStatus;
import ca.gc.dfo.psffs.domain.repositories.LengthFrequencyRepository;
import ca.gc.dfo.psffs.domain.repositories.TripSetSpeciesRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.SamplingStatusRepository;
import ca.gc.dfo.psffs.export.extract.ExtractReadyService;
import ca.gc.dfo.psffs.export.report.CellTallyReport;
import ca.gc.dfo.psffs.export.report.LSMCellTallyReport;
import ca.gc.dfo.psffs.forms.ExecuteReportForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ca.gc.dfo.psffs.domain.QueryDSLExpressionUtil.*;

@Service
public class SamplingEntityService implements ExtractReadyService<SamplingTypeEntity>, InitializingBean
{
    @Autowired
    private LengthFrequencyRepository lengthFrequencyRepository;

    @Autowired
    private TripSetSpeciesRepository tripSetSpeciesRepository;

    @Autowired
    private SamplingStatusRepository samplingStatusRepository;

    private Map<Integer, SamplingStatus> statusMap; //Necessary while LengthFrequency still has its statusId as a Transient field

    @Override
    public void afterPropertiesSet()
    {
        this.statusMap = samplingStatusRepository.findAll().stream()
                .collect(Collectors.toMap(SamplingStatus::getId, s->s));
    }

    public void saveSamplingEntity(SamplingTypeEntity samplingTypeEntity)
    {
        if (samplingTypeEntity instanceof LengthFrequency) {
            LengthFrequency lf = (LengthFrequency)samplingTypeEntity;
            lf.setStatus(this.statusMap.get(lf.getStatusId()));
            lengthFrequencyRepository.save(lf);
        } else if (samplingTypeEntity instanceof TripSetSpecies) {
            tripSetSpeciesRepository.save((TripSetSpecies)samplingTypeEntity);
        } else {
            String error = "Cannot save sampling type entity: no repository found for this sub type.";
            logger.error(error);
            throw new RuntimeException(error);
        }
    }

    public SamplingTypeEntity getBySamplingCodeAndType(String samplingCode, Class<? extends SamplingTypeEntity> samplingType)
    {
        SamplingTypeEntity ste = null;
        if (samplingType.equals(LengthFrequency.class)) {
            List<LengthFrequency> lfList = lengthFrequencyRepository.queryBySampleCode(samplingCode);
            if (lfList != null && lfList.size() > 0) {
                ste = lfList.get(0);
            }
        } else if (samplingType.equals(TripSetSpecies.class)) {
            List<TripSetSpecies> tssList = tripSetSpeciesRepository.queryBySampleCode(samplingCode);
            if (tssList != null && tssList.size() > 0) {
                ste = tssList.get(0);
            }
        } else {
            String error = "Cannot get sampling type entity: no repository found for this sub type.";
            logger.error(error);
            throw new RuntimeException(error);
        }
        return ste;
    }

    public List<Object> getSamplingTypeEntityDataByReportForm(ExecuteReportForm reportForm)
    {
        List<Object> data = new ArrayList<>();
        BooleanExpression exp;
        if (reportForm.getReportType().equals(CellTallyReport.class.getName())) {
            exp = getLFWhereExpression(reportForm, false);
            lengthFrequencyRepository.findAll(exp).iterator().forEachRemaining(data::add);
            exp = getTSSWhereExpression(reportForm);
            tripSetSpeciesRepository.findAll(exp).iterator().forEachRemaining(data::add);
        } else if (reportForm.getReportType().equals(LSMCellTallyReport.class.getName())) {
            exp = getLFWhereExpression(reportForm, true);
            lengthFrequencyRepository.findAll(exp).iterator().forEachRemaining(data::add);
        } else {
            throw new RuntimeException("Unsupported report type has been selected : " + reportForm.getReportType());
        }

        return data;
    }

    private BooleanExpression getLFWhereExpression(ExecuteReportForm reportForm, boolean isLSM)
    {
        BooleanExpression exp = null;
        QLengthFrequency lfQry = QLengthFrequency.lengthFrequency;
        if (isLSM) {
            exp = qAnd(lfQry.sexType.id.eq(10), exp);
        } else {
            exp = qAnd(lfQry.sexType.id.ne(10), exp);
        }

        return queryCommonSamplingCriteria(exp, lfQry.sampling, reportForm);
    }

    private BooleanExpression getTSSWhereExpression(ExecuteReportForm reportForm)
    {
        QTripSetSpecies tssQry = QTripSetSpecies.tripSetSpecies;
        return queryCommonSamplingCriteria(null, tssQry.sampling, reportForm);
    }

    private BooleanExpression queryCommonSamplingCriteria(BooleanExpression exp, QSampling qry, ExecuteReportForm reportForm)
    {
        exp = qAnd(qry.yearCreated.eq(reportForm.getYear()), exp);
        exp = qAnd(qry.speciesId.eq(reportForm.getSampleSpeciesId()), exp);

        Integer dataSourceId = reportForm.getDataSourceId();
        if (dataSourceId != null) {
            if (!dataSourceId.equals(0)) {
                exp = qAnd(qry.dataSourceId.eq(dataSourceId), exp);
            } else {
                exp = qAnd(qry.dataSourceId.isNull(), exp);
            }
        }

        Integer bycatchInd = reportForm.getBycatchInd();
        if (bycatchInd != null) {
            if (!bycatchInd.equals(0)) {
                exp = qAnd(qry.cell.bycatchInd.eq(bycatchInd), exp);
            } else {
                exp = qAnd(qry.cell.bycatchInd.isNull(), exp);
            }
        }

        Integer countryId = reportForm.getCountryId();
        if (countryId != null) {
            if (!countryId.equals(0)) {
                exp = qAnd(qry.cell.countryId.eq(countryId), exp);
            } else {
                exp = qAnd(qry.cell.countryId.isNull(), exp);
            }
        }

        Integer quarterId = reportForm.getQuarterId();
        if (quarterId != null) {
            if (!quarterId.equals(0)) {
                exp = qAnd(qry.cell.quarterId.eq(quarterId), exp);
            } else {
                exp = qAnd(qry.cell.quarterId.isNull(), exp);
            }
        }

        Integer nafoDivisionId = reportForm.getNafoDivisionId();
        if (nafoDivisionId != null) {
            if (!nafoDivisionId.equals(0)) {
                exp = qAnd(qry.cell.nafoDivisionId.eq(nafoDivisionId), exp);
            } else {
                exp = qAnd(qry.cell.nafoDivisionId.isNull(), exp);
            }
        }

        String unitAreaData = reportForm.getUnitAreaData();
        if (unitAreaData != null && unitAreaData.trim().length() > 0) {
            Integer unitAreaId = Integer.valueOf(unitAreaData.substring(0, unitAreaData.indexOf(";")));
            exp = qAnd(qry.cell.unitAreaId.eq(unitAreaId), exp);
        }

        Integer lengthCategoryId = reportForm.getVesselLengthCategoryId();
        if (lengthCategoryId != null) {
            if (!lengthCategoryId.equals(0)) {
                exp = qAnd(qry.cell.vesselLengthCategoryId.eq(lengthCategoryId), exp);
            } else {
                exp = qAnd(qry.cell.vesselLengthCategoryId.isNull(), exp);
            }
        }

        Integer gearId = reportForm.getGearId();
        if (gearId != null) {
            if (!gearId.equals(0)) {
                exp = qAnd(qry.cell.gearId.eq(gearId), exp);
            } else {
                exp = qAnd(qry.cell.gearId.isNull(), exp);
            }
        }

        if (reportForm.getMeshSizeMillimeters() != null) {
            exp = qAnd(qry.cell.meshSizeMillimeters.eq(reportForm.getMeshSizeMillimeters()), exp);
        }

        return exp;
    }

    @Override
    public Iterable<SamplingTypeEntity> findRecords(BooleanExpression[] predicates)
    {
        List<SamplingTypeEntity> records = new ArrayList<>();
        Iterable<LengthFrequency> lfRecords = lengthFrequencyRepository.findAll(predicates[0]);
        lfRecords.forEach(records::add);
        return records;
    }

    @Override
    public Long countRecords(BooleanExpression[] predicates)
    {
        Long count = lengthFrequencyRepository.count(predicates[0]);
        return count;
    }

    private static final Logger logger = LoggerFactory.getLogger(SamplingEntityService.class);
}
