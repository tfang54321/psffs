package ca.gc.dfo.psffs.json;

import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.MessageSourceHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.Locale;

@Data
public class LFListItem
{
    private Long id;
    private String lfId;
    @JsonIgnore
    private String speciesCode;
    @JsonIgnore
    private String speciesEng;
    @JsonIgnore
    private String speciesFra;
    @JsonIgnore
    private String nafoDivision;
    @JsonIgnore
    private String gearCode;
    @JsonIgnore
    private String gearEng;
    @JsonIgnore
    private String gearFra;
    @JsonIgnore
    private String statusEng;
    @JsonIgnore
    private String statusFra;

    public LFListItem() {}

    public LFListItem(Long id, String lfId, String speciesCode, String speciesEng, String speciesFra,
                      String nafoDivision, String gearCode, String gearEng, String gearFra, String statusEng, String statusFra)
    {
        setId(id);
        setLfId(lfId);
        setSpeciesCode(speciesCode);
        setSpeciesEng(speciesEng);
        setSpeciesFra(speciesFra);
        setNafoDivision(nafoDivision);
        setGearCode(gearCode);
        setGearEng(gearEng);
        setGearFra(gearFra);
        setStatusEng(statusEng);
        setStatusFra(statusFra);
    }

    public String getSampleSpeciesIdText()
    {
        String value = "";
        if(this.speciesCode != null) {
            value = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                    this.speciesEng + " (" + this.speciesCode + ")" :
                    this.speciesFra + " (" + this.speciesCode + ")";
        }
        return value;
    }

    public String getNafoDivisionIdText()
    {
        return this.nafoDivision;
    }

    public String getGearIdText()
    {
        String value = "";
        if(this.gearCode != null) {
            value = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                    this.gearEng + " (" + this.gearCode + ")" :
                    this.gearFra + " (" + this.gearCode + ")";
        }
        return value;
    }

    public String getStatusIdText()
    {
        return LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                    this.statusEng : this.statusFra;
    }

    public String getButtons()
    {
        MessageSource ms = MessageSourceHolder.getMessageSource();
        Locale locale = LocaleContextHolder.getLocale();
        StringBuilder b = new StringBuilder();
        b.append("<a href=\"javascript: lengthFrequency.editLF('" + lfId + "')\" class=\"btn btn-link cell-btn large-buttons\" title=\"" + ms.getMessage("js.icon.edit.title", null, locale) + "\">");
        b.append("<span class=\"glyphicon glyphicon-edit\"></span></a>");
        b.append("<a id=\"delete_" + lfId + "\" href=\"javascript: lengthFrequency.deleteLF('" + lfId + "')\" class=\"btn btn-link cell-btn large-buttons\" title=\"" + ms.getMessage("js.icon.delete.title", null, locale) + "\">");
        b.append("<span class=\"glyphicon glyphicon-trash\"></span></a>");
        return b.toString();
    }

    public String getCheckbox()
    {
        return "<input type=\"checkbox\" class=\"lf-on-list-checkbox list-checkbox\" value=\""+ lfId +"\" />";
    }
}
