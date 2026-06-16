package ra.yourprojectname.dao.impl;

import ra.yourprojectname.dao.AdminDAO;
import ra.yourprojectname.until.DBUtility;
import ra.yourprojectname.until.PasswordBcrypt;

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
            pstmt = con.prepareStatement("select * from Admin where username=?");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (PasswordBcrypt.checkPassword(password, hashedPassword)){
                    flag = true;
                } else {
                    System.out.println("Sai mật khẩu!");
                }
            } else {
                System.out.println("Tài khoản đăng nhập không tồn tại!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
        return flag;
    }
}
