import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;


class PostgresAutocomplete
{
  private Map<String,ArrayList<String>> tablesColumns; 
  private TreeSet<String> tables; 
  Connection databaseConnection;
  String databaseName;
  String schemaName;

  PostgresAutocomplete(Connection c, String databaseName,String schemaName)
  {
    tablesColumns = new HashMap<String, ArrayList<String>>();
    databaseConnection = c;
    this.databaseName = databaseName;
    this.schemaName = schemaName;
  }

  @SuppressWarnings("unchecked")
  public void updateTablesColumns()
  {
    try {

      Statement stmt = databaseConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
      String sql = "select table_name,column_name from Information_Schema.columns where table_catalog = '"+databaseName+"' and table_schema = '"+schemaName+"' order by table_name"; 
      ResultSet rs = stmt.executeQuery(sql);

      int count = 0;
      while(rs.next())
      {
        ArrayList<String> columns = new ArrayList<String>();
        String lastTable = rs.getString("table_name");
        String currentTable = rs.getString("table_name");

        while(lastTable.equals(currentTable))
        {
          columns.add(rs.getString("column_name"));
          lastTable = currentTable;
          if(rs.next())
	  {
            currentTable = rs.getString("table_name");
          } else {
            break;
          }
        }
        
        rs.previous();
        tablesColumns.put(lastTable,columns);
      }

      rs.close();
      stmt.close();
      //databaseConnection.close();

      tables = new TreeSet((Collection)tablesColumns.keySet());

    } catch (Exception e) {

      e.printStackTrace();
      System.out.println(e.getClass().getName() + ": " + e.getMessage());

    }
  }

  public void printMapping()
  {
    int totalColumns = 0;
    for(String table : tablesColumns.keySet()){
      ArrayList<String> columnList = tablesColumns.get(table);
      System.out.println("Table: " + table);
      
      for(String column : columnList){
	totalColumns++;
        System.out.println(column);
      }
    }
    System.out.println("Total Columns: " + totalColumns);
  }

  @SuppressWarnings("unchecked")
  public Set<String> getTableSuggestions(String text)
  {
    Set<String> suggestionList = tables.tailSet(text, true);
    Set<String> tableSuggestions = new HashSet<String>();
    Iterator it = suggestionList.iterator();   
    boolean matchingThreshold = true;
 
    while(matchingThreshold && it.hasNext())
    {
      String current = (String)it.next();
      
      if(!text.regionMatches(true,0,current,0,text.length()))
      {
        matchingThreshold = false;
      }else{
        tableSuggestions.add(current);
      }
    } 

    return tableSuggestions;
  }
 
  public Set<String> getColumnSuggestions(String table, String text)
  {
    Set<String> columnSuggestion = new HashSet<String>();
     
    if(tablesColumns.containsKey(table))
    {
      ArrayList<String> columns = tablesColumns.get(table);
      
      Iterator it = columns.iterator();

      while(it.hasNext())
      {
        String current = (String)it.next(); 
 
        if(current.regionMatches(true,0,text,0,text.length()))
        {
          columnSuggestion.add(current);
        }
      }
    }
    return columnSuggestion;
  }

  public boolean isTableInSuggestions(String table)
  {
    return tablesColumns.containsKey(table);
  }

  public boolean isColumnInSuggestions(String table,String Column)
  {
    boolean columnExist = false;

    if(isTableInSuggestions(table))
    {
      ArrayList<String> columns = tablesColumns.get(table);

      if(columns.indexOf(Column) > -1)
      {
        columnExist = true;
      } 
    }
   
    return columnExist;
  }

  public boolean thiswilldonothing()
  { 

    System.out.println("what is it thdat everyone jj");
  }
}
