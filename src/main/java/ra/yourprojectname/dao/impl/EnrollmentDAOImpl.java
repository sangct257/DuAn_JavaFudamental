package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.EnrollmentDAO;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.EnrollmentStatus;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    // Admin
    @Override
    public List<Enrollment> getAllEnrollments(int limit, int offset) {
        List<Enrollment> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement(
                    "SELECT e.id, e.registered_at, e.status, s.id AS s_id, s.name AS s_name, c.id AS c_id, c.name AS c_name " +
                            "FROM Enrollment e " +
                            "JOIN Student s ON e.student_id = s.id " +
                            "JOIN Course c ON e.course_id = c.id " +
                            "WHERE e.status = 'WAITING'::enrollment_status " +
                            "ORDER BY e.id DESC LIMIT ? OFFSET ?");
            pstmt.setInt(1,limit);
            pstmt.setInt(2,offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student s = new Student();
                s.setId(rs.getInt("s_id"));
                s.setName(rs.getString("s_name"));

                Course c = new Course();
                c.setId(rs.getInt("c_id"));
                c.setName(rs.getString("c_name"));

                Enrollment e = new Enrollment(
                        rs.getInt("id"),
                        s,
                        c,
                        rs.getTimestamp("registered_at").toLocalDateTime(),
                        EnrollmentStatus.valueOf(rs.getString("status"))
                );
                list.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public int getTotalEnrollments() {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Enrollment WHERE status = 'WAITING'::enrollment_status");
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

    // Hiển thị danh sách sinh viên đăng ký theo từng khóa học (có phân trang)
    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId,int limit, int offset) {
        List<Enrollment> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement(
                    "SELECT e.id, e.registered_at, e.status, s.id AS s_id, s.name AS s_name, c.id AS c_id, c.name AS c_name " +
                    "FROM Enrollment e " +
                    "JOIN Student s ON e.student_id = s.id " +
                    "JOIN Course c ON e.course_id = c.id " +
                    "WHERE e.course_id = ? " +
                    "ORDER BY e.id DESC LIMIT ? OFFSET ?");
            pstmt.setInt(1, courseId);
            pstmt.setInt(2,limit);
            pstmt.setInt(3,offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student s = new Student();
                s.setId(rs.getInt("s_id"));
                s.setName(rs.getString("s_name"));

                Course c = new Course();
                c.setId(rs.getInt("c_id"));
                c.setName(rs.getString("c_name"));

                Enrollment e = new Enrollment(
                        rs.getInt("id"),
                        s,
                        c,
                        rs.getTimestamp("registered_at").toLocalDateTime(),
                        EnrollmentStatus.valueOf(rs.getString("status"))
                );
                list.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    // Đếm tổng số sinh viên đã đăng ký của một khoá học (để phân trang)
    @Override
    public int getTotalEnrollmentsByCourse(int courseId){
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Enrollment WHERE course_id = ?");
            pstmt.setInt(1, courseId);
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

    // Duyệt trạng thái đăng ký (CONFIRMED / DENIED) hoặc Xóa học viên khỏi lớp (CANCELED)
    @Override
    public boolean updateEnrollmentStatus(int studentId, int courseId, EnrollmentStatus status) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("UPDATE Enrollment SET status = ?::enrollment_status WHERE student_id = ? AND course_id = ?");
            pstmt.setString(1, status.name());
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, courseId);
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

    @Override
    public boolean deleteEnrollment(int studentId, int courseId) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("DELETE FROM Enrollment WHERE student_id = ? AND course_id = ?");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
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

    // Student
    // Học viên đăng ký khóa học mới (Mặc định lưu vào DB là WAITING)
    @Override
    public boolean addEnrollment(Enrollment enrollment) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("INSERT INTO Enrollment(student_id, course_id, registered_at, status) VALUES (?, ?, ?, ?::enrollment_status)");
            pstmt.setInt(1, enrollment.getStudent().getId());
            pstmt.setInt(2, enrollment.getCourse().getId());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(enrollment.getRegisteredAt()));
            pstmt.setString(4, enrollment.getStatus().name());
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

    // Xem danh sách khóa học mà chính học viên đó đã đăng ký (có phân trang)
    @Override
    public List<Enrollment> getEnrollmentsByStudent(int studentId, int limit, int offset) {
        return getEnrollmentsByStudentWithSort(studentId, "e.id", "ASC", limit, offset);
    }

    // Đếm tổng số lượng đơn đăng ký của riêng học viên đó (Để tính tổng số trang ở View Student)
    @Override
    public int getTotalEnrollmentsByStudent(int studentId) {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Enrollment WHERE student_id = ?");
            pstmt.setInt(1, studentId);
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

    // Sắp xếp khóa học đã đăng ký theo tiêu chí động: Tên khóa học hoặc Ngày đăng ký (có phân trang)
    @Override
    public List<Enrollment> getEnrollmentsByStudentWithSort(int studentId, String orderByColumn, String direction, int limit, int offset) {
        List<Enrollment> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT e.id, e.registered_at, e.status, c.id AS c_id, c.name AS c_name " +
                    "FROM Enrollment e " +
                    "JOIN Course c ON e.course_id = c.id " +
                    "WHERE e.student_id = ? " +
                    "ORDER BY " + orderByColumn + " " + direction + " LIMIT ? OFFSET ?");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Course c = new Course();
                c.setId(rs.getInt("c_id"));
                c.setName(rs.getString("c_name"));

                Enrollment e = new Enrollment(
                        rs.getInt("id"),
                        null, // Không cần tạo lặp lại đối tượng Student đang đăng nhập
                        c,
                        rs.getTimestamp("registered_at").toLocalDateTime(),
                        EnrollmentStatus.valueOf(rs.getString("status"))
                );
                list.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }
}
