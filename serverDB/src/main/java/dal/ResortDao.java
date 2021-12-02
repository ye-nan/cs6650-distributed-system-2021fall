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
        String queryStr = "COUNT(DISTINCT(skierId)) as \"numSkiers\" FROM ResortDB.LiftRides WHERE resortId=? AND"
                + " day=?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(queryStr)) {
            preparedStatement.setInt(1, resortId);
            preparedStatement.setInt(2, dayId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    numSkiers += result.getInt("numSkiers");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return numSkiers;
    }

    public int geTotalVertical(int skierId) {
        int totalVertical = 0;
        String selectCreditCard = "SELECT SUM(vertical) as \"totalVert\" FROM SkierDB.LiftRides WHERE skierId=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(selectCreditCard)) {
            preparedStatement.setInt(1, skierId);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    totalVertical += result.getInt("totalVert");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return totalVertical;
    }
}
