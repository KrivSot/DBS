package GUI;

import Database.SQLOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginForm extends JFrame {

    private SQLOperations sql = new SQLOperations();

    public LoginForm(int width, int height) throws SQLException {
        super("DBS2 Project");
        setSize(width,height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initGui();
        setVisible(true);
    }

    public void initGui() {
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(initPanel(), BorderLayout.NORTH);
        add(panelMain);
    }

    public JPanel initPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JTextField username = new JTextField("",50);
        username.setBorder(BorderFactory.createTitledBorder("Username"));
        JPasswordField password = new JPasswordField();
        password.setBorder(BorderFactory.createTitledBorder("Password"));
        JPanel panel2 = new JPanel();
        JButton b = new JButton("Přihlásit se jako zákazník");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    GUI gui = new GUI(getWidth(), getHeight(), sql, "Zakaznik", -1);
                    gui.setVisible(true);
                    dispose();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton button = new JButton("Login");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                    if (username.getText().equals("admin") && password.getText().equals("Adm1n")) {
                        try {
                            GUI gui = new GUI(getWidth(), getHeight(), sql, "Admin", -1);
                            gui.setVisible(true);
                            dispose();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        String[] passwordParts = password.getText().split("\\.", 3);
                        String[] usernameParts = username.getText().split("\\.", 3);
                        try {
                            if(passwordParts.length < 3 || usernameParts.length < 3){authErr(); return;}
                            ArrayList<String> dataZP = sql.ExecuteSelectQuery("SELECT * FROM ZAKAZNIK WHERE Jmeno = '" + passwordParts[0] + "' AND Prijmeni ='" + passwordParts[1] + "' AND ZakaznikID = '" + passwordParts[2] + "'");
                            ArrayList<String> dataSP = sql.ExecuteSelectQuery("SELECT * FROM Skladnik WHERE Jmeno = '" + passwordParts[0] + "' AND Prijmeni ='" + passwordParts[1] + "' AND SkladnikID = '" + passwordParts[2] + "'");

                            ArrayList<String> dataZU = sql.ExecuteSelectQuery("SELECT * FROM ZAKAZNIK WHERE Jmeno = '" + usernameParts[2] + "' AND Prijmeni ='" + usernameParts[1] + "' AND ZakaznikID = '" + usernameParts[0] + "'");
                            ArrayList<String> dataSU = sql.ExecuteSelectQuery("SELECT * FROM Skladnik WHERE Jmeno = '" + usernameParts[2] + "' AND Prijmeni ='" + usernameParts[1] + "' AND SkladnikID = '" + usernameParts[0] + "'");
                            if (dataZP.size() > 0 && dataZU.size() > 0) {
                                GUI gui = new GUI(getWidth(), getHeight(), sql, "Zakaznik", Integer.parseInt(passwordParts[2]));
                                gui.setVisible(true);
                                dispose();
                            } else if (dataSP.size() > 0 && dataSU.size() > 0) {
                                GUI gui = new GUI(getWidth(), getHeight(), sql, "Skladnik", Integer.parseInt(passwordParts[2]));
                                gui.setVisible(true);
                                dispose();
                            }
                            else{
                                authErr();
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                else {
                    authErr();
                }
            }
        });
        panel.add(username);
        panel.add(password);
        panel2.add(button);
        panel2.add(b);
        panel.add(panel2);
        return panel;
    }

    public void authErr(){
        JOptionPane.showMessageDialog(this, "Špatné uživatelské údaje!");
    }
}
