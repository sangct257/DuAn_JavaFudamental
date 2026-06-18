package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.dao.CourseDAO;
import ra.yourprojectname.dao.impl.CourseDAOImpl;
import ra.yourprojectname.model.Course;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    private final CourseDAO courseDAO = new CourseDAOImpl();

    // Hiển thị danh sách khóa học có phân trang
    @Override
    public List<Course> getAllCourses(int limit, int offset) {
        return courseDAO.getAllCourses(limit, offset);
    }

    // Đếm tổng số khoá học
    @Override
    public int getTotalCourses() {
        return courseDAO.getTotalCourses();
    }

    // Thêm mới khóa học
    @Override
    public boolean addCourse(Course course) {
        return courseDAO.addCourse(course);
    }

    // Tìm kiếm khoá học theo ID
    @Override
    public Course getCourseById(int id) {
        return courseDAO.getCourseById(id);
    }

    // Chỉnh sửa thông tin khóa học (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    @Override
    public boolean updateCourse(Course course) {
        return courseDAO.updateCourse(course);
    }

    // Xóa khóa học theo id (Xác nhận trước khi xóa)
    @Override
    public boolean deleteCourse(int id) {
        return courseDAO.deleteCourse(id);
    }

    // Tim kiếm khóa học theo tên (tìm kiếm tương đối) có phân trang
    @Override
    public List<Course> searchCourseByName(String name, int limit, int offset) {
        return courseDAO.searchCourseByName(name, limit, offset);
    }

    // Đếm tổng số khoá học theo tên
    @Override
    public int getTotalCoursesByName(String name) {
        return courseDAO.getTotalCoursesByName(name);
    }

    // Sắp xếp khóa học (theo tên/id - tăng dần/giảm dần) có phân trang
    @Override
    public List<Course> sortCourses(String column, String direction, int limit, int offset) {
        return courseDAO.sortCourses(column, direction, limit, offset);
    }

    @Override
    public boolean hasStudentsEnrolled(int courseId) {
        return courseDAO.hasStudentsEnrolled(courseId);
    }
}
