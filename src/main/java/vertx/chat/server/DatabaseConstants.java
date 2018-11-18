package vertx.chat.server;

public interface DatabaseConstants {
    String CONFIG_CHATDB_JDBC_URL = "chatdb.jdbc.url";
    String CONFIG_CHATDB_JDBC_DRIVER_CLASS = "chatdb.jdbc.driver_class";
    String CONFIG_CHATDB_JDBC_MAX_POOL_SIZE = "chatdb.jdbc.max_pool_size";
    String CONFIG_CHATDB_QUEUE = "db.queue";
    String DEFAULT_CHATDB_JDBC_URL = "jdbc:mysql://localhost:3306/chat";
    String DEFAULT_CHATDB_JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    int DEFAULT_JDBC_MAX_POOL_SIZE = 30;
}
