package GUI;

import Database.SQLOperations;
import Models.SelectedData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderProduct extends JFrame {

    private SQLOperations sql;
    private JComboBox cbSklad,cbMaterial;
    private JTextField txtProduct,txtColor,txtCena;

    public OrderProduct(int width, int height,SQLOperations sql) throws SQLException {
        super("DBS2 Project");
        this.sql = sql;
        setSize(width,height);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initGui();
        setVisible(true);
    }

    private void initGui() throws SQLException {
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(initForm(), BorderLayout.NORTH);
        add(panelMain);
    }

    private JPanel initForm() throws SQLException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        txtProduct = new JTextField("",50);
        txtProduct.setBorder(BorderFactory.createTitledBorder("Produkt"));
        txtColor = new JTextField("",50);
        txtColor.setBorder(BorderFactory.createTitledBorder("Barva"));

        String[] cbItems = new String[sql.ExecuteSelectQuery("SELECT Nazev FROM SKLAD;").size()];
        int counter = 0;
        for(int i = 0; i < sql.ExecuteSelectQuery("SELECT Nazev FROM SKLAD;").size();i++)
        {
            if(sql.ExecuteSelectQuery("SELECT Nazev FROM SKLAD;").get(i) != null) {
                cbItems[counter] = sql.ExecuteSelectQuery("SELECT Nazev FROM SKLAD;").get(i);
                counter++;
            }
        }

        counter = 0;
        String[] cbItemsm = new String[sql.ExecuteSelectQuery("SELECT Nazev_Materialu FROM Material;").size()];
        for(int i = 0; i < sql.ExecuteSelectQuery("SELECT Nazev_Materialu FROM Material;").size();i++)
        {
            if(sql.ExecuteSelectQuery("SELECT Nazev_Materialu FROM Material;").get(i) != null) {
                cbItemsm[counter] = sql.ExecuteSelectQuery("SELECT Nazev_Materialu FROM Material;").get(i);
                counter++;
            }
        }

        cbSklad = new JComboBox(cbItems);
        cbSklad.setBorder(BorderFactory.createTitledBorder("Sklad"));
        cbSklad.setSelectedIndex(1);

        cbMaterial = new JComboBox(cbItemsm);
        cbMaterial.setBorder(BorderFactory.createTitledBorder("Material"));
        cbMaterial.setSelectedIndex(1);


        txtCena = new JTextField("",50);
        txtCena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String value = txtCena.getText();
                int l = value.length();
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    txtCena.setEditable(true);
                } else {
                    txtCena.setEditable(false);
                }
            }
        });
        txtCena.setBorder(BorderFactory.createTitledBorder("Cena"));




        JButton jButton = new JButton("Vytvořit objednávku");

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    createOrder();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.add(txtProduct);
        panel.add(txtColor);
        panel.add(cbSklad);
        panel.add(cbMaterial);
        panel.add(txtCena);


        panel.add(jButton);
        return panel;
    }

    public void createOrder() throws SQLException {
        if(!emptyTextFields()){
            int sklad = cbSklad.getSelectedIndex()+1;
            int material = cbMaterial.getSelectedIndex()+1;
            sql.ExecuteQuery("Insert into PRODUKT (Nazev,Barva,Cena,FK_Material,FK_SKLAD,FK_STAV) VALUES ('"+txtProduct.getText()+"','"+txtColor.getText()+"',"+Integer.parseInt(txtCena.getText())+","+material+","+sklad+",1);");
            JOptionPane.showMessageDialog(this, "Produkt byl pridan do databaze");
        }
        else {
            JOptionPane.showMessageDialog(this, "Vyplnte vsechny textboxy");
        }
    }

    public boolean emptyTextFields(){
        if(txtProduct.getText().isEmpty() || txtColor.getText().isEmpty()){
            return true;
        }
        return false;
    }
}