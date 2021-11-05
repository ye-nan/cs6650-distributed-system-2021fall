package dal;

import com.zaxxer.hikari.HikariDataSource;

public class HikariCPDataSource {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String HOST_NAME = System.getenv("MySQL_IP_ADDRESS");
    private static final String PORT = System.getenv("MySQL_PORT");
    private static final String DATABASE = "ResortDB";
    private static final String USERNAME = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static final HikariDataSource dataSource = new HikariDataSource();
    static {
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setDriverClassName(JDBC_DRIVER);

        dataSource.setMaximumPoolSize(60);
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
