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

public class MyParser6 extends MyParser{
		
	public String getTagName(String line)
	{
		char[] l;
		int len=0;
		StringBuffer l2 = new StringBuffer("");
		l=line.toCharArray();
		if(l[len]!='<')
		{
			while(l[len]!='<')
			{
				l2.append(l[len]);
				len++;
			}
				
		}
		else
		{
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
		}
		return l2.toString();
	}
	
	public String getDescName(String line)
	{
		char[] l;
		int len=0;
		StringBuffer l2 = new StringBuffer("");

		l=line.toCharArray();
		while(l[len]!='/')
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
		int catCount=0,itCount=0,descCount = 0;
		int rid = 0,cid=0;
		String category = null,category1 = null,line = null;	
		String item = null,itemDesc;	
	
		URL url = new URL("http://www.theporchatschenley.com/menu_lunch.aspx");
		InputStream is = url.openConnection().getInputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( is,"UTF-8" )  );
		rid = getRestaurantId("The Porch");
		while( ( line = reader.readLine() ) != null && itCount<22)  
		{	
			if(!line.isEmpty())
			{	
					if(line.contains("</h1>") && line.length()>12)
					{
						category=getTagName(line);
						//al.add(category);
						category = convertMixedCase(category);
						insertCategory(rid,category);
						if(category1 != category)
						{
							category1=category;
							cid=getCategoryId(rid,category1);
						}
						catCount++;
					}
					else if(line.contains("<p>"))
					{	
						if(!line.contains("Pennsylvania") && !line.contains("Lunch"))
						{
							item=getTagName(line);
							if(item.contains("&amp;"))
							{
								item = tokenize(item,"&amp; ");
							}
							if(item.contains("'"))
							{
								item = tokenize(item,"'");
							}
							
							if(item.length()>1)
							{
							item = item.substring(0, (item.length()-2));
							item = convertMixedCase(item);
							insertItem(cid,item);
							//map.put(category, item);
							itCount++;
							}
							
							if(!line.contains("Add") && !line.contains("*"))
							{
								itemDesc=getDescName(line);
								if(itemDesc.contains("&amp;"))
								{
								itemDesc = tokenize(itemDesc,"&amp; ");
								}
								if(!itemDesc.contains(">"))
								{
								updateDescription(cid,item,itemDesc);	
								descCount++;
								}
							}
						}
					}
			}
		}
		reader.close();
		System.out.println("Category count "+catCount);
		System.out.println("item count "+itCount);
		System.out.println("Desc count "+descCount);
		}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MyParser6 p = new MyParser6();
		p.connectDb();
		p.parse();
	}
}
