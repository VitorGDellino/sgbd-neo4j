/*
 * Prática 5 - Laboratório de Banco de Dados
 * Elisa Saltori Trujillo - 8551100
 */
package aula05.oracleinterface;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author junio e Elisa Saltori Trujillo
 */
public class DBFuncionalidades {
    Connection connection;
    Statement stmt;
    ResultSet rs;
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
    
    
    public DBFuncionalidades(JTextArea jtaTextArea, JPanel insertPanel, JComboBox comboBox, JPanel displayPanel, JPanel findPanel, JPanel schemaPanel){
        jtAreaDeStatus = jtaTextArea;
        insertPanelLocal = insertPanel;
        tableNameBox = comboBox;
        this.displayPanel = displayPanel;
        this.findPanel = findPanel;
        this.schemaPanel = schemaPanel;
    }
    
    /**
     * Connect to oracle database. First tries to connect from outside the lab,
     * then tries local connection.
     * @return true if connection was succesful, false otherwise
     */
    public boolean conectar(){       
        try {
            //Connection from outside the lab
            jtAreaDeStatus.setText("Conectando... Por favor, aguarde");
            DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@grad.icmc.usp.br:15215:orcl",
                    "L8551100",
                    "bcc2015");
            return true;
        } catch(SQLException ex){
            try{
                //Local connection
                connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@192.168.183.15:1521:orcl",
                    "L8551100",
                    "bcc2015");
                
                return true;
            }catch(SQLException ex2){
                jtAreaDeStatus.setText("Problema: verifique seu usuário e senha");
            }
            
        }
        
        return false;
    }
    
    /**
     * Gets tablenames from database and shows them in combobox
     * @param jc comboBox where tablenames should be shown
     */
    public void pegarNomesDeTabelas(JComboBox jc){
        String s = "";
        try {
            getTableNames();
            while (rs.next()) {
                jc.addItem(rs.getString("table_name"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            jtAreaDeStatus.setText("Erro na consulta: \"" + s + "\"");
        }        
    }
    
    /**
     * Get all tablenames from user
     */
    public void getTableNames(){
        String s = "";
        try{
            s = "SELECT table_name FROM user_tables";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        }catch (SQLException ex) {
            jtAreaDeStatus.setText("Erro na consulta: \"" + s + "\"");
        } 
         
    }
    
 
    /**
     * Gets relevant metadata for the given table
     * @param tableName name of the desired table
     */
    private void getMetaData(String tableName){
        String s = "";
                
        try {
            s = "SELECT COLUMN_ID, COLUMN_NAME, DATA_TYPE, NULLABLE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+tableName+"'";
            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
            
        } catch (SQLException ex) {
            
            System.out.println("getMetaData: "+ex.getMessage());
            jtAreaDeStatus.setText("Erro na consulta: \"" + s + "\"");
           

        }     
    }
    
    /**
     * Gets metadata from table and turns it into a string
     * @param tableName name of the desired table
     * @return string with information about the table
     */
    public String pegarMetaDados(String tableName){
        String metaDados = "\n";
       
        try {
            getMetaData(tableName);

             while (rs.next()) {
                metaDados +="("+rs.getString("COLUMN_ID")+") ";
                metaDados +=rs.getString("COLUMN_NAME");
                metaDados +=": "+rs.getString("DATA_TYPE");
                metaDados +=" ("+rs.getString("NULLABLE")+")\n";
            }
            rs.close();
            stmt.close();
            
            return metaDados;
        } catch (SQLException ex) {
            
            jtAreaDeStatus.setText("Erro ao obter metadados");
            return null;
        }        
    }
    
    /**
     * Return a list with the name of all the columns of a given a table
     * @param tableName
     * @return list with the name of all columns
     */
    ArrayList<String> getColumnNames(String tableName){
        ArrayList<String> columnNames = new ArrayList<String>();
        ResultSet res;
        String s = "";
        
        try {
            s = "SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+tableName+"'";
            
            stmt = connection.createStatement();
            res = stmt.executeQuery(s);
            
             while (res.next()) {
                columnNames.add(res.getString("COLUMN_NAME"));
            }
            res.close();
            stmt.close();
            
            return columnNames;
        } catch (SQLException ex) {
            
            jtAreaDeStatus.setText("Erro ao obter metadados");
            return null;
        }     
    }
    
    /**
     * Get data from all columns of a given table
     * @param tableName Name of the table where the data comes from
     */
    void getAllDataFromTable(String tableName){
        String s = "";

        try{
            s = "SELECT * FROM "+tableName;

            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName);
             System.out.println("Error in get all data");
             System.out.println(ex.getMessage());
          
        }
    }
    
    /**
     * Get data from given table, querying only the desired columns
     * @param tableName name of the table
     * @param columns array with the name of the desired columns
     */
    void getDataFromTable(String tableName, String[] columns){
        String s;
        try{
            s = "SELECT ";
            if(columns.length==1)
                s+=columns[0];
            else{
                for (int i=0; i< columns.length-1; i++){
                    s+= columns[i] + ", ";
                }
                s+= columns[columns.length-1];
            }
            
            s+= " FROM "+tableName;
         
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName);
   
          
        }
    }
    
    /**
     * Get distinct data from a table for a single column and order it by that
     * column. Used for showing foreign key options in the insert panel
     * @param tableName name of the table
     * @param column name of the column
     */
     void getDinstinctDataFromTable(String tableName, String column){
        String s;
        try{
            s = "SELECT ";
            s += "DISTINCT("+column+")";
            s+= " FROM "+tableName;
            s+= " ORDER BY " + column;
         
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName);
             System.out.println(ex.getMessage());
   
          
        }
    }
    
     /**
      * Display data in given panel
      * @param pPainelDeExibicaoDeDados 
      */
    public void displayData(JPanel pPainelDeExibicaoDeDados){
        String tableName;
        
        try{
        
            //get name of table and of its columns
            ArrayList<String> columnNames;
            tableName = (String)this.tableNameBox.getSelectedItem();
            //Column names
            columnNames = getColumnNames(tableName);
            int nColumns = columnNames.size();
            String columns[] = new String[nColumns];

            columns = columnNames.toArray(columns);
            
            //create new table
            displayTable = new JTable(new DefaultTableModel(columns,0)){
                private static final long serialVersionUID = 1L;
                //turns off cell editing
                @Override
                public boolean isCellEditable(int row, int column) {                
                        return false;               
                };

            };
            
            DefaultTableModel model = (DefaultTableModel) displayTable.getModel();
            
            //get data from all rows in the given table
            getAllDataFromTable(tableName);
            
            //goes through resultset saving the data
            while(rs.next()){
                String data[] = new String[columns.length];
                for(int i=0; i< columns.length; i++){
                    data[i] = rs.getString(columns[i]);

                }
                //add row to table
                model.addRow(data);
            
            }
        
            rs.close();
            stmt.close();

            model.fireTableDataChanged();
            
            //adds table to containing panel
            JScrollPane jsp = new JScrollPane(displayTable);
            pPainelDeExibicaoDeDados.removeAll();
            pPainelDeExibicaoDeDados.add(jsp);
            pPainelDeExibicaoDeDados.validate();
            pPainelDeExibicaoDeDados.repaint();
        
        }catch(Exception e){
            jtAreaDeStatus.setText("Erro ao obter dados da tabela.\n"+e.getMessage());
        }
        
    }
    
    /**
     * Get SEARCH_CONDITION of the constraints of given time
     * @param tableName name of the table
     * @param constraintType type of the constraint (C, R...)
     */
    void getTableConstraints(String tableName, String constraintType){
        
        String s;
        try{
            s = "SELECT SEARCH_CONDITION FROM USER_CONSTRAINTS WHERE TABLE_NAME='";
            s+= tableName+"' ";
            s+="AND CONSTRAINT_TYPE='"+constraintType+"'";

            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter constraints da tabela "+tableName);
             System.out.println(ex.getMessage());
        }
    }
    
    /**
     * 
     * @param tableName
     * @param constraintType
     * @return array with SEARCH_CONDITIONS of contraints
     */
    String[] getTableConstraintsString(String tableName, String constraintType){
        ArrayList<String> constraints = new ArrayList<String>();
        
        try{
            getTableConstraints(tableName, constraintType);
            while(rs.next()){
                constraints.add(rs.getString("SEARCH_CONDITION"));
            }
            String strConstraints[] = new String[constraints.size()];

            strConstraints = constraints.toArray(strConstraints);
            rs.close();
            stmt.close();
            return strConstraints;
            
        }catch (Exception ex) {
             jtAreaDeStatus.setText("Erro ao obter constraints da tabela "+tableName);
             System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * Get data about foreign keys for a given column
     * @param tableName the name where the column is
     * @param columnName 
     */
    void getForeignReference(String tableName, String columnName){
        
        
        String s;
        try{
            s = "select c.constraint_name, g.table_name as original_table, g.column_name as table_column ,f.constraint_name as foreign_constraint_name, f.table_name as foreign_table, f.column_name as foreign_column, g.position " +
                "from user_constraints c " +
                "join user_cons_columns f " +
                "on f.constraint_name = c.r_constraint_name " +
                "join user_cons_columns g " +
                "on g.constraint_name = c.constraint_name and g.position = f.position and g.table_name='"
                    + tableName
                    + "'"
                    + " and g.column_name='" + columnName + "' " 
                    + "where c.constraint_type='R'";

            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName);
             System.out.println("getForeignReference: "+ex.getMessage());
          
        }
    }
    
  
    /**
     * Finds what column the given column refers to and gets possible values (foreign key)
     * @param tableName name of the table where the column is located
     * @param columnName name of the column
     * @return possible values for that column
     */
    String[] getPossibleValuesFromReference(String tableName, String columnName){
        
        ArrayList<String> values = new ArrayList<String>();
        String[] strValues = new String[1];

        
        String foreignTable, foreignColumn;
        //get data from foreign key constraints
        getForeignReference(tableName, columnName);
        try{
            //check for foreign key constraint
            if(rs.next()){
                
                //get name of table and of the column it refers to
                foreignTable = rs.getString("foreign_table");
                foreignColumn = rs.getString("foreign_column");
               
                rs.close();
                stmt.close();
             
                //get possible values for column
                this.getDinstinctDataFromTable(foreignTable, foreignColumn);
                while(rs.next()){
                    values.add(rs.getString(foreignColumn));
                }
                
                rs.close();
                stmt.close();
                
                strValues = values.toArray(strValues);
                return strValues;

            }
           
            
        }catch( Exception ex){
             jtAreaDeStatus.setText("Erro ao obter referencias da tabela "+tableName);
             System.out.println("getPossibleValuesFromReference: "+ex.getMessage());
        }
        return null;
    }
    
    /**
     * For the insert window, makes the field used for inputing data:
     *  a JTextField or a ComboBox
     * @param columnName name of the column
     * @param insertPanel reference to the panel where the input should go
     * @param checkConstraints string containing all check constraints for the table in question
     */
    void insertField(String tableName, String columnName, JPanel insertPanel, String[] checkConstraints){
        
        String[] values = getValuesFromCheckConstraint(columnName, checkConstraints);
        String[] foreignValues = this.getPossibleValuesFromReference(tableName, columnName);
        //no check constraints
        if(values==null){
            if(foreignValues != null){
                 //add values according to foreign key constraints
                JComboBox comboBox = new JComboBox();
                for(int i=0; i<foreignValues.length; i++)
                    comboBox.addItem(foreignValues[i]);
                
                comboBox.addItemListener(
                    new java.awt.event.ItemListener() {
                        @Override
                        public void itemStateChanged(java.awt.event.ItemEvent e) {
                            JComboBox jcTemp = (JComboBox) e.getSource();
                            checkForeignKey("insert");
                            
                        }
                    });

                insertPanel.add(comboBox);
            }else{
                insertPanel.add(new JTextField("Digite aqui"));
            }
            
        }else{ 
            //add values according to check contraints
            JComboBox comboBox = new JComboBox();
            for(int i=0; i<values.length; i++)
                comboBox.addItem(values[i]);
            
            insertPanel.add(comboBox);
        }
        
    }
    
     /**
     * For the insert window, makes the field used for inputing data:
     *  a JTextField or a ComboBox
     * This version fills the data with given value.
     * @param columnName name of the column
     * @param insertPanel reference to the panel where the input should go
     * @param checkConstraints string containing all check constraints for the table in question
     * @param value value for column
     */
    void insertField(String tableName, String columnName, JPanel insertPanel, String[] checkConstraints, String value){
        
        String[] values = getValuesFromCheckConstraint(columnName, checkConstraints);
        String[] foreignValues = this.getPossibleValuesFromReference(tableName, columnName);
        //no check constraints
        if(values==null){
            if(foreignValues != null){
                 //add values according to foreign key constraints
                JComboBox comboBox = new JComboBox();
                for(int i=0; i<foreignValues.length; i++){
                    comboBox.addItem(foreignValues[i]);
                }
                
                //set to current value
                comboBox.setSelectedItem(value);
                
                
                comboBox.addItemListener(
                    new java.awt.event.ItemListener() {
                        @Override
                        public void itemStateChanged(java.awt.event.ItemEvent e) {
                            JComboBox jcTemp = (JComboBox) e.getSource();
                            checkForeignKey("update");
                            
                        }
                    });

                insertPanel.add(comboBox);
            }else{
                insertPanel.add(new JTextField(value));
            }
            
        }else{ 
            //add values according to check contraints
            JComboBox comboBox = new JComboBox();
            for(int i=0; i<values.length; i++)
                comboBox.addItem(values[i]);
            
            //set to current value
            comboBox.setSelectedItem(value);
            insertPanel.add(comboBox);
        }
        
    }
    
    /**
     * Parse Check constraints to get possible values for a certain column.
     * Only works with CHECK IN constraints
     * @param ColumnName name of the column
     * @param checkConstraints string of checkConstraints for the table of the column
     * @return possible values for ColumnName. Null if no CHECK IN found
     */
    String[] getValuesFromCheckConstraint(String columnName, String[] checkConstraints){
        
        
        String[] parts;
        ArrayList<String> possibleValues = new ArrayList<String>();
       
        //Format of a check in condition:
        //Columnname IN ('VALUE1', 'VALUE2')
        for(int i=0; i<checkConstraints.length; i++){
            
            //check for multiple check conditions in one constraint
            //type: column1 in (value1, value2) and column2 in (value3, value4)
            String[] multipleConstraints = checkConstraints[i].split("(AND )|(and )");
            for (int j=0; j<multipleConstraints.length; j++){

                //split constraint string
                parts = multipleConstraints[j].split(" ");

                //check if first part is to higher string
                //if not, skip condition
                if(!columnName.toUpperCase().equals(parts[0].toUpperCase()))
                    continue;

                //check for IN in constraint
                //else skip condition
                if(!parts[1].equals("IN"))
                    continue;

                
                //gets possible values
                Pattern p = Pattern.compile("[\']([^\']*)[\']|[0-9]+[.]?[0-9]*");
                Matcher m = p.matcher(multipleConstraints[j]);
                //for each value, add to output
                while (m.find()) {
                  //gets integers
                  if(m.group(1)==null)
                      possibleValues.add(m.group(0));
                  else //gets strings
                      possibleValues.add(m.group(1));

                }

                String values[] = new String[possibleValues.size()];
                values= possibleValues.toArray(values);

                return values;
            }
        }
        return null;
    }
    
    
    /**
     * Update insert panel with data from selected table
     * @param tableName name of selected table
     * @param insertPanel panel where the data is shown
     * @param insertButton button on panel
     */
    public void updateInsertTable(String tableName, JPanel insertPanel, JButton insertButton){
        
        //empty panel
        insertPanel.removeAll();
        try{
            //Column names
           ArrayList<String> columnNames = getColumnNames(tableName);
           int nColumns = columnNames.size();
           String columns[] = new String[nColumns];

           columns = columnNames.toArray(columns);

           //get MetaData
           String[] checkConstraints = getTableConstraintsString(tableName, "C");
           //String[] foreignConstraints = getTableConstraintsString(tableName, "F");
           String dataType;
           String label;
           
           //Adds labels for each column and the datafield
           insertPanel.setLayout(new GridLayout(columns.length+1, 2));
           for(int i=0; i<columns.length; i++){

               label = columns[i];
              
               insertPanel.add(new JLabel(label));
               
               //inserts appropriate field for inputting data
               insertField(tableName, columns[i], insertPanel, checkConstraints);
               
           }
           insertPanel.add(new JLabel("Clique para inserir:"));

           insertPanel.add(insertButton);
        
        }catch(Exception e){
            jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName+":"+e.getMessage());
            System.out.println(e.getMessage());
        }
       
    }
    
    /**
     * Gets data from insert panel and inserts it in database
     * @param insertTable panel with the data
     * @param tableName  name of the table where data is to be inserted
     */
    public void insertDataFromTable(JPanel insertTable, String tableName){
     
        //removing label and button from count
        int nColumns = (insertTable.getComponentCount()-2)/2;
        
        
        String data[] = new String[nColumns];
        

        JTextField temp;
        JComboBox tempBox;
        for(int i=1; i< nColumns*2; i+=2){
            
            if(insertTable.getComponent(i) instanceof JTextField){
                temp = (JTextField)insertTable.getComponent(i);
                data[(i-1)/2] = temp.getText();
            }else{
                tempBox = (JComboBox) insertTable.getComponent(i);
                data[(i-1)/2] = (String)tempBox.getSelectedItem();
            }
            
        }
        
        insertDataAllColumns(tableName, data);
      
    }
    
    /**
     * Inserts data into database using all columns
     * @param tableName name of the table
     * @param data array containing data in order of column definition
     */
    void insertDataAllColumns(String tableName, String[] data){

        String s = "INSERT INTO "+tableName+" VALUES (";
        
        //getting metadata
        this.getMetaData(tableName);
        //constructing query
        try{
             for(int i=0; i<data.length; i++){
                //check columns type
                if(i != 0)
                    s+= ", ";
                rs.next();
                String dataType = rs.getString("DATA_TYPE");
                if(dataType.equals("DATE")){
                    s+="TO_DATE('"+data[i]+"', 'YYYY-MM-DD HH24:MI:SS')";
                }else{
                    if(dataType.equals("BLOB")){
                        s+= "empty_blob()";
                    }else{
                        s+= "'"+data[i]+"'";
                    }
                }

            }
            s+=")";
            
            rs.close();
            stmt.close();
            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
            rs.close();
            stmt.close();
            jtAreaDeStatus.setText("Registro adicionado com sucesso!");
        }catch(Exception e){
            String errorText = "Erro ao inserir dados na tabelas "+tableName+"\n";
            errorText+= e.getMessage();
            errorText+= "Formato da data a ser utilizado: 'YYYY-MM-DD HH24:MI:SS'";
            jtAreaDeStatus.setText(errorText);
            System.out.println(e.getMessage());
        }
       
        
    }
    
    /**
     * Query database for all foreign keys constraints of a given table
     * @param tableName name of the table
     */
    void getForeignReferenceTable(String tableName){
        
        String s;
        try{
            s = "select c.constraint_name, g.table_name as original_table, g.column_name as table_column ,f.constraint_name as foreign_constraint_name, f.table_name as foreign_table, f.column_name as foreign_column\n" +
                "from user_constraints c\n" +
                "join user_cons_columns f\n" +
                "on f.constraint_name = c.r_constraint_name\n" +
                "join user_cons_columns g\n" +
                "on g.constraint_name = c.constraint_name and g.position = f.position and g.table_name='"
                    +tableName+"'\n" +
                "where c.constraint_type='R'\n"
                +"ORDER BY c.constraint_name";

            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName);
             System.out.println("getForeignReferenceTable: "+ex.getMessage());
          
        }
    }
    
    /**
     * Check if foreign keys combinations composed of more than one element are valid.
     * Should be called when an item is selected
     * @param tableName 
     */
    public void checkForeignKey(String mode){
        
        String tableName = (String)this.tableNameBox.getSelectedItem();
        
        String constraintName="";
        ArrayList<String> columns = new ArrayList<String>();
        String foreignTableName="";
        
        jtAreaDeStatus.setText("");
        
        try{
            //gets foreig key constraints for table
            //ordered by constraint name
            this.getForeignReferenceTable(tableName);
            
            while(rs.next()){

                //no constraint selected
                
                if(constraintName.equals("")){
                    //get constraint name and add column to list
                    constraintName = rs.getString("CONSTRAINT_NAME");
                    columns.add(rs.getString("foreign_column"));
                 
                    foreignTableName = rs.getString("foreign_table");
                }else{
                    //one more column of current restraint
                    if(constraintName.equals(rs.getString("CONSTRAINT_NAME"))){
                        columns.add(rs.getString("foreign_column"));
                        
                    }else{
                        
                        //gets all columns collected for first restriction and check if combination exists
                        //only checks if there's more than one column in the condition
                        if(columns.size()>1){
                            //get column values
                            //removing label and button from count
                            if(mode.equals("insert")){
                                if(!this.checkCombination(foreignTableName, columns, this.insertPanelLocal))
                                    jtAreaDeStatus.setText(constraintName+ ": combinação inválida de chaves extrangeiras!");
                            }
                            else{
                                if(!this.checkCombination(foreignTableName, columns, this.displaySearchPanel))
                                     jtAreaDeStatus.setText(constraintName+ ": combinação inválida de chaves extrangeiras!");
                            }   
                        }
                        //proceed to next constraint
                        columns.clear(); //clear column list
                    
                        columns.add(rs.getString("table_column"));
                        foreignTableName = rs.getString("foreign_table");
                        constraintName = rs.getString("CONSTRAINT_NAME");
                        
                    }
                    
                   
                }
                
            }
            
            //last constraint, after rs is done
            if(columns.size()>1){
               
                if(mode.equals("insert")){
                    if(!this.checkCombination(foreignTableName, columns, this.insertPanelLocal))
                        jtAreaDeStatus.setText(constraintName+ ": combinação inválida de chaves extrangeiras!");
                }
                else{
                    if(!this.checkCombination(foreignTableName, columns, this.displaySearchPanel))
                         jtAreaDeStatus.setText(constraintName+ ": combinação inválida de chaves extrangeiras!");
                }           
            }
            
            rs.close();
            stmt.close();
            
        }catch (SQLException ex) {
             jtAreaDeStatus.setText("Erro ao obter dados da tabela "+tableName);
             System.out.println("getForeignKey: "+ex.getMessage());
          
        }
    }
    
    /**
     * Returns true if values for columns are found in the given table
     * @param tableName
     * @param columns
     * @return 
     */
    Boolean checkCombination(String tableName, ArrayList<String> columns, JPanel insertTable){
        
        int nColumns = (insertTable.getComponentCount()-2)/2;

        String data[] = new String[columns.size()];
        
        

        //get data from fields
        JTextField temp;
        JComboBox tempBox;
        for(int i=0; i< nColumns*2; i+=2){
           String curColumn = ((JLabel)insertTable.getComponent(i)).getText();
           for(int j=0; j < columns.size(); j++){
               if(curColumn.equals(columns.get(j))){
                    if(insertTable.getComponent(i+1) instanceof JTextField){
                        temp = (JTextField)insertTable.getComponent(i+1);
                        data[j] = temp.getText();
                    }else{
                        tempBox = (JComboBox) insertTable.getComponent(i+1);
                        data[j] = (String)tempBox.getSelectedItem();
                        
                    }
                  
               }
           }
        
        }
            
        Statement stmtLocal= null;
        ResultSet rsLocal = null;
        String s = getDataWhere(tableName, columns, data, true);
        
        
        
        try{
            
            stmtLocal = connection.createStatement();
            rsLocal = stmtLocal.executeQuery(s);
            if(rsLocal.next()){
                
                if(rsLocal.getString(1).equals("0")==true)
                    return false;
                else
                    return true;
            }
            
            rsLocal.close();
            stmtLocal.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        
        return false;
        
    }
    
    /**
     * Get string for selecting data using WHERE conditions
     * @param tableName name of the table
     * @param columns columns used in the where search
     * @param values data values for the columns
     * @param count true if select count(*). false is select *
     * @return 
     */
     String getDataWhere(String tableName, ArrayList<String> columns, String[] values, boolean count){
        String s;
        try{
            if(count)
                s = "SELECT COUNT(*) FROM "+tableName;
            else
                s = "SELECT * FROM "+tableName;
            s+= " WHERE";
            if(columns.size()==1)
                s+=" "+columns.get(0)+"='"+values[0]+"'";
            else{
                for (int i=0; i< columns.size()-1; i++){
                    s+=" "+columns.get(i)+"='"+values[i]+"' AND";
                }
                s+=" "+columns.get(columns.size()-1)+"='"+values[columns.size()-1]+"'";
            }
            
            return s;
        } catch (Exception ex) {
             jtAreaDeStatus.setText("getDataWhere: Erro ao obter dados da tabela "+tableName);
   
          
        }
        return null;
    }
     
    /**
     * Get the name of the primary keys of a given table
     * @param tableName name of the table
     */
    void getPrimaryKeys(String tableName){
        
        String s = "";
        try{
            s = "select ucc.column_name\n" +
                "from user_constraints uc\n" +
                "join user_cons_columns ucc on ucc.constraint_name = uc.constraint_name\n" +
                "where uc.constraint_type='P' and uc.table_name='"+tableName+"'";
            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
        }catch (SQLException ex) {
            jtAreaDeStatus.setText("getPrimaryKeys"+ex.getMessage());
            System.out.println("getPrimaryKeys"+ex.getMessage());
        } 
        
    }
    
    /**
     * Get string with name of the primary keys of a table
     * @param tableName name of the table
     * @return name of the columns which are primary keys
     */
    ArrayList<String> getNameOfPrimaryKeys(String tableName){
        
        ArrayList<String> primaryKeys = new ArrayList();
        getPrimaryKeys(tableName);
        
        try{
            while(rs.next()){
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
            
            rs.close();
            stmt.close();
            
            return primaryKeys;
        }catch (SQLException ex) {
            jtAreaDeStatus.setText("getNameOfPrimaryKeys"+ex.getMessage());
            System.out.println("getNameOfPrimaryKeys"+ex.getMessage());
        } 
        return null;
    }
    
    /**
     * Delete the selected row in the table from the database
     * Should be called by delete button in main view.
     * @return true if row was deleted successfully
     */
    public Boolean deleteSelectedRow(){
        
        
        int selectedIndex = displayTable.getSelectedRow();
        if(selectedIndex == -1){
            jtAreaDeStatus.setText("Erro ao excluir: nenhum registro selecionado.");
            return false;
            
        }
        //get name of primary key columns
        ArrayList<String> primaryKeyColumns = this.getNameOfPrimaryKeys((String)this.tableNameBox.getSelectedItem());
        
        //now get key values from table        
        int nCols = displayTable.getColumnCount();
        String[] data = new String[primaryKeyColumns.size()];
        
        //go through the selectedRow to get values of primary keys
        for(int i=0; i<nCols; i++){
            //iterate through all primary key column names
            for(int j=0; j<primaryKeyColumns.size(); j++)
                //is it the right column?
                if(displayTable.getColumnName(i).equals(primaryKeyColumns.get(j))){
                    //get data from cell
                    data[j] = (String)displayTable.getModel().getValueAt(selectedIndex, i);
                    break;
                }
        }
        
        deleteRow((String)this.tableNameBox.getSelectedItem(), primaryKeyColumns, data);
        
        return true;
    }
    
    /**
     * Delete rows given WHERE conditions
     * @param tableName name of the table
     * @param columnNames name of columns used in WHERE condition
     * @param data data used for WHERE condition, same order as columnNames
     */
    void deleteRow(String tableName, ArrayList<String> columnNames, String[] data){
        
        String s;
        try {
            s = "DELETE FROM "+tableName+" WHERE ";
            
            s += columnNames.get(0)+"='"+data[0]+"'";
            
            for(int i=1; i<columnNames.size(); i++){
                s +=" AND " + columnNames.get(i)+"='"+data[i]+"'";
            }
            
           
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            
            System.out.println("getMetaData: "+ex.getMessage());
            jtAreaDeStatus.setText("Erro ao deletar registro: "+ ex.getMessage());
           

        }  
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
        
        //do search
        String s = getDataWhere((String) this.tableNameBox.getSelectedItem(), columnNames, data, false);
         try {
           
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
            
            //display data
            if(rs.next()){
                //get column names
                ResultSetMetaData rsmd = rs.getMetaData();
                int nDataColumns = rsmd.getColumnCount();
                String[] dataColumnNames = new String[nDataColumns];
                String[] retrievedData = new String[nDataColumns];
                
                for(int i=0; i<nDataColumns; i++){
                    dataColumnNames[i] = rsmd.getColumnName(i+1);
                    retrievedData[i] = rs.getString(dataColumnNames[i]);
                }
                BorderLayout layout = (BorderLayout) findPanel.getLayout();
                findPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
                
                this.displaySearchPanel = new JPanel();
                
               
                displaySearchedData(displaySearchPanel, dataColumnNames, retrievedData);
                
                findPanel.add(displaySearchPanel, BorderLayout.CENTER);
                findPanel.validate();
                 
            }else{
                BorderLayout layout = (BorderLayout) findPanel.getLayout();
                findPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
                findPanel.add(new JLabel("Registro não encontado. Tente novamente.", SwingConstants.CENTER), BorderLayout.CENTER);
                findPanel.validate();
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            
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
           

           //get MetaData
           String[] checkConstraints = getTableConstraintsString(tableName, "C");
           
           String dataType;
           String label;

           insertPanel.setLayout(new GridLayout(columnNames.length+1, 2));
           for(int i=0; i<columnNames.length; i++){

               label = columnNames[i];
              
               //name of column
               insertPanel.add(new JLabel(label));
               //data of the column
               insertField(tableName, columnNames[i], insertPanel, checkConstraints, data[i]);
               
            }
           
            insertPanel.add(new JLabel("Clique para alterar dados:"));
            JButton searchButton = new JButton("Salvar");
            insertPanel.add(searchButton);
            searchButton.addMouseListener(new java.awt.event.MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                   updateDataFromTable();
                   displayData(displayPanel);
                   
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
    
    /**
     * Update a row of data in the database
     * @param tableName name of the table
     * @param columns columns to be updated
     * @param data new value for listed columns
     */
    void updateDataColumns(String tableName,String[] columns, String[] data){
        
        String s = "UPDATE "+tableName+"\nSET ";
        
        //getting metadata
        this.getMetaData(tableName);
        
        //constructing query
        try{
            //columns to be updated
            for(int i=0; i<data.length; i++){
                //check columns type
                if(i != 0)
                    s+= ", ";
                
                s+=columns[i]+" = ";
                rs.next();
                
                String dataType = rs.getString("DATA_TYPE");
                if(dataType.equals("DATE")){
                    s+="TO_DATE('"+data[i]+"', 'YYYY-MM-DD HH24:MI:SS')";
                }else{
                    if(dataType.equals("BLOB")){
                        s+= "empty_blob()";
                    }else{
                        s+= "'"+data[i]+"'";
                    }
                }
            }
            s+=" WHERE ";
            //add where conditions
            s+=this.primaryKeyNames.get(0)+" = "+this.primaryKeyValues[0];
            for(int i=1; i<this.primaryKeyNames.size(); i++){
                s+=" AND "+this.primaryKeyNames.get(i)+" = "+this.primaryKeyValues[i];
            }
            
            
            rs.close();
            stmt.close();
            
            //executing update
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
            jtAreaDeStatus.setText("Dados atualizados!");
            rs.close();
            stmt.close();
        }catch(Exception e){
            String errorText = "Erro ao inserir dados na tabelas "+tableName+"\n";
            errorText+= e.getMessage();
            errorText+= "Formato da data a ser utilizado: 'YYYY-MM-DD HH24:MI:SS'";
            jtAreaDeStatus.setText(errorText);
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Generates DDL specified by the username and passwords given
     * Saves on a file named username.sql
     */
    public void getDDLFromTable(){
   
        String username, password;
        //get username
        JTextField aux;
        aux = (JTextField) schemaPanel.getComponent(1);
        username = aux.getText();
        
        //get password
        JPasswordField auxPassword;
        auxPassword = (JPasswordField) schemaPanel.getComponent(3);
        password = String.valueOf(auxPassword.getPassword()); 
        
        jtAreaDeStatus.setText("Gerando DDL...");
        jtAreaDeStatus.validate();
        getDDL(username, password);
        
        try{
            
            BufferedWriter outputWriter = null;
            outputWriter = new BufferedWriter(new FileWriter(username+".sql"));
            
            while(rs.next()){
                outputWriter.write(rs.getString("DDL_RESULTS"));
                outputWriter.newLine();
            }
            
            outputWriter.flush();  
            outputWriter.close(); 
            jtAreaDeStatus.setText("DDL salvo em "+username+".sql");
            
            rs.close();
            stmt.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
            
        }
       
    }
    
    /**
     * Gets DDL for given username and password
     * @param username
     * @param password 
     */
    void getDDL(String username, String password){
        
        Connection localConnection = null;
         try {
            //try connection from outside the laboratory
            DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
            localConnection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@grad.icmc.usp.br:15215:orcl",
                    username,
                    password);
         }catch(SQLException e){
             try{
                 localConnection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@192.168.183.15:1521:orcl",
                    username,
                    password);
                 
             }catch(SQLException e2){
                 jtAreaDeStatus.setText("Problema: verifique seu usuário e senha\n"+e2.getMessage());
             }
         }
         try{
            //settings plus get_ddl statement
            String[] s = {"begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'PRETTY', true); end;",
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'STORAGE', false); end;", 
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'TABLESPACE', false); end;" ,
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'SQLTERMINATOR', true); end;" ,
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'SEGMENT_ATTRIBUTES', false); end;" ,
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'OID', false); end;" ,
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'SPECIFICATION', false); end;" ,
                "begin dbms_metadata.set_transform_param (dbms_metadata.session_transform, 'EMIT_SCHEMA', false); end;" ,
                "SELECT DBMS_METADATA.GET_DDL('TABLE', TABLE_NAME) AS DDL_RESULTS FROM USER_TABLES"};
            
            stmt = localConnection.createStatement();
            for(int i=0; i<s.length-1; i++){

                stmt.addBatch(s[i]);
       
            }

            stmt.executeBatch();


            
            rs = stmt.executeQuery(s[s.length-1]);
            
        } catch(SQLException ex){
            jtAreaDeStatus.setText("Problema: verifique seu usuário e senha\n"+ex.getMessage());
            
        }
    }
}
