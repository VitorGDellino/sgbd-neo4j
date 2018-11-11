/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aula05.oracleinterface;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 *
 * @author junio
 */
public class JanelaPrincipal {

    JFrame j;
    JPanel pPainelDeCima;
    JPanel pPainelDeBaixo;
    JComboBox jc;
    JComboBox jcRel;
    JTextArea jtAreaDeStatus;
    JTabbedPane tabbedPane;
    JPanel pPainelDeExibicaoDeDados;
    JPanel pPainelDeExibicaoDeRel;
    JTable jt;
    JTable jtRel;
    JPanel pPainelDeInsecaoDeDados;
    JPanel pPanelDeInsercaoDeRel;
    JPanel boxDeInsercaoDeDados;
    JButton insertButton;
    JButton insertFieldButton;
    JTextField columnName;
    Neo4jFunctionality bd;
    JButton deleteButton;
    JButton deleteButtonRel;
    JButton ddlButton;
    JPanel findPanel;
    JPanel schemaPanel;
    JComboBox jcN1;
    JComboBox jcN2;
    JPanel panelRel;
    JButton directButton;
    JButton inverseButton;
    JList list1;
    JList list2;
    DefaultListModel lmodel1;
    DefaultListModel lmodel2;
    
    ArrayList<JLabel> inputLabels;
    ArrayList<JTextField> inputFields;

