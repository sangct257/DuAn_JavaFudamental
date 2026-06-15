package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.dao.StudentDAO;
import ra.yourprojectname.dao.impl.StudentDAOImpl;
import ra.yourprojectname.model.Student;

import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final StudentDAO studentDAO = new StudentDAOImpl();

    // Đăng nhập email và mật khẩu học viên
    @Override
    public boolean login(String email, String password) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                !email.matches(emailRegex)) {
            return false;
        }
        return studentDAO.login(email, password);
    }

    // Tìm học viên theo email
    @Override
    public Student getStudentByEmail(String email) {
        return studentDAO.getStudentByEmail(email);
    }

    // Hiển thị danh sách học viên có phân trang
    @Override
    public List<Student> getAllStudents(int limit, int offset) {
        return studentDAO.getAllStudents(limit, offset);
    }

    // Đếm tổng số lượng học viên
    @Override
    public int getTotalStudents() {
        return studentDAO.getTotalStudents();
    }

    // Thêm mới học viên
    @Override
    public boolean addStudent(Student student) {
        int existingCount = studentDAO.getTotalStudentsByKeyWord(student.getEmail().trim());
        if (existingCount > 0) {
            System.err.println("Lỗi nghiệp vụ: Email '" + student.getEmail() + "' đã tồn tại trên hệ thống!");
            return false;
        }
        return studentDAO.addStudent(student);
    }

    // Tìm kiếm học viên theo ID
    @Override
    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
    }

    // Chỉnh sửa thông tin học viên (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    @Override
    public boolean updateStudent(Student student) {
        return studentDAO.updateStudent(student);
    }

    // Xóa học viên theo id (Xác nhận trước khi xóa)
    @Override
    public boolean deleteStudent(int id) {
        return studentDAO.deleteStudent(id);
    }

    // Đếm tổng số lượng học viên theo tên, email hoặc mã id (tìm kiếm tương đối)
    @Override
    public int getTotalStudentsByKeyWord(String keyword) {
        return studentDAO.getTotalStudentsByKeyWord(keyword);
    }

    // Tim kiếm học viên theo tên, email hoặc mã id (tìm kiếm tương đối) có phân trang
    @Override
    public List<Student> searchStudent(String keyword, int limit, int offset) {
        return studentDAO.searchStudent(keyword, limit, offset);
    }

    // Sắp xếp học viên (theo tên/id - tăng dần/giảm dần) có phân trang
    @Override
    public List<Student> sortStudents(String column, String direction, int limit, int offset) {
        return studentDAO.sortStudents(column, direction, limit, offset);
    }

}
