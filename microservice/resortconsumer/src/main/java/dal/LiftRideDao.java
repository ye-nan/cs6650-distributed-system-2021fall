package dal;

import java.sql.*;

import com.zaxxer.hikari.HikariDataSource;

public class LiftRideDao {
    private static HikariDataSource dataSource;

    public LiftRideDao() {
        dataSource = HikariCPDataSource.getDataSource();
    }

    public void createLiftRide(LiftRide newLiftRide) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO LiftRides (skierId, liftId, resortId, day, time) " +
                "VALUES (?,?,?,?,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, newLiftRide.getSkierId());
            preparedStatement.setInt(2, newLiftRide.getLiftId());
            preparedStatement.setInt(3, newLiftRide.getResortId());
            preparedStatement.setInt(4, newLiftRide.getDay());
            preparedStatement.setInt(5, newLiftRide.getTime());

            // execute insert SQL statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
