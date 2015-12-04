import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyParser {

	Connection conn;
	public void connectDb()
	{
		String url = "jdbc:mysql://localhost:3306/rest";
		String username = "root";
		String password = "root";

		try 
		{
		    Class.forName("com.mysql.jdbc.Driver");
		    System.out.println("Driver loaded!");
		    try  
		    {
		    	
		    	conn = DriverManager.getConnection(url, username, password);
			    System.out.println("Database connected!");
			} 
		    catch (SQLException e) 
		    {
			    throw new IllegalStateException("Cannot connect the database!", e);
			}
		} 
		
		catch (ClassNotFoundException e) 
		{
		    throw new IllegalStateException("Cannot find the driver in the classpath!", e);
		}
	}
	
	public int getRestaurantId(String resName)
	{
		int resId = 0;
		Statement stmt;
		try 
		{	
			stmt = conn.createStatement();
			String sql1="SELECT rid FROM restaurant WHERE rname LIKE '%"+resName+"%'";
			ResultSet rs = stmt.executeQuery(sql1);
			while (rs.next()) 
			{
				resId = rs.getInt("rid");
				System.out.println(resId + "is resId");
			}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resId;
	}
	
	public int getCategoryId(int resId,String catName)
	{
		int catId = 0;
		try {
			String sql1="SELECT cid FROM category WHERE cname ='"+catName+"' and resId="+resId;
			//System.out.println(sql1);
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql1);
			while (rs.next()) {
				  catId = rs.getInt("cid");
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return catId;
	}
	
	public boolean ifCategoryExists(int resId,String catName)
	{
		try {
			String sql1="SELECT cid FROM category WHERE cname ='"+catName+"' and resId="+resId;
			//System.out.println(sql1);
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.isBeforeFirst() ) {  
				 return true; 
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void insertCategory(int resId,String cat)
	{
		Statement stmt;
		try
		{	
			if(!ifCategoryExists(resId,cat))
			{
			stmt = conn.createStatement();
			String sql2 = "INSERT INTO category(cname,resid) VALUES ('"+cat+"', "+resId+")";
			System.out.println(sql2);
			stmt.executeUpdate(sql2);
			}
			else
			{
				System.out.println("Category already exists");
			}
		} 
		catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public boolean ifItemExists(int catId,String it)
	{
		try {
			String sql1="SELECT itid FROM item WHERE itname ='"+it+"' and catid="+catId;
			//System.out.println(sql1);
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.isBeforeFirst() ) {  
				 return true; 
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void insertItem(int catId,String it)
	{
		try
		{
			if(!ifItemExists(catId,it))
			{
			Statement stmt = conn.createStatement();
			String sql2 = "INSERT INTO item(itname,itdesc,catid) VALUES ('"+it+"','', "+catId+")";
			System.out.println(sql2);
			stmt.executeUpdate(sql2);
			}
			else
			{
				System.out.println("Item already exists");
			}
		} 
		catch (SQLException e) {
			System.out.println("Item cannot be inserted");
			e.printStackTrace();
		}
	}
	
	public void updateDescription(int catId,String it,String itdesc)
	{
		try
		{	
			Statement stmt;
			if(ifItemExists(catId,it))
			{
			stmt = conn.createStatement();
			String sql2 = "UPDATE item SET itdesc = '"+itdesc+"' WHERE itname = '"+it+"'";
			System.out.println(sql2);
			stmt.executeUpdate(sql2);
			}
			else
			{
				System.out.println("Item doesn't exist");
			}
		} 
		catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public String tokenize(String str,String token)
	{
		int i = 0;
		String items[]=str.split(token);
		str = "";
		for( i = 0; i<items.length;i++)
		{
			str=str.concat(items[i]);
		}
		return str;
	}
	
	public String toProperCase(String s) {
		if (s.length()==0)
			return "";
	    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	public String convertMixedCase(String str)
	{	
			   String[] parts = str.split(" ");
			   String camelCaseString = "";
			   for (String part : parts){
				   if(camelCaseString.equals(""))
				   {
					   camelCaseString = camelCaseString + toProperCase(part);
				   }
				   else
				   {
					   camelCaseString = camelCaseString +" "+ toProperCase(part);   
				   }
			   }
			   return camelCaseString;
			}
}
