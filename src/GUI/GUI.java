package GUI;

import Database.SQLOperations;
import Models.SelectedData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GUI extends JFrame {
    private JTable jt;
    private String column[]= {"ID","TIME"};
    private String[] tableListAdmin = {"Adresa","Dodavatel","Kontakt","Material","Mena","Objednavka","Produkt","Ridic","Sklad","Skladnik","Stavy","Vozidla","Zakaznik"};
    private String[] tableListSkladnik = {"Produkt"};
    private String table;
    private JPopupMenu popup;
    private DefaultTableModel model;
    private String selectedID = "-1";
    JComboBox tableCombobox;
    private SQLOperations sql;
    private String user;
    private final SelectedData selectedData;
    private int userID;
    private boolean orderingProduct;
    boolean doubleclickEnabled = true;
    public GUI(int width, int height, SQLOperations sql, String user, int userID) throws SQLException {
        super("DBS2 Project");
        this.table = "Produkt";
        this.sql = sql;
        this.user = user;
        this.userID = userID;
        orderingProduct = true;
        //sql.tryConnection();
        setSize(width,height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initGui();
        selectedData = new SelectedData(sql.getTableColumns(table),sql.getColumnsTypes(table),table,selectedID,sql);
        setVisible(true);
    }

    public void initGui() throws SQLException {
        JPanel panelMain = new JPanel(new BorderLayout());
        if(!user.equals("Zakaznik")){
            panelMain.add(initSeachPanel(), BorderLayout.NORTH);
        }
        panelMain.add(initTablePanel(), BorderLayout.CENTER);
        add(panelMain);
        setJMenuBar(initMenuBar());
    }

    public JPanel initTablePanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());

        if(!user.equals("Zakaznik")) {
            if(user.equals("Admin")) {
                popup = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem("Add data");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            updateSelectedData();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        OperationsForm add = new OperationsForm(getWidth(), getHeight(), sql, Mode.ADD, selectedData);
                    }
                });
                popup.add(menuItem);
                menuItem = new JMenuItem("Delete data");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            updateSelectedData();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        OperationsForm delete = new OperationsForm(getWidth(), getHeight(), sql, Mode.DELETE, selectedData);
                    }
                });
                popup.add(menuItem);
                menuItem = new JMenuItem("Edit data");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            updateSelectedData();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        OperationsForm edit = new OperationsForm(getWidth(), getHeight(), sql, Mode.EDIT, selectedData);
                    }
                });
                popup.add(menuItem);
            }
            else {
                popup = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem("Objednávka Dokončena");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            updateSelectedData();
                            String produktID = sql.ExecuteSelectQuery("Select FK_Produkt from OBJEDNAVKA where ObjednavkaID = "+selectedID+";").get(0);
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            sql.ExecuteQuery("UPDATE Objednavka\n" +
                                    "SET FK_Stav = 5, DatumPrevzeti = '"+dtf.format(now)+"' \n" +
                                    "WHERE ObjednavkaID = "+selectedID+";");
                            sql.ExecuteQuery("UPDATE PRODUKT SET FK_STAV = 2 WHERE ProduktID = "+Integer.parseInt(produktID)+";");
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                popup.add(menuItem);
                menuItem = new JMenuItem("Objednávka Zpracovávána");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            updateSelectedData();
                            String produktID = sql.ExecuteSelectQuery("Select FK_Produkt from OBJEDNAVKA where ObjednavkaID = "+selectedID+";").get(0);
                            sql.ExecuteQuery("UPDATE Objednavka\n" +
                                    "SET FK_Stav = 4, DatumPrevzeti = '' \n" +
                                    "WHERE ObjednavkaID = "+selectedID+";");
                            sql.ExecuteQuery("UPDATE PRODUKT SET FK_STAV = 4 WHERE ProduktID = "+Integer.parseInt(produktID)+";");
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                popup.add(menuItem);
            }
        }

        if(user.equals("Zakaznik")){
            model = sql.getDatafromView("SELECT A.ProduktID AS 'ID Produktu', A.Nazev AS 'Název', A.Barva AS 'Barva', B.Nazev_materialu AS 'Materiál', C.Nazev AS 'Sklad', D.Stav AS 'Stav', A.Cena AS 'Cena' \n" +
                    "FROM Produkt A, Material B, Sklad C, Stavy D\n" +
                    "WHERE B.MaterialID = A.FK_Material AND C.SkladID = A.FK_Sklad AND D.StavyID = A.FK_Stav AND A.FK_Stav = 1");
        }
        else {model = sql.getData(table);}
        jt=new JTable();
        jt.setModel(model);
        jt.setBounds(30,40,getWidth(),getHeight());
        JScrollPane sp=new JScrollPane(jt);
        sp.setBounds(30,40,getWidth(),getHeight());
            jt.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    selectedID = jt.getValueAt(jt.getSelectedRow(),0).toString();
                    try {
                        updateSelectedData();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    if(!user.equals("Zakaznik")){
                        if(e.isPopupTrigger()) {
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
                @Override
                public void mouseClicked(MouseEvent e){
                    if (e.getClickCount() == 2 && !e.isConsumed() && doubleclickEnabled) {
                        e.consume();
                        try {
                            if(!user.equals("Admin")) {
                                if (orderingProduct) {
                                    CreateOrder c = new CreateOrder(getWidth(), getHeight(), sql, selectedData, user);
                                } else {
                                    jt.setModel(sql.getDatafromView("SELECT A.ObjednavkaID AS 'ID Objednávky', A.DatumVytvoreniObjednavky AS 'Datum vytvoření', A.DatumPrevzeti AS 'Datum převzetí', A.Mnozstvi AS 'Množství', D.Nazev AS 'Název produktu', E.Stav AS 'Stav', A.Cena AS 'Cena', C.Typ_meny AS 'Měna', F.Jmeno AS 'Jméno', F.Prijmeni AS 'Příjmení', B.Ulice AS 'Ulice', B.Cislo_popisne AS 'ČP', B.Mesto AS 'Město', B.PSC AS 'PSČ' \n" +
                                            "FROM Objednavka A, Adresa B, Mena C, Produkt D, Stavy E, Zakaznik F\n" +
                                            "WHERE B.AdresaID = A.FK_Adresa AND C.MenaID = A.FK_Mena AND D.ProduktID = A.FK_Produkt AND E.StavyID = A.FK_Stav AND A.FK_Zakaznik = F.ZakaznikID AND A.ObjednavkaID = " + selectedID + ";"));
                                }
                            }
                            } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });
        sp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(!Objects.equals(selectedID, "-1")) selectedID = jt.getValueAt(jt.getSelectedRow(),0).toString();
                try {
                    updateSelectedData();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                if(e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        panel.add(sp);

        return panel;
    }

    public JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Zobraz");
        JMenu menu2 = new JMenu("Objednat");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);
        if(user.equals("Skladnik")) menuBar.add(menu2);

        JMenuItem menuItem = new JMenuItem("Zobrazit objednávky",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open Image");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderingProduct = false;
                try {
                    if (user.equals("Zakaznik")) {
                        doubleclickEnabled = false;
                        model = sql.getDatafromView("SELECT A.ObjednavkaID AS 'ID Objednávky', A.DatumVytvoreniObjednavky AS 'Datum vytvoření', A.DatumPrevzeti AS 'Datum převzetí', A.Mnozstvi AS 'Množství', D.Nazev AS 'Název produktu', E.Stav AS 'Stav', A.Cena AS 'Cena', C.Typ_meny AS 'Měna', F.Jmeno AS 'Jméno', F.Prijmeni AS 'Příjmení', B.Ulice AS 'Ulice', B.Cislo_popisne AS 'ČP', B.Mesto AS 'Město', B.PSC AS 'PSČ' \n" +
                                "FROM Objednavka A, Adresa B, Mena C, Produkt D, Stavy E, Zakaznik F\n" +
                                "WHERE B.AdresaID = A.FK_Adresa AND C.MenaID = A.FK_Mena AND D.ProduktID = A.FK_Produkt AND E.StavyID = A.FK_Stav AND A.FK_Zakaznik = F.ZakaznikID AND A.FK_Zakaznik = " + userID + ";");
                    } else if (user.equals("Skladnik")) {
                        doubleclickEnabled = false;
                        model = sql.getDatafromView("SELECT A.ObjednavkaID AS 'ID Objednávky', A.DatumVytvoreniObjednavky AS 'Datum vytvoření', A.DatumPrevzeti AS 'Datum převzetí', A.Mnozstvi AS 'Množství', D.Nazev AS 'Název produktu', E.Stav AS 'Stav', A.Cena AS 'Cena', C.Typ_meny AS 'Měna', F.Jmeno AS 'Jméno', F.Prijmeni AS 'Příjmení', B.Ulice AS 'Ulice', B.Cislo_popisne AS 'ČP', B.Mesto AS 'Město', B.PSC AS 'PSČ' \n" +
                                "FROM Objednavka A, Adresa B, Mena C, Produkt D, Stavy E, Zakaznik F\n" +
                                "WHERE B.AdresaID = A.FK_Adresa AND C.MenaID = A.FK_Mena AND D.ProduktID = A.FK_Produkt AND E.StavyID = A.FK_Stav AND A.FK_Zakaznik = F.ZakaznikID AND (A.FK_Stav = 4 OR A.FK_Stav = 5);");
                    } else {
                        doubleclickEnabled = false;
                        model = sql.getDatafromView("SELECT A.ObjednavkaID AS 'ID Objednávky', A.DatumVytvoreniObjednavky AS 'Datum vytvoření', A.DatumPrevzeti AS 'Datum převzetí', A.Mnozstvi AS 'Množství', D.Nazev AS 'Název produktu', E.Stav AS 'Stav', A.Cena AS 'Cena', C.Typ_meny AS 'Měna', F.Jmeno AS 'Jméno', F.Prijmeni AS 'Příjmení', B.Ulice AS 'Ulice', B.Cislo_popisne AS 'ČP', B.Mesto AS 'Město', B.PSC AS 'PSČ' \n" +
                                "FROM Objednavka A, Adresa B, Mena C, Produkt D, Stavy E, Zakaznik F\n" +
                                "WHERE B.AdresaID = A.FK_Adresa AND C.MenaID = A.FK_Mena AND D.ProduktID = A.FK_Produkt AND E.StavyID = A.FK_Stav AND A.FK_Zakaznik = F.ZakaznikID;");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                jt.setModel(model);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Zobraz dostupné produkty",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open Image");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateSelectedData();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    doubleclickEnabled = true;
                    orderingProduct = true;
                    if (user.equals("Zakaznik")) {
                        model = sql.getDatafromView("SELECT A.ProduktID AS 'ID Produktu', A.Nazev AS 'Název', A.Barva AS 'Barva', B.Nazev_materialu AS 'Materiál', C.Nazev AS 'Sklad', D.Stav AS 'Stav', A.Cena AS 'Cena' \n" +
                                "FROM Produkt A, Material B, Sklad C, Stavy D\n" +
                                "WHERE B.MaterialID = A.FK_Material AND C.SkladID = A.FK_Sklad AND D.StavyID = A.FK_Stav AND A.FK_Stav = 1");
                    } else
                        model = sql.getDatafromView("SELECT A.ProduktID AS 'ID Produktu', A.Nazev AS 'Název', A.Barva AS 'Barva', B.Nazev_materialu AS 'Materiál', C.Nazev AS 'Sklad', D.Stav AS 'Stav', A.Cena AS 'Cena' \n" +
                                "FROM Produkt A, Material B, Sklad C, Stavy D\n" +
                                "WHERE B.MaterialID = A.FK_Material AND C.SkladID = A.FK_Sklad AND D.StavyID = A.FK_Stav");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                jt.setModel(model);
            }
        });
        menu.add(menuItem);
        if(user.equals("Skladnik")){
            menuItem = new JMenuItem("Zobraz nové objednávky",
                    KeyEvent.VK_T);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_3, ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription(
                    "Open Image");
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        updateSelectedData();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        doubleclickEnabled = true;
                        orderingProduct = false;
                        model = sql.getDatafromView("SELECT * FROM ZaznamyObjednavek;");
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    jt.setModel(model);
                }
            });
            menu.add(menuItem);

            menuItem = new JMenuItem("Objednat Produkt",
                    KeyEvent.VK_T);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_3, ActionEvent.CTRL_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription(
                    "Open Image");
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        OrderProduct op = new OrderProduct(getWidth(),getHeight(),sql);
                        op.setVisible(true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            menu2.add(menuItem);
        }
        return menuBar;
    }

    public JPanel initSeachPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField jTxtField = new JTextField("",50);
        if(user.equals("Admin")){
            this.tableCombobox = new JComboBox(this.tableListAdmin);
        }
        else {
            this.tableCombobox = new JComboBox(this.tableListSkladnik);
        }
        this.table = tableCombobox.getSelectedItem().toString();
        tableCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table = tableCombobox.getSelectedItem().toString();
                try {
                    model = sql.getData(table);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                jt.setModel(model);
            }
        });
        jTxtField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
            }
        });
        //panel.add(jTxtField);
        panel.add(tableCombobox);
        return panel;
    }

    public void updateSelectedData() throws SQLException {
        selectedData.setSelectedID(selectedID);
        selectedData.setCollumsName(sql.getTableColumns(table));
        selectedData.setCollumDataTypes(sql.getColumnsTypes(table));
        selectedData.setTable(table);
        selectedData.getSQLData();
        selectedData.setIndexesOfFK();
    }
}
