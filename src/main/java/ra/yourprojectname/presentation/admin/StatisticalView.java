package ra.yourprojectname.presentation.admin;

import ra.yourprojectname.business.StatisticalService;
import ra.yourprojectname.business.impl.StatisticalServiceImpl;

import java.util.Map;
import java.util.Scanner;

public class StatisticalView {
    private final StatisticalService statisticalService = new StatisticalServiceImpl();

    public StatisticalView(Scanner scanner) {
        boolean flag = true;
        int choose;
        while (flag) {
            System.out.println("\n===================== MENU THỐNG KÊ =====================");
            System.out.println("""
            1. Thống kê tổng số lượng khoá học và học viên
            2. Thống kê số lượng học viên theo từng khoá học
            3. Top 5 khoá học đông học viên nhất
            4. Liệt kê khoá học trên 10 học viên
            5. Quay về menu chính """);
            System.out.println("==========================================================");

            while (true) {
                System.out.print("Mời chọn: ");
                try {
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (Exception e) {
                    System.out.println("Lỗi: Bạn phải nhập vào là số nguyên!");
                }
            }
            switch (choose) {
                case 1:
                    showTotalOverview();
                    break;
                case 2:
                    showStudentCountByCourse();
                    break;
                case 3:
                    showTop5Courses();
                    break;
                case 4:
                    showCoursesWithMoreThan10Students();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Bạn chỉ được nhập từ 1 đến 5");
            }
        }
    }

    // Case 1: Tổng số lượng tổng quan
    private void showTotalOverview() {
        System.out.println("\n--- THỐNG KÊ TỔNG QUAN TOÀN HỆ THỐNG ---");
        Map<String, Integer> data = statisticalService.getTotalOverview();

        int totalCourses = data.getOrDefault("total_courses", 0);
        int totalStudents = data.getOrDefault("total_students", 0);

        System.out.println("------------------------------------------");
        System.out.printf("| %-26s | %-9s |\n", "HẠNG MỤC THỐNG KÊ", "SỐ LƯỢNG");
        System.out.println("------------------------------------------");
        System.out.printf("| %-26s | %-9d |\n", "Tổng số khóa học hiện có", totalCourses);
        System.out.printf("| %-26s | %-9d |\n", "Tổng số học viên kích hoạt", totalStudents);
        System.out.println("------------------------------------------");
    }

    // Case 2: Số lượng học viên từng lớp
    private void showStudentCountByCourse() {
        System.out.println("\n--- THỐNG KÊ SỐ LƯỢNG HỌC VIÊN THEO TỪNG KHÓA HỌC ---");
        Map<String, Integer> data = statisticalService.getStudentCountByCourse();
        printStatisticalTable(data, "Không có dữ liệu khóa học nào!");
    }

    // Case 3: Top 5 khóa học đông nhất
    private void showTop5Courses() {
        System.out.println("\n--- TOP 5 KHÓA HỌC CÓ LƯỢNG HỌC VIÊN ĐÔNG NHẤT ---");
        Map<String, Integer> data = statisticalService.getTop5Courses();
        printStatisticalTable(data, "Hiện chưa có học viên nào được duyệt vào các khóa học!");
    }

    // Case 4: Khóa học trên 10 học viên
    private void showCoursesWithMoreThan10Students() {
        System.out.println("\n--- DANH SÁCH KHÓA HỌC CÓ TRÊN 10 HỌC VIÊN ---");
        Map<String, Integer> data = statisticalService.getCoursesWithMoreThan10Students();
        printStatisticalTable(data, "Không có khóa học nào đạt trên 10 học viên!");
    }

    // Hàm dùng chung để in bảng dữ liệu thống kê dạng Map<Tên khóa học, Số lượng>
    private void printStatisticalTable(Map<String, Integer> data, String emptyMessage) {
        System.out.println("----------------------------------------------------------");
        System.out.printf("| %-40s | %-11s |\n", "TÊN KHÓA HỌC", "SỐ HỌC VIÊN");
        System.out.println("----------------------------------------------------------");

        if (data == null || data.isEmpty()) {
            System.out.printf("| %-53s |\n", emptyMessage);
        } else {
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                System.out.printf("| %-40s | %-11d |\n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("----------------------------------------------------------");
    }
}
