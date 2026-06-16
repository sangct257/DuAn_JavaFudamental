package ra.yourprojectname.until;

import java.sql.*;

public class DatabaseSeeder {
    public static void seendData(){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        con = DBUtility.openConnection();
        try {
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Admin");
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                pstmt = con.prepareStatement("INSERT INTO Admin (username, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, PasswordBcrypt.passwordBcrypt( "123456"));
                pstmt.executeUpdate();
                System.out.println("[Database Seed]: Khởi tạo tài khoản Admin mặc định thành công! (Tài khoản: admin / Pass: 123456)");
            }
            if (rs != null) {
                rs.close();
            }
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM Student");
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0){
                pstmt = con.prepareStatement("INSERT INTO Student (name, dob, email, sex, phone, password) VALUES (?, ?, ?, ?, ?, ?)");
                pstmt.setString(1, "Nguyen Van A");
                pstmt.setDate(2, Date.valueOf("2002-02-02"));
                pstmt.setString(3, "hocvien@gmail.com");
                pstmt.setBoolean(4, true);
                pstmt.setString(5, "0976541238");
                pstmt.setString(6, PasswordBcrypt.passwordBcrypt( "123456"));
                pstmt.executeUpdate();

                System.out.println("[Database Seed]: Khởi tạo tài khoản Học viên mẫu thành công! (Email: hocvien@gmail.com / Pass: 123456)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtility.closeConnection(rs, pstmt, con);
        }
    }
}
