package fun.freechat.service.util;

import org.mybatis.dynamic.sql.SortSpecification;

public class SortSpecificationWrapper implements SortSpecification {
    private final String prefix;
    private final SortSpecification sortSpecification;

    private SortSpecificationWrapper(String tableAlias, SortSpecification sortSpecification) {
        prefix = tableAlias + ".";
        this.sortSpecification = sortSpecification;
    }

    public static SortSpecificationWrapper of(String tableAlias, SortSpecification sortSpecification) {
        return new SortSpecificationWrapper(tableAlias, sortSpecification);
    }

    @Override
    public SortSpecification descending() {
        return sortSpecification.descending();
    }

    @Override
    public String orderByName() {
        return prefix + sortSpecification.orderByName();
    }

    @Override
    public boolean isDescending() {
        return sortSpecification.isDescending();
    }
}
