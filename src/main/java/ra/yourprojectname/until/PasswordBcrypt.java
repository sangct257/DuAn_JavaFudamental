package ra.yourprojectname.until;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordBcrypt {
    public static String passwordBcrypt(String password){
        if (password == null) return null;
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean checkPassword(String password, String hash){
        if (password == null || hash == null) return false;
        return BCrypt.checkpw(password, hash);
    }
}
