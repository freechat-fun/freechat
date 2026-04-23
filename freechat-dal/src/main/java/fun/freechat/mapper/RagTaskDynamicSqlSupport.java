package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class RagTaskDynamicSqlSupport {
    public static final RagTask ragTask = new RagTask();

    public static final SqlColumn<Long> id = ragTask.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = ragTask.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = ragTask.gmtModified;

    public static final SqlColumn<LocalDateTime> gmtStart = ragTask.gmtStart;

    public static final SqlColumn<LocalDateTime> gmtEnd = ragTask.gmtEnd;

    public static final SqlColumn<String> characterUid = ragTask.characterUid;

    public static final SqlColumn<String> sourceType = ragTask.sourceType;

    public static final SqlColumn<Integer> maxSegmentSize = ragTask.maxSegmentSize;

    public static final SqlColumn<Integer> maxOverlapSize = ragTask.maxOverlapSize;

    public static final SqlColumn<String> status = ragTask.status;

    public static final SqlColumn<String> source = ragTask.source;

    public static final SqlColumn<String> ext = ragTask.ext;

    public static final class RagTask extends AliasableSqlTable<RagTask> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<LocalDateTime> gmtStart = column("gmt_start", JDBCType.TIMESTAMP).withJavaProperty("gmtStart");

        public final SqlColumn<LocalDateTime> gmtEnd = column("gmt_end", JDBCType.TIMESTAMP).withJavaProperty("gmtEnd");

        public final SqlColumn<String> characterUid = column("character_uid", JDBCType.VARCHAR).withJavaProperty("characterUid");

        public final SqlColumn<String> sourceType = column("source_type", JDBCType.VARCHAR).withJavaProperty("sourceType");

        public final SqlColumn<Integer> maxSegmentSize = column("max_segment_size", JDBCType.INTEGER).withJavaProperty("maxSegmentSize");

        public final SqlColumn<Integer> maxOverlapSize = column("max_overlap_size", JDBCType.INTEGER).withJavaProperty("maxOverlapSize");

        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR).withJavaProperty("status");

        public final SqlColumn<String> source = column("source", JDBCType.LONGVARCHAR).withJavaProperty("source");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public RagTask() {
            super("rag_task", RagTask::new);
        }
    }
}