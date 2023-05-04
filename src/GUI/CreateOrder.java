package GUI;

import Database.SQLOperations;
import Models.SelectedData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CreateOrder extends JFrame {

    private SQLOperations sql;
    private SelectedData selectedData;
    private String user;
    ArrayList<String> zakaznikInfo,kontaktInfo,adresaInfo = new ArrayList<>();
    private JComboBox cbMena;
    private JTextField txtName,txtSurname,txtTelefon,txtEmail,txtProduct,txtColor,txtSklad,txtMaterial,txtPC,txtMesto,txtPSC,txtUlice,txtCena;

    public CreateOrder(int width, int height,SQLOperations sql,SelectedData selectedData, String user) throws SQLException {
        super("DBS2 Project");
        this.sql = sql;
        this.selectedData = selectedData;
        this.user = user;
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
        txtName = new JTextField("",50);
        txtName.setBorder(BorderFactory.createTitledBorder("Jméno"));


        txtSurname = new JTextField("",50);
        txtSurname.setBorder(BorderFactory.createTitledBorder("Příjmení"));

        txtTelefon = new JTextField("",50);
        txtTelefon.setBorder(BorderFactory.createTitledBorder("Telefonní číslo"));
        txtTelefon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String value = txtTelefon.getText();
                int l = value.length();
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    txtTelefon.setEditable(true);
                } else {
                    txtTelefon.setEditable(false);
                }
            }
        });

        txtEmail = new JTextField("",50);
        txtEmail.setBorder(BorderFactory.createTitledBorder("Email"));
        txtProduct = new JTextField("",50);
        txtProduct.setBorder(BorderFactory.createTitledBorder("Produkt"));
        txtProduct.setText(selectedData.getData().get(2));
        txtProduct.setEditable(false);
        txtColor = new JTextField("",50);
        txtColor.setBorder(BorderFactory.createTitledBorder("Barva"));
        txtColor.setEditable(false);
        txtColor.setText(selectedData.getData().get(1));
        txtSklad = new JTextField("",50);
        txtSklad.setBorder(BorderFactory.createTitledBorder("Sklad"));
        txtSklad.setEditable(false);
        txtSklad.setText(sql.ExecuteSelectQuery("SELECT * FROM SKLAD WHERE SkladID = '"+selectedData.getData().get(5)+"';").get(2));
        txtMaterial = new JTextField("",50);
        txtMaterial.setEditable(false);
        txtMaterial.setBorder(BorderFactory.createTitledBorder("Material"));
        txtMaterial.setText(sql.ExecuteSelectQuery("SELECT * FROM Material WHERE MaterialID = '"+selectedData.getData().get(5)+"';").get(1));

        txtPSC = new JTextField("",50);
        txtPSC.setBorder(BorderFactory.createTitledBorder("PSC"));
        txtPSC.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String value = txtPSC.getText();
                int l = value.length();
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    txtPSC.setEditable(true);
                } else {
                    txtPSC.setEditable(false);
                }
            }
        });

        txtMesto = new JTextField("",50);
        txtMesto.setBorder(BorderFactory.createTitledBorder("Mesto"));

        txtUlice = new JTextField("",50);
        txtUlice.setBorder(BorderFactory.createTitledBorder("Ulice"));

        txtPC = new JTextField("",50);
        txtPC.setBorder(BorderFactory.createTitledBorder("Cislo popisne"));
        txtPC.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String value = txtPC.getText();
                int l = value.length();
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    txtPC.setEditable(true);
                } else {
                    txtPC.setEditable(false);
                }
            }
        });

        String[] cbItems = new String[sql.ExecuteSelectQuery("SELECT * FROM MENA;").size()/2];
        int counter = 0;
        for(int i = 0; i < sql.ExecuteSelectQuery("SELECT * FROM MENA;").size();i+=2)
        {
            if(sql.ExecuteSelectQuery("SELECT * FROM MENA;").get(i) != null) {
                cbItems[counter] = sql.ExecuteSelectQuery("SELECT * FROM MENA;").get(i);
                counter++;
            }
        }

        cbMena = new JComboBox(cbItems);
        cbMena.setBorder(BorderFactory.createTitledBorder("Způsob platby"));
        cbMena.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double cena;
                switch(cbMena.getSelectedItem().toString()){
                    case "CZK": txtCena.setText(selectedData.getData().get(3)); break;
                    case "USD":cena = Double.parseDouble(selectedData.getData().get(3))/21;txtCena.setText(String.valueOf(cena));break;
                    case "EUR":cena = Double.parseDouble(selectedData.getData().get(3))/24;txtCena.setText(String.valueOf(cena));break;
                    case "GBP":cena = Double.parseDouble(selectedData.getData().get(3))/26;txtCena.setText(String.valueOf(cena));break;
                    default: break;
                }
            }
        });


        txtCena = new JTextField("",50);
        txtCena.setEditable(false);
        txtCena.setBorder(BorderFactory.createTitledBorder("Cena"));
        cbMena.setSelectedIndex(1);



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

        panel.add(txtName);
        panel.add(txtSurname);
        panel.add(txtTelefon);
        panel.add(txtEmail);
        panel.add(txtProduct);
        panel.add(txtColor);
        panel.add(txtSklad);
        panel.add(txtMaterial);
        panel.add(txtPSC);
        panel.add(txtMesto);
        panel.add(txtUlice);
        panel.add(txtPC);
        panel.add(cbMena);
        panel.add(txtCena);
        panel.add(jButton);
        return panel;
    }

    public void createOrder() throws SQLException {

        zakaznikInfo = sql.ExecuteSelectQuery("SELECT * FROM Zakaznik WHERE Jmeno = '"+txtName.getText()+"' AND Prijmeni = '"+txtSurname.getText()+"';");
        kontaktInfo = sql.ExecuteSelectQuery("SELECT * FROM Kontakt WHERE Email = '"+txtEmail.getText()+"' AND Telefon = '"+txtTelefon.getText()+"'");
        adresaInfo = sql.ExecuteSelectQuery("SELECT * FROM ADRESA WHERE Cislo_popisne ='"+txtPC.getText()+"' AND Mesto='"+txtMesto.getText()+"' AND PSC ='"+txtPSC.getText()+"' AND ULICE = '"+txtUlice.getText()+"'");

        if(!emptyTextFields()){
            //Jestli existuje zakaznik v databazi
            if(zakaznikInfo.size() > 0){
                //Jestli existuje kontakt
                if(kontaktInfo.size() > 0){
                    sql.ExecuteQuery("UPDATE ZAKAZNIK SET FK_Kontakt = '"+kontaktInfo.get(0)+"' WHERE ZakaznikID = '"+zakaznikInfo.get(0)+"';");
                    zakaznikInfo = sql.ExecuteSelectQuery("SELECT * FROM Zakaznik WHERE Jmeno = '"+txtName.getText()+"' AND Prijmeni = '"+txtSurname.getText()+"';");
                }
                else {
                    //Vytvoří kontakt a přířadí k zákazníkovi
                    sql.ExecuteQuery("INSERT INTO KONTAKT (Email, Telefon) VALUES('"+txtEmail.getText()+"', '"+txtTelefon.getText()+"');");
                    kontaktInfo = sql.ExecuteSelectQuery("SELECT * FROM Kontakt WHERE Email = '"+txtEmail.getText()+"' AND Telefon = '"+txtTelefon.getText()+"'");
                    sql.ExecuteQuery("UPDATE ZAKAZNIK SET FK_Kontakt = '"+kontaktInfo.get(0)+"' WHERE ZakaznikID = '"+zakaznikInfo.get(0)+"';");
                    zakaznikInfo = sql.ExecuteSelectQuery("SELECT * FROM Zakaznik WHERE Jmeno = '"+txtName.getText()+"' AND Prijmeni = '"+txtSurname.getText()+"';");
                }
            }
            //Jestli existuje kontakt ale zakaznik neni
            else if(kontaktInfo.size() > 0) {
                sql.ExecuteQuery("INSERT INTO ZAKAZNIK (Jmeno, Prijmeni, FK_Kontakt) VALUES('"+txtName.getText()+"', '"+txtSurname.getText()+"', '"+kontaktInfo.get(0)+"');");
                zakaznikInfo = sql.ExecuteSelectQuery("SELECT * FROM Zakaznik WHERE Jmeno = '"+txtName.getText()+"' AND Prijmeni = '"+txtSurname.getText()+"';");
            }
            //Neexistuje ani jeden zaznam v databazi
            else {
                sql.ExecuteQuery("INSERT INTO KONTAKT (Email, Telefon) VALUES('"+txtEmail.getText()+"', '"+txtTelefon.getText()+"');");
                kontaktInfo = sql.ExecuteSelectQuery("SELECT * FROM Kontakt WHERE Email = '"+txtEmail.getText()+"' AND Telefon = '"+txtTelefon.getText()+"'");
                sql.ExecuteQuery("INSERT INTO ZAKAZNIK (Jmeno, Prijmeni, FK_Kontakt) VALUES('"+txtName.getText()+"', '"+txtSurname.getText()+"', '"+kontaktInfo.get(0)+"');");
                zakaznikInfo = sql.ExecuteSelectQuery("SELECT * FROM Zakaznik WHERE Jmeno = '"+txtName.getText()+"' AND Prijmeni = '"+txtSurname.getText()+"';");
            }
            //Adresa neexistuje v databazi
            if(!(adresaInfo.size() > 0))
            {
                sql.ExecuteQuery("INSERT INTO Adresa (Cislo_Popisne, Mesto, PSC, Ulice) VALUES('"+txtPC.getText()+"', '"+txtMesto.getText()+"', '"+txtPSC.getText()+"', '"+txtUlice.getText()+"');");
                adresaInfo = sql.ExecuteSelectQuery("SELECT * FROM ADRESA WHERE Cislo_popisne ='"+txtPC.getText()+"' AND Mesto='"+txtMesto.getText()+"' AND PSC ='"+txtPSC.getText()+"' AND ULICE = '"+txtUlice.getText()+"'");
            }

            //Vytvoreni objednavky
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            //System.out.println(dtf.format(now));

            sql.ExecuteQuery("INSERT INTO Objednavka(cena,datumprevzeti,Datumvytvoreniobjednavky,mnozstvi,fk_adresa,fk_mena,fk_produkt,fk_stav,fk_zakaznik) " +
                    "Values('"+txtCena.getText()+"', '','"+dtf.format(now)+"',1 ,"+adresaInfo.get(0)+", '"+cbMena.getSelectedItem().toString()+"', "+selectedData.getData().get(0)+",4,"+zakaznikInfo.get(0)+");");
            sql.ExecuteQuery("UPDATE PRODUKT SET FK_STAV = 4 WHERE ProduktID = "+selectedData.getData().get(0)+";");
            JOptionPane.showMessageDialog(this, "Objednavka byla vytvorena váš kód k objednávkám je: "+zakaznikInfo.get(1)+"."+zakaznikInfo.get(2)+"."+zakaznikInfo.get(0));
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this, "Vyplnte vsechny textboxy");
        }
    }

    public boolean emptyTextFields(){
        if(txtName.getText().isEmpty() || txtSurname.getText().isEmpty() || txtTelefon.getText().isEmpty() || txtEmail.getText().isEmpty() || txtProduct.getText().isEmpty() ||
                txtColor.getText().isEmpty() || txtSklad.getText().isEmpty() || txtMaterial.getText().isEmpty() || txtPC.getText().isEmpty() || txtMesto.getText().isEmpty()
                || txtUlice.getText().isEmpty() || txtPSC.getText().isEmpty()){
            return true;
        }
        return false;
    }
}