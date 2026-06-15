package ra.yourprojectname.presentation.student;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.business.impl.CourseServiceImpl;
import ra.yourprojectname.business.impl.EnrollmentServiceImpl;
import ra.yourprojectname.business.impl.StudentServiceImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.EnrollmentStatus;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.PasswordHasher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class StudentView {
    private final CourseService courseService = new CourseServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();
    private Student currentStudent;
    private final int pageSize = 5;

    public StudentView(Scanner scanner, String loginEmail) {
        this.currentStudent = studentService.getStudentByEmail(loginEmail);
        if (this.currentStudent == null) {
            System.out.println("Lỗi hệ thống: Không tìm thấy thông tin tài khoản học viên!");
            return;
        }

        boolean flag = true;
        int choose;
        while (flag) {
            System.out.println("\n============= MENU STUDENT ==========");
            System.out.println("Chào mừng học viên: " + this.currentStudent.getName());
            System.out.println("""
                    1. Xem danh sách 
                    2. Đăng ký khoá học
                    3. Xem khoá học đã đăng ký
                    4. Huỷ đăng ký khoá học (nếu chưa bắt đầu)
                    5. Đổi mật khẩu
                    6. Đăng xuất """);
            System.out.println("===================================");

            while (true) {
                System.out.println("Mời chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Vui lòng nhập vào một số!");
                }
            }

            switch (choose) {
                case 1:
                    showCourseList(scanner);
                    break;
                case 2:
                    addEnrollment(scanner);
                    break;
                case 3:
                    showMyEnrollment(scanner);
                    break;
                case 4:
                    cancelEnrollment(scanner);
                    break;
                case 5:
                    changePassword(scanner);
                    break;
                case 6:
                    flag = false;
                    break;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 6!");
            }
        }
    }

    // --- Chức năng 1: Xem danh sách khóa học hệ thống---
    private void showCourseList(Scanner scanner) {
        int currentPage = 1;
        String keyword = "";
        while (true) {
            int totalRecords = courseService.getTotalCourses();
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1;
            }

            int offset = (currentPage - 1) * pageSize;

            List<Course> list ;

            if (keyword.isEmpty()) {
                list = courseService.getAllCourses(pageSize, offset);
                System.out.printf("\n--- DANH SÁCH KHÓA HỌC (TRANG %d/%d) ---\n", currentPage, totalPages);
            } else {
                list = courseService.searchCourseByName(keyword, pageSize, offset);
                System.out.printf("\n--- KẾT QUẢ TÌM KIẾM CHO '%s' (TRANG %d/%d) ---\n", keyword, currentPage, totalPages);
            }
            printCourseTable(list);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [F]: Tìm theo tên  |  [R]: Reset xem tất cả  |  [E]: Thoát  |  Mời chọn: ");
            String action = scanner.nextLine().trim().toUpperCase();
            if (action.matches("E")) {
                break;
            }

            if (action.matches("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối!");
                }
                continue;
            }
            if (action.matches("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu!");
                }
                continue;
            }
            if (action.matches("F")) {
                System.out.print("Nhập tên khóa học cần tìm kiếm: ");
                keyword = scanner.nextLine().trim();
                currentPage = 1; // Tìm từ khóa mới thì quay về trang đầu tiên
                continue;
            }

            if (action.equals("R")) {
                keyword = ""; // Xóa bộ lọc tìm kiếm
                currentPage = 1;
                System.out.println("Đã cài đặt lại danh sách hiển thị mặc định.");
                continue;
            }
            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N, F, R hoặc E.");
        }
    }

    // --- Chức năng 2: Đăng ký khóa học mới ---
    private void addEnrollment(Scanner scanner) {
        showCourseList(scanner);
        System.out.println("\n--- ĐĂNG KÝ KHÓA HỌC MỚI ---");
        int id = inputInt(scanner, "Nhập ID khoá học muốn đăng ký: ");
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("Không tìm thấy khóa học với ID này!");
            return;
        }

        // Duyệt qua danh sách đăng ký khoá học
        List<Enrollment> myHistory = enrollmentService.getEnrollmentsByStudent(currentStudent.getId(), 200, 0);
        Enrollment existEnrollment = null;
        for (Enrollment e : myHistory) {
            if (e.getCourse().getId() == id) {
                existEnrollment = e;
                break;
            }
        }

        if (existEnrollment != null){
            EnrollmentStatus currentStatus = existEnrollment.getStatus();

            if (currentStatus == EnrollmentStatus.WAITING || currentStatus == EnrollmentStatus.CONFIRMED) {
                System.out.println("Bạn đã đăng ký khoá học này rồi và khoá học đang chờ duyệt hoặc đang học!");
                return;
            }

            if (currentStatus == EnrollmentStatus.CANCELED) {
                System.out.println("Khóa học này đã được huỷ. Bạn có muốn đăng ký lại không? (Y/N): ");
            } else {
                System.out.println("Khóa học này từng bị từ chối. Bạn có muốn nộp lại đơn không? (Y/N): ");
            }

            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("Y")){
                if (enrollmentService.updateEnrollmentStatus(currentStudent.getId(), id, EnrollmentStatus.WAITING)) {
                    System.out.println("Đăng ký lại thành công! Trạng thái đơn quay về: WAITING (Chờ admin duyệt)!");
                } else {
                    System.out.println("Đăng ký lại thất bại!");
                }
            } else {
                System.out.println("Đã hủy thao tác đăng ký lại.");
            }
        } else {
            Enrollment enrollment = new Enrollment(0, currentStudent, course, LocalDateTime.now(), EnrollmentStatus.WAITING);
            if (enrollmentService.addEnrollment(enrollment)) {
                System.out.println("Đăng ký thành công! Trạng thái đơn: WAITING (Chờ admin duyệt)!");
            } else {
                System.out.println("Đăng ký thất bại!");
            }
        }
    }

    // --- Chức năng 3: Xem lịch sử khóa học bản thân đã đăng ký (Hỗ trợ sắp xếp + Phân trang) ---
    private void showMyEnrollment(Scanner scanner) {
        System.out.println("\n--- XEM KHÓA HỌC ĐÃ ĐĂNG KÝ ---");
        String column = "e.id";
        String direction = "ASC";
        boolean useSort = false; // Mặc định hiển thị tuần tự theo ID bảng ghi danh

        int currentPage = 1;
        while (true) {
            int totalRecords = enrollmentService.getTotalEnrollmentsByStudent(currentStudent.getId());
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1;
            }
            int offset = (currentPage - 1) * pageSize;

            List<Enrollment> list;
            if (useSort) {
                list = enrollmentService.getEnrollmentsByStudentWithSort(currentStudent.getId(), column, direction, pageSize, offset);
                System.out.printf("\n--- LỊCH SỬ ĐĂNG KÝ CỦA BẠN [CÓ SẮP XẾP: %s/%s] (TRANG %d/%d) ---\n", column,direction,currentPage, totalPages);
            } else {
                list = enrollmentService.getEnrollmentsByStudent(currentStudent.getId(), pageSize, offset);
                System.out.printf("\n--- LỊCH SỬ ĐĂNG KÝ CỦA BẠN (TRANG %d/%d) ---\n", currentPage, totalPages);
            }

            printEnrollmentTable(list);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [F]: Thay đổi cách sắp xếp  |  [E]: Thoát  |  Mời chọn: ");

            String action = scanner.nextLine().trim().toUpperCase();
            if (action.matches("E")) {
                break;
            }

            if (action.matches("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối!");
                }
                continue;
            }
            if (action.matches("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu!");
                }
                continue;
            }
            if (action.matches("F")) {
                System.out.print("Sắp xếp theo (1: Tên khóa học | 2: Ngày đăng ký): ");
                column = scanner.nextLine().trim().equals("1") ? "c.name" : "e.registered_at";
                System.out.print("Thứ tự hiển thị (1: Tăng dần [ASC] | 2: Giảm dần [DESC]): ");
                direction = scanner.nextLine().trim().equals("1") ? "ASC" : "DESC";
                useSort = true; // Kích hoạt trạng thái lấy dữ liệu kèm Sort lệnh SQL
                currentPage = 1; // Đưa về trang thứ nhất để áp dụng cấu trúc sắp xếp mới
                continue;
            }
            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N hoặc E.");
        }
    }

    // --- Chức năng 4: Huỷ đăng ký khóa học ---
    private void cancelEnrollment(Scanner scanner) {
        showMyEnrollment(scanner);
        System.out.println("\n--- HUỶ ĐƠN ĐĂNG KÝ KHÓA HỌC ---");
        int courseId = inputInt(scanner, "Nhập ID khóa học bạn muốn rút đơn đăng ký: ");

        // Lấy danh sách đăng ký thực tế của học viên để kiểm tra trạng thái đơn trước khi gửi lệnh đi DB
        List<Enrollment> myHistory = enrollmentService.getEnrollmentsByStudent(currentStudent.getId(), 200, 0);
        Enrollment target = null;
        for (Enrollment e : myHistory) {
            if (e.getCourse().getId() == courseId) {
                target = e;
                break;
            }
        }

        if (target == null) {
            System.out.println("Bạn chưa từng nộp đơn đăng ký cho mã khóa học này!");
            return;
        }

        // Kiểm tra nghiệp vụ: Chỉ được rút khi đơn đang ở hàng đợi duyệt
        if (target.getStatus() != EnrollmentStatus.WAITING) {
            System.out.println("Rút đơn thất bại! Đơn của bạn đã được admin xử lý sang trạng thái: [" + target.getStatus() + "]");
            return;
        }

        System.out.print("Bạn có chắc chắn muốn hủy đơn đăng ký khóa học này không? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            if (enrollmentService.updateEnrollmentStatus(currentStudent.getId(), courseId, EnrollmentStatus.CANCELED)) {
                System.out.println("Đã hủy đơn đăng ký khóa học thành công.");
            } else {
                System.out.println("Thao tác hủy đơn gặp lỗi hệ thống.");
            }
        }
    }

    // --- Chức năng 5: Đổi mật khẩu ---
    private void changePassword(Scanner scanner) {
        System.out.println("\n--- ĐỔI MẬT KHẨU TÀI KHOẢN ---");
        String passwordOld = inputString(scanner,"Nhập mật khẩu hiện tại: ");
        if (!passwordOld.equals(currentStudent.getPassword())) {
            System.out.println("Mật khẩu không chính xác!");
            return;
        }
        String passwordNew = inputString(scanner,"Nhập mật khẩu mới: ");
        currentStudent.setPassword(PasswordHasher.hashPassword(passwordNew));
        if (studentService.updateStudent(currentStudent)) {
            System.out.println("Đổi mật khẩu thành công!");
        } else {
            System.out.println("Đổi mật khẩu thất bại!");
        }
    }

    public int inputInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input < 0) {
                    System.out.println("Không được nhỏ hơn 0!");
                }
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhật số!");
            }
        }
    }

    public String inputString(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Không được để trống!");
            }
        }
    }
    public void printCourseTable(List<Course> list) {
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-25s | %-12s | %-20s | %-20s |\n", "ID", "TÊN KHÓA HỌC", "THỜI LƯỢNG", "GIẢNG VIÊN", "NGÀY TẠO");
        System.out.println("--------------------------------------------------------------------------------------------------");
        if (list == null || list.isEmpty()) {
            System.out.println("|                                    Không có dữ liệu khóa học                                    |");
        } else {
            for (Course course : list) {
                System.out.printf("| %-5d | %-25s | %-10d H | %-20s | %-20s |\n",
                        course.getId(), course.getName(), course.getDuration(), course.getInstructor(), course.getCreateAt());
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    private void printEnrollmentTable(List<Enrollment> list) {
        System.out.println("------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-30s | %-15s | %-15s |\n", "MÃ KHÓA", "TÊN KHÓA HỌC", "NGÀY ĐĂNG KÝ", "TRẠNG THÁI");
        System.out.println("------------------------------------------------------------------------------------");
        if (list == null || list.isEmpty()) {
            System.out.println("|                          Bạn chưa đăng ký khóa học nào!                           |");
        } else {
            for (Enrollment e : list) {
                System.out.printf("| %-10d | %-30s | %-15s | %-15s |\n",
                        e.getCourse().getId(), e.getCourse().getName(), e.getRegisteredAt().toLocalDate(), e.getStatus());
            }
        }
        System.out.println("------------------------------------------------------------------------------------");
    }
}
