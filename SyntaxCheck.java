import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Random;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.awt.event.*;
import java.lang.Runtime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

class SyntaxCheck extends JTextArea
{
  SyntaxCheck()
  {

  }
  
  public static void main(String [] args)
  {
    //Instantiation of variables
    Connection c = null;
    Statement stmt = null;

    try
    {
      Class.forName("org.postgresql.Driver");
      c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/northwind","chris","123");
      c.setAutoCommit(false);

      System.out.println("Opened database successfully");
    } catch (Exception e ) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": "+ e.getMessage());
      System.exit(0);
    } 

    JFrame frame = new JFrame("Syntax Check");
    SyntaxCheck syntax = new SyntaxCheck();
    //JTextArea suggestionArea = new JTextArea();
    //String[] sampleList = {"One","Two","Three","Four","Five"};
    DefaultListModel<String> listModel = new DefaultListModel<>();     
    JList<String> suggestionList = new JList<>(listModel);
    JTextArea terminal = new JTextArea();
    JPanel panel = new JPanel();
    JPanel panelButtons = new JPanel();
    JPanel panelTypeSuggestion = new JPanel();
    JScrollPane scrollType = new JScrollPane(syntax);
    JScrollPane listScrollPane = new JScrollPane(suggestionList);
    JScrollPane scrollDisplay = new JScrollPane(terminal);
    JButton compileButton = new JButton("Compile");
    JTabbedPane tabbedPane = new JTabbedPane();
    PostgresAutocomplete PA = new PostgresAutocomplete(c,"northwind","public");



    //ImageIcon icon = createImageIcon("tabIcon.png");  


    //Configure objects
    compileButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
	terminal.setText(null);

	SyntaxChecker check = new SyntaxChecker();
	StringBuilder returnMessage = new StringBuilder();	

	if(check.CheckSyntax(syntax.getText(),returnMessage))
	{
		terminal.append("Correct Syntax\n");
	}else{
		terminal.append(returnMessage.toString());
	}
      }
    });

    syntax.addKeyListener(new KeyListener(){
  
      public void keyTyped(KeyEvent e) {
      
      }

      public void keyPressed(KeyEvent e) {

      }

      public void keyReleased(KeyEvent e) {
        JTextArea textBox = (JTextArea) e.getSource();
        String text = textBox.getText();
        int currentCaretPosition = textBox.getCaretPosition();

        if(e.getKeyChar() == KeyEvent.VK_TAB && !(suggestionList.isSelectionEmpty()))
        {
          if(text.contains(" "))
          {
            textBox.replaceRange("",text.lastIndexOf(" ") + 1,text.length());
            textBox.append(suggestionList.getSelectedValue());
            textBox.setCaretPosition((int)textBox.getText().length());
          } else if(!text.contains(" "))
          {
            textBox.setText(suggestionList.getSelectedValue()); 
          } 
        } else {
          listModel.clear();
          if(text.contains(" "))
          {
	    boolean caretAtEnd = (currentCaretPosition == text.length());
            boolean spaceAfterCaret = false;

            if(!caretAtEnd)
            {
              spaceAfterCaret = true;
           }

            if((text.lastIndexOf(" ") + 1) < text.length())
            {
              String lastWord = text.substring(text.lastIndexOf(" ") + 1, text.length());
              //System.out.println("textLength: " + text.length());
              //System.out.println("currentCarePosition: " + currentCaretPosition);
              //System.out.println("lastIndexOf: " + (text.lastIndexOf(" ") + 1));
              //System.out.println(lastWord);
              //String result = PA.getSuggestion(lastWord);
              //System.out.println(result);

              //if(!((result == null) || (lastWord == null)))
              //{
              //  textBox.replaceRange("",text.lastIndexOf(" ") + 1,text.length());
              //  textBox.append(result);
              //  textBox.setCaretPosition(currentCaretPosition);
              //}
              //System.out.println("criteria reached");
              //String lastWord = text.substring(text.lastIndexOf(" ") + 1);
              //String extension = "Extend";
              //textBox.insert(extension,text.lastIndexOf(" ") + 1);
              //System.out.println(lastWord);
              Set<String> results = PA.getTableSuggestions(lastWord);

              if(!results.isEmpty()) 
              {
                //System.out.println("List of Suggestions:");
                Iterator it = results.iterator();

                while(it.hasNext())
                {
                  listModel.addElement((String)it.next());
                }
                suggestionList.setSelectedIndex(0);
              }
            }
          } else if(!(text.contains(" ") || text.isEmpty())){
            Set<String> results = PA.getTableSuggestions(text);

            if(!results.isEmpty()) 
            {
              //System.out.println("List of Suggestions:");
              Iterator it = results.iterator();

              while(it.hasNext())
              {
                listModel.addElement((String)it.next());
              }
              suggestionList.setSelectedIndex(0);
            }

            //String result = PA.getSuggestion(text);
            //System.out.println(result);
          }
      }}
    });

    panel.setLayout(null);
    syntax.setFocusTraversalKeysEnabled(false);
    terminal.setBackground(Color.BLACK);
    terminal.setForeground(Color.WHITE);
    frame.setSize(720,330);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    scrollType.setMaximumSize(new Dimension(500,100));
    scrollDisplay.setMaximumSize(new Dimension(500,100));
    listScrollPane.setMaximumSize(new Dimension(200,100));
    suggestionList.setMaximumSize(new Dimension(100,150));
    suggestionList.setMinimumSize(new Dimension(100,40));
    syntax.setMaximumSize(new Dimension(500,150));
    syntax.setMinimumSize(new Dimension(100,40));
    terminal.setMaximumSize(new Dimension(500,100));
    terminal.setMinimumSize(new Dimension(100,40));
    tabbedPane.setMaximumSize(new Dimension(700,200));
    tabbedPane.setMinimumSize(new Dimension(100,40));
    suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   

    //Setting up Database Connection
    

    PA.updateTablesColumns();
    //PA.printMapping();    

    //System.out.println(PA.getColumn());

    //Add object to panel 
    //listScrollPane.add(suggestionList); 
    panelButtons.setLayout(new BoxLayout(panelButtons,BoxLayout.X_AXIS)); 
    panelTypeSuggestion.setLayout(new BoxLayout(panelTypeSuggestion,BoxLayout.X_AXIS));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
 
    panelButtons.add(compileButton);
    panelTypeSuggestion.add(scrollType);
    panelTypeSuggestion.add(listScrollPane);
    tabbedPane.addTab("Tab 1", scrollDisplay);
    tabbedPane.setMnemonicAt(0,KeyEvent.VK_1);

    panel.add(panelButtons);
    panel.add(panelTypeSuggestion);
    //panel.add(scrollType);
    //panel.add(scrollDisplay);
    panel.add(tabbedPane);
    //panel.add(listScrollPane);

    frame.add(panel);
    frame.setVisible(true);
  }
} 
