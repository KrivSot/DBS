package GUI;

import Database.SQLOperations;
import Models.SelectedData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

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
        JButton button = new JButton("Login");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                    String[] arrOfStr = password.getText().split("\\.", 3);
                    try {
                        ArrayList<String> dataZ = sql.ExecuteSelectQuery("SELECT * FROM ZAKAZNIK WHERE Jmeno = '" + arrOfStr[0] + "' AND Prijmeni ='" + arrOfStr[1] + "' AND ZakaznikID = '" + arrOfStr[2] + "'");
                        ArrayList<String> dataS = sql.ExecuteSelectQuery("SELECT * FROM Skladnik WHERE Jmeno = '" + arrOfStr[0] + "' AND Prijmeni ='" + arrOfStr[1] + "' AND SkladnikID = '" + arrOfStr[2] + "'");
                        if (dataZ.size() > 0) {
                            GUI gui = new GUI(getWidth(), getHeight(), sql, "Zakaznik", Integer.parseInt(arrOfStr[2]));
                            gui.setVisible(true);
                            hide();
                            return;
                        }
                        else if(dataS.size() > 0){
                            GUI gui = new GUI(getWidth(), getHeight(), sql, "Skladnik", Integer.parseInt(arrOfStr[2]));
                            gui.setVisible(true);
                            hide();
                            return;
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if(username.getText().equals("admin") && password.getText().equals("Adm1n")){
                    try {
                        GUI gui = new GUI(getWidth(),getHeight(),sql,"Admin",-1);
                        gui.setVisible(true);
                        hide();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    try {
                        GUI gui = new GUI(getWidth(),getHeight(),sql,"Skladnik",13);
                        gui.setVisible(true);
                        hide();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        panel.add(username);
        panel.add(password);
        panel.add(button);
        return panel;
    }
}
