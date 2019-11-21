package ca.gc.dfo.psffs.export.extract;

import ca.gc.dfo.psffs.services.ExtractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractExtractRunner<T,S extends ExtractReadyService> implements Runnable
{
    private StringBuilder contents;
    private BooleanExpression[] predicates;
    private String actor;
    private String type;
    private String fileExt;
    private Integer spaceIndicatorLength;
    private ExtractService extractService;
    private S service;
    private boolean runAtNight;

    private final ScheduledExecutorService executor;

    public AbstractExtractRunner(String actor, String type, String fileExt, ExtractService extractService, S service, BooleanExpression... predicates)
    {
        this.contents = new StringBuilder();
        this.predicates = predicates;
        this.actor = actor;
        this.type = type;
        this.fileExt = fileExt.toLowerCase();
        this.spaceIndicatorLength = getSpaceIndicatorLength();
        this.extractService = extractService;
        this.service = service;
        this.runAtNight = false;

        this.executor = Executors.newScheduledThreadPool(1);
    }

    protected Integer getSpaceIndicatorLength()
    {
        return null;
    }

    @Override
    public void run()
    {
        if (this.runAtNight) {
            doRun();
        } else {
            Long recordCount = this.service.countRecords(predicates);
            if (recordCount > 0 && recordCount <= 200) {
                doRun();
            } else if (recordCount > 200) {
                this.runAtNight = true;
                this.extractService.queueExtract(this);
            }
        }
    }

    private void doRun()
    {
        Iterable<T> dataList = this.service.findRecords(this.predicates);

        List<T> postProcessList = new ArrayList<>();
        String renderedRecord;
        for (T data : dataList) {
            if (data != null) {
                renderedRecord = renderRecord(data);
                this.contents.append(renderedRecord);
                if (!renderedRecord.endsWith("\r\n")) this.contents.append("\r\n");
                postProcessList.add(data);
            }
        }

        String extractContents = trimTrailingCarriageReturns(this.contents.toString());

        if (this.spaceIndicatorLength != null) {
            String indicatorRepeat = "123456789-";
            StringBuilder spaceIndicatorBuilder = new StringBuilder();
            Integer roundedSpaceIndicatorLength = (new Double(Math.ceil(10d/this.spaceIndicatorLength.doubleValue())
                    * this.spaceIndicatorLength)).intValue();
            int iterations = roundedSpaceIndicatorLength / 10;
            if (roundedSpaceIndicatorLength % 10 > 0) iterations += 1;
            for (int i = 0; i < iterations; i++) {
                spaceIndicatorBuilder.append(indicatorRepeat);
            }
            String spaceIndicator = enforceValueLength(spaceIndicatorBuilder.toString(), this.spaceIndicatorLength) + "\r\n";
            extractContents = spaceIndicator + extractContents;
        }

        if (this.extractService.processResults(extractContents, this.actor, this.type, this.fileExt)) {
            postProcessList.forEach(d -> postProcess(d, this.service, this.actor));
        }
    }

    private String trimTrailingCarriageReturns(String contents)
    {
        String toReturn = contents;
        if (toReturn.endsWith("\r\n")) {
            while (toReturn.endsWith("\r\n")) {
                toReturn = StringUtils.chomp(toReturn);
            }
        }
        return toReturn;
    }

    protected abstract String renderRecord(T data);

    protected void postProcess(T data, S service, String actor)
    {
        //Do nothing, override and implement if there is a need to process anything after an extract completes.
    }

    public void execute()
    {
        this.executor.execute(this);
    }

    protected String enforceValueLength(String value, int length)
    {
        return enforceValueLength(value, length, true);
    }

    protected String enforceValueLength(String value, int length, boolean prepended)
    {
        String toReturn;
        if (value != null && value.length() > 0) {
            if (value.length() < length) {
                toReturn = padString(value, ' ', length - value.length(), prepended);
            } else if (value.length() > length) {
                toReturn = value.substring(0, length);
            } else {
                toReturn = value;
            }
        } else {
            toReturn = padString("", ' ', length, prepended);
        }
        return toReturn;
    }

    protected List<String> enforceValueLengths(List<String> values, Integer[] lengthSpecs)
    {
        return enforceValueLengths(values, lengthSpecs, true);
    }

    protected List<String> enforceValueLengths(List<String> values, Integer[] lengthSpecs, boolean prepended)
    {
        List<String> enforcedValueLengths = new ArrayList<>();

        if (values != null && lengthSpecs != null && lengthSpecs.length == values.size()) {
            for (int x = 0; x < values.size(); x++) {
                enforcedValueLengths.add(x, enforceValueLength(values.get(x), lengthSpecs[x], prepended));
            }
        } else {
            logger.error("An error occurred while trying to enforce value lengths: Number of values passed does not equal number of length specs passed.");
        }

        return enforcedValueLengths;
    }

    private String padString(String str, char padding, int paddingLength, boolean prepended)
    {
        StringBuilder toReturn = new StringBuilder(str);
        for (int x = 0; x < paddingLength; x++) {
            if (prepended) toReturn.insert(0, padding);
            else toReturn.append(padding);
        }
        return toReturn.toString();
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractExtractRunner.class);
}
