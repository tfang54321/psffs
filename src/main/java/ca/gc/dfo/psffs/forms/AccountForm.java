package ca.gc.dfo.psffs.forms;

import lombok.Data;

import java.util.List;

@Data
public class AccountForm
{
    private String username;
    private String fullname;
    private String initials;
    private String email;
    private List<String> roles;
}
