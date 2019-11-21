package ca.gc.dfo.psffs.forms.lookups;

import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.MessageSourceHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.Locale;

@Data
public class BaseLookupForm
{
    private Integer id;
    private String legacyCode;
    @JsonIgnore
    private String englishDescription;
    @JsonIgnore
    private String frenchDescription;
    private String description;
    @JsonIgnore
    private Integer activeFlag;
    private Integer order;
    @JsonIgnore
    private LocalDate createdDate;
    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    private LocalDate modifiedDate;
    @JsonIgnore
    private String modifiedBy;

    @JsonIgnore
    private String editFunction;
    @JsonIgnore
    private String activateFunction;

    public BaseLookupForm()
    {
        this("lookups.editLookup", "lookups.activateLookup");
    }

    public BaseLookupForm(String editFunction, String activateFunction)
    {
        setEditFunction(editFunction);
        setActivateFunction(activateFunction);
        setActiveFlag(1);
    }

    @JsonIgnore
    public boolean getActive()
    {
        return activeFlag != null && activeFlag.equals(1);
    }

    public String getButtons()
    {
        String desc;
        if(description != null && description.length() > 60){
            desc = description.substring(0, 60) + "...";
        }
        else {
            desc = description;
        }

        Locale locale = LocaleContextHolder.getLocale();
        MessageSource messageSource = MessageSourceHolder.getMessageSource();
        StringBuilder b = new StringBuilder();
        boolean active = getActive();
        b.append("<a href='javascript: ").append(editFunction).append("(").append(id)
         .append(")' class='btn btn-link cell-btn large-buttons' title='")
         .append(messageSource.getMessage("js.icon.edit.title", null, locale)).append("'>");
        b.append("<span class='glyphicon glyphicon-edit'></span></a>");
        b.append("<a id='activate_").append(id).append("' href='javascript: ").append(activateFunction).append("(").append(id).append(",")
         .append("`").append(desc != null ? desc.replaceAll("'", "&apos;"):"").append("`,").append(!active).append(")' class='btn btn-link cell-btn large-buttons' title='")
         .append(messageSource.getMessage("js.icon."+(active ? "deactivate" : "activate")+".title", null, locale)).append("'>");
        b.append("<span class='glyphicon glyphicon-").append(active ? "ok-circle text-green" : "ban-circle text-red").append("'></span></a>");
        return b.toString();
    }
}
