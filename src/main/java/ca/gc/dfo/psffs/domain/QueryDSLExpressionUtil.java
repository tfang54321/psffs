package ca.gc.dfo.psffs.domain;

import com.querydsl.core.types.dsl.BooleanExpression;

public class QueryDSLExpressionUtil
{
    public static BooleanExpression qOr(BooleanExpression append, BooleanExpression existing)
    {
        if (existing != null) {
            existing = existing.or(append);
        } else {
            existing = append;
        }
        return existing;
    }

    public static BooleanExpression qAnd(BooleanExpression append, BooleanExpression existing)
    {
        if (existing != null) {
            existing = existing.and(append);
        } else {
            existing = append;
        }
        return existing;
    }
}
