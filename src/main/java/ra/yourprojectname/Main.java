package ra.yourprojectname;


import ra.yourprojectname.presentation.login.LoginView;
import ra.yourprojectname.until.DatabaseSeeder;

public class Main {
    private static final LoginView loginView = new LoginView();
    public static void main(String[] args) {
        DatabaseSeeder.seendData();
        loginView.showLoginInfo();
    }
}