    public void ExibeJanelaPrincipal() {
        /*Janela*/
        j = new JFrame("ICMC-USP - SCC0241 - Trabalho Final");
        j.setSize(700, 500);
        j.setLayout(new BorderLayout());
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*Painel da parte superior (north) - com combobox e outras informações*/
        pPainelDeCima = new JPanel();
        j.add(pPainelDeCima, BorderLayout.NORTH);
        jc = new JComboBox();
        pPainelDeCima.add(new JLabel("Nós:"));
        pPainelDeCima.add(jc);
        pPainelDeCima.add(new JLabel("Rel.:"));
        jcRel = new JComboBox();
        pPainelDeCima.add(jcRel);

        /*Painel da parte inferior (south) - com área de status*/
        pPainelDeBaixo = new JPanel();
        j.add(pPainelDeBaixo, BorderLayout.SOUTH);
        jtAreaDeStatus = new JTextArea();
        jtAreaDeStatus.setText("Aqui é sua área de status");
        pPainelDeBaixo.add(jtAreaDeStatus);
        

        /*Painel tabulado na parte central (CENTER)*/
        tabbedPane = new JTabbedPane();
        j.add(tabbedPane, BorderLayout.CENTER);

        /*Tab de exibicao*/
        pPainelDeExibicaoDeDados = new JPanel();
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.PAGE_AXIS));
        pPainelDeExibicaoDeDados.setLayout(new GridLayout(1, 1));
        displayPanel.add(pPainelDeExibicaoDeDados);
        
        deleteButton = new JButton("Excluir registro");
        displayPanel.add(deleteButton);
        //tabbedPane.add(pPainelDeExibicaoDeDados, "Exibição");
        tabbedPane.add(displayPanel, "Exibição - Nós");
        
        /*Table de exibição*/
        int nColunas = 3;
        String colunas[] = new String[nColunas];
        colunas[0] = "Coluna1";
        colunas[1] = "Coluna2";
        colunas[2] = "Coluna3";
        int nTuplas = 4;
        String dados[][] = new String[nTuplas][nColunas];
        dados[0][0] = "d00";
        dados[0][1] = "d10";
        dados[0][2] = "d20";
        dados[1][0] = "d10";
        dados[1][1] = "d11";
        dados[1][2] = "d21";
        dados[2][0] = "d20";
        dados[2][1] = "d12";
        dados[2][2] = "d22";
        dados[3][0] = "d30";
        dados[3][1] = "d13";
        dados[3][2] = "d23";
        jt = new JTable(dados, colunas);
        JScrollPane jsp = new JScrollPane(jt);
        pPainelDeExibicaoDeDados.add(jsp);
        
       
        
        /*Tab de exibicao de relacionamentos*/
        pPainelDeExibicaoDeRel = new JPanel();
        JPanel displayPanelRel = new JPanel();
        displayPanelRel.setLayout(new BoxLayout(displayPanelRel, BoxLayout.PAGE_AXIS));
        pPainelDeExibicaoDeRel.setLayout(new GridLayout(1, 1));
        displayPanelRel.add(pPainelDeExibicaoDeRel);
        
        jtRel = new JTable(dados, colunas);
        JScrollPane jspRel = new JScrollPane(jtRel);
        pPainelDeExibicaoDeRel.add(jspRel);
        
        deleteButtonRel = new JButton("Excluir relacionamento");
        displayPanelRel.add(deleteButtonRel);
        
        tabbedPane.add(displayPanelRel, "Exibição - Rel.");
        
        
        
        

        /*Tab de inserção*/
        pPainelDeInsecaoDeDados = new JPanel();
        
        pPainelDeInsecaoDeDados.setLayout(new BoxLayout(pPainelDeInsecaoDeDados, BoxLayout.PAGE_AXIS));
        boxDeInsercaoDeDados = new JPanel();
        boxDeInsercaoDeDados.setLayout(new BoxLayout(boxDeInsercaoDeDados, BoxLayout.PAGE_AXIS));
        pPainelDeInsecaoDeDados.add(boxDeInsercaoDeDados);    
        
        JPanel boxDeBotoes = new JPanel();
        boxDeBotoes.setLayout(new BoxLayout(boxDeBotoes, BoxLayout.LINE_AXIS));
        
        insertButton = new JButton("Confirmar");
        insertFieldButton = new JButton("Inserir Novo Campo");
        columnName = new JTextField("Nome do Campo");
        
        boxDeBotoes.add(insertButton);
        boxDeBotoes.add(insertFieldButton);
        boxDeBotoes.add(columnName);        
        pPainelDeInsecaoDeDados.add(boxDeBotoes);
        
        tabbedPane.add(pPainelDeInsecaoDeDados, "Inserção");
      
        findPanel = new JPanel();
        findPanel.setLayout(new BorderLayout());
        
        tabbedPane.add(findPanel, "Busca");
        
       /*Tab de inserção de relação*/
       pPanelDeInsercaoDeRel = new JPanel(new GridBagLayout());
       GridBagConstraints c = new GridBagConstraints();
       
       tabbedPane.add(pPanelDeInsercaoDeRel, "Inserção de Relação");
       lmodel1 = new DefaultListModel();
       lmodel2 = new DefaultListModel();
       list1 = new JList(lmodel1);
       list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       list1.setLayoutOrientation(JList.VERTICAL_WRAP);
       list1.setVisibleRowCount(-1);
       list2 = new JList(lmodel2);
       list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       list2.setLayoutOrientation(JList.VERTICAL_WRAP);
       list2.setVisibleRowCount(-1);
       
       
       jcN1 = new JComboBox();
       jcN2 = new JComboBox();
       panelRel = new JPanel(new GridBagLayout());
       directButton = new JButton(">>");
       inverseButton = new JButton("<<");
       panelRel.add(directButton);
       panelRel.add(inverseButton);
       c.anchor = GridBagConstraints.FIRST_LINE_START;
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 1;
       c.weighty = 0.95;
       c.gridx = 0;
       c.gridy = 1;
       pPanelDeInsercaoDeRel.add(new JScrollPane(list1), c);
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 1;
       c.weighty = 0.01;
       c.gridx = 0;
       c.gridy = 0;
       pPanelDeInsercaoDeRel.add(jcN1, c);
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 1;
       c.weighty = 0.95;
       c.gridx = 1;
       c.gridy = 0;
       c.gridheight = 2;
       pPanelDeInsercaoDeRel.add(panelRel, c);
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 1;
       c.weighty = 0.01;
       c.gridx = 2;
       c.gridy = 0;
       c.gridheight = 1;
       pPanelDeInsercaoDeRel.add(jcN2, c);
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 1;
       c.weighty = 0.95;
       c.gridx = 2;
       c.gridy = 1;
       pPanelDeInsercaoDeRel.add(new JScrollPane(list2), c);
       
       
       
        
        j.setVisible(true);

        bd = new Neo4jFunctionality(jtAreaDeStatus, pPainelDeInsecaoDeDados, jc,jcRel, pPainelDeExibicaoDeDados, findPanel);
        bd.connect("bolt://localhost:11001", "neo4j", "1234");
        bd.setLabels(jc);
        bd.setLabels(jcN1);
        bd.setLabels(jcN2);
        bd.setRelationshipTypes(jcRel);
        updateDisplay();
      
        
        this.DefineEventos();
    }

    private void DefineEventos() {
 
        deleteButton.addMouseListener(
                new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(bd.deleteSelectedNode())
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
        }
        );
        
        deleteButtonRel.addMouseListener(
                new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(bd.deleteSelectedRelationship())
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
        }
        );
        

        jc.addItemListener(
                new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent e) {
                JComboBox jcTemp = (JComboBox) e.getSource();
                updateDisplay();
               // bd.checkForeignKey("insert");
            }
        });
        
        jcRel.addItemListener(
                new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent e) {
                JComboBox jcTemp = (JComboBox) e.getSource();
                bd.getAllRelationshipsAndDisplay(pPainelDeExibicaoDeRel);
               // bd.checkForeignKey("insert");
            }
        });
        
        
        insertButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //bd.insertDataFromTable(pPainelDeInsecaoDeDados, (String)jc.getSelectedItem());
                //bd.displayData(pPainelDeExibicaoDeDados);
                
            }

        });
        
        insertFieldButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
                panel.add(new JLabel(columnName.getText().toString()));
                panel.add(new JTextField(20));
                boxDeInsercaoDeDados.add(panel); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        jcN1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                lmodel1.clear();
                for(String s : bd.getNodes(jcN1.getSelectedItem().toString())) {
                    lmodel1.addElement(s);
                }
            }
        });
        
        jcN2.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                lmodel2.clear();
                for(String s : bd.getNodes(jcN2.getSelectedItem().toString())) {
                    lmodel2.addElement(s);
                }
            }
        });
        
        directButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(list1.getSelectedValue() == null || list2.getSelectedValue() == null){
                    jtAreaDeStatus.setText("Selecione os dois nós");
                    return;
                }
                bd.createRelationship(jcN1.getSelectedItem().toString(), list1.getSelectedValue().toString(), jcN2.getSelectedItem().toString(), list2.getSelectedValue().toString(), jcRel.getSelectedItem().toString());
                updateDisplay();
            }
        });
        
        inverseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if(list1.getSelectedValue() == null || list2.getSelectedValue() == null){
                    jtAreaDeStatus.setText("Selecione os dois nós");
                    return;
                }
                bd.createRelationship(jcN2.getSelectedItem().toString(), list2.getSelectedValue().toString(), jcN1.getSelectedItem().toString(), list1.getSelectedValue().toString(), jcRel.getSelectedItem().toString());
                updateDisplay();
            }
        });
    }
    
    void updateDisplay(){
        //jtAreaDeStatus.setText((String) jc.getSelectedItem()+bd.pegarMetaDados((String) jc.getSelectedItem()));     
        bd.getAllNodesAndDisplay(pPainelDeExibicaoDeDados);
        bd.getAllRelationshipsAndDisplay(pPainelDeExibicaoDeRel);
        //bd.updateInsertTable((String)jc.getSelectedItem(), pPainelDeInsecaoDeDados, insertButton);
        bd.updateFindPanel((String)jc.getSelectedItem());
    }
}
