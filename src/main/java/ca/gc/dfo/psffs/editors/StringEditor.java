package ca.gc.dfo.psffs.editors;

import java.beans.PropertyEditorSupport;

public class StringEditor extends PropertyEditorSupport {

    public void setAsText(String value){
        try {
            setValue(value.replaceAll("\r\n", "\r"));
        } catch(Exception e) {
            setValue(null);
        }
    }

    public String getAsText() {
        String s = "";
        if (getValue() != null) {
            s = getValue().toString();
        }
        return s;
    }
}
