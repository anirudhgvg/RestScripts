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


public class MyParser1 extends MyParser {

		
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
		while(l[len+1]!='<')
		{
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
			l2.append(l[len+1]);
			len++;
		}
		return l2.toString();
	}
	
	public void parse() throws IOException
	{
		int count1=0,count2=0,count3=0;
		int rid = 0,cid=0;
		String category = null,category1 = null,line = null;	
		String item=null,itDesc;	
	
		URL url = new URL("http://eatgaucho.com/menu");
		InputStream is = url.openConnection().getInputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( is,"UTF-8" )  );
		rid = getRestaurantId("Gaucho");
		while( ( line = reader.readLine() ) != null )  
		{	
			if(!line.isEmpty())
			{	
				if(line.contains("div"))
				{
					if(line.contains("pm-group"))
					{
						category=getTagName(line);
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
				else if(line.contains("td"))
				{
					if(line.contains("pmtitle2") || line.contains("pmtitle") )
					{
						item=getTagName(line);
						item = convertMixedCase(item);
						//map.put(category, item);
						insertItem(cid,item);
						count2++;
					}
					else if(line.contains("pmdesc"))
					{
						itDesc=getDescription(line);
						if(itDesc.contains("'"))
						{
							itDesc=tokenize(itDesc,"'");
						}
						updateDescription(cid,item,itDesc);
						count3++;
					}
				}
			}
		}
		reader.close();
		System.out.println("Category count "+count1);
		System.out.println("item count "+count2);
		System.out.println("Desc count "+count3);
		}
	
		
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MyParser1 p = new MyParser1();
		p.connectDb();
		p.parse();
		//p.display();
	}
}
