package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.TripSetSpecies;
import ca.gc.dfo.psffs.json.TSSListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripSetSpeciesRepository extends JpaRepository<TripSetSpecies, Long>, QuerydslPredicateExecutor<TripSetSpecies>
{
    @Query("select tss from TripSetSpecies tss inner join tss.sampling s where s.samplingCode = :samplingCode")
    List<TripSetSpecies> queryBySampleCode(@Param("samplingCode") String sampleCode);


    @Query("select new ca.gc.dfo.psffs.json.TSSListItem(tss.id, s.samplingCode, tss.catchDate, tss.tripNumber, " +
            "tss.setNumber, ss.englishDescription, ss.frenchDescription, oc.englishDescription, oc.frenchDescription, " +
            "sts.englishDescription, sts.frenchDescription) " +
            "from TripSetSpecies tss inner join tss.sampling s inner join tss.observerCompany oc " +
            "inner join tss.sampledSpecies ss inner join tss.status sts " +
            "where tss.catchDate >= :fromDate and tss.catchDate < :toDate")
    List<TSSListItem> queryListByDateRange(LocalDate fromDate, LocalDate toDate);
}
