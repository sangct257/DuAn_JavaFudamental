package ra.yourprojectname.until;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordBcrypt {
    public static String passwordBcrypt(String password){
        if (password == null) return null;
        // Mã hoá mật khẩu thông qua BCrypt.gensalt(10)
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean checkPassword(String password, String hash){
        if (password == null || hash == null) return false;
        // Đối chiếu mật khẩu thô và mật khẩu đã dc mã hoá thông qua BCrypt.checkpw
        return BCrypt.checkpw(password, hash);
    }
}
