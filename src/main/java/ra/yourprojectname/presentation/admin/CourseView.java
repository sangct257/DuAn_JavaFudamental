package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.CourseService;
import ra.yourprojectname.business.impl.CourseServiceImpl;
import ra.yourprojectname.model.Course;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CourseView {

    private final CourseService courseService = new CourseServiceImpl();
    private final int pageSize = 5; // Mỗi trang hiển thị 5 dòng dữ liệu

    public CourseView(Scanner scanner) {
        int choose;
        while (true) {
            System.out.println("\n=============== QUẢN LÝ KHÓA HỌC =============");
            System.out.println("""
                    1. Hiển thị danh sách khóa học
                    2. Thêm mới khóa học
                    3. Chỉnh sửa thông tin
                    4. Xóa khóa học
                    5. Tìm kiếm theo tên
                    6. Sắp xếp danh sách
                    7. Quay về menu chính""");
            System.out.println("==============================================");
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
                    addCourse(scanner);
                    break;
                case 3:
                    updateCourse(scanner);
                    break;
                case 4:
                    deleteCourse(scanner);
                    break;
                case 5:
                    searchCourseByName(scanner);
                    break;
                case 6:
                    sortCourseList(scanner);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Vui lòng chọn từ 1 đến 7!");
            }
        }
    }

    // --- CASE 1: HIỂN THỊ DANH SÁCH CÓ PHÂN TRANG ---
    public void showCourseList(Scanner scanner) {
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

    // --- CASE 2: THÊM MỚI KHÓA HỌC ---
    public void addCourse(Scanner scanner) {
        showCourseList(scanner);
        System.out.println("\n--- THÊM MỚI KHÓA HỌC ---");
        String name = inputString(scanner, "Nhập tên khoá học: ");
        int duration = inputInt(scanner, "Nhập thời lượng khoá học (h): ");
        String instructor = inputString(scanner, "Nhập giảng viên phụ trách: ");

        Course course = new Course(0, name, duration, instructor, new Date());
        if (courseService.addCourse(course)) {
            System.out.println("Thêm khoá học thành công!");
        } else {
            System.out.println("Thêm khoá học thất bại!");
        }
    }

    // --- CASE 3: CHỈNH SỬA THÔNG TIN (Hiển thị menu con thuộc tính) ---
    public void updateCourse(Scanner scanner) {
        showCourseList(scanner);
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN KHÓA HỌC ---");
        int id = inputInt(scanner, "Nhập ID khoá học muốn sửa: ");

        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("Không tìm thấy khoá học có ID: " + id);
            return;
        }

        boolean isEditing = true;
        while (isEditing) {
            System.out.println("\n>> Chọn thuộc tính cần sửa cho khóa học [" + course.getName() + "]:");
            System.out.println("""
                    1. Đổi tên khóa học
                    2. Đổi thời lượng
                    3. Đổi tên giảng viên
                    4. Lưu lại & Thoát
                    """);

            int choise = inputInt(scanner, "Mời chọn từ 1 đến 4: ");

            switch (choise) {
                case 1:
                    course.setName(inputString(scanner, "Mời nhập tên khoá học mới: "));
                    break;
                case 2:
                    course.setDuration(inputInt(scanner, "Mời nhập thời lượng mới của khoá học: "));
                    break;
                case 3:
                    course.setInstructor(inputString(scanner, "Mời nhập tên giảng viên mới: "));
                    break;
                case 4:
                    isEditing = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }

            if (courseService.updateCourse(course)) {
                System.out.println("Cập nhập thông tin thành công!");
            } else {
                System.out.println("Cập nhập thông tin thất bại!");
            }
        }
    }

    // --- CASE 4: XÓA KHÓA HỌC (Xác nhận trước khi xóa) ---
    public void deleteCourse(Scanner scanner) {
        showCourseList(scanner);
        System.out.println("\n--- XÓA KHÓA HỌC ---");
        int id = inputInt(scanner, "Nhập ID khoá học cần xoá: ");
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("Không tìm thấy ID khoá học cần xoá!");
            return;
        }
        System.out.printf("Bạn có chắc chắn muốn xóa khóa học [%s] không? (Y/N): ", course.getName());
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.matches("Y")) {
            if (courseService.deleteCourse(id)) {
                System.out.println("Xoá thành công khoá học!");
            } else {
                System.out.println("Xoá khoá học thất bại!");
            }
        } else {
            System.out.println("Đã huỷ thao tác xoá!");
        }
    }

    // --- CASE 5: TÌM KIẾM THEO TÊN (CÓ PHÂN TRANG) ---
    public void searchCourseByName(Scanner scanner) {
        showCourseList(scanner);
        String searchName = inputString(scanner, "Nhập từ khóa tên khóa học muốn tìm: ");
        int currentPage = 1; // Trang đầu tiên

        while (true) {
            // Tổng số khoá học theo tên
            int totalRecords = courseService.getTotalCoursesByName(searchName);
            // Tổng số trang
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1; // trang 1
            }

            int offset = (currentPage - 1) * pageSize;

            List<Course> list = courseService.searchCourseByName(searchName, pageSize, offset);
            System.out.printf("\n--- KẾT QUẢ TÌM KIẾM KHÓA HỌC: '%s' (TRANG %d / %d) ---\n", searchName, currentPage, totalPages);
            printCourseTable(list);
            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [E]: Thoát tìm kiếm  |  Mời chọn: ");
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

            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N hoặc E.");
        }
    }

    // --- CASE 6: SẮP XẾP DANH SÁCH KHOÁ HỌC (CÓ PHÂN TRANG) ---
    public void sortCourseList(Scanner scanner) {
        String column = "name";
        String direction = "ASC";

        System.out.println("\n>> Chọn tiêu chí sắp xếp:");
        System.out.println("1. Sắp xếp theo Tên khóa học");
        System.out.println("2. Sắp xếp theo ID khóa học");
        int choiceCol = inputInt(scanner, "Lựa chọn (1-2): ");
        if (choiceCol == 2) column = "id";

        System.out.println(">> Chọn chiều sắp xếp:");
        System.out.println("1. Tăng dần (A-Z / Thấp đến Cao)");
        System.out.println("2. Giảm dần (Z-A / Cao đến Thấp)");
        int choiceDir = inputInt(scanner, "Lựa chọn (1-2): ");
        if (choiceDir == 2) direction = "DESC";

        int currentPage = 1; // trang đầu tiên
        while (true) {
            // Tổng số khoá học
            int totalRecords = courseService.getTotalCourses();
            // Tổng số trang
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1; // trang 1
            }
            int offset = (currentPage - 1) * pageSize;
            List<Course> list = courseService.sortCourses(column, direction, pageSize, offset);
            System.out.printf("\n--- DANH SÁCH SẮP XẾP THEO [%s - %s] (TRANG %d / %d) ---\n", column.toUpperCase(), direction, currentPage, totalPages);
            printCourseTable(list);
            System.out.print("\n[P]: Trang trước  |  [N]: Trang kế  |  [E]: Thoát tìm kiếm  |  Mời chọn: ");
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

            System.out.println("Lệnh không hợp lệ! Vui lòng chỉ nhập P, N hoặc E.");
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
}
