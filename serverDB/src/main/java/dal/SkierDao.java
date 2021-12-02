package dal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SkierDao {
    private static HikariDataSource dataSource;

    public SkierDao() {
        dataSource = SkierDataSource.getDataSource();
    }

    public int getDayVertical(int resortId, String season, int dayId,
                              int skierId) {
        int totalVertical = 0;
        String queryStr = "SELECT SUM(vertical) as \"totalVert\" FROM SkierDB.LiftRides WHERE skierId=? AND"
                + " season=? AND day=?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(queryStr)) {
            preparedStatement.setInt(1, skierId);
            preparedStatement.setString(2, season);
            preparedStatement.setInt(3, dayId);
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

    public int geTotalVertical(int skierId) {
        int totalVertical = 0;
        String queryStr = "SELECT SUM(vertical) as \"totalVert\" FROM SkierDB.LiftRides WHERE skierId=?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(queryStr)) {
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
