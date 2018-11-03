/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aula05.oracleinterface;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author junio
 */
public class JanelaPrincipal {

    JFrame j;
    JPanel pPainelDeCima;
    JPanel pPainelDeBaixo;
    JComboBox jc;
    JTextArea jtAreaDeStatus;
    JTabbedPane tabbedPane;
    JPanel pPainelDeExibicaoDeDados;
    JTable jt;
    JPanel pPainelDeInsecaoDeDados;
    JButton insertButton;
    Neo4jFunctionality bd;
    JButton deleteButton;
    JButton ddlButton;
    JPanel findPanel;
    JPanel schemaPanel;

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
        pPainelDeCima.add(jc);

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
        tabbedPane.add(displayPanel, "Exibição");
        
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

        /*Tab de inserção*/
        pPainelDeInsecaoDeDados = new JPanel();
        pPainelDeInsecaoDeDados.setLayout(new GridLayout(nColunas+1, 2));
        pPainelDeInsecaoDeDados.add(new JLabel("Coluna1"));
        pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
        pPainelDeInsecaoDeDados.add(new JLabel("Coluna2"));
        pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
        pPainelDeInsecaoDeDados.add(new JLabel("Coluna3"));
        pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
        pPainelDeInsecaoDeDados.add(new JLabel("Clique para inserir:"));
        insertButton = new JButton("Confirmar");
        pPainelDeInsecaoDeDados.add(insertButton);
        tabbedPane.add(pPainelDeInsecaoDeDados, "Inserção");

      
        findPanel = new JPanel();
        findPanel.setLayout(new BorderLayout());
        
        tabbedPane.add(findPanel, "Busca");
        
        schemaPanel = new JPanel();
        schemaPanel.setLayout(new GridLayout(3,2));
        schemaPanel.add(new JLabel("Usuário:"));
        schemaPanel.add(new JTextField(""));
        schemaPanel.add(new JLabel("Senha"));
        schemaPanel.add(new JPasswordField());
        schemaPanel.add(new JLabel(""));
        
        ddlButton = new JButton("Gerar DDL");
        schemaPanel.add(ddlButton);
        
        ddlButton.addMouseListener(
                new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jtAreaDeStatus.setText("Gerando DDL...");
                jtAreaDeStatus.validate();
                pPainelDeBaixo.validate();
                j.validate();
                //bd.getDDLFromTable();
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
        
        
        tabbedPane.add(schemaPanel, "Gerar DDL");
        
        j.setVisible(true);

        bd = new Neo4jFunctionality(jtAreaDeStatus, pPainelDeInsecaoDeDados, jc, pPainelDeExibicaoDeDados, findPanel, schemaPanel);
        bd.connect("bolt://localhost:11001", "neo4j", "1234");
        bd.setLabels(jc);
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
        

        jc.addItemListener(
                new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent e) {
                JComboBox jcTemp = (JComboBox) e.getSource();
                updateDisplay();
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
    }
    
    void updateDisplay(){
        //jtAreaDeStatus.setText((String) jc.getSelectedItem()+bd.pegarMetaDados((String) jc.getSelectedItem()));     
        bd.getAllNodesAndDisplay(pPainelDeExibicaoDeDados);
        //bd.updateInsertTable((String)jc.getSelectedItem(), pPainelDeInsecaoDeDados, insertButton);
       //bd.updateFindPanel((String)jc.getSelectedItem());
    }
}
