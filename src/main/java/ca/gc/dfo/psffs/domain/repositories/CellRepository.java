package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellRepository extends JpaRepository<Cell, Long>
{
    @Query("select c from Cell c where c.cellDefinition.id=:cellDefinitionId and c.cellCode=:cellCode")
    List<Cell> findAllByCellDefinitionIdAndCellCode(@Param("cellDefinitionId") Long cellDefinitionId,
                                                    @Param("cellCode") String cellCode);

    @Query("select c from Cell c where c.cellDefinition.id=:cellDefinitionId")
    List<Cell> findAllByCellDefinitionId(@Param("cellDefinitionId") Long cellDefinitionId);
}
