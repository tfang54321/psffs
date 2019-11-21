package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.CellDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellDefinitionRepository extends JpaRepository<CellDefinition, Integer>
{
    CellDefinition getCellDefinitionById(Long id);

    CellDefinition getCellDefinitionByYearAndSpeciesId(int year, int species);

    List<CellDefinition> getAllByYear(int year);

    List<CellDefinition> getCellDefinitionsByStatusId_IdNot(int id);
}
