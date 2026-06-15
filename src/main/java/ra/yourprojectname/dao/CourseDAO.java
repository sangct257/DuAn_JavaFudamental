package ra.yourprojectname.dao;

import ra.yourprojectname.model.Course;

import java.util.List;

public interface CourseDAO {
    // Hiển thị danh sách khóa học có phân trang
    List<Course> getAllCourses(int limit, int offset);
    // Đếm tổng số khoá học
    int getTotalCourses();

    // Thêm mới khóa học
    boolean addCourse(Course course);

    // Tìm kiếm khoá học theo ID
    Course getCourseById(int id);

    // Chỉnh sửa thông tin khóa học (hiển thị menu con cho phép chọn thuộc tính cần sửa)
    boolean updateCourse(Course course);

    // Xóa khóa học theo id (Xác nhận trước khi xóa)
    boolean deleteCourse(int id);

    // Tim kiếm khóa học theo tên (tìm kiếm tương đối) có phân trang
    List<Course> searchCourseByName(String name, int limit, int offset);

    // Đếm tổng số khoá học theo tên
    int getTotalCoursesByName(String name);

    // Sắp xếp khóa học (theo tên/id - tăng dần/giảm dần) có phân trang
    List<Course> sortCourses(String column, String direction, int limit, int offset);
}
