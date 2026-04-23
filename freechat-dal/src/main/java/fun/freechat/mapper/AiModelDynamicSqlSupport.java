package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AiModelDynamicSqlSupport {
    public static final AiModel aiModel = new AiModel();

    public static final SqlColumn<Long> id = aiModel.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = aiModel.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = aiModel.gmtModified;

    public static final SqlColumn<String> modelId = aiModel.modelId;

    public static final SqlColumn<String> referType = aiModel.referType;

    public static final SqlColumn<String> referId = aiModel.referId;

    public static final class AiModel extends AliasableSqlTable<AiModel> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> modelId = column("model_id", JDBCType.VARCHAR).withJavaProperty("modelId");

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR).withJavaProperty("referType");

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR).withJavaProperty("referId");

        public AiModel() {
            super("ai_model", AiModel::new);
        }
    }
}