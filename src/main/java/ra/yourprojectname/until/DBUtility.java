package ra.yourprojectname.until;

import java.sql.*;

public class DBUtility {
    public static Connection openConnection(){
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            try {
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/projectjavafudamental", "postgres", "123456");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public static void closeConnection(ResultSet rs, PreparedStatement pstmt, Connection con){
        try {
            if (rs != null){
                rs.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (pstmt != null){
                pstmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (con != null){
                con.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
