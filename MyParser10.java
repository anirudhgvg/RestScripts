import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MyParser10 extends MyParser{
		
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
		while(l[len+1]!='/')
		{
			len++;
		}
		len=len+9;
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
	
		URL url = new URL("http://dinette-pgh.com/daily-menu");
		InputStream is = url.openConnection().getInputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( is,"UTF-8" )  );
		rid = getRestaurantId("Dinette");
		while( ( line = reader.readLine() ) != null && itCount<21)  
		{	
			if(!line.isEmpty())
			{	
					if(line.contains("<h4>") && line.contains("</h4>") && !line.contains("Notes"))
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
					else if(line.contains("<li>"))
					{
						item=getTagName(line);
						if(item.length()>1)
						{
							if(item.contains(","))
							{
								item = tokenize(item,",");
							}
							item = convertMixedCase(item);
							insertItem(cid,item);
							//map.put(category, item);
							itCount++;
						}
						
						if(line.contains("<p>"))
						{
							itemDesc=getDescName(line);
							//System.out.println(itemDesc);
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
		MyParser10 p = new MyParser10();
		p.connectDb();
		p.parse();
	}
}
