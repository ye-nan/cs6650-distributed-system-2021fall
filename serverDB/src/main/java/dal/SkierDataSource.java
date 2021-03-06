package dal;

import com.zaxxer.hikari.HikariDataSource;

public class SkierDataSource {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String HOST_NAME = System.getProperty("SkierDB_IP_ADDRESS");
    private static final String PORT = System.getProperty("MySQL_PORT");
    private static final String DATABASE = "SkierDB";
    private static final String USERNAME = System.getProperty("DB_USERNAME");
    private static final String PASSWORD = System.getProperty("DB_PASSWORD");

    private static final HikariDataSource dataSource = new HikariDataSource();
    static {
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setDriverClassName(JDBC_DRIVER);
        dataSource.setMaxLifetime(300000);
        dataSource.setMaximumPoolSize(40);
//        dataSource.setMinimumIdle(30);
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
