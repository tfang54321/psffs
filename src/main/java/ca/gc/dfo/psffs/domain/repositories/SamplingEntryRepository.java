package ca.gc.dfo.psffs.domain.repositories;

import ca.gc.dfo.psffs.domain.objects.SamplingEntry;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface SamplingEntryRepository extends JpaRepository<SamplingEntry, Long>, JpaSpecificationExecutor<SamplingEntry>, QuerydslPredicateExecutor<SamplingEntry>
{
    @Override
    @EntityGraph(attributePaths = {
            "samplingData", "samplingData.otolithEdge", "samplingData.otolithReliability",
            "samplingData.primaryStomachContent", "samplingData.secondaryStomachContent", "samplingData.parasite",
            "samplingData.status"
    })
    Iterable<SamplingEntry> findAll(@NotNull Predicate predicate);

    @Override
    @EntityGraph(attributePaths = {
            "samplingData", "samplingData.otolithEdge", "samplingData.otolithReliability",
            "samplingData.primaryStomachContent", "samplingData.secondaryStomachContent", "samplingData.parasite",
            "samplingData.status"
    })
    Page<SamplingEntry> findAll(@NotNull Predicate predicate, Pageable pageable);
}
