package ca.gc.dfo.psffs.domain.repositories.security;

import ca.gc.dfo.psffs.domain.objects.security.User;
import ca.gc.dfo.spring_commons.commons_offline_wet.domain.EAccessUserRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends EAccessUserRepository<User, Integer>
{
    List<User> findAllByOrderByInitialsAsc();

    @Query("select u from User u where u.initials=:initials")
    User findByInitials(@Param("initials") String initials);

    @Query("select u from User u where u.ntPrincipal=:ntPrincipal")
    User findByNtPrincipal(@Param("ntPrincipal") String ntPrincipal);

    @Query("select count(u) from User u where u.initials=:initials")
    Long countByInitialsEquals(@Param("initials") String initials);
}
