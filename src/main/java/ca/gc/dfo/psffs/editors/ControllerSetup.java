package ca.gc.dfo.psffs.editors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice(annotations = Controller.class)
public class ControllerSetup {

    @InitBinder
    public void InitBinder(WebDataBinder binder){
        binder.registerCustomEditor(String.class, new StringEditor());
    }
}
