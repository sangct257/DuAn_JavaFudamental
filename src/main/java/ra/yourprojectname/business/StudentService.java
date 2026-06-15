package ra.yourprojectname.business;

import ra.yourprojectname.model.Student;

import java.util.List;

public interface StudentService {
    // Đăng nhập email và mật khẩu học viên
    boolean login(String email, String password);
    // Tìm học viên theo email
    Student getStudentByEmail(String email);
    // Hiển thị danh sách học viên có phân trang
    List<Student> getAllStudents(int limit, int offset);
    // Đếm tổng số lượng học viên
    int getTotalStudents();
    // Thêm mới học viên
    boolean addStudent(Student student);
    // Tìm kiếm học viên theo ID
    Student getStudentById(int id);
    // Chỉnh sửa thông tin học viên (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    boolean updateStudent(Student student);
    // Xóa học viên theo id (Xác nhận trước khi xóa)
    boolean deleteStudent(int id);
    // Đếm tổng số lượng học viên theo tên, email hoặc mã id (tìm kiếm tương đối)
    int getTotalStudentsByKeyWord(String keyword);
    // Tim kiếm học viên theo tên, email hoặc mã id (tìm kiếm tương đối) có phân trang
    List<Student> searchStudent(String keyword, int limit, int offset);
    // Sắp xếp học viên (theo tên/id - tăng dần/giảm dần) có phân trang
    List<Student> sortStudents(String column, String direction, int limit, int offset);
}
