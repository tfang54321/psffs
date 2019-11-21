package ca.gc.dfo.psffs.web.security;

import ca.gc.dfo.spring_commons.commons_offline_wet.security.EAccessSecurityHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityHelper extends EAccessSecurityHelper
{
    private static final String EL_HAS_ROLE_OPEN = "hasRole(";
    private static final String EL_HAS_ANY_ROLE_OPEN = "hasAnyRole(";
    private static final String EL_HAS_ROLE_CLOSE = ")";

    public static final String ROLE_BASE_ACCESS = "BASE_ACCESS";
    public static final String ROLE_GUEST = "GUEST";
    public static final String ROLE_STUDENT_TRAINEE = "STUDENT_TRAINEE";
    public static final String ROLE_CONTRIBUTOR = "CONTRIBUTOR";
    public static final String ROLE_ADMIN = "ADMIN";

    //AUTHORITY GRANT CONSTANTS
    public static final String GRANT_BASE_ACCESS = "'" + ROLE_BASE_ACCESS + "'";
    public static final String GRANT_GUEST = "'" + ROLE_GUEST + "'";
    public static final String GRANT_STUDENT_TRAINEE = "'" + ROLE_STUDENT_TRAINEE + "'";
    public static final String GRANT_CONTRIBUTOR = "'" + ROLE_CONTRIBUTOR + "'";
    public static final String GRANT_ADMIN = "'" + ROLE_ADMIN + "'";

    //VIEW - ROLE SPEL
    public static final String EL_VIEW_USER_MANAGEMENT = EL_HAS_ANY_ROLE_OPEN + GRANT_STUDENT_TRAINEE + "," +
            GRANT_CONTRIBUTOR + "," + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_VIEW_CODE_TABLES = EL_VIEW_USER_MANAGEMENT; //Same access, so declaration re-used.
    public static final String EL_VIEW_SAMPLING_SETTINGS = EL_HAS_ANY_ROLE_OPEN + GRANT_GUEST + "," +
            GRANT_STUDENT_TRAINEE + "," + GRANT_CONTRIBUTOR + "," + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_VIEW_CELL_DEFINITIONS = EL_VIEW_SAMPLING_SETTINGS; //Same access, so declaration re-used.
    public static final String EL_VIEW_LENGTH_FREQUENCIES = EL_VIEW_SAMPLING_SETTINGS; //Same access, so declaration re-used.
    public static final String EL_VIEW_TRIP_SET_SPECIES = EL_VIEW_SAMPLING_SETTINGS; //Same access, so declaration re-used.
    public static final String EL_VIEW_SAMPLING_DATA = EL_VIEW_SAMPLING_SETTINGS; //Same access, so declaration re-used.
    public static final String EL_VIEW_EXTRACTS = EL_VIEW_SAMPLING_SETTINGS; //Same access, so declaration re-used.

    //MODIFY (ADD/UPDATE) - ROLE SPEL
    public static final String EL_MODIFY_USER_MANAGEMENT = EL_HAS_ROLE_OPEN + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_MODIFY_CODE_TABLES = EL_MODIFY_USER_MANAGEMENT; //Same access, so declaration re-used.
    public static final String EL_MODIFY_SAMPLING_SETTINGS = EL_MODIFY_USER_MANAGEMENT; //Same access, so declaration re-used.
    public static final String EL_MODIFY_CELL_DEFINITIONS = EL_MODIFY_USER_MANAGEMENT; //Same access, so declaration re-used.
    public static final String EL_MODIFY_LENGTH_FREQUENCIES = EL_HAS_ANY_ROLE_OPEN + GRANT_STUDENT_TRAINEE + "," +
            GRANT_CONTRIBUTOR + "," + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_MODIFY_TRIP_SET_SPECIES = EL_MODIFY_LENGTH_FREQUENCIES; //Same access, so declaration re-used.
    public static final String EL_MODIFY_SAMPLING_DATA = EL_MODIFY_LENGTH_FREQUENCIES; //Same access, so declaration re-used.
    public static final String EL_EXECUTE_EXTRACTS = EL_HAS_ANY_ROLE_OPEN + GRANT_CONTRIBUTOR + "," + GRANT_ADMIN +
            EL_HAS_ROLE_CLOSE;
    public static final String EL_MARK_FOR_ARCHIVE_LENGTH_FREQUENCIES = EL_MODIFY_USER_MANAGEMENT; //Same accessm so declaration re-used

    //DELETE - ROLE SPEL
    public static final String EL_DELETE_CELL_DEFINITIONS = EL_HAS_ROLE_OPEN + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;; //Same access, so declaration re-used.
    public static final String EL_DELETE_LENGTH_FREQUENCIES = EL_HAS_ANY_ROLE_OPEN + GRANT_CONTRIBUTOR + "," +
            GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_DELETE_TRIP_SET_SPECIES = EL_DELETE_LENGTH_FREQUENCIES; //Same access, so declaration re-used.

    public static String TH_EL(String elConstantName) throws IllegalAccessException
    {
        String thEl = "false";
        List<Field> fieldList = Arrays.stream(SecurityHelper.class.getDeclaredFields())
                                      .filter(f -> Modifier.isPublic(f.getModifiers()))
                                      .filter(f -> Modifier.isStatic(f.getModifiers()))
                                      .filter(f -> Modifier.isFinal(f.getModifiers()))
                                      .filter(f -> f.getType().equals(String.class))
                                      .filter(f -> f.getName().equals(elConstantName))
                                      .collect(Collectors.toList());
        if (fieldList.size() > 0) {
            thEl = "#authorization.expression('";
            thEl += ((String)fieldList.get(0).get(null)).replaceAll("'", "''");
            thEl += "')";
        }
        return thEl;
    }
}
