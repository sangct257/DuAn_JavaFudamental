package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.business.EnrollmentService;
import ra.yourprojectname.business.impl.CourseServiceImpl;
import ra.yourprojectname.business.impl.EnrollmentServiceImpl;
import ra.yourprojectname.model.Course;
import ra.yourprojectname.model.Enrollment;
import ra.yourprojectname.model.EnrollmentStatus;
import ra.yourprojectname.model.Student;

import java.util.List;
import java.util.Scanner;

public class EnrollmentView {
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final CourseService courseService = new CourseServiceImpl();
    private final int pageSize = 5;

    public EnrollmentView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag) {
            System.out.println("\n================= QUẢN LÝ ĐĂNG KÝ KHOÁ HỌC =================");
            System.out.println("""
                    1. Hiển thị học viên theo từng khoá học
                    2. Duyệt học viên đăng ký khóa học
                    3. Xóa học viên khỏi khoá học (Xem danh sách trước)
                    4. Quay về menu chính """);
            System.out.println("============================================================");

            while (true) {
                System.out.print("Mời chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Lỗi: Bạn phải nhập vào là số nguyên!");
                }
            }

            switch (choose) {
                case 1:
                    showEnrollmentByCourse(scanner);
                    break;
                case 2:
                    approveEnrollment(scanner);
                    break;
                case 3:
                    deleteEnrollment(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 4");
            }
        }
    }

    // --- Chức năng 1: Hiển thị học viên theo từng khóa học (Có phân trang) ---
    private void showEnrollmentByCourse(Scanner scanner) {
        System.out.println("\n--- DANH SÁCH KHÓA HỌC HIỆN CÓ ---");
        showCourseList(scanner);

        System.out.print("Nhập ID khóa học muốn xem danh sách học viên: ");
        int courseId = inputInt(scanner);

        Course selectedCourse = courseService.getCourseById(courseId);
        if (selectedCourse == null) {
            System.out.println("Không tìm thấy khóa học với ID vừa nhập!");
            return;
        }

        int currentPage = 1;
        while (true) {
            int totalRecords = enrollmentService.getTotalEnrollmentsByCourse(courseId);
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1;
            }

            int offset = (currentPage - 1) * pageSize;
            List<Enrollment> list = enrollmentService.getEnrollmentsByCourse(courseId, pageSize, offset);
            System.out.printf("\n--- DANH SÁCH HỌC VIÊN LỚP: %s (TRANG %d/%d) ---\n", selectedCourse.getName(), currentPage, totalPages);
            printEnrollmentTable(list);

            // Điều hướng phân trang
            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [E]: Thoát xem  |  Nhập lựa chọn: ");
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
                    System.out.println("Bạn đang ở trang đầu tiên!");
                }
                continue;
            }

            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N hoặc E.");
        }
    }

    // --- Chức năng 2: Duyệt đơn đăng ký khóa học ---
    private void approveEnrollment(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalRecords = enrollmentService.getTotalEnrollments();
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) totalPages = 1;

            int offset = (currentPage - 1) * pageSize;
            List<Enrollment> listOnPage = enrollmentService.getAllEnrollments(pageSize, offset);

            System.out.printf("\n--- DANH SÁCH DUYỆT ĐƠN ĐĂNG KÝ (TRANG %d/%d) ---\n", currentPage, totalPages);
            printEnrollmentTable(listOnPage);

            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [S]: Chọn duyệt đơn trang này  |  [E]: Thoát  | Lựa chọn: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) {
                break;
            }
            if (action.equals("N")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối!");
                }
                continue;
            }
            if (action.equals("P")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu tiên!");
                }
                continue;
            }

            if (action.equals("S")) {
                System.out.print("\nNhập ID Học viên cần duyệt: ");
                int studentId = inputInt(scanner);
                System.out.print("Nhập ID Khóa học cần duyệt: ");
                int courseId = inputInt(scanner);

                Enrollment target = findEnrollment(listOnPage, studentId, courseId);
                if (target == null) {
                    System.out.println("Không tìm thấy khoá đăng ký nào!");
                    continue;
                }

                if (target.getStatus() != EnrollmentStatus.WAITING) {
                    System.out.println("Khoá học này không thể xác nhận! Trạng thái hiện tại: [" + target.getStatus() + "]");
                    continue;
                }

                System.out.print("Chọn hành động duyệt (1: CONFIRMED [Chấp nhận] | 2: DENIED [Từ chối]): ");
                String choose = scanner.nextLine().trim();
                EnrollmentStatus newStatus = choose.equals("1") ? EnrollmentStatus.CONFIRMED : EnrollmentStatus.DENIED;

                if (enrollmentService.updateEnrollmentStatus(studentId, courseId, newStatus)) {
                    System.out.println("Cập nhật trạng thái đơn thành công sang: " + newStatus);
                    currentPage = 1; // Quay về trang 1
                } else {
                    System.out.println("Thao tác duyệt thất bại.");
                }
                continue; // 🌟 Thêm continue để vòng lặp quay lại từ đầu, không chạy xuống dòng lỗi bên dưới
            }

            // Dòng này chỉ chạy khi action KHÔNG PHẢI là P, N, E, S
            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N, S hoặc E.");
        }
    }

    // --- Chức năng 3: Xóa học viên khỏi khóa học ---
    private void deleteEnrollment(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalRecords = enrollmentService.getTotalEnrollments();
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) totalPages = 1;

            int offset = (currentPage - 1) * pageSize;
            List<Enrollment> listOnPage = enrollmentService.getAllEnrollments(pageSize, offset);

            System.out.printf("\n--- DANH SÁCH ĐĂNG KÝ HỌC VIÊN TOÀN HỆ THỐNG (TRANG %d/%d) ---\n", currentPage, totalPages);
            printEnrollmentTable(listOnPage);

            // 🌟 Sửa menu lựa chọn rõ ràng: Bấm D để chọn xóa
            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [D]: Chọn xóa học viên trang này  |  [E]: Thoát  | Lựa chọn: ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) break;
            if (action.equals("N")) {
                if (currentPage < totalPages) currentPage++;
                else System.out.println("Bạn đang ở trang cuối!");
                continue;
            }
            if (action.equals("P")) {
                if (currentPage > 1) currentPage--;
                else System.out.println("Bạn đang ở trang đầu tiên!");
                continue;
            }

            // 🌟 Gom toàn bộ logic nhập ID xóa vào block 'D'
            if (action.equals("D")) {
                System.out.print("\nNhập ID Học viên muốn loại bỏ: ");
                int studentId = inputInt(scanner);
                System.out.print("Nhập ID Khóa học muốn xóa học viên: ");
                int courseId = inputInt(scanner);

                Enrollment target = findEnrollment(listOnPage, studentId, courseId);
                if (target == null) {
                    System.out.println("Không tìm thấy thông tin đăng ký khóa học khớp với dữ liệu trên trang này!");
                    continue;
                }

                System.out.printf("Bạn có chắc chắn muốn xóa học viên [%s] khỏi khóa học [%s] không? (Y/N): ",
                        target.getStudent().getName(), target.getCourse().getName());

                if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                    if (enrollmentService.deleteEnrollment(studentId, courseId)) {
                        System.out.println("Đã xóa học viên ra khỏi khóa học thành công!");
                        currentPage = 1; // Reset về trang 1
                    } else {
                        System.out.println("Thao tác xóa gặp lỗi hệ thống.");
                    }
                } else {
                    System.out.println("Đã hủy thao tác xóa học viên.");
                }
                continue; // 🌟 Thêm continue để lặp lại từ đầu
            }

            // Dòng này chỉ chạy khi action KHÔNG PHẢI là P, N, E, D
            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N, D hoặc E.");
        }
    }

    private Enrollment findEnrollment(List<Enrollment> list, int studentId, int courseId) {
        for (Enrollment e : list) {
            if (e.getStudent().getId() == studentId && e.getCourse().getId() == courseId) {
                return e;
            }
        }
        return null;
    }

    private void showCourseList(Scanner scanner) {
        int currentPage = 1;

        while (true) {
            // Tổng số khoá học
            int totalRecords = courseService.getTotalCourses();
            // Số trang : (tổng số khoá học + 5 số khoá học mỗi trang - 1) / 5
            // (10 + 5 - 1) / 5 = 2
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            // VD (0 + 5 - 1) / 5 = 1
            if (totalPages == 0) {
                totalPages = 1;
            }

            int offset = (currentPage - 1) * pageSize;

            List<Course> list = courseService.getAllCourses(pageSize, offset);
            System.out.printf("\n--- DANH SÁCH KHÓA HỌC (TRANG %d / %d) ---\n", currentPage, totalPages); // Bắt đầu là 1/1
            printCourseTable(list);

            // Điều hướng phân trang
            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [E]: Thoát xem  |  Nhập lựa chọn: ");
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
                    System.out.println("Bạn đang ở trang đầu tiên!");
                }
                continue;
            }

            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N hoặc E.");
        }
    }

    private void printCourseTable(List<Course> list) {
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
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-12s | %-25s | %-12s | %-30s | %-15s |\n", "MÃ HỌC VIÊN", "TÊN HỌC VIÊN", "MÃ KHÓA HỌC", "TÊN KHÓA HỌC", "TRẠNG THÁI");
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        if (list == null || list.isEmpty()) {
            System.out.println("|                                   Không có dữ liệu học viên đăng ký!                                          |");
        } else {
            for (Enrollment e : list) {
                System.out.printf("| %-12d | %-25s | %-12d | %-30s | %-15s |\n",
                        e.getStudent().getId(), e.getStudent().getName(), e.getCourse().getId(), e.getCourse().getName(), e.getStatus());
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------");
    }

    private int inputInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Định dạng không hợp lệ, vui lòng nhập lại số nguyên: ");
            }
        }
    }
}
