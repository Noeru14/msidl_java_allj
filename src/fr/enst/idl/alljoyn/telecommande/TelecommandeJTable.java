package fr.enst.idl.alljoyn.telecommande;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class TelecommandeJTable extends AbstractTableModel {


	private static final long serialVersionUID = 6003780920745528748L;
	private ArrayList<Object[]> data;
    private ArrayList<String> columnNames;

    public TelecommandeJTable(ArrayList<String> columnNames, ArrayList<Object[]> data) {
        super();
        this.columnNames = columnNames;
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    	try {
    		return data.get(rowIndex)[columnIndex];
    	} catch(IndexOutOfBoundsException iofe) {
    		System.err.println("Concurent Access");
    		return "";
    	}
    }

    public void deleteData() {
        int rows = getRowCount();
        if (rows == 0) {
            return;
        }
        data.clear();
        fireTableRowsDeleted(0, rows - 1);
    }
    
    @Override
    public void fireTableDataChanged() {
    	super.fireTableDataChanged();
    }
}