import java.lang.reflect.Method;
import java.util.*;


public class VickyHelper {

	private static String[] word;
	private static HashMap<String, Boolean> visit;
	private static HashMap<String, ArrayList<String>> AdjList;
	
	private static int editDistance(String A, String B)
	{
		int[][] dp = new int[A.length()+1][B.length()+1];
		for (int i = 0; i <= A.length(); ++i)
			dp[i][0] = i;
		for (int i = 0; i <= B.length(); ++i)
			dp[0][i] = i;
		
		for (int i = 1; i <= A.length(); ++i)
			for (int j = 1; j <= B.length(); ++j)
				if (A.charAt(i-1) == B.charAt(j-1))
					dp[i][j] = Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1])+1);
				else
					dp[i][j] = Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]))+1;
		
		//System.out.println(A + " " + B + ": " + dp[A.length()][B.length()]);
		return dp[A.length()][B.length()];
	}
	
	private static String dfs(String u, int i)
	{
		//System.out.println(u);
		if (visit.get(u) != null)
			return null;
		
		if (i == word.length - 1)
			return u;
		
		visit.put(u, true);
		
		String method = null;
		for (String v : AdjList.get(u))
		{
			if (v.equals("%n") || v.equals("%t"))
			{
				DBVicky.param = word[i+1];
				method = dfs(v, i+1);
				if (method != null)
					return method;
			}
			else if (DBVicky.Sinonimos.get(v) != null)
			{
				for (String sv : DBVicky.Sinonimos.get(v))
					if (editDistance(word[i+1].toLowerCase(), sv) <= 1)
					{
						method = dfs(v, i+1);
						if (method != null)
							return method;
					}
			}
			else
			{
				if (editDistance(word[i+1].toLowerCase(), v) <= 1)
				{
					method = dfs(v, i+1);
					if (method != null)
						return method;
				}
			}
		}
		
		return method;
	}
	
	public static Firma procesaQuery(String query)
	{
		String aux = "";
		boolean ok = false;
		for (int i = 0; i < query.length(); ++i)
		{
			if (query.charAt(i) == '"')
			{
				ok = !ok;
				continue;
			}
			
			if (ok && query.charAt(i) == ' ')
			{
				aux = aux + ':';
				continue;
			}
			aux += query.charAt(i);
		}
		
		query = "$ " + aux.replace('¿', ' ').replace('?', ' ').replace('.', ' ').replace(',', ' ').trim();
		
		word = query.split(" ");
		
		/*for (int i = 0; i < word.length; ++i)
			System.out.println(word[i]);
		System.out.println();*/
		
		for (int i = 0; i < DBVicky.Grafo.size(); ++i)
		{
			visit = new HashMap<String, Boolean>();	
			AdjList = DBVicky.Grafo.get(i);
			String terminal = dfs(word[0], 0);
			if (terminal != null && DBVicky.Terminales.get(i).get(terminal) != null)
				return DBVicky.Terminales.get(i).get(terminal);
		}
		
		return null;
	}
}
