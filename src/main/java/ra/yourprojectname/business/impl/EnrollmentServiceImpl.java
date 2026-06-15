package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.dao.EnrollmentDAO;
import ra.yourprojectname.dao.impl.EnrollmentDAOImpl;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.EnrollmentStatus;

import java.util.List;

public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();

    // Admin
    // Hiển thị tất cả danh sách sinh viên đăng ký khóa học (có phân trang)
    @Override
    public List<Enrollment> getAllEnrollments(int limit, int offset) {
        return enrollmentDAO.getAllEnrollments(limit, offset);
    }

    // Đếm tất cả danh sách sinh viên đăng ký khóa học (để phân trang)
    @Override
    public int getTotalEnrollments() {
        return enrollmentDAO.getTotalEnrollments();
    }

    // Hiển thị danh sách sinh viên đăng ký theo từng khóa học (có phân trang)
    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId,int limit, int offset) {
        return enrollmentDAO.getEnrollmentsByCourse(courseId,limit, offset);
    }

    // Đếm tổng số sinh viên đã đăng ký của một khoá học (để phân trang)
    @Override
    public int getTotalEnrollmentsByCourse(int courseId) {
        return enrollmentDAO.getTotalEnrollmentsByCourse(courseId);
    }

    // Duyệt trạng thái đăng ký (CONFIRMED / DENIED) hoặc Xóa học viên khỏi lớp (CANCELED)
    @Override
    public boolean updateEnrollmentStatus(int studentId, int courseId, EnrollmentStatus status) {
        return enrollmentDAO.updateEnrollmentStatus(studentId, courseId, status);
    }

    @Override
    public boolean deleteEnrollment(int studentId, int courseId) {
        return enrollmentDAO.deleteEnrollment(studentId, courseId);
    }

    // Student
    // Học viên đăng ký khóa học mới (Mặc định lưu vào DB là WAITING)
    @Override
    public boolean addEnrollment(Enrollment enrollment) {
        return enrollmentDAO.addEnrollment(enrollment);
    }

    // Xem danh sách khóa học mà chính học viên đó đã đăng ký (có phân trang)
    @Override
    public List<Enrollment> getEnrollmentsByStudent(int studentId, int limit, int offset) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId, limit, offset);
    }

    // Đếm tổng số lượng đơn đăng ký của riêng học viên đó (Để tính tổng số trang ở View Student)
    @Override
    public int getTotalEnrollmentsByStudent(int studentId) {
        return enrollmentDAO.getTotalEnrollmentsByStudent(studentId);
    }

    // Sắp xếp khóa học đã đăng ký theo tiêu chí động: Tên khóa học hoặc Ngày đăng ký (có phân trang)
    @Override
    public List<Enrollment> getEnrollmentsByStudentWithSort(int studentId, String orderByColumn, String direction, int limit, int offset) {
        return enrollmentDAO.getEnrollmentsByStudentWithSort(studentId, orderByColumn, direction, limit, offset);
    }
}
