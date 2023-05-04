package Models;

import Database.SQLOperations;
import GUI.Mode;

import java.sql.SQLException;
import java.util.ArrayList;

public class SelectedData {

    private String[] collumsName;
    private String[] collumDataTypes;
    private String table;
    private String selectedID;
    private ArrayList<String> data;
    private ArrayList<Integer> indexesOfFK = new ArrayList<>();
    private ArrayList<String> FKtables = new ArrayList<>();
    private final SQLOperations sql;

    public SelectedData(String[] collumsName, String[] collumDataTypes, String table, String selectedID,SQLOperations sql) throws SQLException {
        this.collumsName = collumsName;
        this.collumDataTypes = collumDataTypes;
        this.table = table;
        this.selectedID = selectedID;
        this.sql = sql;
        getSQLData();
        setIndexesOfFK();
    }

    public void getSQLData() throws SQLException {
        data = new ArrayList<>();
        data = sql.ExecuteSelectQuery("SELECT * FROM "+table+" WHERE "+collumsName[0]+" = '"+selectedID+"';");
    }

    public ArrayList<String> getData(){
        return data;
    }

    public String[] getCollumsName() {
        return collumsName;
    }

    public void setCollumsName(String[] collumsName) {
        this.collumsName = collumsName;
    }

    public String[] getCollumDataTypes() {
        return collumDataTypes;
    }

    public void setCollumDataTypes(String[] collumDataTypes) {
        this.collumDataTypes = collumDataTypes;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSelectedID() {
        return selectedID;
    }

    public void setIndexesOfFK(){
        indexesOfFK = new ArrayList<>();
        FKtables = new ArrayList<>();
        for(int i = 0; i < collumsName.length;i++){
            if(collumsName[i].startsWith("FK_")){
                indexesOfFK.add(i);
                FKtables.add(collumsName[i].substring(3));
            }
        }
    }

    public void setSelectedID(String selectedID) {
        this.selectedID = selectedID;
    }

    public ArrayList<Integer> getIndexesOfFK() {
        return indexesOfFK;
    }

    public ArrayList<String> getFKtables() {
        return FKtables;
    }
}
