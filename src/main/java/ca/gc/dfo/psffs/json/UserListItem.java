package ca.gc.dfo.psffs.json;

import lombok.Data;

@Data
public class UserListItem {
    private Integer userId;

    private String ntPrincipal;

    private String fullname;

    private String email;

    private String buttons;

    private Boolean activeStatus;
}
