package ca.gc.dfo.psffs.export.extract;

import ca.gc.dfo.psffs.domain.objects.*;
import ca.gc.dfo.psffs.domain.objects.lookups.Vessel;
import ca.gc.dfo.psffs.services.ExtractService;
import ca.gc.dfo.psffs.services.SamplingEntityService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LengthFrequencyExtract extends AbstractExtractRunner<SamplingTypeEntity, SamplingEntityService>
{
    protected static final Integer[] PREFIX_FIELD_SPECS = new Integer[] {
            1, //Data source [P,O,G,Q] code
            4, //Frequency number (LF) / Set Number (TSS)
            2, //Country/Quota code
            4, //Sampled species code
            1, //Sex [Male=1, Female=5, Unsexed=Blank]
            3, //Trip Number (LF) / Deployment Number (TSS)
            8, //Vessel CFV Number
            1, //Tonnage code
            2, //Gear code
            3, //Mesh size (mm)
            3, //NAFO Division code
            2, //Unit area code
            4, //Latitude
            4, //Longitude
            4, //Catch time in GMT
            1, //Catch category code
            2, //2-digit year [2000 = 00]
            2, //2-digit month [1-12]
            2, //2-digit day [1-31]
            1, //Quarter period number
            3, //Port code (LF) / Quota code (TSS)
            4, //Depth (m)
            6, //Turnout weight kg (LF) / Total weight (TSS)
            4 //Sample weight kg
    };

    private static final Integer[] DATA_FIELD_SPECS = new Integer[] {
            3, //Number of otoliths
            1, //Length group code
            4, //Directed species code
            1, //Leave blank
            600 //LF Data (lengths 1 to 200, 3 digits each)
    };

    public LengthFrequencyExtract(ExtractService extractService, SamplingEntityService service, String actor, BooleanExpression... predicates)
    {
        this(LengthFrequencyExtract.class.getName(), extractService, service, actor, predicates);
    }

    protected LengthFrequencyExtract(String type, ExtractService extractService, SamplingEntityService service, String actor, BooleanExpression... predicates)
    {
        super(actor, type, ".LF", extractService, service, predicates);
    }

    protected LengthFrequencyExtract(String type, ExtractService extractService, SamplingEntityService service, String actor, String fileExt, BooleanExpression... predicates)
    {
        super(actor, type, fileExt, extractService, service, predicates);
    }

    @Override
    public String renderRecord(SamplingTypeEntity ste)
    {
        String record = "";
        List<String> values = extractValues(ste);

        if (values.size() > 0) {
            record = values.stream().collect(Collectors.joining());
        }

        return record;
    }

    // Values extracted in order dictated by PREFIX_FIELD_SPECS
    protected List<String> extractValues(SamplingTypeEntity ste)
    {
        LengthFrequency lf = (LengthFrequency) ste;

        List<String> values = new ArrayList<>();
        List<String> dataValues;
        List<String> genders = lf.getSexTypeId().equals(9) ? Arrays.asList("M", "F") : Arrays.asList("U");


        for (int g = 0; g < genders.size(); g++) {
            final String gender = genders.get(g);
            dataValues = new ArrayList<>();
            //Otolith count
            if (lf.getLengthFrequencyCounts() != null) {
                long otolithCount = lf.getLengthFrequencyCounts().stream()
                        .filter(lfc -> lfc.getSex().equals(gender))
                        .map(LengthFrequencyCount::getSampleType)
                        .filter(st -> st.getLegacyCode().equals("o"))
                        .count();
                dataValues.add("" + otolithCount);
            } else {
                dataValues.add("");
            }
            //Length group interval
            dataValues.add(lf.getLengthGroup() != null ? lf.getLengthGroup().getLengthGroup().toString() : "");
            //Directed species code
            dataValues.add(lf.getDirectedSpecies() != null ? lf.getDirectedSpecies().getLegacyCode() : "");
            //Blank (on purpose)
            dataValues.add("");
            //LF Counts (LF only)
            if (lf.getLengthFrequencyCounts().size() > 0) {
                String lfCounts;
                Map<Integer, String> lengthCountMap = lf.getLengthFrequencyCounts().stream()
                        .filter(lfc -> lfc.getSex().equals(gender))
                        .filter(lfc -> lfc.getSampleType().getLegacyCode().equals("l"))
                        .collect(Collectors.toMap(LengthFrequencyCount::getLength, c -> c.getCount().toString()));
                for (int x = 0; x < lf.getLengthGroupMax() + 1; x++) {
                    if (!lengthCountMap.containsKey(x)) {
                        lengthCountMap.put(x, "");
                    }
                }

                lfCounts = lengthCountMap.keySet().stream()
                        .map(length -> enforceValueLength(lengthCountMap.get(length), 3))
                        .collect(Collectors.joining());
                dataValues.add(lfCounts);
            } else {
                dataValues.add("");
            }

            values.addAll(enforceValueLengths(prefixExtractValues(ste, gender), PREFIX_FIELD_SPECS));
            values.addAll(enforceValueLengths(dataValues, DATA_FIELD_SPECS));
            if ((g+1) < genders.size()) {
                values.add("\r\n");
            }
        }

        return values;
    }

    protected List<String> prefixExtractValues(SamplingTypeEntity ste, String sexCode)
    {
        LengthFrequency lf = (LengthFrequency) ste;
        String genderLegacyCode = "";
        if (sexCode != null) {
            switch (sexCode) {
                case "M": genderLegacyCode = "1"; break;
                case "F": genderLegacyCode = "5"; break;
                case "U": genderLegacyCode = ""; break;
            }
        }

        List<String> values = new ArrayList<>();
        //Datasource code
        values.add(ste.getSampling().getDataSource().getLegacyCode());
        //Frequency number (LF) / Set number (TSS)
        values.add(lf.getFrequencyNumber().toString());
        //Country/Quota code
        values.add(lf.getCountry() != null ? lf.getCountry().getLegacyCode() : "");
        //Sampled species code
        values.add(lf.getSampleSpecies().getLegacyCode());
        values.add(genderLegacyCode); //Sex code (1=Male, 5=Female, ""=Unsexed)
        //Trip number (left blank for LF as this field is not currently recorded as a part of an LF)
        values.add("");
        //Vessel CFV Number/Tonnage
        Vessel vessel = lf.getVessel();
        if (vessel != null) {
            values.add(vessel.getCfvSideNumber());
            values.add(vessel.getTonnage().getLegacyCode());
        } else {
            values.add("");
            values.add("");
        }
        //Gear code
        values.add(lf.getGear() != null ? lf.getGear().getLegacyCode() : "");
        //Mesh size (mm)
        values.add(lf.getMeshSizeMillimeters() != null ? lf.getMeshSizeMillimeters().intValue()+"" : "");
        //Nafo division code
        values.add(lf.getNafoDivision() != null ? lf.getNafoDivision().getLegacyCode() : "");
        //Unit area code
        values.add(lf.getUnitArea() != null ? lf.getUnitArea().getLegacyCode() : "");
        //Latitude / Longitude / Catch time (GMT)
        values.add(lf.getLatitude() != null ? lf.getLatitude().toString() : "");
        values.add(lf.getLongitude() != null ? lf.getLongitude().toString() : "");
        values.add(lf.getCatchTime() != null ? lf.getCatchTime().plusHours(5).toString().replaceAll("[^\\d]", "") : "");
        //Catch category code
        values.add(lf.getCatchCategory() != null ? lf.getCatchCategory().getLegacyCode() : "");
        //(Catch date) YY / mm / dd
        LocalDate catchDate = lf.getCatchDate();
        if (catchDate != null) {
            values.add(("" + catchDate.getYear()).substring(2)); //Year 2-digit (2000 = 00)
            values.add("" + catchDate.getMonthValue()); //Month 1-12
            values.add("" + catchDate.getDayOfMonth()); //Day 1-31
        } else {
            values.add(""); //Year
            values.add(""); //Month
            values.add(""); //Day
        }
        //Quarter period number
        values.add(lf.getQuarter() != null ? lf.getQuarter().getPeriodNumber().toString() : "");
        //Port code (LF) / Country code (TSS) ??? Duplicate
        values.add(lf.getPortOfLanding() != null ? lf.getPortOfLanding().getLegacyCode() : "");
        //Depth (m)
        values.add(lf.getDepthMeters() != null ? lf.getDepthMeters().intValue()+"" : "");
        //Turnout weight (kg for LF) / Total weight per set (TSS)
        values.add(lf.getTurnoutWeightKilograms() != null ? Math.round(lf.getTurnoutWeightKilograms())+"" : "");
        //Sample weight (kg) (LF)
        values.add(lf.getSampleWeightKilograms() != null ? Math.round(lf.getSampleWeightKilograms())+"" : "");

        return values;
    }

    @Override
    public void postProcess(SamplingTypeEntity ste, SamplingEntityService service, String actor)
    {
        LengthFrequency lf = (LengthFrequency)ste;
        lf.setStatusId(5); //Archived
        lf.setActor(actor);
        service.saveSamplingEntity(ste);
    }

    private static final Logger logger = LoggerFactory.getLogger(LengthFrequencyExtract.class);
}
