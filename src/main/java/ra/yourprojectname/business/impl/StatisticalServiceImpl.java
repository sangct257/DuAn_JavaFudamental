package ra.yourprojectname.business.impl;

import ra.yourprojectname.business.StatisticalService;
import ra.yourprojectname.dao.StatisticalDAO;
import ra.yourprojectname.dao.impl.StatisticalDAOImpl;

import java.util.Map;

public class StatisticalServiceImpl implements StatisticalService {

    private final StatisticalDAO statisticalDAO = new StatisticalDAOImpl();

    // Thống kê tổng số lượng khóa học và tổng số học viên
    @Override
    public Map<String, Integer> getTotalOverview() {
        return statisticalDAO.getTotalOverview();
    }

    // Thống kê tổng số học viên theo từng khóa
    @Override
    public Map<String, Integer> getStudentCountByCourse() {
        return statisticalDAO.getStudentCountByCourse();
    }

    // Thống kê top 5 khóa học đông sinh viên nhất
    @Override
    public Map<String, Integer> getTop5Courses() {
        return statisticalDAO.getTop5Courses();
    }

    // Liệt kê các khóa học có trên 10 học viên
    @Override
    public Map<String, Integer> getCoursesWithMoreThan10Students() {
        return statisticalDAO.getCoursesWithMoreThan10Students();
    }
}
