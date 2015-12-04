import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MyParser2 extends MyParser {
		
	public String getTagName(String line)
	{
		char[] l;
		int len=0;
		StringBuffer l2 = new StringBuffer("");

		l=line.toCharArray();
		while(l[len]!='>')
		{
			len++;
		}
		while(l[len+1]!='>')
		{
			len++;
		}
		len++;
		while(l[len+1]!='<')
		{
			if(l[len+1]=='#')
			{
				len = len + 7;
			}
			l2.append(l[len+1]);
			len++;
		}
		return l2.toString();
	}

	public String getDescription(String line)
	{
		char[] l;
		int len=0;
		StringBuffer l2 = new StringBuffer("");

		l=line.toCharArray();
		while(l[len]!='<')
		{
			if(l[len]=='#')
			{
				len = len + 7;
			}
			l2.append(l[len]);
			len++;
		}
		return l2.toString();
	}

	public void parse() throws IOException
	{
		int count1=0,count2=0,count3=0;
		int rid = 0,cid=0;
		String category = null,category1 = null,line = null;	
		String item=null,desc;	
	
		URL url = new URL("http://www.szmidts.com/menu/");
		InputStream is = url.openConnection().getInputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( is,"UTF-8" )  );
		rid = getRestaurantId("szmid");
		while( ( line = reader.readLine() ) != null && count1 < 8 && count2 < 23 )  
		{	
			if(!line.isEmpty())
			{	
				if(line.contains("h1") && count1 < 7)
				{
					if(line.contains("strong"))
					{
						category=getTagName(line);
						if(!category.equalsIgnoreCase("MENU"))
						{
							category = convertMixedCase(category);
							insertCategory(rid,category);
							if(category1 != category)
							{
								category1=category;
								cid=getCategoryId(rid,category1);
							}
							count1++;
						}
										}
				}
				else if(line.contains("<p") )
				{
					if(line.contains("strong") )
					{
						item=getTagName(line);
						item = convertMixedCase(item);
						insertItem(cid,item);
						count2++;
					}
				}
				else if(line.contains("<br"))
				{
					if(!line.contains("$") && !line.contains("¢") )
					{
					desc = getDescription(line);
					updateDescription(cid,item,desc);
					count3++;
					}
				}
			}
		}
		reader.close();
		System.out.println("Category count "+count1);
		System.out.println("item count "+count2);
		System.out.println("desc count "+count3);
		}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MyParser2 p = new MyParser2();
		p.connectDb();
		p.parse();
		//p.display();
	}
}
