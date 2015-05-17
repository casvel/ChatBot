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
	static String param;
	
	private static void initSinonimos()
	{
		Sinonimos = new HashMap<String, String[]>();
		
		Sinonimos.put("lider", new String[]{"lider", "líder", "encargado", "jefe"});
		Sinonimos.put("quien", new String[]{"quien", "quién"});
		Sinonimos.put("cuentame", new String[]{"cuentame", "cuéntame"});
		Sinonimos.put("quienes", new String[]{"quienes", "quiénes"});
		Sinonimos.put("integrantes", new String[]{"miembros", "integrantes"});
		Sinonimos.put("puedes", new String[]{"puedes", "podrías"});
		Sinonimos.put("equipo", new String[]{"equipo", "área", "area", "departamento"});
		Sinonimos.put("dias", new String[]{"dias", "días"});
		Sinonimos.put("que", new String[]{"que", "qué"});
		Sinonimos.put("como", new String[]{"como", "cómo"});
		Sinonimos.put("adios", new String[]{"adiós", "adios", "bye", "chao"});
		Sinonimos.put("termine", new String[]{"termine", "terminé", "acabe", "acabé", "finalicé", "finalice"});
	}
	
	private static HashMap<String, ArrayList<String>> creaGrafo(String archivo, String[] argumentos, Method method) throws NoSuchMethodException, SecurityException, IOException
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
			for (int i = 0; i < argumentos.length; ++i)
				terminales.get(t).args.add(argumentos[i]);
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
	
	private static void initGrafo() throws NoSuchMethodException, SecurityException, IOException
	{
		Grafo = new ArrayList<HashMap<String, ArrayList<String>>>();
		Terminales = new ArrayList<HashMap<String, Firma>>();
		
		// Saludo //
		Grafo.add(creaGrafo("saludo.txt", new String[]{null}, ChatBotController.class.getMethod("saludo")));
		// Lider del proyecto //
		Grafo.add(creaGrafo("lider.txt", new String[]{"Lider"}, ChatBotController.class.getMethod("getEmpleados", String.class)));
		// Integrantes equipo //
		Grafo.add(creaGrafo("equipo.txt", new String[]{""}, ChatBotController.class.getMethod("getEmpleados", String.class)));
		// Programador //
		Grafo.add(creaGrafo("programadores.txt", new String[]{"Programador"}, ChatBotController.class.getMethod("getEmpleados", String.class)));
		// Chiste //
		Grafo.add(creaGrafo("joke.txt", new String[]{null}, ChatBotController.class.getMethod("tellJoke")));
		// Adiós //
		Grafo.add(creaGrafo("adios.txt", new String[]{null}, ChatBotController.class.getMethod("adios")));
		// Jefe //
		Grafo.add(creaGrafo("jefe.txt", new String[]{"%n"}, ChatBotController.class.getMethod("getBoss", String.class)));
		// Puesto empleado //
		Grafo.add(creaGrafo("puesto.txt", new String[]{"%n"}, ChatBotController.class.getMethod("getPuesto", String.class)));
		// Wifi //
		Grafo.add(creaGrafo("wifi.txt", new String[]{null}, ChatBotController.class.getMethod("getWifi")));
		// Subordinados //
		Grafo.add(creaGrafo("subordinados.txt", new String[]{"%n"}, ChatBotController.class.getMethod("getSubordinados", String.class)));
		// Autoconciente //
		Grafo.add(creaGrafo("autoconciente.txt", new String[]{null}, ChatBotController.class.getMethod("autoconciente")));
		// Asigna tarea //
		Grafo.add(creaGrafo("asigna_tarea.txt", new String[]{"%t"}, ChatBotController.class.getMethod("setTarea", String.class)));
		// Termina tarea //
		Grafo.add(creaGrafo("termina_tarea.txt", new String[]{null}, ChatBotController.class.getMethod("terminarTareaEmpleado")));

		
		/*for (String u : Grafo.get(Grafo.size()-1).keySet())
		{
			System.out.print(u + "->");
			for (String v : Grafo.get(Grafo.size()-1).get(u))
				System.out.print(v + " ");
			System.out.println();
		}*/
	}
	
	
	public static void init() throws NoSuchMethodException, SecurityException, IOException
	{
		initGrafo();
		initSinonimos();	
	}
}
