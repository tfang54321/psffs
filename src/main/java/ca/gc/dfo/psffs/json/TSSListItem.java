package ca.gc.dfo.psffs.json;

import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.MessageSourceHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.Locale;

@Data
public class TSSListItem
{
    private Long id;
    private String tssId;
    private LocalDate catchDate;
    private Integer tripNumber;
    private Integer setNumber;
    @JsonIgnore
    private String speciesEng;
    @JsonIgnore
    private String speciesFra;
    @JsonIgnore
    private String observerCompanyEng;
    @JsonIgnore
    private String observerCompanyFra;
    @JsonIgnore
    private String statusEng;
    @JsonIgnore
    private String statusFra;

    public TSSListItem() {}

    public TSSListItem(Long id, String tssId, LocalDate catchDate, Integer tripNumber, Integer setNumber,
                       String speciesEng, String speciesFra, String observerCompanyEng, String observerCompanyFra,
                       String statusEng, String statusFra)
    {
        setId(id);
        setTssId(tssId);
        setCatchDate(catchDate);
        setTripNumber(tripNumber);
        setSetNumber(setNumber);
        setSpeciesEng(speciesEng);
        setSpeciesFra(speciesFra);
        setObserverCompanyEng(observerCompanyEng);
        setObserverCompanyFra(observerCompanyFra);
        setStatusEng(statusEng);
        setStatusFra(statusFra);
    }

    public String getSpeciesText()
    {
        return LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                this.speciesEng : this.speciesFra;
    }

    public String getObserverCompanyText()
    {
        return LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                this.observerCompanyEng : this.observerCompanyFra;
    }

    public String getStatusText()
    {
        return LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ?
                this.statusEng : this.statusFra;
    }

    public String getButtons()
    {
        MessageSource ms = MessageSourceHolder.getMessageSource();
        Locale locale = LocaleContextHolder.getLocale();
        StringBuilder b = new StringBuilder();
        b.append("<a href=\"javascript: observer.editTSS('" + this.tssId + "')\" class=\"btn btn-link cell-btn large-buttons\" title=\"" + ms.getMessage("js.icon.edit.title", null, locale) + "\">");
        b.append("<span class=\"glyphicon glyphicon-edit\"></span></a>");
        b.append("<a id=\"delete_" + this.tssId + "\" href=\"javascript: observer.deleteTSS('" + this.tssId + "')\" class=\"btn btn-link cell-btn large-buttons\" title=\"" + ms.getMessage("js.icon.delete.title", null, locale) + "\">");
        b.append("<span class=\"glyphicon glyphicon-trash\"></span></a>");
        return b.toString();
    }

    public String getCheckbox()
    {
        return "<input type=\"checkbox\" class=\"tss-list-checkbox list-checkbox\" value=\""+ this.tssId +"\" />";
    }
}
