package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.StudentDAO;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.DBUtility;
import ra.yourprojectname.until.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO{

    // Đăng nhập email và mật khẩu học viên
    @Override
    public boolean login(String email, String password) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Student WHERE email = ? AND password = ?");
            pstmt.setString(1,email);
            pstmt.setString(2, PasswordHasher.hashPassword(password));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                flag = true;
            } else {
                System.err.println("Sai email hoặc mật khẩu đăng nhập!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return flag;
    }

    @Override
    public Student getStudentByEmail(String email) {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Student WHERE email = ?");
            pstmt.setString(1,email);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setSex(rs.getBoolean("sex"));
                student.setPhone(rs.getString("phone"));
                student.setPassword(rs.getString("password"));
                student.setCreateAt(rs.getDate("create_at"));
                return student;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return null;
    }

    // Hiển thị danh sách học viên có phân trang
    @Override
    public List<Student> getAllStudents(int limit, int offset) {
        List<Student> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT id,name,dob,email,sex,phone,create_at FROM Student ORDER BY id LIMIT ? OFFSET ?");
            pstmt.setInt(1, limit);
            pstmt.setInt(2,offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setSex(rs.getBoolean("sex"));
                student.setPhone(rs.getString("phone"));
                student.setCreateAt(rs.getDate("create_at"));
                list.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    // Đếm tổng số lượng học viên
    @Override
    public int getTotalStudents() {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student");
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

    // Thêm mới học viên
    @Override
    public boolean addStudent(Student student) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("INSERT INTO Student(name,dob,email,sex,phone,password) VALUES (?,?,?,?,?,?)");
            pstmt.setString(1,student.getName());
            pstmt.setDate(2, new java.sql.Date(student.getDob().getTime()));
            pstmt.setString(3, student.getEmail());
            pstmt.setBoolean(4, student.isSex());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, PasswordHasher.hashPassword(student.getPassword()));
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

    // Tìm kiếm học viên theo ID
    @Override
    public Student getStudentById(int id) {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT * FROM Student WHERE id = ?");
            pstmt.setInt(1,id);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setSex(rs.getBoolean("sex"));
                student.setPhone(rs.getString("phone"));
                student.setPassword(rs.getString("password"));
                student.setCreateAt(rs.getDate("create_at"));
                return student;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return null;
    }

    // Chỉnh sửa thông tin học viên (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    @Override
    public boolean updateStudent(Student student) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("UPDATE Student SET name = ?, dob = ?, email = ?, sex = ?, phone = ? WHERE id = ?");
            pstmt.setString(1,student.getName());
            pstmt.setDate(2, new java.sql.Date(student.getDob().getTime()));
            pstmt.setString(3,student.getEmail());
            pstmt.setBoolean(4,student.isSex());
            pstmt.setString(5,student.getPhone());
            pstmt.setInt(6,student.getId());
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

    // Xóa học viên theo id (Xác nhận trước khi xóa)
    @Override
    public boolean deleteStudent(int id) {
        boolean flag = false;
        Connection con;
        PreparedStatement pstmt = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("DELETE FROM Student WHERE id = ?");
            pstmt.setInt(1,id);
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

    // Đếm tổng số lượng học viên theo tên, email hoặc mã id (tìm kiếm tương đối)
    @Override
    public int getTotalStudentsByKeyWord(String keyword) {
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student WHERE name ILIKE ? OR email ILIKE ? OR CAST(id AS TEXT) LIKE ?");
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
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

    // Tim kiếm học viên theo tên, email hoặc mã id (tìm kiếm tương đối)
    @Override
    public List<Student> searchStudent(String keyword, int limit, int offset) {
        List<Student> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT id,name,dob,email,sex,phone,create_at FROM Student " +
                    "WHERE name ILIKE ? OR email ILIKE ? OR CAST(id AS TEXT) LIKE ? LIMIT ? OFFSET ?");
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            pstmt.setInt(4, limit);
            pstmt.setInt(5, offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setSex(rs.getBoolean("sex"));
                student.setPhone(rs.getString("phone"));
                student.setCreateAt(rs.getDate("create_at"));
                list.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }

    // Sắp xếp học viên (theo tên/id - tăng dần/giảm dần)
    @Override
    public List<Student> sortStudents(String column, String direction, int limit, int offset) {
        List<Student> list = new ArrayList<>();
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT id,name,dob,email,sex,phone,create_at FROM Student ORDER BY " + column + " " + direction + " LIMIT ? OFFSET ?");
            pstmt.setInt(1,limit);
            pstmt.setInt(2,offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setSex(rs.getBoolean("sex"));
                student.setPhone(rs.getString("phone"));
                student.setCreateAt(rs.getDate("create_at"));
                list.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return list;
    }
}
