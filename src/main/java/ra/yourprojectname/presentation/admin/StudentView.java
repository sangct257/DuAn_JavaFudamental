package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.StudentService;
import ra.yourprojectname.business.impl.StudentServiceImpl;
import ra.yourprojectname.model.Student;
import ra.yourprojectname.until.PasswordBcrypt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StudentView {
    private final StudentService studentService = new StudentServiceImpl();
    private final int pageSize = 5; // Mỗi trang hiển thị 5 dòng dữ liệu

    public StudentView(Scanner scanner) {
        int choose;
        while (true) {
            System.out.println("\n=============== QUẢN LÝ HỌC VIÊN =============");
            System.out.println("""
                    1. Hiển thị danh sách học viên
                    2. Thêm mới học viên
                    3. Chỉnh sửa thông tin
                    4. Xóa học viên
                    5. Tìm kiếm học viên
                    6. Sắp xếp danh sách
                    7. Quay về menu chính """);
            System.out.println("==============================================");

            while (true) {
                try {
                    System.out.println("Mời chọn: ");
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Vui lòng nhập vào một số!");
                }
            }

            switch (choose) {
                case 1:
                    showStudentList(scanner);
                    break;
                case 2:
                    addStudent(scanner);
                    break;
                case 3:
                    updateStudent(scanner);
                    break;
                case 4:
                    deleteStudent(scanner);
                    break;
                case 5:
                    searchStudent(scanner);
                    break;
                case 6:
                    sortStudentList(scanner);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 7!");
            }
        }
    }

    // --- CASE 1: DANH SÁCH PHÂN TRANG ---
    private void showStudentList(Scanner scanner) {
        int currentPage = 1;
        while (true) {
            int totalRecords = studentService.getTotalStudents();
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1;
            }
            int offset = (currentPage - 1) * pageSize;
            List<Student> list = studentService.getAllStudents(pageSize, offset);
            System.out.printf("\n--- DANH SÁCH HỌC VIÊN (TRANG %d / %d) ---\n", currentPage, totalPages);
            printStudentTable(list);

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

    // --- CASE 2: THÊM MỚI HỌC VIÊN ---
    private void addStudent(Scanner scanner) {
        showStudentList(scanner);
        System.out.println("\n--- THÊM MỚI HỌC VIÊN ---");
        String name = inputString(scanner, "Nhập tên học viên: ");
        Date dob = inputDate(scanner, "Nhập ngày sinh (dd/MM/yyyy): ");
        String email = inputEmail(scanner, "Nhập email học viên: ");
        boolean sex = inputGender(scanner, "Chọn giới tính (1: Nam / 0: Nữ): ");
        String phone = inputPhone(scanner, "Nhập số điện thoại học viên: ");
        String password = inputString(scanner, "Đặt mật khẩu tài khoản học viên: ");
        Student student = new Student(0, name, dob, email, sex, phone, password, new java.util.Date());
        if (studentService.addStudent(student)) {
            System.out.println("Thêm học viên thành công!");
        } else {
            System.out.println("Thêm học viên thất bại!");
        }
    }

    // --- CASE 3: CHỈNH SỬA THÔNG TIN ---
    private void updateStudent(Scanner scanner) {
        showStudentList(scanner);
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN HỌC VIÊN ---");
        int id = inputInt(scanner, "Nhập ID học viên cần sửa: ");
        Student student = studentService.getStudentById(id);
        if (student == null) {
            System.out.println("Không tìm thấy học viên mang ID này!");
            return;
        }

        boolean isEditing = true;
        while (isEditing) {
            System.out.println("\n>> Chọn thuộc tính cần sửa cho [" + student.getName() + "]:");
            System.out.println("""
                    1. Sửa tên
                    2. Sửa ngày sinh
                    3. Sửa email
                    4. Sửa giới tính
                    5. Sửa SĐT
                    6. Sửa mật khẩu
                    7. Lưu & Thoát """);
            int choice = inputInt(scanner, "Chọn (1-7): ");
            switch (choice) {
                case 1:
                    student.setName(inputString(scanner, "Nhập tên mới: "));
                    break;
                case 2:
                    student.setDob(inputDate(scanner, "Nhập ngày sinh mới (dd/MM/yyyy): "));
                    break;
                case 3:
                    student.setEmail(inputEmail(scanner, "Nhập email mới: "));
                    break;
                case 4:
                    student.setSex(inputGender(scanner, "Chọn giới tính mới (1: Nam / 0: Nữ):"));
                    break;
                case 5:
                    student.setPhone(inputPhone(scanner, "Nhập SĐT mới: "));
                    break;
                case 6:
                    String passNew = inputString(scanner, "Nhập mật khẩu mới: ");
                    student.setPassword(PasswordBcrypt.passwordBcrypt(passNew));
                    break;
                case 7:
                    isEditing = false;
                    break;
                default:
                    System.out.println("Vui lòng nhập từ 1 đến 6!");
            }
        }
        if (studentService.updateStudent(student)) {
            System.out.println("Cập nhật thông tin học viên thành công!");
        } else {
            System.out.println("Cập nhật thất bại!");
        }
    }

    // --- CASE 4: XÓA HỌC VIÊN ---
    private void deleteStudent(Scanner scanner) {
        showStudentList(scanner);
        System.out.println("\n--- XÓA HỌC VIÊN ---");
        int id = inputInt(scanner, "Nhập ID cần xóa: ");
        Student student = studentService.getStudentById(id);
        if (student == null) {
            System.out.println("Không tìm thấy học viên cần xóa!");
            return;
        }

        System.out.printf("Bạn có chắc chắn muốn xóa học viên [%s] không? (Y/N): ", student.getName());
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.matches("Y")) {
            if (studentService.deleteStudent(id)) {
                System.out.println("Đã xóa thành công!");
            } else {
                System.out.println("Xoá thất bại!");
            }
        } else {
            System.out.println("Đã hủy thao tác.");
        }
    }

    // --- CASE 5: TÌM KIẾM ĐA NĂNG PHÂN TRANG ---
    private void searchStudent(Scanner scanner) {
        showStudentList(scanner);
        String keyword = inputString(scanner, "Nhập từ khóa tìm kiếm (Tên/ID/Email): ");
        int currentPage = 1;
        while (true) {
            int totalRecords = studentService.getTotalStudentsByKeyWord(keyword);
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1;
            }

            int offset = (currentPage - 1) * pageSize;

            List<Student> list = studentService.searchStudent(keyword, pageSize, offset);
            System.out.printf("\n--- KẾT QUẢ TÌM KIẾM CHO: '%s' (TRANG %d / %d) ---\n", keyword, currentPage, totalPages);
            printStudentTable(list);

            System.out.print("\n[P]: Trước  |  [N]: Kế tiếp  |  [E]: Thoát xem  | Chọn: ");
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

    // --- CASE 6: SẮP XẾP PHÂN TRANG ---
    private void sortStudentList(Scanner scanner) {
        String column = "name";
        String direction = "ASC";

        System.out.println("1. Sắp xếp theo Tên");
        System.out.println("2. Sắp xếp theo ID");
        if (inputInt(scanner, "Chọn tiêu chí (1-2): ") == 2) {
            column = "id";
        }

        System.out.println("1. Tăng dần");
        System.out.println("2. Giảm dần");
        if (inputInt(scanner, "Chọn chiều (1-2): ") == 2) {
            direction = "DESC";
        }

        int currentPage = 1;
        while (true) {
            int totalRecords = studentService.getTotalStudents();
            int totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) {
                totalPages = 1;
            }
            int offset = (currentPage - 1) * pageSize;
            List<Student> list = studentService.sortStudents(column, direction, pageSize, offset);
            System.out.printf("\n--- DANH SÁCH SẮP XẾP [%s - %s] (TRANG %d / %d) ---\n", column.toUpperCase(), direction, currentPage, totalPages);
            printStudentTable(list);

            System.out.print("\n[P]: Trước  |  [N]: Kế tiếp  |  [E]: Thoát xem  | Chọn: ");
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

    public Date inputDate(Scanner scanner, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Chặn việc nhập ngày sai cấu trúc (Ví dụ: 32/01)
        while (true) {
            System.out.print(message);
            try {
                return sdf.parse(scanner.nextLine().trim());
            } catch (ParseException e) {
                System.out.println("Định dạng ngày không hợp lệ! Vui lòng nhập đúng định dạng dd/MM/yyyy.");
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

    private String inputEmail(Scanner scanner, String message) {
        String emailRegex = "^^[A-Za-z0-9+_.-]+@(.+)$";
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Email không được để trống!");
                continue;
            }
            if (!input.matches(emailRegex)) {
                System.out.println("Lỗi: Định dạng email không hợp lệ (Ví dụ hợp lệ: nguyenvan@gmail.com)!");
                continue;
            }
            return input;
        }
    }

    private String inputPhone(Scanner scanner, String message) {
        String phoneRegex = "^(03|05|07|08|09)\\d{8}$";
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Số điện thoại không được để trống!");
                continue;
            }
            if (!input.matches(phoneRegex)) {
                System.out.println("Lỗi: Số điện thoại phải gồm 10 chữ số và bắt đầu bằng các đầu số hợp lệ (03, 05, 07, 08, 09)!");
                continue;
            }
            return input;
        }
    }

    private boolean inputGender(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 1) {
                    return true;
                } else if (choice == 0) {
                    return false;
                } else {
                    System.out.println("Lỗi: Bạn chỉ được chọn 1 (Nam) hoặc 0 (Nữ). Vui lòng nhập lại!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập vào một số nguyên (1 hoặc 2)!");
            }
        }
    }

    public void printStudentTable(List<Student> list) {
        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-22s | %-12s | %-25s | %-10s | %-12s | %-12s |\n", "ID", "HỌ VÀ TÊN", "NGÀY SINH", "EMAIL", "GIỚI TÍNH", "SỐ ĐIỆN THOẠI", "NGÀY TẠO");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
        if (list == null || list.isEmpty()) {
            System.out.println("|                                             Không có dữ liệu học viên                                               |");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Student s : list) {
                System.out.printf("| %-5d | %-22s | %-12s | %-25s | %-10s | %-13s | %-12s |\n",
                        s.getId(), s.getName(), sdf.format(s.getDob()), s.getEmail(), s.isSex() ? "Nam" : "Nữ", s.getPhone(), s.getCreateAt());
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
    }
}
