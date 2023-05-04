package Database;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SQLOperations {

    private final String connectionString = "jdbc:sqlite:database.db";

    private String sql = ""; //zde se uloží SQL příkaz
    private Connection cnn; // Vytváří připojení do databáze
    public SQLOperations(){
    }

    //Snad to pujde
    public String[] getTableColumns(String table) throws SQLException {
        //Class.forName("com.mysql.jdbc.Driver").newInstance ();
        cnn = DriverManager.getConnection(connectionString);
        Statement stmt = cnn.createStatement();
        sql = "SELECT * from "+table+";";
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        String[] columns = new String[rsMetaData.getColumnCount()];
        for(int i = 0;i<rsMetaData.getColumnCount();i++)
        {
            columns[i] = rsMetaData.getColumnName(i+1);
        }
        cnn.close();
        return columns;
    }

    public String[] getColumnsTypes(String table) throws SQLException {
        cnn = DriverManager.getConnection(connectionString);
        Statement stmt = cnn.createStatement();
        sql = "Select * From "+table+";";
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        String[] columns = new String[rsMetaData.getColumnCount()];
        for(int i = 0;i<rsMetaData.getColumnCount();i++)
        {
            columns[i] = rsMetaData.getColumnTypeName(i+1); //První záznam v rsMetaData je na místě 1 a ne 0 ..... Proč ????
        }
        cnn.close();
        return columns;
    }

    public void ExecuteQuery(String sql) throws SQLException {
        Connection cnn = DriverManager.getConnection(connectionString);
        Statement statement = cnn.createStatement();
        statement.executeUpdate(sql);
        cnn.close();
    }

    public ArrayList<String> ExecuteSelectQuery(String sql) throws SQLException {
        ArrayList<String> results = new ArrayList<>();
        Connection cnn = DriverManager.getConnection(connectionString);
        Statement statement = cnn.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        int collumCount = rs.getMetaData().getColumnCount();
        while(rs.next()){
            int counter = 1;
            for(int i = 0; i < collumCount;i++)
            {
                results.add(rs.getString(counter));
                counter++;
            }
        }
        cnn.close();
        return results;
    }

    public void tryConnection() throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
            cnn = DriverManager.getConnection("jdbc:sqlite:database.db");
            //ExecuteQuery("Create Table ZaznamyObjednavek (ID INT NOT NULL, CAS TEXT NOT NULL)");
            /*ExecuteQuery("CREATE TRIGGER smazZaznam AFTER UPDATE   \n" +
                    "ON Objednavka  \n" +
                    "BEGIN  \n" +
                    "DELETE FROM ZAZNAMYOBJEDNAVEK WHERE new.ObjednavkaID = ID;  \n" +
                    "END;  \n");*/
           /* ExecuteQuery("DROP TRIGGER zaznam;\n");
            ExecuteQuery("CREATE TRIGGER zaznam AFTER INSERT ON Objednavka BEGIN INSERT INTO ZaznamyObjednavek(ID,CAS) VALUES(new.ObjednavkaID, datetime('now')); END;");*/
            //ExecuteQuery("DROP TABLE MATERIAL;");
            /*ExecuteQuery("CREATE TABLE [Material]\n" +
                    "(\n" +
                    "\t[MaterialID] int NOT NULL,\n" +
                    "\t[Nazev_materialu] varchar(50) NULL,\n" +
                    "\t[FK_Dodavatel] int NOT NULL,\n" +
                    "FOREIGN KEY (FK_Dodavatel) REFERENCES Dodavatel(DodavatelID)"+
                    ")");*/
            /*ExecuteQuery("INSERT INTO Material(materialid,nazev_materialu,fk_dodavatel)\n" +
                    "VALUES(1,'dřevěné latě',4),\n" +
                    "(2,'plast',3),\n" +
                    "(3,'litina',2),\n" +
                    "(4,'beton',1),\n" +
                    "(5,'dřevotříska',4),\n" +
                    "(6,'dřevěná prkna',4),\n" +
                    "(7,'asfalt',3);");*/
            /*ExecuteQuery("DROP TABLE PRODUKT;");
            ExecuteQuery("CREATE TABLE Produkt\n" +
                    "(\n" +
                    "\tProduktID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tBarva varchar(50) NULL,\n" +
                    "\tNazev varchar(50) NULL,\n" +
                    "\tCena int NOT NULL,\n" +
                    "\tFK_Material int NOT NULL,\n" +
                    "\tFK_Sklad int NOT NULL,\n" +
                    "\tFK_Stav int NOT NULL,\n" +
                    "\tFOREIGN KEY (FK_Stav) REFERENCES Stavy(StavyID),\n" +
                    "\tFOREIGN KEY (FK_Sklad) REFERENCES Sklad(SkladID),\n" +
                    "\tFOREIGN KEY (FK_Material) REFERENCES Material(MaterialID)\n" +
                    ");");
            ExecuteQuery(
                    "INSERT INTO Produkt(produktid,barva,nazev, cena, fk_material,fk_sklad,fk_stav)\n" +
                    "VALUES(1,'lískově oříšková','stylová jídelní židle', 500,6,1,1),\n" +
                    "(2,'lískově oříšková','stylový jídelní stůl', 1500,6,1,1),\n" +
                    "(3,'lískově oříšková','stylová jídelní lavice', 1300,6,1,1),\n" +
                    "(4,'Sněhově Bílá','švihácká zahradní židle', 1200,2,2,1),\n" +
                    "(5,'Sněhově Bílá','společenský zahradní stoleček', 2500,2,2,1),\n" +
                    "(6,'Sněhově Bílá','zahradní teréní lavice',4500,2,2,1),\n" +
                    "(7,'Kovově Stříbrná','moderní pracovní židle', 7500,3,3,1),\n" +
                    "(8,'Kovově Stříbrná','pracovní stůl', 4300,3,3,1),\n" +
                    "(9,'Betonová/komunistická Šedá','módní venkovní křeslo', 8000,4,3,1),\n" +
                    "(10,'Betonová/komunistická Šedá','solidní tvrzená lavice', 9000,4,3,1); "); /*+
                            "INSERT INTO Objednavka(objednavkaid,cena,datumprevzeti,Datumvytvoreniobjednavky,mnozstvi,fk_adresa,fk_mena,fk_produkt,fk_stav,fk_zakaznik)\n" +
                            "VALUES(1,1500,'2023-04-18 16:03:30','2023-04-17 11:41:15',1,1,'CZK',1,5,6),\n" +
                            "(2,4500,'2023-04-18 17:03:30','2023-04-17 10:41:15',3,2,'CZK',1,5,7),\n" +
                            "(3,2000,null,'2023-04-18 09:22:15',1,3,'CZK',2,4,8),\n" +
                            "(4,4000,null,'2023-04-18 09:40:59',2,4,'CZK',2,4,9),\n" +
                            "(5,1800,null,'2023-04-18 12:41:15',1,5,'CZK',3,4,10),\n" +
                            "(6,1800,null,'2023-04-18 12:52:14',1,6,'CZK',3,4,11),\n" +
                            "(7,2000,null,'2023-04-18 13:01:00',4,7,'CZK',4,4,12),\n" +
                            "(8,750,null,'2023-04-18 13:54:34',1,8,'CZK',5,4,13),\n" +
                            "(9,200,null,'2023-04-18 15:58:00',2,9,'EUR',9,3,14),\n" +
                            "(10,150,null,'2023-04-18 16:14:53',1,10,'USD',10,3,15);");*/
            /*ExecuteQuery("CREATE TABLE Adresa1(\tAdresaID INTEGER PRIMARY KEY autoincrement,\tCislo_popisne int NULL,\tMesto varchar(50) NULL,\tPsc int NULL,\tUlice varchar(50) NULL);");
            ExecuteQuery("CREATE TABLE Kontakt1\n" +
                    "(\n" +
                    "\tKontaktID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tEmail varchar(50) NULL,\n" +
                    "\tTelefon int NULL\n" +
                    ");");
            ExecuteQuery("CREATE TABLE Dodavatel1(\tDodavatelID INTEGER PRIMARY KEY autoincrement,\tNazev varchar(50) NULL,FK_Kontakt int NOT NULL, \tFOREIGN KEY (FK_Kontakt) REFERENCES Kontakt(KontaktID)\n);");
            ExecuteQuery("CREATE TABLE [Material1]\n" +
                    "(\n" +
                    "\t[MaterialID] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t[Nazev_materialu] varchar(50) NULL,\n" +
                    "\t[FK_Dodavatel] int NOT NULL,\n" +
                    "FOREIGN KEY (FK_Dodavatel) REFERENCES Dodavatel(DodavatelID)"+
                    ")");
            ExecuteQuery("CREATE TABLE Mena1\n" +
                    "(\n" +
                    "\tMenaID int PRIMARY KEY,\n" +
                    "\tTyp_meny varchar(50) NULL\n" +
                    ");");
            ExecuteQuery("CREATE TABLE Objednavka1\n" +
                    "(\n" +
                    "\tObjednavkaID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tCena int NULL,\n" +
                    "\tDatumprevzeti datetime NULL,\n" +
                    "\tDatumvytvoreniobjednavky datetime NULL,\n" +
                    "\tMnozstvi int NULL,\n" +
                    "\tFK_Adresa int NOT NULL,\n" +
                    "\tFK_Mena int NOT NULL,\n" +
                    "\tFK_Produkt int NOT NULL,\n" +
                    "\tFK_Stav int NOT NULL,\n" +
                    "\tFK_Zakaznik int NOT NULL,\n" +
                    "\tFOREIGN KEY (FK_Adresa) REFERENCES Adresa(AdresaID),\n" +
                    "\tFOREIGN KEY (FK_Mena) REFERENCES Mena(MenaID),\n" +
                    "\tFOREIGN KEY (FK_Produkt) REFERENCES Produkt(ProduktID),\n" +
                    "\tFOREIGN KEY (FK_Stav) REFERENCES Stavy(StavyID),\n" +
                    "\tFOREIGN KEY (FK_Zakaznik) REFERENCES Zakaznik(ZakaznikID)\n" +
                    ");");
            ExecuteQuery("CREATE TABLE Produkt1\n" +
                    "(\n" +
                    "\tProduktID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tBarva varchar(50) NULL,\n" +
                    "\tNazev varchar(50) NULL,\n" +
                    "\tFK_Material int NOT NULL,\n" +
                    "\tFK_Sklad int NOT NULL,\n" +
                    "\tFK_Stav int NOT NULL,\n" +
                    "\tFOREIGN KEY (FK_Stav) REFERENCES Stavy(StavyID),\n" +
                    "\tFOREIGN KEY (FK_Sklad) REFERENCES Sklad(SkladID),\n" +
                    "\tFOREIGN KEY (FK_Material) REFERENCES Material(MaterialID)\n" +
                    ");");
            ExecuteQuery("CREATE TABLE Ridic1\n" +
                    "(\n" +
                    "\tRidicID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tJmeno varchar(50) NULL,\n" +
                    "\tPrijmeni varchar(50) NULL,\n" +
                    "\tFK_Kontakt int NOT NULL,\n FOREIGN KEY (FK_Kontakt) REFERENCES Kontakt(KontaktID)" +
                    ");");
            ExecuteQuery("CREATE TABLE Sklad4\n" +
                    "(\n" +
                    "\tSkladID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tKapacita int NULL,\n" +
                    "\tNazev varchar(50) NULL,\n" +
                    "\tFK_Adresa int NOT NULL,\n" +
                    "\tFOREIGN KEY (FK_Adresa) REFERENCES Adresa(AdresaID)\n" +
                    ");");
            ExecuteQuery("CREATE TABLE Skladnik1\n" +
                    "(\n" +
                    "\tSkladnikID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tJmeno varchar(50) NULL,\n" +
                    "\tPrijmeni varchar(50) NULL,\n" +
                    "\tFK_Kontakt int NOT NULL, FK_Sklad int NOT NULL," +
                    "\n FOREIGN KEY (FK_Kontakt) REFERENCES Kontakt(KontaktID)," +
                    "\nFOREIGN KEY (FK_Sklad) REFERENCES Sklad(SkladID)" +
                    ");");*//*
            ExecuteQuery("CREATE TABLE Stavy1\n" +
                    "(\n" +
                    "\tStavyID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tStav varchar(50) NULL\n" +
                    ");");
            ExecuteQuery("CREATE TABLE Vozidla1\n" +
                    "(\n" +
                    "\tVozidlaID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tZnacka varchar(50) NULL,\n" +
                    "\tFK_Ridic int NOT NULL,FK_Sklad int NOT NULL," +
                    "\nFOREIGN KEY (FK_Ridic) REFERENCES Ridic(RidicID)" +
                    "\nFOREIGN KEY (FK_Sklad) REFERENCES Sklad(SkladID)" +
                    ");");
            ExecuteQuery("CREATE TABLE Zakaznik1\n" +
                    "(\n" +
                    "\tZakaznikID INTEGER PRIMARY KEY autoincrement,\n" +
                    "\tJmeno varchar(50) NULL,\n" +
                    "\tPrijmeni varchar(50) NULL,\n" +
                    "\tFK_Kontakt int NOT NULL,\nFOREIGN KEY (FK_Kontakt) REFERENCES Kontakt(KontaktID)" +
                    ")");*/
            /*ExecuteQuery("INSERT INTO ADRESA1(AdresaID, Cislo_popisne, Mesto, Psc, Ulice) SELECT AdresaID, Cislo_popisne, Mesto, Psc, Ulice FROM ADRESA;");
            ExecuteQuery("INSERT INTO Kontakt1(KontaktID, Email, Telefon) SELECT KontaktID, Email, Telefon FROM KONTAKT;");
            ExecuteQuery("INSERT INTO Dodavatel1(DodavatelID, Nazev, FK_Kontakt) SELECT DodavatelID, Nazev, FK_Kontakt FROM Dodavatel;");
            ExecuteQuery("INSERT INTO Material1(MaterialID, Nazev_materialu, FK_Dodavatel) SELECT MaterialID, Nazev_materialu, FK_Dodavatel FROM Material;");
            ExecuteQuery("INSERT INTO Mena1(MenaID, Typ_meny) SELECT MenaID, Typ_meny FROM Mena;");
            ExecuteQuery("INSERT INTO Objednavka1(ObjednavkaID, Cena, Datumprevzeti, datumvytvoreniobjednavky, Mnozstvi, FK_Adresa, FK_Mena, FK_Produkt, FK_Stav, FK_Zakaznik) SELECT ObjednavkaID, Cena, Datumprevzeti, datumvytvoreniobjednavky, Mnozstvi, FK_Adresa, FK_Mena, FK_Produkt, FK_Stav, FK_Zakaznik FROM Objednavka;");
            ExecuteQuery("INSERT INTO Produkt1(ProduktID, Barva, Nazev, Fk_Material, FK_Sklad, FK_Stav) SELECT ProduktID, Barva, Nazev, Fk_Material, FK_Sklad, FK_Stav FROM Produkt;");
            ExecuteQuery("INSERT INTO Ridic1(RidicID, Jmeno, Prijmeni, FK_Kontakt) SELECT RidicID, Jmeno, Prijmeni, FK_Kontakt FROM Ridic;");
            ExecuteQuery("INSERT INTO Sklad4(SkladID, Kapacita, Nazev, FK_Adresa) SELECT SkladID, Kapacita, Nazev, FK_Adresa FROM Sklad;");
            ExecuteQuery("INSERT INTO Skladnik1(SkladnikID, Jmeno, Prijmeni, FK_Kontakt, FK_Sklad) SELECT SkladnikID, Jmeno, Prijmeni, FK_Kontakt, FK_Sklad FROM Skladnik;");
            ExecuteQuery("INSERT INTO Stavy1(StavyID, Stav) SELECT StavyID, Stav FROM Stavy;");
            ExecuteQuery("INSERT INTO Vozidla1(VozidlaID, Znacka, Fk_Ridic, FK_Sklad) SELECT VozidlaID, Znacka, Fk_Ridic, FK_Sklad FROM Vozidla;");
            ExecuteQuery("INSERT INTO Zakaznik1(ZakaznikID, Jmeno, Prijmeni, FK_Kontakt) SELECT ZakaznikID, Jmeno, Prijmeni, FK_Kontakt FROM Zakaznik;");*/
            /*ExecuteQuery("DROP TABLE ADRESA");
            ExecuteQuery("DROP TABLE KONTAKT");
            ExecuteQuery("DROP TABLE MATERIAL");
            ExecuteQuery("DROP TABLE MENA");
            ExecuteQuery("DROP TABLE OBJEDNAVKA");
            ExecuteQuery("DROP TABLE PRODUKT");
            ExecuteQuery("DROP TABLE RIDIC");
            ExecuteQuery("DROP TABLE SKLAD");
            ExecuteQuery("DROP TABLE SKLADNIK");
            ExecuteQuery("DROP TABLE STavy");
            ExecuteQuery("DROP TABLE Vozidla");
            ExecuteQuery("DROP TABLE Zakaznik");
            ExecuteQuery("DROP TABLE DODAVATEL");
            ExecuteQuery("ALTER TABLE Adresa1 RENAME TO Adresa;");
            ExecuteQuery("ALTER TABLE Kontakt1 RENAME TO Kontakt;");
            ExecuteQuery("ALTER TABLE Material1 RENAME TO Material;");
            ExecuteQuery("ALTER TABLE Mena1 RENAME TO Mena;");
            ExecuteQuery("ALTER TABLE Objednavka1 RENAME TO Objednavka;");
            ExecuteQuery("ALTER TABLE Produkt1 RENAME TO Produkt;");
            ExecuteQuery("ALTER TABLE Ridic1 RENAME TO Ridic;");
            ExecuteQuery("ALTER TABLE Sklad4 RENAME TO Sklad;");
            ExecuteQuery("ALTER TABLE Skladnik1 RENAME TO Skladnik;");
            ExecuteQuery("ALTER TABLE Stavy1 RENAME TO Stavy;");
            ExecuteQuery("ALTER TABLE Vozidla1 RENAME TO Vozidla;");
            ExecuteQuery("ALTER TABLE Zakaznik1 RENAME TO Zakaznik;");
            ExecuteQuery("ALTER TABLE Dodavatel1 RENAME TO Dodavatel;");*/
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        cnn.close();
        System.out.println("Opened database successfully");
    }

    public DefaultTableModel getData(String table) throws SQLException {
        Connection cnn = DriverManager.getConnection(connectionString);
        Statement statement = cnn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * from "+table+";");
        String[] columns = getTableColumns(table);
        DefaultTableModel tblModel = new DefaultTableModel(getTableColumns(table), 0){
            public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }
        };
        int position = 0;
        while(rs.next()){
            tblModel.insertRow(position,new Object[] {"test"});
            for(int i = 0;i < columns.length;i++){
                tblModel.setValueAt(rs.getString(columns[i]),position,i);
            }
            position++;
        }
        cnn.close();
        return tblModel;
    }

    public DefaultTableModel getDatafromView(String sql) throws SQLException {
        Connection cnn = DriverManager.getConnection(connectionString);
        Statement statement = cnn.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        String[] columns = new String[rsMetaData.getColumnCount()];
        for(int i = 0;i<rsMetaData.getColumnCount();i++)
        {
            columns[i] = rsMetaData.getColumnName(i+1);
        }
        DefaultTableModel tblModel = new DefaultTableModel(columns, 0){
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        int position = 0;
        while(rs.next()){
            tblModel.insertRow(position,new Object[] {"test"});
            for(int i = 0;i < rs.getMetaData().getColumnCount();i++){
                tblModel.setValueAt(rs.getString(columns[i]),position,i);
            }
            position++;
        }
        cnn.close();
        return tblModel;
    }
}