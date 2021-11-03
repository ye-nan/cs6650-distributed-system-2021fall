package dal;

import java.sql.*;

import model.LiftRide;
import org.apache.commons.dbcp2.*;

public class LiftRideDao {
    private static BasicDataSource dataSource;

    public LiftRideDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createLiftRide(LiftRide newLiftRide) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO LiftRides (skierId, liftId, resortId, seasonId, dayId, time) " +
                "VALUES (?,?,?,?,?,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, newLiftRide.getSkierId());
            preparedStatement.setInt(2, newLiftRide.getLiftId());
            preparedStatement.setInt(3, newLiftRide.getResortId());
            preparedStatement.setString(4, newLiftRide.getSeasonId());
            preparedStatement.setString(5, newLiftRide.getDayId());
            preparedStatement.setInt(6, newLiftRide.getTime());

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
