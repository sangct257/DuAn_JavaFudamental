package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.CourseDAO;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    // Hiển thị danh sách khóa học có phân trang
    @Override
    public List<Course> getAllCourses(int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT id,name,duration,instructor,create_at FROM Course ORDER BY id LIMIT ? OFFSET ?");
            pstmt.setInt(1, limit);
            pstmt.setInt(2,offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreateAt(rs.getDate("create_at"));
                list.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    // Đếm tổng số khoá học
    @Override
    public int getTotalCourses() {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Course");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return 0;
    }

    // Thêm mới khóa học
    @Override
    public boolean addCourse(Course course) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("INSERT INTO Course(name,duration,instructor) VALUES (?,?,?)");
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getDuration());
            pstmt.setString(3, course.getInstructor());
            int i = pstmt.executeUpdate();
            if (i > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    // Tìm kiếm khoá học theo ID
    @Override
    public Course getCourseById(int id) {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Course WHERE id = ?");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreateAt(rs.getDate("create_at"));
                return course;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return null;
    }

    // Chỉnh sửa thông tin khóa học (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    @Override
    public boolean updateCourse(Course course) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("UPDATE Course SET name = ? ,duration = ? ,instructor = ? WHERE id = ?");
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getDuration());
            pstmt.setString(3, course.getInstructor());
            pstmt.setInt(4, course.getId());
            int i = pstmt.executeUpdate();
            if (i > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    // Xóa khóa học theo id (Xác nhận trước khi xóa)
    @Override
    public boolean deleteCourse(int id) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("DELETE FROM Course WHERE id = ?");
            pstmt.setInt(1, id);
            int i = pstmt.executeUpdate();
            if (i > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(null, pstmt, con);
        }
        return flag;
    }

    // Tim kiếm khóa học theo tên (tìm kiếm tương đối) có phân trang
    @Override
    public List<Course> searchCourseByName(String name, int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT id,name,duration,instructor,create_at FROM Course WHERE name ILIKE ? LIMIT ? OFFSET ?");
            pstmt.setString(1,"%" + name + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreateAt(rs.getDate("create_at"));
                list.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    // Đếm tổng số khoá học theo tên
    @Override
    public int getTotalCoursesByName(String name) {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Course WHERE name ILIKE ?");
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return 0;
    }

    // Sắp xếp khóa học (theo tên/id - tăng dần/giảm dần) có phân trang
    @Override
    public List<Course> sortCourses(String column, String direction, int limit, int offset) {
        List<Course> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT id,name,duration,instructor,create_at FROM Course ORDER BY " + column + " " + direction + " LIMIT ? OFFSET ?");
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                course.setInstructor(rs.getString("instructor"));
                course.setCreateAt(rs.getDate("create_at"));
                list.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }
}
