package ca.gc.dfo.psffs.export.extract;

import com.querydsl.core.types.dsl.BooleanExpression;

public interface ExtractReadyService<T>
{
    Iterable<T> findRecords(BooleanExpression[] predicates);

    Long countRecords(BooleanExpression[] predicates);
}
