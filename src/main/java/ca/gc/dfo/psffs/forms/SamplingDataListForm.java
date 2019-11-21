package ca.gc.dfo.psffs.forms;

import ca.gc.dfo.psffs.domain.objects.lookups.SamplingDataStatus;
import ca.gc.dfo.psffs.domain.objects.lookups.SamplingStatus;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.MessageSourceHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class SamplingDataListForm extends SamplingDataForm
{
    public String getOtolith_otolithEdgeCode()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String otolithEdgeCode = getOtolithEdgeCode() != null ? getOtolithEdgeCode() : "";
        b.append("<input type=\"text\" class=\"samplingData_otolithEdgeCode\" id=\"otolith_")
         .append(getSamplingDataId()).append("_otolithEdgeCode").append("\" value=\"").append(otolithEdgeCode)
         .append("\" list=\"otolithEdges\" style=\"width: 3em;\" ")
         .append("data-field=\"samplingData_otolithEdgeCode\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("data-ap-auto-select-first=\"true\" ")
         .append("/>");
        return b.toString();
    }

    public String getOtolith_otolithReliabilityCode()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String otolithReliabilityCode = getOtolithReliabilityCode() != null ? getOtolithReliabilityCode() : "";
        b.append("<input type=\"text\" class=\"samplingData_otolithReliabilityCode\" id=\"otolith_")
         .append(getSamplingDataId()).append("_otolithReliabilityCode").append("\" value=\"")
         .append(otolithReliabilityCode).append("\" list=\"otolithReliabilities\" style=\"width: 3em;\" ")
         .append("data-field=\"samplingData_otolithReliabilityCode\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("data-ap-auto-select-first=\"true\" ")
         .append("/>");
        return b.toString();
    }

    public String getOtolith_age()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String age = getAge() != null ? getAge().toString() : "";
        b.append("<input type=\"number\" min=\"0\" class=\"samplingData_age\" id=\"otolith_")
         .append(getSamplingDataId()).append("_age").append("\" value=\"").append(age)
         .append("\" style=\"width: 75px;height:28px;\" ")
         .append("data-field=\"samplingData_age\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightKg_roundWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String roundWt = getRoundWt() != null ? getRoundWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_roundWt\" id=\"weightKg_")
                .append(getSamplingDataId()).append("_roundWt").append("\" value=\"").append(roundWt)
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_roundWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightKg_guttedWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String guttedWt = getGuttedWt() != null ? getGuttedWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_guttedWt\" id=\"weightKg_")
                .append(getSamplingDataId()).append("_guttedWt").append("\" value=\"").append(getGuttedWt())
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_guttedWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightKg_ggWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String ggWt = getGgWt() != null ? getGgWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_ggWt\" id=\"weightKg_")
                .append(getSamplingDataId()).append("_ggWt").append("\" value=\"").append(ggWt)
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_ggWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightG_gonadWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String gonadWt = getGonadWt() != null ? getGonadWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_gonadWt\" id=\"weightG_")
                .append(getSamplingDataId()).append("_gonadWt").append("\" value=\"").append(gonadWt)
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_gonadWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightG_liverWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String liverWt = getLiverWt() != null ? getLiverWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_liverWt\" id=\"weightG_")
                .append(getSamplingDataId()).append("_liverWt").append("\" value=\"").append(liverWt)
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_liverWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightG_gutsWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String gutsWt = getGutsWt() != null ? getGutsWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_gutsWt\" id=\"weightG_")
                .append(getSamplingDataId()).append("_gutsWt").append("\" value=\"").append(gutsWt)
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_gutsWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getWeightG_stomachWt()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String stomachWt = getStomachWt() != null ? getStomachWt().toString() : "";
        b.append("<input type=\"text\" data-mask=\"0ZZPZZ\" class=\"samplingData_stomachWt\" id=\"weightG_")
                .append(getSamplingDataId()).append("_stomachWt").append("\" value=\"").append(stomachWt)
                .append("\" style=\"width: 4em;\" placeholder=\"000.00\" data-mask-translation='")
                .append("{\"Z\":{\"pattern\":\"[0-9]\",\"optional\":\"true\"},\"P\":{\"pattern\":\".\",\"optional\":\"true\"}}' ")
                .append("data-field=\"samplingData_stomachWt\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getStomach_fullness()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String fullness = getFullness() != null ? getFullness().toString() : "";
        b.append("<input type=\"number\" min=\"0\" max=\"9\" class=\"samplingData_fullness\" id=\"stomach_")
                .append(getSamplingDataId()).append("_fullness").append("\" value=\"").append(fullness)
                .append("\" style=\"width: 75px;height:28px;\" ")
                .append("data-field=\"samplingData_fullness\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getStomach_parasiteCode()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String parasiteCode = getParasiteCode() != null ? getParasiteCode() : "";
        b.append("<input type=\"text\" class=\"samplingData_parasiteCode\" id=\"stomach_")
                .append(getSamplingDataId()).append("_parasiteCode").append("\" value=\"").append(parasiteCode)
                .append("\" list=\"parasites\" style=\"width: 3em;\" ")
                .append("data-field=\"samplingData_parasiteCode\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("data-ap-auto-select-first=\"true\" ")
         .append("/>");
        return b.toString();
    }

    public String getStomach_nbrOfParasites()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String nbrOfParasites = getNbrOfParasites() != null ? getNbrOfParasites().toString() : "";
        b.append("<input type=\"number\" min=\"0\" class=\"samplingData_nbrOfParasites\" id=\"stomach_")
                .append(getSamplingDataId()).append("_nbrOfParasites").append("\" value=\"").append(nbrOfParasites)
                .append("\" style=\"width: 75px;height:28px;\" ")
                .append("data-field=\"samplingData_nbrOfParasites\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("/>");
        return b.toString();
    }

    public String getStomach_primaryStomachContentCode()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String primaryStomachContentCode = getPrimaryStomachContentCode() != null ? getPrimaryStomachContentCode() : "";
        b.append("<input type=\"text\" class=\"samplingData_primaryStomachContentCode\" id=\"stomach_")
                .append(getSamplingDataId()).append("_primaryStomachContentCode").append("\" value=\"")
                .append(primaryStomachContentCode).append("\" list=\"stomachContents\" style=\"width: 3em;\" ")
                .append("data-field=\"samplingData_primaryStomachContentCode\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("data-ap-auto-select-first=\"true\" ")
         .append("/>");
        return b.toString();
    }

    public String getStomach_secondaryStomachContentCode()
    {
        boolean readonly = statusNotEntered();
        StringBuilder b = new StringBuilder();
        String secondaryStomachContentCode = getSecondaryStomachContentCode() != null ? getSecondaryStomachContentCode() : "";
        b.append("<input type=\"text\" class=\"samplingData_secondaryStomachContentCode\" id=\"stomach_")
                .append(getSamplingDataId()).append("_secondaryStomachContentCode").append("\" value=\"")
                .append(secondaryStomachContentCode).append("\" list=\"stomachContents\" style=\"width: 3em;\" ")
                .append("data-field=\"samplingData_secondaryStomachContentCode\" ");
        if (readonly) b.append("readonly=\"readonly\" ");
        b.append("data-ap-auto-select-first=\"true\" ")
         .append("/>");
        return b.toString();
    }

    public String getSdlist_statusId()
    {
        boolean checked = statusNotEntered();
        boolean enabled = !checked;
        StringBuilder b = new StringBuilder();
        b.append("<input type=\"checkbox\" class=\"sdlist_statusId\" id=\"sdlist_").append(getSamplingDataId())
                .append("_statusId\" value=\"").append(SamplingDataStatus.COMPLETED_STS_ID).append("\"");
        if (checked) b.append(" checked=\"checked\"");
        if (!enabled) b.append(" onclick=\"return false;\"");
        b.append(" data-field=\"samplingData_statusId\" style=\"transform: scale(1.5); margin-left: 3px;\" />");
        return b.toString();
    }

    public String getArchives_status()
    {
        return LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("fr") ? getStatusFra() : getStatusEng();
    }

    public String getButtons()
    {
        boolean saveDisabled = statusNotEntered();
        Locale locale = LocaleContextHolder.getLocale();
        MessageSource messageSource = MessageSourceHolder.getMessageSource();
        StringBuilder b = new StringBuilder();
        b.append("<div style=\"width: 6em;\">");
        b.append("<button type=\"button\" class=\"btn btn-link cell-btn btn-sd-save large-buttons\" onclick=\"samplingData.saveSamplingDataRecord(")
         .append("this)\" data-sd-id=\"")
         .append(getSamplingDataId())
         .append("\" ");
        if (saveDisabled) b.append("disabled=\"disabled\" ");
        b.append("title=\"")
         .append(messageSource.getMessage("table.action.save_record", null, locale))
         .append("\"><span class=\"glyphicon glyphicon-floppy-disk\" style=\"font-size: 1.2em\"></span></button>");
        b.append("<button type=\"button\" class=\"btn btn-link cell-btn large-buttons\" onclick=\"samplingData.openSamplingDataRecord(")
         .append(getSamplingDataId())
         .append(")\" title=\"")
         .append(messageSource.getMessage("table.action.open_record", null, locale))
         .append("\"><span class=\"glyphicon glyphicon-search\"></span></button>");
        b.append("</div>");
        return b.toString();
    }

    public String getArchives_checkbox()
    {
        boolean enabled = statusNotEntered();
        StringBuilder b = new StringBuilder();
        b.append("<input type=\"checkbox\" class=\"archives_checkbox\" value=\"").append(getSamplingDataId()).append("\"");
        if (!enabled) b.append(" disabled=\"disabled\"");
        b.append(" style=\"transform: scale(1.5); margin-left: 3px;\" />");
        return b.toString();
    }

    private boolean statusNotEntered()
    {
        return getStatusId() != null && !getStatusId().equals(SamplingDataStatus.ENTERED_STS_ID);
    }
}
