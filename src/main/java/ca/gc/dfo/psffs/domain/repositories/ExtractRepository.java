package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.Extract;
import ca.gc.dfo.psffs.json.ExtractListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExtractRepository extends JpaRepository<Extract, Long>
{
    @Query("select new ca.gc.dfo.psffs.json.ExtractListItem(e.extractId, e.type, e.createdDate, e.fileName) from Extract e where e.createdBy=:createdBy and e.type=:etype and e.hasData=true")
    List<ExtractListItem> findListByCreatedByAndType(@Param("createdBy") String createdBy, @Param("etype") String type);

    @Query("select new ca.gc.dfo.psffs.json.ExtractListItem(e.extractId, e.type, e.createdDate, e.fileName) from Extract e where e.createdBy=:createdBy and e.hasData=true")
    List<ExtractListItem> findListByCreatedBy(@Param("createdBy") String createdBy);

    List<Extract> findAllByCreatedDateBeforeAndHasDataIsTrue(LocalDate beforeDate);
}
