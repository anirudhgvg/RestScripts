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

public class MyParser4 extends MyParser{
		
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
		int catCount=0,itCount=0,descCount = 0;
		int rid = 0,cid=0;
		String category = null,category1 = null,line = null;	
		String item = null,itemDesc;	
	
		URL url = new URL("http://www.smilingbananaleaf.com/?page_id=277");
		InputStream is = url.openConnection().getInputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( is,"UTF-8" )  );
		rid = getRestaurantId("Smiling Banana");
		while( ( line = reader.readLine() ) != null && itCount<56)  
		{	
			if(!line.isEmpty())
			{	
					if(line.contains("menu-list__title"))
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
					else if(line.contains("menu-list__item-title"))
					{
						if(!line.contains("<em>") && !line.contains("LUNCH") && !line.contains("DINNER"))
						{
						item=getItemName(line);
						
						if(item.contains("amp; "))
						{
							item = tokenize(item,"amp; ");
						}
						item = convertMixedCase(item);
						insertItem(cid,item);
						//map.put(category, item);
						itCount++;
						}
					}
					else if(line.contains("menu-list__item-desc") && !line.contains("<em>"))
					{
						itemDesc=getItemName(line);
						if(!(itemDesc.length()==0))
						{
							if(itemDesc.contains("&amp;"))
							{
								itemDesc = tokenize(itemDesc,"&amp;");
							}
							updateDescription(cid,item,itemDesc);	
							descCount++;
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
		MyParser4 p = new MyParser4();
		p.connectDb();
		p.parse();
	}
}
