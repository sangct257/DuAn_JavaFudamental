package ra.yourprojectname.business;

import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.EnrollmentStatus;

import java.util.List;

public interface EnrollmentService{
    // Admin
    // Hiển thị tất cả danh sách sinh viên đăng ký khóa học (có phân trang)
    List<Enrollment> getAllEnrollments(int limit, int offset);

    // Đếm tất cả danh sách sinh viên đăng ký khóa học (để phân trang)
    int getTotalEnrollments();

    // Hiển thị danh sách sinh viên đăng ký theo từng khóa học (có phân trang)
    List<Enrollment> getEnrollmentsByCourse(int courseId, int limit, int offset);

    // Đếm tổng số sinh viên đã đăng ký của một khoá học (để phân trang)
    int getTotalEnrollmentsByCourse(int courseId);

    // Duyệt trạng thái đăng ký (CONFIRMED / DENIED) hoặc Xóa học viên khỏi lớp (CANCELED)
    boolean updateEnrollmentStatus(int studentId, int courseId, EnrollmentStatus status);

    // Xóa sinh viên khỏi khóa học
    boolean deleteEnrollment(int studentId, int courseId);

    // Student
    // Học viên đăng ký khóa học mới (Mặc định lưu vào DB là WAITING)
    boolean addEnrollment(Enrollment enrollment);

    // Xem danh sách khóa học mà chính học viên đó đã đăng ký (có phân trang)
    List<Enrollment> getEnrollmentsByStudent(int studentId, int limit, int offset);

    // Đếm tổng số lượng đơn đăng ký của riêng học viên đó (Để tính tổng số trang ở View Student)
    int getTotalEnrollmentsByStudent(int studentId);

    // Sắp xếp khóa học đã đăng ký theo tiêu chí động: Tên khóa học hoặc Ngày đăng ký (có phân trang)
    List<Enrollment> getEnrollmentsByStudentWithSort(int studentId, String orderByColumn, String direction, int limit, int offset);

}
