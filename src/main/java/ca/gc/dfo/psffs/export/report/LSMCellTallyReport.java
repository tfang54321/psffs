package ca.gc.dfo.psffs.export.report;

import ca.gc.dfo.psffs.domain.objects.LengthFrequency;
import ca.gc.dfo.psffs.domain.objects.SamplingEntry;
import ca.gc.dfo.psffs.domain.objects.lookups.Maturity;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.ModelMap;

import java.util.*;
import java.util.stream.Collectors;

public class LSMCellTallyReport extends AbstractReport<LengthFrequency>
{
    public LSMCellTallyReport()
    {
        super("report.lsm_cell_tally.title", "layouts/report/lsmCellTally");
    }

    @Override
    public void populateModel(ModelMap model, List<LengthFrequency> data)
    {
        List<LSMTallyValues> tallyList = new ArrayList<>();
        if (data.size() > 0) {
            String lang = LocaleContextHolder.getLocale().getLanguage().toLowerCase();
            Map<Maturity, List<Integer>> maturityLengthMap = new HashMap<>();
            data.stream()
                    .map(lf -> lf.getSampling().getEntries())
                    .flatMap(Collection::stream)
                    .forEach(entry -> addEntryToMap(entry, maturityLengthMap));
            int lengthCount;
            List<Integer> lengthList;
            LSMTallyValues values;
            for (Maturity key : maturityLengthMap.keySet()) {
                lengthCount = 0;
                values = null;
                lengthList = maturityLengthMap.get(key);
                Collections.sort(lengthList);
                for (int x = 0; x < lengthList.size(); x++) {
                    if (lengthCount == 0) {
                        values = new LSMTallyValues(key.getLegacyCode(), lang.equals("fr") ? key.getFrenchDescription() :
                                key.getEnglishDescription());
                    }
                    values.setLengthByIndex(lengthCount + 1, lengthList.get(x));
                    lengthCount++;
                    if (lengthCount == 15) {
                        lengthCount = 0;
                        tallyList.add(values);
                        values = null;
                    }
                }
                if (values != null) {
                    tallyList.add(values);
                }
            }
        }

        model.addAttribute("tallyList", tallyList);
        model.addAttribute("nbrLF", data.size());
    }

    private void addEntryToMap(SamplingEntry entry, Map<Maturity, List<Integer>> maturityLengthMap)
    {
        if (!maturityLengthMap.containsKey(entry.getMaturity())) {
            maturityLengthMap.put(entry.getMaturity(), new ArrayList<>());
        }
        maturityLengthMap.get(entry.getMaturity()).add(entry.getLength());
    }
}
