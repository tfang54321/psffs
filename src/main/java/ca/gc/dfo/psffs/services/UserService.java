package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.security.User;
import ca.gc.dfo.psffs.domain.objects.security.UserPreference;
import ca.gc.dfo.psffs.domain.objects.security.UserRole;
import ca.gc.dfo.psffs.domain.repositories.security.RoleRepository;
import ca.gc.dfo.psffs.domain.repositories.security.UserRepository;
import ca.gc.dfo.psffs.forms.UserManagementForm;
import ca.gc.dfo.psffs.json.UserList;
import ca.gc.dfo.psffs.json.UserListItem;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.AbstractEAccessUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractEAccessUserService implements Serializable
{
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuditingServiceInterface userAuditService;

    public UserPreference getUserPreferenceByNtPrincipal(UserPreference.PreferenceType prefType, String ntPrincipal)
    {
        UserPreference userPref = null;
        User user = (User)getUserByNtPrincipal(ntPrincipal);
        if (user != null) {
            List<UserPreference> userPrefs = user.getUserPreferences().stream()
                                                .filter(pref -> pref.getPreferenceType().equals(prefType))
                                                .limit(1)
                                                .collect(Collectors.toList());
            if (userPrefs.size() > 0) {
                userPref = userPrefs.get(0);
                userPref.setUser(user);
            }
        }
        return userPref;
    }

    @Transactional
    public UserPreference saveUserPreferenceData(UserPreference.PreferenceType preferenceType, String ntPrincipal, String preferenceData)
    {
        UserPreference userPref = getUserPreferenceByNtPrincipal(preferenceType, ntPrincipal);
        if (userPref != null) {
            userPref.setPreferenceData(preferenceData);
            String actor = SecurityHelper.getNtPrincipal();
            userPref.setActor(actor);
            getUserRepository().save(userPref.getUser());
        }
        return userPref;
    }

    @Transactional
    public User saveUser(User user)
    {
        User savedUser = null;
        String actor = SecurityHelper.getNtPrincipal();
        user.setActor(actor);
         try {
        savedUser = (User)getUserRepository().saveAndFlush(user);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to save user: " + ex.getMessage(), ex);
        }

        return savedUser;
    }

    @Transactional
    public User addUserForm(UserManagementForm uaForm){
        User user = convertFromForm(uaForm);
        List<UserRole> userRoleList = new ArrayList<>();

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(roleRepository.getOne(uaForm.getRole()));
        userRole.setActiveFlag(1);

        userRoleList.add(userRole);

        UserRole baseUserRole = new UserRole();
        baseUserRole.setUser(user);
        baseUserRole.setRole(roleRepository.getOne(1));
        baseUserRole.setActiveFlag(1);

        userRoleList.add(baseUserRole);

        user.setUserRoles(userRoleList);

        return saveUser(user);
    }

    @Transactional
    @PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
    public void toggleUserActive(Integer id)
    {
        User user = getUserById(id);
        Integer active = user.getActiveFlag();
        if(active.equals(1)){
            user.setActiveFlag(0);
        }
        else {
            user.setActiveFlag(1);
        }
        saveUser(user);
    }

    @Transactional
    public User editUserForm(UserManagementForm uaForm){
        User user = convertFromForm(uaForm);
        User oldUser = getUserById(user.getUserId());

        List<UserRole> userRoles = oldUser.getUserRoles();
        for (UserRole userRole:
             userRoles) {
            if(!userRole.getRole().getRoleName().equals("ROLE_BASE_ACCESS")){
                userRole.setRole(roleRepository.getOne(uaForm.getRole()));
            }
        }
        user.setUserRoles(userRoles);

        return saveUser(user);
    }

    public User getUserById(Integer id){
        return (User) getUserRepository().getOne(id);
    }

    public List<User> getAllUsers(){
        return (List<User>) getUserRepository().findAll();
    }

    public UserList getUserList(){
        List<UserListItem> userItemList = new ArrayList<>();
        List<User> tempList = getAllUsers();

        for (User user:
             tempList) {
            UserListItem userListItem = new UserListItem();
            userListItem.setUserId(user.getUserId());
            userListItem.setFullname(user.getFullname());
            userListItem.setNtPrincipal(user.getNtPrincipal());
            userListItem.setEmail(user.getEmail());
            userListItem.setActiveStatus(user.getActiveFlag() == 1);

            if(!user.getNtPrincipal().equals(SecurityHelper.getNtPrincipal())) {
                userItemList.add(userListItem);
            }
        }

        List<Integer> allUserID = userAuditService.getAllUserID();

        UserList userList = new UserList(userItemList,allUserID);

        return userList;
    }

    public User findUserByUsername(String username){ return userRepository.findByNtPrincipal(username); }

    public User findUserByInitials(String initials){ return userRepository.findByInitials(initials); }

    private User convertFromForm(UserManagementForm uaForm){
        User user = new User();
        user.setUserId(uaForm.getUserId());
        user.setNtPrincipal(uaForm.getNtPrincipal());
        user.setPartyId(uaForm.getPartyId());
        user.setActiveFlag(uaForm.getActiveFlag());
        user.setEmail(uaForm.getEmail());
        user.setFirstName(uaForm.getFirstName());
        user.setLastName(uaForm.getLastName());
        user.setInitials(uaForm.getInitials());

        return user;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
}
