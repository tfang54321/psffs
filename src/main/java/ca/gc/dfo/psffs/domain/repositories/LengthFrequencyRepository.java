package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.LengthFrequency;
import ca.gc.dfo.psffs.json.LFListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LengthFrequencyRepository extends JpaRepository<LengthFrequency, Long>, QuerydslPredicateExecutor<LengthFrequency>
{
    @Query("select lf from LengthFrequency lf inner join lf.sampling s where s.samplingCode = :samplingCode")
    List<LengthFrequency> queryBySampleCode(@Param("samplingCode") String sampleCode);

    @Query("select new ca.gc.dfo.psffs.json.LFListItem(lf.id, s.samplingCode, sp.legacyCode, sp.englishDescription,  " +
            "sp.frenchDescription, nd.legacyCode, g.legacyCode, g.englishDescription, g.frenchDescription, " +
            "st.englishDescription, st.frenchDescription) " +
            "from LengthFrequency lf inner join lf.sampling s inner join lf.status st inner join lf.sampleSpecies sp " +
            "left outer join lf.nafoDivision nd left outer join lf.gear g " +
            "where lf.catchDate >= :fromDate and lf.catchDate < :toDate")
    List<LFListItem> queryListByDateRange(LocalDate fromDate, LocalDate toDate);

    @Query("select count(lf) from LengthFrequency lf where lf.sampling.yearCreated = :year")
    Integer countByYear(@Param("year") Integer year);
}
