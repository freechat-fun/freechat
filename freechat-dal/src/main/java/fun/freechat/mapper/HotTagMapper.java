package fun.freechat.mapper;

import fun.freechat.model.HotTag;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.List;

public interface HotTagMapper extends TagMapper {
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="HotTag", value={
            @Result(column="content", property="content"),
            @Result(column="count", property="count")
    })
    List<HotTag> selectHotTags(SelectStatementProvider selectStatement);
}