import GUI.GUI;
import GUI.LoginForm;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        LoginForm lf = new LoginForm(800,600);
        lf.setVisible(true);
        lf.setResizable(true);
    }
}