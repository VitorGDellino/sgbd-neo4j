/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aula05.oracleinterface;

/**
 *
 * @author Vitor Giovani
 */
import java.util.*;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jFunctionality{
    // Driver objects are thread-safe and are typically made available application-wide.
    Driver driver;
    JTextArea jtAreaDeStatus;
    JPanel insertPanelLocal;
    JPanel displayPanel;
    JTable displayTable;
    JComboBox tableNameBox;
    JPanel findPanel;
    JPanel primaryKeysPanel;
    JPanel displaySearchPanel;
    String primaryKeyValues[];
    ArrayList<String> primaryKeyNames;
    JPanel schemaPanel;

    public Neo4jFunctionality(JTextArea jtaTextArea, JPanel insertPanel, JComboBox comboBox, JPanel displayPanel, JPanel findPanel, JPanel schemaPanel){
        
        jtAreaDeStatus = jtaTextArea;
        insertPanelLocal = insertPanel;
        tableNameBox = comboBox;
        this.displayPanel = displayPanel;
        this.findPanel = findPanel;
        this.schemaPanel = schemaPanel;
    }
    
    public void connect(String uri, String user, String password){
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    private void addPerson(String name){
        // Sessions are lightweight and disposable connection wrappers.
        try (Session session = driver.session())
        {
            // Wrapping Cypher in an explicit transaction provides atomicity
            // and makes handling errors much easier.
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MERGE (a:Person {name: {x}})", parameters("x", name));
                tx.success();  // Mark this write as successful.
            }
        }
    }
    
    private ArrayList<String> getAllLabels(){
        ArrayList<String> labels = new ArrayList<String>();
        try(Session session = driver.session()){
            StatementResult result = session.run("MATCH (l) RETURN DISTINCT LABELS(l)");

            while(result.hasNext()){
                Record record = result.next();
                labels.add(record.get(0).get(0).toString().substring(1, record.get(0).get(0).toString().length() - 1));
            }
        }
        
        return labels;
    }
    
    public void setLabels(JComboBox jc){
        ArrayList<String> labels = getAllLabels();
        for(String label : labels){
            jc.addItem(label);
        }
    }
    
    
    public void getAllNodesAndDisplay(JPanel displayPanel){
        ArrayList<String> columnsName = new ArrayList<String>();
        String[] data = null;
        DefaultTableModel model = null;
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run("MATCH (a:" + (String)this.tableNameBox.getSelectedItem() + ") RETURN a");
            // Each Cypher execution returns a stream of records.
            Record record = null;
            Iterable<String> it = null;
            
            if(result.hasNext()){
                record = result.next();
                it = record.get(0).keys();
                
                for(String str : it){
                    columnsName.add(str);
                }
                
                data = new String[columnsName.size()];
                data =  columnsName.toArray(data);
                
                displayTable = new JTable(new DefaultTableModel(data,0)){
                    private static final long serialVersionUID = 1L;
                    //turns off cell editing
                    @Override
                    public boolean isCellEditable(int row, int column) {                
                            return false;               
                    };

                };
            
                model = (DefaultTableModel) displayTable.getModel();
            }

            while (result.hasNext()){
                if(it != null){
                    int i = 0;
                    for(String str : it){
                        data[i] = record.get(0).get(str).toString();
                        data[i] = data[i].replace("\"", "");
                        i++;
                    }
                    
                    model.addRow(data);
                    System.out.println();
                }
                record = result.next();                  
            }
            
            model.fireTableDataChanged();
            
            //adds table to containing panel
            JScrollPane jsp = new JScrollPane(displayTable);
            displayPanel.removeAll();
            displayPanel.add(jsp);
            displayPanel.validate();
            displayPanel.repaint();
        
        }
    }

    public void close(){
        // Closing a driver immediately shuts down all open connections.
        driver.close();
    }
    
    /**
     * Delete selected node of displaytable
     * @return true if node was delete successfully
     */
    public Boolean deleteSelectedNode(){
        
        //MATCH (n { name: 'Andy' }) DETACH DELETE n
        int selectedIndex = displayTable.getSelectedRow();
        if(selectedIndex == -1){
            jtAreaDeStatus.setText("Erro ao excluir: nenhum registro selecionado.");
            return false;
            
        }
        
        //get column names and values from table        
        int nCols = displayTable.getColumnCount();
        String[] data = new String[nCols];
        String[] columnNames = new String[nCols];
        
        //go through the selectedRow to get values of primary keys
        for(int i=0; i<nCols; i++){
            //get data from cell
            columnNames[i] = displayTable.getColumnName(i);
            data[i] = (String)displayTable.getModel().getValueAt(selectedIndex, i);      
            System.out.println(columnNames[i]+" - "+data[i]);
        }
        deleteNode(columnNames, data);
        return true;
    }
    
    /**
     * Delete a Node with DETACH DELETE (deletes related relationships)
     * @param columnNames name of properties used in matching
     * @param data data of properties used in matching
     */
    private void deleteNode(String[] columnNames, String[] data){
        
        //Query Example
        //MATCH (n { name: 'Andy' }) DETACH DELETE n
        String query = "MATCH (n {";
        
        //all properties are used in search
        for(int i=0; i< columnNames.length-1; i++){
            query+= " "+columnNames[i]+": '"+data[i]+"',";
        }
        
        query+= " "+columnNames[columnNames.length-1]+": '"+data[columnNames.length-1]+"'";
        query+= "}) DETACH DELETE n";
        
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(query);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}