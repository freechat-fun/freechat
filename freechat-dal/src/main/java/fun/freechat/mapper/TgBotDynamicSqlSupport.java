package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TgBotDynamicSqlSupport {
    public static final TgBot tgBot = new TgBot();

    public static final SqlColumn<String> botId = tgBot.botId;

    public static final SqlColumn<LocalDateTime> gmtCreate = tgBot.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = tgBot.gmtModified;

    public static final SqlColumn<Long> tgBotId = tgBot.tgBotId;

    public static final SqlColumn<String> username = tgBot.username;

    public static final SqlColumn<String> name = tgBot.name;

    public static final SqlColumn<String> token = tgBot.token;

    public static final SqlColumn<String> webhookUrl = tgBot.webhookUrl;

    public static final SqlColumn<String> webhookSecret = tgBot.webhookSecret;

    public static final SqlColumn<Byte> enabled = tgBot.enabled;

    public static final SqlColumn<String> description = tgBot.description;

    public static final SqlColumn<String> allowedUpdates = tgBot.allowedUpdates;

    public static final SqlColumn<String> commands = tgBot.commands;

    public static final SqlColumn<String> ext = tgBot.ext;

    public static final class TgBot extends AliasableSqlTable<TgBot> {
        public final SqlColumn<String> botId = column("bot_id", JDBCType.VARCHAR).withJavaProperty("botId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<Long> tgBotId = column("tg_bot_id", JDBCType.BIGINT).withJavaProperty("tgBotId");

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR).withJavaProperty("username");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> token = column("token", JDBCType.VARCHAR).withJavaProperty("token");

        public final SqlColumn<String> webhookUrl = column("webhook_url", JDBCType.VARCHAR).withJavaProperty("webhookUrl");

        public final SqlColumn<String> webhookSecret = column("webhook_secret", JDBCType.VARCHAR).withJavaProperty("webhookSecret");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR).withJavaProperty("description");

        public final SqlColumn<String> allowedUpdates = column("allowed_updates", JDBCType.LONGVARCHAR).withJavaProperty("allowedUpdates");

        public final SqlColumn<String> commands = column("commands", JDBCType.LONGVARCHAR).withJavaProperty("commands");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public TgBot() {
            super("tg_bot", TgBot::new);
        }
    }
}