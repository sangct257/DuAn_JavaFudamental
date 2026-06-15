package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.AdminDAO;
import ra.yourprojectname.until.DBUtility;
import ra.yourprojectname.until.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAOImpl implements AdminDAO {
    // Đăng nhập tài khoản mật và mật khẩu adnin
    @Override
    public boolean login(String username, String password) {
        boolean flag = true;
        Connection con;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("select * from Admin where username=? and password=?");
            pstmt.setString(1, username);
            pstmt.setString(2, PasswordHasher.hashPassword(password));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                flag = true;
            } else {
                System.out.println("Sai username hoặc password");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return flag;
    }
}
