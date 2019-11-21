package ca.gc.dfo.psffs.export.extract;

import ca.gc.dfo.psffs.domain.objects.LengthFrequency;
import ca.gc.dfo.psffs.domain.objects.SamplingEntry;
import ca.gc.dfo.psffs.domain.objects.SamplingTypeEntity;
import ca.gc.dfo.psffs.services.ExtractService;
import ca.gc.dfo.psffs.services.SamplingEntityService;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LFLSMExtract extends LengthFrequencyExtract
{
    private static final Integer[] DATA_FIELD_SPECS = new Integer[] {
            3, //Number of otoliths
            1, //Length group code
            4, //Directed species code
            1 //Leave blank
    };

    public LFLSMExtract(ExtractService extractService, SamplingEntityService service, String actor, BooleanExpression... predicates)
    {
        super(LFLSMExtract.class.getName(), extractService, service, actor, ".LSM", predicates);
    }

    @Override
    protected Integer getSpaceIndicatorLength()
    {
        List<Integer> lengths = new ArrayList<>(Arrays.asList(LengthFrequencyExtract.PREFIX_FIELD_SPECS));
        lengths.addAll(Arrays.asList(DATA_FIELD_SPECS));
        return lengths.stream().mapToInt(Integer::intValue).sum() + 49; //Added 49 (45+3+1) for Maturity code, LF Length data and extract char not included in SPECS arrays
    }

    @Override
    protected List<String> extractValues(SamplingTypeEntity ste)
    {
        LengthFrequency lf = (LengthFrequency)ste;
        List<String> values = new ArrayList<>();
        List<String> dataValues;
        List<String> genders = Arrays.asList("M", "F", "U");
        List<String> maturityCodes;
        List<SamplingEntry> genderSeperatedEntries;
        List<SamplingEntry> genderAndMaturitySeperatedEntries;
        Map<Integer, List<String>> maturityLengthMap;
        int mapCount, entryCount, entryDiff;

        for (int g = 0; g < genders.size(); g++) {
            final String gender = genders.get(g);
            dataValues = new ArrayList<>();
            genderSeperatedEntries = lf.getSampling().getEntries().stream()
                                                                  .filter(se -> se.getSex().equals(gender.toLowerCase()))
                                                                  .collect(Collectors.toList());
            dataValues.add(genderSeperatedEntries.stream().filter(SamplingEntry::getOtolithSampledInd).count()+""); //Number of Otoliths
            dataValues.add(lf.getLengthGroup() != null ? lf.getLengthGroup().getLengthGroup()+"" : "");//Length group interval
            dataValues.add(lf.getDirectedSpecies() != null ? lf.getDirectedSpecies().getLegacyCode() : "");//Directed species code
            dataValues.add("");//Leave blank

            maturityCodes = genderSeperatedEntries.stream()
                                                  .map(SamplingEntry::getMaturityLevel)
                                                  .distinct()
                                                  .collect(Collectors.toList());
            for (int m = 0; m < maturityCodes.size(); m++) {
                final String maturityLevel = maturityCodes.get(m);
                maturityLengthMap = new ConcurrentHashMap<>();
                mapCount = 1;
                entryCount = 1;
                genderAndMaturitySeperatedEntries = lf.getSampling().getEntries().stream()
                                                                                 .filter(se -> se.getMaturityLevel().equals(maturityLevel))
                                                                                 .sorted(Comparator.comparingInt(SamplingEntry::getLength))
                                                                                 .collect(Collectors.toList());
                for (int e = 0; e < genderAndMaturitySeperatedEntries.size(); e++) {
                    if (!maturityLengthMap.containsKey(mapCount)) {
                        maturityLengthMap.put(mapCount, new ArrayList<>());
                    }
                    maturityLengthMap.get(mapCount).add(genderAndMaturitySeperatedEntries.get(e).getLength().toString());
                    entryCount += 1;
                    if (entryCount > 15) {
                        mapCount += 1;
                        entryCount = 1;
                    } else {
                        if ((e+1) == genderAndMaturitySeperatedEntries.size() && entryCount < 15) {
                            entryDiff = 15 - entryCount;
                            for (int d = 0; d <= entryDiff; d++) {
                                maturityLengthMap.get(mapCount).add("");//Empty for non-existant length entry
                            }
                        }
                    }
                }

                for (Integer mlmCount : maturityLengthMap.keySet()) {
                    values.addAll(enforceValueLengths(prefixExtractValues(ste, gender), LengthFrequencyExtract.PREFIX_FIELD_SPECS));
                    values.addAll(enforceValueLengths(dataValues, DATA_FIELD_SPECS));
                    values.add(enforceValueLength(maturityLevel, 3));
                    for (String len : maturityLengthMap.get(mlmCount)) {
                        values.add(enforceValueLength(len, 3));
                    }

                    if (mlmCount.equals(maturityLengthMap.keySet().size())) {
                        values.add("\r\n");
                    }
                }
            }
        }
        return values;
    }
}
