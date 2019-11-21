package ca.gc.dfo.psffs.export.extract;

import ca.gc.dfo.psffs.domain.objects.LengthFrequency;
import ca.gc.dfo.psffs.domain.objects.SamplingData;
import ca.gc.dfo.psffs.domain.objects.TripSetSpecies;
import ca.gc.dfo.psffs.domain.objects.lookups.SamplingDataStatus;
import ca.gc.dfo.psffs.services.ExtractService;
import ca.gc.dfo.psffs.services.SamplingDataService;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AgeGrowthExtract extends AbstractExtractRunner<SamplingData, SamplingDataService>
{
    private Integer longestLine;

    private static final Integer[] FIELD_SPECS = new Integer[] {
            1, //Data Source
            3, //Sampled Species (Species code)
            2, //Sample Type
            3, //Trip Number
            3, //Set Number(not used, left blank)
            2, //Year (2-digit)
            2, //Month
            2, //Day
            2, //Country (Quota) code
            2, //Gear
            3, //Nafo Division code
            2, //Unit area code
            4, //Depth Fished
            4, //Directed Species
            3, //Not Used
            1, //Commercial sample type (c=COMMERCIAL RANDOM, D=COMMERCIAL CATEGORY, 7=COMMERCIAL STRATIFIED)
            5, //Storage number (specimen number)
            3, //Length (cm)
            1, //Sex code (1=Male, 2=Female, Blank=Unsexed)
            3, //Maturity level
            2, //Age
            1, //Otolith edge code
            1, //Otolith reliability code
            2, //Spawning age (Left blank as it is not used anymore)
            4, //Round weight (kg, to 2 decimals)
            4, //Gutted weight (kg, to 2 decimals)
            4, //Gonad weight (grams, whole number)
            1, //Stomach fullness (0-9 for all species)
            2, //Primary stomach content code
            2, //Secondary stomach content code
            3, //Girth in mm  (Left blank as it is not used anymore)
            1, //Parasite code
            2, //Number of parasites
            4, //G&G Weight (kg, to 2 decimals)
            4, //Volume of Gonad (millimeters, whole number)
            1, //Average Egg Diameter (millimeters, whole number)
            4, //Weight of Liver (grams, whole number)
            4, //Volume of Liver (millimeters, whole number)
            4, //Weight of Stomach (grams, whole number)
            4, //Volume of Stomach (millimeters, whole number)
            4, //Weight of Remainder Guts (grams, whole number)
            4  //Volume of Remainder Guts (millimeters, whole number)
    };

    @Override
    public Integer getSpaceIndicatorLength()
    {
        return this.longestLine;
    }

    public AgeGrowthExtract(ExtractService extractService, SamplingDataService samplingDataService, String actor, BooleanExpression predicate)
    {
        super(actor, AgeGrowthExtract.class.getName(), ".WGT", extractService, samplingDataService, predicate);
        this.longestLine = 0;
    }

    @Override
    public String renderRecord(SamplingData samplingData)
    {
        return enforceValueLengths(extractValues(samplingData), FIELD_SPECS).stream().collect(Collectors.joining());
    }

    protected List<String> extractValues(SamplingData samplingData)
    {
        List<String> values = new ArrayList<>();
        values.add(samplingData.getSampling().getDataSource().getLegacyCode() + ""); //Data Source
        values.add(samplingData.getSampling().getSpecies().getLegacyCode()); //Sampled Species
        values.add(samplingData.getSampling().getSamplingType().getLegacyCode() + ""); //Sample Type
        if (samplingData.getSampling().getSamplingTypeId().equals(31)) { //TSS
            values.add(samplingData.getSampling().getTripSetSpecies().getTripNumber() + ""); //Trip
            values.add(samplingData.getSampling().getTripSetSpecies().getSetNumber() + ""); //Set
        }
        else {
            values.add(""); //Trip
            values.add(""); //Set
        }
        LocalDate createdDate = samplingData.getSampling().getCreatedDate();
        values.add((createdDate.getYear()+"").substring(2)); //Year 2-digit (1977=77)
        values.add(createdDate.getMonthValue()+""); //Month
        values.add(createdDate.getDayOfMonth()+""); //Day
        if (samplingData.getSampling().getSamplingTypeId().equals(30)) { //LF
            LengthFrequency lf = samplingData.getSampling().getLengthFrequency();
            values.add(lf.getCountry() != null ? lf.getCountry().getLegacyCode() : "");//Country (Quota) code
            values.add(lf.getGear() != null ? lf.getGear().getLegacyCode() : "");//Gear code
            values.add(lf.getNafoDivision() != null ?
                    enforceValueLength(lf.getNafoDivision().getLegacyCode(), FIELD_SPECS[10]) : "");//Nafo division code
            values.add(lf.getUnitArea() != null ? lf.getUnitArea().getLegacyCode() : "");//Unit area code
            values.add(lf.getDepthMeters() != null ? Math.round(lf.getDepthMeters())+"" : "");//Depth meters
            values.add(lf.getDirectedSpecies().getLegacyCode());//Directed Species
        } else if (samplingData.getSampling().getSamplingTypeId().equals(31)) { //TSS
            TripSetSpecies tss = samplingData.getSampling().getTripSetSpecies();
            values.add(tss.getCountry() != null ? tss.getCountry().getLegacyCode() : "");//Country (Quota) code
            values.add(tss.getGear() != null ? tss.getGear().getLegacyCode() : "");//Gear code
            values.add(tss.getNafoDivision() != null ?
                    enforceValueLength(tss.getNafoDivision().getLegacyCode(), FIELD_SPECS[10]) : "");//Nafo division code
            values.add(tss.getUnitArea() != null ? tss.getUnitArea().getLegacyCode() : "");//Unit area code
            values.add(tss.getDepthMeters() != null ? Math.round(tss.getDepthMeters())+"" : "");//Depth meters
            values.add(tss.getDirectedSpecies().getLegacyCode());//Directed Species
        } else {
            //All specific values are blank since a specific type not found.
            values.add("");
            values.add("");
            values.add("");
            values.add("");
            values.add("");
            values.add("");
        }
        values.add(""); //Not used
        values.add("7");//Commercial sample type
        values.add(samplingData.getStorageNbr()+"");//Storage number (specimen number)
        values.add(samplingData.getSamplingEntry().getLength()+"");//Length
        values.add(convertSexCode(samplingData.getSamplingEntry().getSex()));//Sex code (1=Male, 5=Female, Blank=Unsexed)
        values.add(samplingData.getSamplingEntry().getMaturityLevel() != null ?
                samplingData.getSamplingEntry().getMaturityLevel() : "");//Maturity level code
        values.add(samplingData.getAge() != null ? samplingData.getAge()+"" : "");//Age
        values.add(samplingData.getOtolithEdgeCode() != null ? samplingData.getOtolithEdgeCode() : "");//Otolith edge code
        values.add(samplingData.getOtolithReliabilityCode() != null ? samplingData.getOtolithReliabilityCode() : "");//Otolith reliability code
        values.add("");//Spawning age (Not used)
        values.add(samplingData.getRoundWt() != null ? (Math.round(samplingData.getRoundWt() * 100.0f) / 100.0f)+"":"");//Round weight (2 decimals)
        values.add(samplingData.getGuttedWt() != null ? (Math.round(samplingData.getGuttedWt() * 100.0f) / 100.0f)+"":"");//Gutted weight (2 decimals)
        values.add(samplingData.getGonadWt() != null ? (Math.round(samplingData.getGonadWt()))+"":"");//Gonad weight rounded to nearest whole
        values.add(samplingData.getFullness() != null ?
                samplingData.getFullness().toString() : "");//Fullness (0-9 for all species)
        values.add(samplingData.getPrimaryStomachContentCode() != null ?
                samplingData.getPrimaryStomachContentCode() : "");//Primary stomach content code
        values.add(samplingData.getSecondaryStomachContentCode() != null ?
                samplingData.getSecondaryStomachContentCode() : "");//Secondary stomach content code
        values.add("");//Girth (Not used)
        values.add(samplingData.getParasiteCode() != null ? samplingData.getParasiteCode() : "");//Parasite type
        values.add(samplingData.getNbrOfParasites() != null ? samplingData.getNbrOfParasites()+"":"");//Number of parasites

        values.add(samplingData.getGgWt() != null ? (Math.round(samplingData.getGgWt() * 100.0f) / 100.0f)+"":""); //G&G weight
        values.add(""); //Volume of Gonad TODO: Not found in PSFFS
        values.add(samplingData.getEggDiameter() != null ? Math.round(samplingData.getEggDiameter()) + "" : ""); //Average Egg Diameter
        values.add(samplingData.getLiverWt() != null ? Math.round(samplingData.getLiverWt()) + "" : ""); //Weight of Liver
        values.add(""); //Volume of Liver TODO: Not found in PSFFS
        values.add(samplingData.getStomachWt() != null ? Math.round(samplingData.getStomachWt()) + "" : ""); //Weight of Stomach
        values.add(""); //Volume of Stomach TODO: Not found in PSFFS
        values.add(samplingData.getGutsWt() != null ? Math.round(samplingData.getGutsWt()) + "" : ""); //Weight of Remainder guts
        values.add(""); //Volume of Remainder guts TODO: Not found in PSFFS
        return values;
    }

    private String convertSexCode(String sexCode)
    {
        String convertedSexCode = "";
        if (sexCode != null) {
            switch (sexCode.toLowerCase()) {
                case "f": convertedSexCode = "5"; break;
                case "m": convertedSexCode = "1"; break;
                default: convertedSexCode = ""; break;
            }
        }
        return convertedSexCode;
    }

    @Override
    public void postProcess(SamplingData samplingData, SamplingDataService samplingDataService, String actor)
    {
        samplingData.setStatusId(SamplingDataStatus.ARCHIVED_STS_ID);
        samplingData.setActor(actor);
        samplingDataService.saveSamplingData(samplingData);
    }
}
