package dal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ResortDao {
    private static HikariDataSource dataSource;

    public ResortDao() {
        dataSource = ResortDataSource.getDataSource();
    }

    public int getNumSkiers(int resortId, String season, int dayId) {
        int numSkiers = 0;
        String queryStr = "SELECT COUNT(DISTINCT(skierId)) as \"numSkiers\" FROM LiftRides WHERE resortId=? AND"
                + " day=?;";
//        String queryStr = "SELECT DISTINCT(skierId) FROM LiftRides WHERE resortId=? AND"
//                + " day=?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(queryStr)) {
            preparedStatement.setInt(1, resortId);
            preparedStatement.setInt(2, dayId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    numSkiers += result.getInt(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return numSkiers;
    }
}
