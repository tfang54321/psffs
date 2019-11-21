package ca.gc.dfo.psffs.export.report;

import ca.gc.dfo.psffs.domain.objects.*;
import org.springframework.ui.ModelMap;

import java.util.*;

public class CellTallyReport extends AbstractReport<SamplingTypeEntity>
{
    public CellTallyReport()
    {
        super("report.cell_tally.title", "layouts/report/cellTally");
    }

    @Override
    protected void populateModel(ModelMap model, List<SamplingTypeEntity> data)
    {
        Map<LengthGroupKey, CellTallyValues> tallyMap = new TreeMap<>();
        LengthGroupKey key;
        LengthFrequency lf;
        TripSetSpecies tss;
        LengthGroupKey.LengthUnitEnum lu;
        String sex;
        Integer lengthUnitId;
        Integer nbrLF = 0;
        Integer nbrTSS = 0;
        Set<CellDefinition> cellDefSet = new HashSet<>();
        for (SamplingTypeEntity ste : data) {
            cellDefSet.add(ste.getSampling().getCell().getCellDefinition());
            if (ste instanceof LengthFrequency) {
                nbrLF++;
                lf = (LengthFrequency) ste;
                lengthUnitId = lf.getLengthUnit() != null ? lf.getLengthUnit().getId() : 14;
                lu = LengthGroupKey.LengthUnitEnum.valueOf("LENGTH_UNIT_" + lengthUnitId);

                for (LengthFrequencyCount count : lf.getLengthFrequencyCounts()) {
                    key = new LengthGroupKey(count.getLength(), lu.UNIT);
                    if (!tallyMap.containsKey(key)) {
                        tallyMap.put(key, new CellTallyValues());
                    }
                    sex = count.getSex().toUpperCase();
                    switch (count.getSampleType().getId()) {
                        case 21: {//Length measured
                            switch (sex) {
                                case "M": tallyMap.get(key).incrementMale(count.getCount()); break;
                                case "F": tallyMap.get(key).incrementFemale(count.getCount()); break;
                                case "U": tallyMap.get(key).incrementUnsexed(count.getCount()); break;
                            }
                            break;
                        }
                        case 22: {//Otolith
                            switch (sex) {
                                case "M": tallyMap.get(key).incrementMaleOtoliths(count.getCount()); break;
                                case "F": tallyMap.get(key).incrementFemaleOtoliths(count.getCount()); break;
                                case "U": tallyMap.get(key).incrementUnsexedOtoliths(count.getCount()); break;
                            }
                            break;
                        }
                        case 23: {//Stomach
                            switch (sex) {
                                case "M": tallyMap.get(key).incrementMaleStomachs(count.getCount()); break;
                                case "F": tallyMap.get(key).incrementFemaleStomachs(count.getCount()); break;
                                case "U": tallyMap.get(key).incrementUnsexedStomachs(count.getCount()); break;
                            }
                            break;
                        }
                        case 24: {//Frozen
                            tallyMap.get(key).incrementFrozens(count.getCount()); break;
                        }
                        case 25: {//Weights
                            switch (sex) {
                                case "M": tallyMap.get(key).incrementMaleWeights(count.getCount()); break;
                                case "F": tallyMap.get(key).incrementFemaleWeights(count.getCount()); break;
                                case "U": tallyMap.get(key).incrementUnsexedWeights(count.getCount()); break;
                            }
                            break;
                        }
                    }
                }
            } else if (ste instanceof TripSetSpecies) {
                nbrTSS++;
                tss = (TripSetSpecies) ste;
                lengthUnitId = tss.getLengthUnitId() != null ? tss.getLengthUnitId() : 14;
                lu = LengthGroupKey.LengthUnitEnum.valueOf("LENGTH_UNIT_" + lengthUnitId);

                for (SamplingEntry entry : ste.getSampling().getEntries()) {
                    key = new LengthGroupKey(entry.getLength(), lu.UNIT);
                    if (!tallyMap.containsKey(key)) {
                        tallyMap.put(key, new CellTallyValues());
                    }
                    sex = entry.getSex().toUpperCase();
                    switch (sex) {
                        case "M": tallyMap.get(key).incrementMaleOtoliths(1); break;
                        case "F": tallyMap.get(key).incrementFemaleOtoliths(1); break;
                        case "U": tallyMap.get(key).incrementUnsexedOtoliths(1); break;
                    }
                }
            }
        }

        model.addAttribute("tallyMap", tallyMap.size() > 0 ? tallyMap : null);
        model.addAttribute("nbrLF", nbrLF);
        model.addAttribute("nbrTSS", nbrTSS);
        model.addAttribute("cellDef", cellDefSet.size() == 1 ? cellDefSet.iterator().next() : null);
    }
}
