package fun.freechat.service.util;

import org.jspecify.annotations.NonNull;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.render.RenderingContext;
import org.mybatis.dynamic.sql.util.FragmentAndParameters;

public class SortSpecificationWrapper implements SortSpecification {
    private final String tableAlias;
    private final String prefix;
    private final SortSpecification sortSpecification;

    private SortSpecificationWrapper(String tableAlias, SortSpecification sortSpecification) {
        this.tableAlias = tableAlias;
        prefix = tableAlias + ".";
        this.sortSpecification = sortSpecification;
    }

    public static SortSpecificationWrapper of(String tableAlias, SortSpecification sortSpecification) {
        return new SortSpecificationWrapper(tableAlias, sortSpecification);
    }

    @Override
    public @NonNull SortSpecification descending() {
        return new SortSpecificationWrapper(tableAlias, sortSpecification.descending());
    }

    @Override
    public @NonNull FragmentAndParameters renderForOrderBy(@NonNull RenderingContext renderingContext) {
        return sortSpecification.renderForOrderBy(renderingContext).mapFragment(f -> prefix + f);
    }
}
