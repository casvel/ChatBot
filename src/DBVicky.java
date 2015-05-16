import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class DBVicky 
{
	static HashMap<String, String[]> Sinonimos;
	static ArrayList<HashMap<String, ArrayList<String>>> Grafo;
	static ArrayList<HashMap<String, Firma>> Terminales;
	
	static void initSinonimos()
	{
		Sinonimos = new HashMap<String, String[]>();
		
		Sinonimos.put("lider", new String[]{"lider", "líder", "encargado", "jefe"});
		Sinonimos.put("quien", new String[]{"quien", "quién"});
		Sinonimos.put("cuentame", new String[]{"cuentame", "cuéntame"});
		Sinonimos.put("quienes", new String[]{"quienes", "quiénes"});
		Sinonimos.put("integrantes", new String[]{"miembros", "integrantes"});
	}
	
	private static HashMap<String, ArrayList<String>> creaGrafo(String archivo, String argumentos, Method method) throws NoSuchMethodException, SecurityException, IOException
	{
		HashMap<String, ArrayList<String>> AdjList = new HashMap<String, ArrayList<String>>();
		HashMap<String, Firma> terminales = new HashMap<String, Firma>();
		
		AdjList.put("$", new ArrayList<String>());
		
		String line;
		BufferedReader br;
		
		br = new BufferedReader(new FileReader("grafo/"+archivo));

		line = br.readLine();		
		String[] T = line.split(" ");
		for (String t: T)
		{
			terminales.put(t, new Firma(method, new ArrayList<String>()));
			terminales.get(t).args.add(argumentos);
		}
		
		while (line != null)
		{
			String[] edges = line.split(" ");
			
			if (AdjList.get(edges[0]) == null)
				AdjList.put(edges[0], new ArrayList<String>());
			
			for(int i = 1; i < edges.length; ++i)
				AdjList.get(edges[0]).add(edges[i]);
				
			line = br.readLine();
		}
		
		br.close();
		
		Terminales.add(terminales);
		return AdjList;
	}
	
	static void initGrafo() throws NoSuchMethodException, SecurityException, IOException
	{
		Grafo = new ArrayList<HashMap<String, ArrayList<String>>>();
		Terminales = new ArrayList<HashMap<String, Firma>>();
		
		// Lider del proyecto //
		Grafo.add(creaGrafo("lider_proyecto.txt", "Lider", ChatBotController.class.getMethod("getEmpleados", String.class)));
		// Integrantes equipo //
		Grafo.add(creaGrafo("equipo.txt", "", ChatBotController.class.getMethod("getEmpleados", String.class)));
		// Chiste //
		Grafo.add(creaGrafo("joke.txt", null, ChatBotController.class.getMethod("tellJoke")));
		
		/*for (String u : AdjList.keySet())
		{
			System.out.print(u + "->");
			for (String v : AdjList.get(u))
				System.out.print(v + " ");
			System.out.println();
		}*/
	}
}
