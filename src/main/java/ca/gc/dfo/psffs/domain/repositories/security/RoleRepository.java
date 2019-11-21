package ca.gc.dfo.psffs.domain.repositories.security;

import ca.gc.dfo.psffs.domain.objects.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>
{
    @Query("select r from Role r where r.roleName<>:role")
    List<Role> findAllExcept(@Param("role") String role);
}
