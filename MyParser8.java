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

public class MyParser8 extends MyParser{

	public String getTagName(String line)
	{
		char[] l;
		int len=0;
		StringBuffer l2 = new StringBuffer("");
		l=line.toCharArray();
		if(l[len]!='<')
		{
			return null;
		}
		
		while(l[len]!='>')
		{
			len++;
		}
		if(l[len+1]=='<')
		{
			return null;
		}
		while(l[len+1]!='<')
		{
			l2.append(l[len+1]);
			len++;
		}
		String temp = l2.toString();
		if(temp.contains("("))
		{
			temp=temp.substring(0,8);
		}
		return temp;
	}
	
	public String getItemName(String line)
	{
		char[] l;
		int len=0;
		StringBuffer l2 = new StringBuffer("");
		l=line.toCharArray();
		
		while(l[len]!='>')
		{
			len++;
		}
		if(l[len+1]!='<')
		{
			while(l[len+1]!='<')
			{
				l2.append(l[len+1]);
				len++;
			}
		}
		else
		{
			while(l[len+1]!='>')
			{
				len++;
			}
			len++;
			if(l[len+1]=='<')
			{
				return null;
			}
			if(l[len+1]=='*') 
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
		len=len+7;
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
	
		URL url = new URL("http://altiuspgh.com/menus/food/");
		InputStream is = url.openConnection().getInputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( is,"UTF-8" )  );
		rid = getRestaurantId("Altius");
		while( ( line = reader.readLine() ) != null)  
		{	
			if(!line.isEmpty())
			{	
					if(line.contains("<h2>") && !line.contains("Wednesday") && line.contains("</h2>") )
					{
						category=getTagName(line);
						if(category!=null)
						{
							
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
					}
					else if(line.contains("<p>") && line.contains("<strong>") && !line.contains("ref") )
					{
						item=getItemName(line);
						if(item.length()>1)
						{
							if(item.contains("&amp;") || item.contains(",") )
							{
							item = tokenize(item,"&amp; ");
							item = tokenize(item,",");
							}
							if(item.contains("|"))
							{
							item = item.substring(0, (item.length()-3));
							}
							item = convertMixedCase(item);
							insertItem(cid,item);	
							//map.put(category, item);
							itCount++;
						}
						
						if(!line.contains(">E<"))
						{
							itemDesc=getDescName(line);
							if(itemDesc.length()>1)
							{
								if(itemDesc.contains("|"))
								{
									itemDesc = itemDesc.substring(2,itemDesc.length());
								}
								if(itemDesc.contains("&amp;"))
								{
									itemDesc = tokenize(itemDesc,"&amp; ");
								}
								
								itemDesc=itemDesc.trim();
								updateDescription(cid,item,itemDesc);	
								descCount++;
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
		MyParser8 p = new MyParser8();
		p.connectDb();
		p.parse();
	}
}
