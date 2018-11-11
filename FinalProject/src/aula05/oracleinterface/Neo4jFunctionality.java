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
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.neo4j.driver.v1.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jFunctionality{
    // Driver objects are thread-safe and are typically made available application-wide.
    Driver driver;
    JTextArea jtAreaDeStatus;
    JPanel insertPanelLocal;
    JPanel displayPanel;
    JTable displayTable;
    JTable displayTableRel;
    JComboBox tableNameBox;
    JPanel findPanel;
    JPanel primaryKeysPanel;
    JPanel displaySearchPanel;
    String primaryKeyValues[];
    ArrayList<String> primaryKeyNames;
    JComboBox relationshipNameBox;

    public Neo4jFunctionality(JTextArea jtaTextArea, JPanel insertPanel, JComboBox comboBox, JComboBox relationshipNameBox, JPanel displayPanel, JPanel findPanel){
        
        jtAreaDeStatus = jtaTextArea;
        insertPanelLocal = insertPanel;
        tableNameBox = comboBox;
        this.relationshipNameBox = relationshipNameBox;
        this.displayPanel = displayPanel;
        this.findPanel = findPanel;
    }
    
    public void connect(String uri, String user, String password){
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    
    private ArrayList<String> getAllLabels(){
        ArrayList<String> labels = new ArrayList<String>();
        try(Session session = driver.session()){
            StatementResult result = session.run("MATCH (l) RETURN DISTINCT LABELS(l)");

            while(result.hasNext()){
                Record record = result.next();
                labels.add(record.get(0).get(0).toString().replace("\"",""));
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
    
    /**
     * Query for all the relationshipTypes in database
     * @return 
     */
    private ArrayList<String> getAllRelationshipTypes(){
        ArrayList<String> labels = new ArrayList<String>();
        try(Session session = driver.session()){
            StatementResult result = session.run("MATCH (n)-[r]-(m) RETURN DISTINCT type(r)");

            while(result.hasNext()){
                Record record = result.next();
                labels.add(record.get(0).toString().replace("\"",""));
            }
        }
        
        return labels;
    }
    
    public void setRelationshipTypes(JComboBox jc){
        ArrayList<String> labels = getAllRelationshipTypes();
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
     * Get all the relationships and display them on the table
     * @param displayPanel 
     */
    public void getAllRelationshipsAndDisplay(JPanel displayPanel){
        ArrayList<String> columnsName = new ArrayList<String>();
        String[] data = null;
        DefaultTableModel model = null;
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            //MATCH (n)-[r:RELATIONSHIP_TYPE]->(m) return n,m
            StatementResult result = session.run("MATCH (n)-[r:"+(String)this.relationshipNameBox.getSelectedItem() + "]->(m) RETURN n, m");
            // Each Cypher execution returns a stream of records.
            Record record = null;
            Iterable<String> it0 = null;
            Iterable<String> it1 = null;
            
            if(result.hasNext()){
                
                /*for(String str : it){
                    columnsName.add(str);
                }
                //data = new String[columnsName.size()];
                //data =  columnsName.toArray(data);
                */
                data = new String[2];
                data[0] = "Nó 1";
                data[1] = "Nó 2";
                displayTableRel = new JTable(new DefaultTableModel(data,0)){
                    private static final long serialVersionUID = 1L;                  

                };
            
                model = (DefaultTableModel) displayTableRel.getModel();
            }

            while (result.hasNext()){
                record = result.next();
                
                it0 = record.get(0).keys();
                it1 = record.get(1).keys();
                
                if(it0 != null){
                   
                    data[0] = "";
                    data[1] = "";
                    for(String str : it0){
                        data[0] += str+": "+record.get(0).get(str).toString()+", ";
                   
                    }
                    
                    for(String str : it1){
                        data[1] += str+": "+record.get(1).get(str).toString()+", ";
                    }
                    
                    model.addRow(data);
                }
                                
            }
           
            model.fireTableDataChanged();
            
            //adds table to containing panel
            JScrollPane jsp = new JScrollPane(displayTableRel);
            displayPanel.removeAll();
            displayPanel.add(jsp);
            displayPanel.validate();
            displayPanel.repaint();
        
        }
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
    
    /**
     * Run query to delete relationship of given type between two nodes
     * @param node1 parameters + data text of node1
     * @param node2 parameters + data text of node2
     * @param relationshipType name of relationship
     */
    private void deleteRelationship(String node1, String node2, String relationshipType){
        
        //Query Example
        //MATCH ({name: 'Andy'})-[r:RELEASED]-({name:'Elisa'}) DELETE r
        String query = "MATCH ({";
        query += node1.substring(0,node1.length()-2);
        query += "})-[r:";
        query += relationshipType;
        query += "]-({";
        query += node2.substring(0,node2.length()-2);
        query +="}) DELETE r";
        
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(query);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Get data from table and delete the selected relationship
     * @return true if the relationship was successfully deleted
     */
    public Boolean deleteSelectedRelationship(){
        
        //MATCH (n { name: 'Andy' }) DETACH DELETE n
        int selectedIndex = displayTableRel.getSelectedRow();
        if(selectedIndex == -1){
            jtAreaDeStatus.setText("Erro ao excluir: nenhum relacionamento selecionado.");
            return false;
            
        }
        
        //get column names and values from table        
        String node1 = (String)displayTableRel.getModel().getValueAt(selectedIndex, 0);   
        String node2 = (String)displayTableRel.getModel().getValueAt(selectedIndex, 1);  
        String relationshipType = (String)this.relationshipNameBox.getSelectedItem();
        
        deleteRelationship(node1, node2, relationshipType);
        
        return true;
    }
    
    /**
     * Update find panel to match current table
     * @param tableName name of selected table
     */
    public void updateFindPanel(String tableName){
        
        //get name of primary key columns
        ArrayList<String> primaryKeyColumns = this.getNameOfPrimaryKeys((String)this.tableNameBox.getSelectedItem());
        
        findPanel.removeAll();
        
        //primary key attributes on the top
        primaryKeysPanel = new JPanel(new GridLayout(1,primaryKeyColumns.size()*2+1));
        //add all attributes with fields
        for(int i=0; i< primaryKeyColumns.size(); i++){
            primaryKeysPanel.add(new JLabel(primaryKeyColumns.get(i), SwingConstants.CENTER));
            primaryKeysPanel.add(new JTextField());
        }
        
        //button for searching data
        JButton searchButton = new JButton("Buscar");
        primaryKeysPanel.add(searchButton);
        searchButton.addMouseListener(new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadSearchData();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        findPanel.add(primaryKeysPanel, BorderLayout.NORTH);
        
        findPanel.add(new JLabel("Resultados da busca serão mostrados aqui", SwingConstants.CENTER), BorderLayout.CENTER);
        findPanel.validate();
        
    }
    
       /**
     * Get string with name of the primary keys of a table
     * @param tableName name of the table
     * @return name of the columns which are primary keys
     */
    ArrayList<String> getNameOfPrimaryKeys(String tableName){
        
        ArrayList<String> primaryKeys = new ArrayList();
        String query = "CALL db.constraints";
        Record record = null;
        Iterable<String> it = null;
        String name;
        
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(query);
            
            while (result.hasNext()){
                record = result.next();
                
                //get name of label to which constraint is applied
                Pattern pattern = Pattern.compile(":([A-Z])\\w+");
                Matcher matcher = pattern.matcher(record.get(0).toString());
              
                if(matcher.find()){
                    name = matcher.group();
                    name = name.substring(1);
                    
                    //is it from the table we're looking for?
                    //if not, moves on to next result
                    if(!name.equals(tableName))
                        continue;
                    
                    pattern = Pattern.compile("\\.([A-Z])\\w+");
                    matcher = pattern.matcher(record.get(0).toString());
                    
                    while(matcher.find()){
                        primaryKeys.add(matcher.group().substring(1));
                    }
                }
                
            }
            
            return primaryKeys;

        }catch (Exception ex) {
            jtAreaDeStatus.setText("getNameOfPrimaryKeys"+ex.getMessage());
            System.out.println("getNameOfPrimaryKeys"+ex.getMessage());
        } 
        return null;
    }
    
     /**
     * Get data with the primary keys specified in the find panel
     */
    void loadSearchData(){
        
        //first get data from panel to use on search
        
        //number of primary keys
        int nColumns = (primaryKeysPanel.getComponentCount())/2;
        
        //column names and data
        ArrayList<String> columnNames = new ArrayList<String>();
        String data[] = new String[nColumns];
        
        //get data from the table by iterating through the components
        JTextField temp;
        JLabel tempLabel;
        JComboBox tempBox;
        for(int i=0; i< nColumns*2; i++){
            if(i%2==0){
                tempLabel = (JLabel)primaryKeysPanel.getComponent(i);
                columnNames.add(tempLabel.getText());
            }else{
                temp = (JTextField)primaryKeysPanel.getComponent(i);
                data[(i-1)/2] = temp.getText();
            }
        }
        
        //saving primary keys and values for posterior use
        this.primaryKeyNames = columnNames;
        this.primaryKeyValues = data;
        
        //build query
        //example query: MATCH (n:Aluno {NROUSP: "1"}) return n
        String query = "MATCH (n:"+ (String)this.tableNameBox.getSelectedItem()+" {";
        
        //for each column in the search
        for(int i=0; i< columnNames.size()-1; i++){
            query += columnNames.get(i) +": \""+data[i]+"\", "; 
        }
        query += columnNames.get(columnNames.size()-1) +": \""+data[data.length-1]+"\"}) RETURN n"; 
        
  
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(query);
            
            if (result.hasNext()){
                Record record = result.next();
                ArrayList<String> dataColumnNames = new ArrayList<>();
                ArrayList<String> retrievedData = new ArrayList<>();
                
                //get column names plus data
                Iterable<String> it = record.get(0).keys();
                for(String str : it){
                    dataColumnNames.add(str);
                    retrievedData.add(record.get(0).get(str).toString());
                }
                
                BorderLayout layout = (BorderLayout) findPanel.getLayout();
                findPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
                
                this.displaySearchPanel = new JPanel();
                
               
                displaySearchedData(displaySearchPanel, dataColumnNames.toArray(new String[0]), retrievedData.toArray(new String[0]));
                
                findPanel.add(displaySearchPanel, BorderLayout.CENTER);
                findPanel.validate();
                
            }else{
                BorderLayout layout = (BorderLayout) findPanel.getLayout();
                findPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
                findPanel.add(new JLabel("Registro não encontado. Tente novamente.", SwingConstants.CENTER), BorderLayout.CENTER);
                findPanel.validate();
            }
            
            
        } catch (Exception ex) {
            
            System.out.println("loadSearchData: "+ex.getMessage());
            jtAreaDeStatus.setText("Erro ao deletar registro: "+ ex.getMessage());
           
        }  
        
    }
    
    /**
     * Display data selected by given columns and values
     * @param insertPanel the panel where the data should be displayed
     * @param columnNames the name of the columns used in the WHERE select statemente
     * @param data value of the columns indicated by columnNames
     */
    void displaySearchedData(JPanel insertPanel, String[] columnNames, String[] data){
        
         //empty panel
        insertPanel.removeAll();
        try{
            //Column names
           String tableName = (String) this.tableNameBox.getSelectedItem();
           
           String dataType;
           String label;

           insertPanel.setLayout(new GridLayout(columnNames.length+1, 2));
           for(int i=0; i<columnNames.length; i++){

               label = columnNames[i];
              
               //name of column
               insertPanel.add(new JLabel(label));
               //data of the column
               insertPanel.add(new JTextField(data[i].replace("\"","")));
               
            }
           
            insertPanel.add(new JLabel("Clique para alterar dados:"));
            JButton searchButton = new JButton("Salvar");
            insertPanel.add(searchButton);
            searchButton.addMouseListener(new java.awt.event.MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                   updateDataFromTable();
                   updateDisplay();
                   
                   
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
           
        
        }catch(Exception e){
            jtAreaDeStatus.setText("DisplaySearchedData:"+e.getMessage());
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Get data from table and update it in database.
     * Called by update button.
     */
    public void updateDataFromTable(){
        
        JPanel insertTable = this.displaySearchPanel;
        
     
        //removing label and button from count
        int nColumns = (insertTable.getComponentCount()-2)/2;
        
        
        String data[] = new String[nColumns];
        String columns[] = new String[nColumns];
        

        JTextField temp;
        JLabel tempLabel;
        JComboBox tempBox;
        
        for(int i=0; i< nColumns*2; i++){
            if(i%2==0){
                tempLabel = (JLabel) insertTable.getComponent(i);
                columns[i/2]= tempLabel.getText();
            }else{
                if(insertTable.getComponent(i) instanceof JTextField){
                    temp = (JTextField)insertTable.getComponent(i);
                    data[(i-1)/2] = temp.getText();
                }else{
                    tempBox = (JComboBox) insertTable.getComponent(i);
                    data[(i-1)/2] = (String)tempBox.getSelectedItem();
                }
            }
            
            
        }
        
        updateDataColumns((String)this.tableNameBox.getSelectedItem(),columns, data);
      
    }
    
    public ArrayList<String> getNodes(String table){
       ArrayList<String> data = new ArrayList<String>();
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run("MATCH (a:" + table + ") RETURN a");
            // Each Cypher execution returns a stream of records.
            Record record = null;
            Iterable<String> it = null;

            while (result.hasNext()){
                record = result.next();
                
                it = record.get(0).keys();
                if(it != null){
                    String aux = "";
                    for(String str : it){
                        aux += str+": "+record.get(0).get(str).toString()+", ";                   
                    }
                    data.add(aux.substring(0, aux.length() - 2));
                }
            }       
        }
        return data;
    }
    
    public void createRelationship(String label1, String data1, String label2, String data2, String relation){
        String query = "MATCH (a:"+ label1 +" {" + data1 + "}), (b:"+ label2 + "{" + data2 + "})\nCREATE (a)-[:" + relation + "]->(b)";
        
        try(Session session = driver.session()){
            StatementResult result = session.run(query);
        }
    }

    /***
     * Update a node using previously collected primary keys and values
     * @param tableName name of node label
     * @param columns name of columns to be updated
     * @param data values of columns to be updated
     */
    void updateDataColumns(String tableName, String[] columns, String[] data){
        
        //example query: MATCH (n:Funcionario { NROUSP: '4432' }) SET n.NROUSP = '5'
        String query = "MATCH (n:"+tableName+" {";
        
        for(int i=0; i<this.primaryKeyNames.size()-1; i++){
            query += this.primaryKeyNames.get(i)+":\""+this.primaryKeyValues[i]+"\", ";
        }
        
        query += this.primaryKeyNames.get(this.primaryKeyNames.size()-1)+":\""
                +this.primaryKeyValues[this.primaryKeyNames.size()-1]+"\"}) SET ";
        
        for(int i=0; i<columns.length-1; i++){
            query +="n."+columns[i]+" = \""+data[i]+"\", ";
        }
        
        query +="n."+columns[columns.length-1]+" = \""+data[columns.length-1]+"\"";
        
        
        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(query);
            jtAreaDeStatus.setText("Nó atualizado!");
          
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void updateDisplay(){
        getAllNodesAndDisplay(displayPanel);
      
    }
    
    
}