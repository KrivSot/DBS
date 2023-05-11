package GUI;

import Database.SQLOperations;
import Models.SelectedData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperationsForm extends JFrame {

    private List<JTextField> textFields = new ArrayList<JTextField>();
    private String[] collumsName;
    private String[] collumDataTypes;
    private String table;
    private SQLOperations sql;
    private String selectedID;
    private Mode mode;
    ArrayList<String> data;
    private ArrayList<Integer> indexesOfFK = new ArrayList<>();
    private ArrayList<String> FKtables = new ArrayList<>();

    public OperationsForm(int width, int height, SQLOperations sql, Mode mode, SelectedData selectedData){
        super("DBS2 Project");
        this.collumsName = selectedData.getCollumsName();
        this.collumDataTypes = selectedData.getCollumDataTypes();
        this.sql = sql;
        this.table = selectedData.getTable();
        this.mode = mode;
        this.selectedID = selectedData.getSelectedID();
        this.data = selectedData.getData();
        this.indexesOfFK=selectedData.getIndexesOfFK();
        this.FKtables=selectedData.getFKtables();
        setSize(width,height);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initGui();
        setVisible(true);
    }

    private void initGui() {
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(initForm(), BorderLayout.NORTH);
        add(panelMain);
    }

    private JPanel initForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        for(int i = 0;i< collumsName.length;i++) {
            JTextField textField = new JTextField("",50);
            if((i == 0 && mode == Mode.EDIT) || i == 0)
            {
                textField.setEditable(false);
            }
            switch(mode){
                case DELETE: textField.setEditable(false);
                            textField.setText(data.get(i));break;
                case EDIT:  textField.setText(data.get(i));break;
                default: break;
            }
            textFields.add(textField);
            textField.setBorder(BorderFactory.createTitledBorder(collumsName[i]));
            panel.add(textField);
        }
        JButton jButton = new JButton();
        switch(mode){
            case ADD: jButton.setText("Přidat data"); break;
            case EDIT: jButton.setText("Editovat data"); break;
            case DELETE: jButton.setText("Smazat data"); break;
        }

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch(mode){
                    case ADD:
                        try {
                            if(FKExists()) {
                                addData();
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case EDIT:
                        try {
                            // Pokud zadavany cizi klice existujou v tabulce
                            if(FKExists()) {
                                editData();
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break ;
                    case DELETE:
                        try {
                            deleteData();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    default:break;
                }
            }
        });
        panel.add(jButton);
        return panel;
    }

    public void addData() throws SQLException {
        String query = "INSERT INTO "+table +" (";
        for(int i = 1; i < collumsName.length;i++)
        {
            if(i != collumsName.length-1) {
                query += collumsName[i] + ", " ;
            }
            else {
                query += collumsName[i];
            }
        }
        query += ")\t VALUES (";
        for(int i = 1; i < collumsName.length;i++)
        {
            if(i != collumsName.length-1) {
                query += "'"+textFields.get(i).getText()+"', ";
            }
            else {
                query += "'"+textFields.get(i).getText()+"'";
            }
        }
        query += ");";
        sql.ExecuteQuery(query);
        JOptionPane.showMessageDialog(this, "Data byly uspesne pridany");
    }

    public void deleteData() throws SQLException {
        String query = "DELETE FROM "+table +" WHERE "+collumsName[0]+"= '"+selectedID+"';";
        sql.ExecuteQuery(query);
        JOptionPane.showMessageDialog(this, "Data byly uspesne smazany");
        dispose();
    }

    public void editData() throws SQLException {
        String query = "UPDATE "+table +" SET ";
        for(int i = 0; i < collumsName.length;i++)
        {
            if(i != collumsName.length-1) {
                query += collumsName[i] + " = '"+textFields.get(i).getText()+"', " ;
            }
            else {
                query += collumsName[i] + " = '"+textFields.get(i).getText()+"' " ;
            }
        }
        query += "WHERE "+collumsName[0]+" = '"+selectedID+"';";
        sql.ExecuteQuery(query);
        JOptionPane.showMessageDialog(this, "Data byly uspesne zmeneny");
        dispose();
    }

    public boolean FKExists() throws SQLException {
        ArrayList<String> testdata = new ArrayList<>();
        if(indexesOfFK.size() == 0) return true;
        for(int i = 0;i < indexesOfFK.size();i++)
        {
            String[] columnsOfTable = sql.getTableColumns(FKtables.get(i));
            if(FKtables.get(i).equals("Stav")) testdata = sql.ExecuteSelectQuery("SELECT * FROM Stavy WHERE "+columnsOfTable[0]+" = '"+textFields.get(indexesOfFK.get(i)).getText()+"';");
            else testdata = sql.ExecuteSelectQuery("SELECT * FROM "+FKtables.get(i)+" WHERE "+columnsOfTable[0]+" = '"+textFields.get(indexesOfFK.get(i)).getText()+"';");
            if(testdata.size() == 0){
                System.out.println("Cizí klíč FK_"+FKtables.get(i)+" s ID "+textFields.get(indexesOfFK.get(i)).getText()+" nebyl nalezen v tabulce "+FKtables.get(i)); return false;
            }
        }
        return testdata.size() > 0;
    }
}
