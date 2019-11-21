package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.Extract;
import ca.gc.dfo.psffs.domain.objects.QLengthFrequency;
import ca.gc.dfo.psffs.domain.objects.QSamplingData;
import ca.gc.dfo.psffs.domain.objects.lookups.SamplingDataStatus;
import ca.gc.dfo.psffs.domain.repositories.ExtractRepository;
import ca.gc.dfo.psffs.export.extract.AbstractExtractRunner;
import ca.gc.dfo.psffs.json.ExtractList;
import ca.gc.dfo.psffs.json.ExtractListItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExtractService
{
    @Autowired
    private ExtractRepository extractRepository;

    private List<AbstractExtractRunner> nightExtracts;

    public ExtractService()
    {
        this.nightExtracts = new ArrayList<>();
    }

    public boolean processResults(String results, String actor, String type, String fileExt)
    {
        boolean success = true;
        Extract extract = new Extract();
        extract.setActor(actor);
        extract.setData(results);
        extract.setHasData(results != null && results.trim().length() > 0);
        extract.setType(type);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String filePrefix = type;
        if (filePrefix.contains(".")) {
            filePrefix = filePrefix.substring(filePrefix.lastIndexOf(".") + 1);
        }
        extract.setFileName(filePrefix+"_"+dateFormat.format(new Date())+fileExt);
        try {
            extractRepository.saveAndFlush(extract);
        } catch (Exception ex) {
            logger.error("Error occurred while trying to save extract: " + ex.getMessage(), ex);
            success = false;
        }
        return success;
    }

    public void queueExtract(AbstractExtractRunner extract)
    {
        this.nightExtracts.add(extract);
    }

    @Scheduled(cron = "0 30 23 * * ?") //11:30PM nightly
    public void runNightExtracts()
    {
        this.nightExtracts.forEach(AbstractExtractRunner::execute);
        this.nightExtracts = new ArrayList<>();
    }

    public ExtractList getListByCreatorAndType(String createdBy, String type)
    {
        List<ExtractListItem> items;
        if (type != null && type.length() > 0) {
            items = extractRepository.findListByCreatedByAndType(createdBy, type);
        } else {
            items = extractRepository.findListByCreatedBy(createdBy);
        }
        return new ExtractList(items);
    }

    public Extract getById(Long extractId)
    {
        return extractRepository.getOne(extractId);
    }

    public BooleanExpression getLFExtractPredicate()
    {
        QLengthFrequency qry = QLengthFrequency.lengthFrequency;
        return qry.status.id.eq(4).and(qry.sexType.id.ne(10));
    }

    public BooleanExpression getLFLSMExtractPredicate()
    {
        QLengthFrequency qry = QLengthFrequency.lengthFrequency;
        return qry.status.id.eq(4).and(qry.sexType.id.eq(10));
    }

    public BooleanExpression getSamplingDataExtractPredicate()
    {
        QSamplingData qry = QSamplingData.samplingData;
        return qry.statusId.eq(SamplingDataStatus.MARK_FOR_ARCHIVE_STS_ID);
    }

    @Transactional
    public void expireOldExtracts()
    {
        List<Extract> oldExtracts = extractRepository.findAllByCreatedDateBeforeAndHasDataIsTrue(LocalDate.now().minusDays(30));
        if (oldExtracts != null && oldExtracts.size() > 0) {
            for (Extract oldExtract : oldExtracts) {
                oldExtract.setData(null);
                oldExtract.setHasData(false);
            }
            extractRepository.saveAll(oldExtracts);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ExtractService.class);
}
