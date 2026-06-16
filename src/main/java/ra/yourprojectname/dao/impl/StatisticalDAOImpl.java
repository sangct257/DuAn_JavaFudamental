package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.StatisticalDAO;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticalDAOImpl implements StatisticalDAO {

    // Thống kê tổng số lượng khóa học và tổng số học viên
    @Override
    public Map<String, Integer> getTotalOverview() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Course");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                map.put("total_courses", rs.getInt(1));
            }
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                map.put("total_students", rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return map;
    }

    // Thống kê tổng số học viên theo từng khóa
    @Override
    public Map<String, Integer> getStudentCountByCourse() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            // LEFT JOIN để khóa học chưa có học viên nào vẫn hiển thị số lượng = 0
            pstmt = con.prepareStatement("SELECT c.name, COUNT(e.student_id) AS total " +
                    "FROM Course c " +
                    "LEFT JOIN Enrollment e ON c.id = e.course_id AND e.status = 'CONFIRMED'::enrollment_status " +
                    "GROUP BY c.id, c.name " +
                    "ORDER BY c.name ASC");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return map;
    }

    // Thống kê top 5 khóa học đông sinh viên nhất
    @Override
    public Map<String, Integer> getTop5Courses() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT c.name, COUNT(e.student_id) AS total " +
                    "FROM Course c " +
                    "JOIN Enrollment e ON c.id = e.course_id " +
                    "WHERE e.status = 'CONFIRMED'::enrollment_status " +
                    "GROUP BY c.id, c.name " +
                    "ORDER BY total DESC, c.name ASC LIMIT 5");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return map;
    }

    // Liệt kê các khóa học có trên 10 học viên
    @Override
    public Map<String, Integer> getCoursesWithMoreThan10Students() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT c.name, COUNT(e.student_id) AS total " +
                    "FROM Course c " +
                    "JOIN Enrollment e ON c.id = e.course_id " +
                    "WHERE e.status = 'CONFIRMED'::enrollment_status " +
                    "GROUP BY c.id, c.name " +
                    "HAVING COUNT(e.student_id) > 10 " +
                    "ORDER BY total DESC");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return map;
    }
}
