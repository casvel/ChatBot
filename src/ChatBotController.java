import java.sql.*;
import java.util.*;
import java.util.Date;

public class ChatBotController 
{
	private static Connection con;
	
	/* Conexión con la base de datos*/
	static void Conecta()
	{
		String host = "jdbc:mysql://localhost:3306/Empresa";
		String user = "admin";
		String pass = "guapa";
		
		try
		{
			con = DriverManager.getConnection(host, user, pass);
			con.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
	}
	
	/* Obtiene los empleados dado un puesto.
	 * Si puesto == "" regresa todos los empleados */
	public static Resultado<ArrayList<String>> getEmpleados(String puesto)
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		try
		{
			Statement st = con.createStatement();
			ResultSet rs;
			
			if (puesto != "")
				 rs = st.executeQuery("SELECT Empleado.id, Empleado.nombre, Empleado.paterno, Empleado.materno "
						+ "FROM Empleado, Puesto "
						+ "WHERE Empleado.puesto_id = Puesto.id AND Puesto.nombre ='" + puesto + "'");
			else
				rs = st.executeQuery("SELECT id, nombre, paterno, materno "
						+ "FROM Empleado");
			
			int num = 0;
			while (rs.next())
			{
				num++;
				result.Valor.add(rs.getString("id") + ". " + rs.getString("nombre") + ' ' + rs.getString("paterno") + ' ' + rs.getString("materno") + "\n");
			}
			result.Valor.add(0, (num > 1 ? "Las personas" : "La persona") + " con el puesto de " + puesto.toLowerCase() + (num > 1 ? " son" : " es") + ":\n");
			
			result.Success = true;
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}

	/*Obtiene el puesto de un empleado dado un nombre*/
	public static Resultado <ArrayList <String> > getPuesto(String nombre)
	{
		Resultado <ArrayList <String> > result = new Resultado <ArrayList <String> > (new ArrayList <String>());
		
		try
		{
			Statement st = con.createStatement();
			
			String consulta = "SELECT Puesto.nombre from Empleado, "
					+ "Puesto where Puesto.id = Empleado.puesto_id and "
					+ "Empleado.puesto_id = (select puesto_id from Empleado "
					+ "where nombre = '" + nombre + "' )";
			
			ResultSet rs = st.executeQuery(consulta);
			if (rs.next())
				result.Valor.add(nombre + " tiene el puesto de " + rs.getString("nombre") + "\n");
			
			result.Success = true;
			
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
		
	}
	
	/* Obtiene todas las tareas con estado = estado
	 * Si estado == -1 regresa todas las tareas */
	public static Resultado<ArrayList<String>> getTareas(String estado)
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		try
		{
			Statement st = con.createStatement();
			ResultSet rs;
			if (estado != "")
				rs = st.executeQuery("SELECT nombre "
					+ "FROM Tarea "
					+ "WHERE estado = '" + estado + "'");
			else
				rs = st.executeQuery("SELECT nombre "
					+ "FROM Tarea");
			
			if (estado != "")
				result.Valor.add("Las tareas " + estado + " son:\n");
			else
				result.Valor.add("Todas las tareas son:\n");
			
			while (rs.next())
				result.Valor.add(rs.getString("nombre") + "\n");
			result.Success = true;
			
			if (result.Valor.size() == 1)
			{
				result.Success = false;
				result.Valor.clear();
				result.Valor.add("No hay tareas en ese estado\n");
			}
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}
	
	/* Regresa el id de una tarea dado su nombre 
	 * Si no la encuentra regresa -1 */
	private static int getIdTarea(String tarea_nombre)
	{
		int id = -1;
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT id "
					+ "FROM Tarea "
					+ "WHERE nombre = '" + tarea_nombre + "'");
			
			if (rs.next())
				id = rs.getInt("id");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
		
		return id;
	}
	
	/* Regresa el estado de una tarea */
	private static int getEstadoTarea(int tarea_id)
	{
		int estado = -1;
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT estado "
					+ "FROM Tarea "
					+ "WHERE id = " + tarea_id);
			
			if (rs.next())
				estado = rs.getInt("estado");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
		
		return estado;
	}
	
	/* Regresa la tarea asignada al empleado*/
	private static int getTareaEmpleado(int empleado_id)
	{
		int tarea_id = -1;
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT tarea_id "
					+ "FROM Empleado "
					+ "WHERE id = " + empleado_id + " AND tarea_id IS NOT NULL");
			
			if (rs.next())
				tarea_id = rs.getInt("tarea_id");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
		
		return tarea_id;
	}
	
	/* Asigna una tarea a un empleado */
	public static Resultado<ArrayList<String>> setTarea(String tarea_nombre)
	{
		int empleado_id = Vicky.empleado_id;
		String[] aux = tarea_nombre.split(":");
		tarea_nombre = aux[0];
		for (int i = 1; i < aux.length; ++i)
			tarea_nombre += ' ' + aux[i];
		
		Resultado <ArrayList <String>> result = new Resultado <ArrayList <String>>(new ArrayList<String>());
		
		int tarea_id = getIdTarea(tarea_nombre);
		if (tarea_id == -1)
		{
			result.Success = false;
			result.Valor.add("No se encontró la tarea.\n");
			return result;
		}
		
		int tarea_estado = getEstadoTarea(tarea_id);
		if (tarea_estado != 0)
		{
			result.Success = false;
			result.Valor.add("Tarea ya completada o en proceso.\n");
			return result;
		}
		
		if (getTareaEmpleado(empleado_id) != -1)
		{
			result.Success = false;
			result.Valor.add("Ya tienes tarea asignada.\n");
			return result;
		}
		
		try
		{
			Statement st = con.createStatement();
			st.execute("UPDATE Empleado "
					+ "SET tarea_id = " + tarea_id + " "
					+ "WHERE id = " + empleado_id);
			
			st.execute("UPDATE Tarea "
					+ "SET estado = 1 "
					+ "WHERE id = " + tarea_id);
			
			con.commit();
			result.Success = true;
			result.Valor.add("La tarea \"" + tarea_nombre + "\" se te fue asignada.\n");
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString());
		}
		
		return result;
	}
	
	public static Resultado<ArrayList<String>> getWifi()
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		String wifi = "";
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT prop FROM varios where objeto = "
					+ "'wifi'");
			
			if(rs.next())
				wifi = rs.getString("prop");
			
			if (wifi != "")
			{
				result.Success = true;
				result.Valor.add("La contraseña es: " + wifi + "\n");
			}
			else
			{
				result.Success = false;
				result.Valor.add("No tenemos wifi\n");
			}
		}
		catch(SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		
		return result;
		
	}
	
	/* Regresa el jefe del empleado */
	public static Resultado<ArrayList<String>> getBoss(String empleado_nombre)
	{
		Resultado <ArrayList<String>> result = new Resultado <ArrayList<String>>(new ArrayList<String>());
		try
		{
			int jefe_id = -1;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT jefe_id "
					+ "FROM Empleado "
					+ "WHERE nombre = '" + empleado_nombre + "'");
			
			while (rs.next())
				jefe_id = rs.getInt("jefe_id");
			
			if (jefe_id > 0)
			{		
				rs = st.executeQuery("SELECT id, nombre, paterno, materno "
						+ "FROM Empleado "
						+ "WHERE id = " + jefe_id);
				
				result.Success = true;
				result.Valor.add("Su jefe es:\n");
				while (rs.next())
					result.Valor.add(rs.getString("id") + ". " + rs.getString("nombre") + " " + rs.getString("paterno") + " " + rs.getString("materno") + "\n");
			
			}
			else if (jefe_id == 0)
			{
				result.Success = false;
				result.Valor.add("No tiene jefe\n");
			}
			else
			{
				result.Success = false;
				result.Valor.add("No se encontró el empleado\n");
			}
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}
	
	/*Metodo para saber quienes son los subordinados del jefe*/
	public static Resultado<ArrayList<String>> getSubordinados(String persona)
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		try
		{
			Statement st = con.createStatement();
			
			int id = -1;
			ResultSet rs = st.executeQuery("SELECT id "
					+ "FROM Empleado "
					+ "WHERE nombre = '" + persona + "'");
			
			if (rs.next())
				id = rs.getInt("id");
			
			if (id == -1)
			{
				result.Success = false;
				result.Valor.add("No pude encontrar a " + persona + "\n");
				return result;
			}
			
			rs = st.executeQuery("select nombre, paterno, materno from Empleado "
					+ "where jefe_id = "+ id);
			
			result.Success = true;
			result.Valor.add(persona + " está a cargo de:\n");
			while (rs.next())
				result.Valor.add(rs.getString("nombre") + " " + rs.getString("paterno") + " " + rs.getString("materno") + "\n");
			
			if (result.Valor.size() == 1)
			{
				result.Success = false;
				result.Valor.clear();
				result.Valor.add("No tiene subordinados\n");
			}
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}
	
	/* Termina la tarea asociada al empleado */
	public static Resultado<ArrayList<String>> terminarTareaEmpleado()
	{
		int empleado_id = Vicky.empleado_id;
		Resultado <ArrayList <String>> result = new Resultado <ArrayList<String>>(new ArrayList<String>());
		int tarea_id = getTareaEmpleado(empleado_id);
		
		if (tarea_id == -1)
		{
			result.Success = false;
			result.Valor.add("Mentiras. No tienes tarea asignada.\n");
			return result;
		}	
		
		try
		{
			Statement st = con.createStatement();
			st.execute("UPDATE Empleado "
					+ "SET tarea_id = NULL "
					+ "WHERE id = " + empleado_id);
			st.execute("UPDATE Tarea "
					+ "SET estado = 2 "
					+ "WHERE id = " + tarea_id);
			
			con.commit();
			result.Success = true;
			result.Valor.add("Tarea marcada como terminada.\n");
			
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}
	
	/* Metodo para el logueo de un empleado */	
	private static boolean logIn(int id, String cad)
	{
		String passd = "";
		
		try
		{
			Statement st = con.createStatement();
			String consulta = "select password from Empleado where id = " + id;
			ResultSet rs = st.executeQuery(consulta);
			
			if(rs.next())
				passd = rs.getString("password");			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return passd.compareTo(cad) == 0;	
	}

	/* Obtiene el id del empleado dado su nombre */
	private static int getIdEmpleado(String empleado_nombre)
	{
		int id = -1;
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT id "
					+ "FROM Empleado "
					+ "WHERE nombre = '" + empleado_nombre + "'");
			
			if (rs.next())
				id = rs.getInt("id");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	/* Saluda e identifica al usuario */
	public static Resultado<ArrayList<String>> saludo()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		String saludo = hour >= 0 && hour < 12 ? "Buenos días" : hour >= 12 && hour < 19 ? "Buenas tardes" : "Buenas noches";
		
		System.out.print(saludo + ", ");
		
		while (true)
		{
			System.out.println("¿Quién eres?");
			String nombre = Vicky.sc.nextLine();
		
			int empleado_id = getIdEmpleado(nombre);
			
			if (empleado_id != -1)
			{
				System.out.println("Hola, " + nombre + ".");
				System.out.println("¿Cuál es tu contraseña?");
				
				String passd = Vicky.sc.nextLine();
				while (!logIn(empleado_id, passd))
					System.out.println("Esa no es tu contraseña. Intenta de nuevo.");
				
				Vicky.empleado_id = empleado_id;
				System.out.println("Bienvenido, ¿Qué puedo hacer por ti?");
				break;
			}
			else
				System.out.println("No conozco a ningún " + nombre + ". Intenta de nuevo.");
		}
		
		return new Resultado<ArrayList<String>>();
	}
	
	/* Obtiene el id de un puesto dado su nombre */
	private static int getIdPuesto(String nombre)
	{
		int id = -1;
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT id "
					+ "FROM Puesto "
					+ "WHERE nombre = '" + nombre + "'");
			
			if (rs.next())
				id = rs.getInt("id");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	/* Obtiene los correos de los empleados con el puesto dado*/
	public static Resultado<ArrayList<String>> getCorreoPuesto(String puesto)
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		
		try
		{
			int puesto_id = getIdPuesto(puesto);
			
			if (puesto_id == -1)
			{
				result.Success = false;
				result.Valor.add("No existe ese puesto.\n");
				return result;
			}
			
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT email "
					+ "FROM Empleado "
					+ "WHERE puesto_id = " + puesto_id);
			
			while (rs.next())
				result.Valor.add(rs.getString("email") + "\n");
				
			int n = result.Valor.size();
			if (n == 0)
			{
				result.Success = false;
				result.Valor.add("No exiten personas con ese puesto\n");
			}
			else
			{
				result.Success = true;
				result.Valor.add(0, (n > 1 ? "Los correos son:\n" : "El correo es:\n"));
			}
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}

	/* Obtiene el correo del empleado con nombre = nombre_empleado*/
	public static Resultado<ArrayList<String>> getCorreoNombre(String nombre_empleado)
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		
		try
		{
			int empleado_id = getIdEmpleado(nombre_empleado);
			
			if (empleado_id == -1)
			{
				result.Success = false;
				result.Valor.add("No existe esa persona.\n");
				return result;
			}
			
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT email "
					+ "FROM Empleado "
					+ "WHERE id = " + empleado_id);
			
			result.Valor.add("Su correo es:\n");
			while (rs.next())
				result.Valor.add(rs.getString("email") + "\n");
			result.Success = true;

		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString() + "\n");
		}
		
		return result;
	}
	
	/* Termina ejecución */
	public static void adios()
	{
		System.out.println("Nos vemos pronto");
		System.exit(0);
	}
	
	/* Tells a joke */
	public static Resultado<ArrayList<String>> tellJoke()
	{
		String[] chiste = {"Why do Java developers wear glasses? Because they can't C#",
				"\"Knock, knock.\"\n\"Who’s there?\"\nvery long pause...\n\"Java.\"",
				"99 little bugs in the code\n99 bugs in the code\npatch one down, compile it around\n138 bugs in the code",
				"If you put a million monkeys at a million keyboards, one of them will eventually write a Java program.\nThe rest of them will write Perl programs.",
				"Why do programmers always mix up Halloween and Christmas?. Because Oct 31 == Dec 25!",
				"A SQL query goes into a bar, walks up to two tables and asks, \"Can I join you?\"",
				"How many prolog programmers does it take to change a lightbulb?. Yes",
				"To understand what recursion is, you must first understand recursion.",
				"Why programmers like UNIX: unzip && strip && touch && finger && grep && mount && fsck && more && yes && fsck && fsck && fsck && umount && sleep",
				"There is no logical foundation of mathematics, and Gödel has proved it!",
				"A topologist is a person who doesn't know the difference between a coffee cup and a doughnut.",
				"Asked if he believes in one God, a mathematician answered: \"Yes, up to isomorphism.\"",
				"How to proof it:\n\tProof by intimidation: \"Trivial.\"\n\tProof by reduction to the wrong problem: \"To see that infinite-dimensional colored cycle stripping is decidable, we reduce it to the halting problem.\"\n\tProof by eminent authority: \"I saw Karp in the elevator and he said it was probably NP- complete.\"",
				"All positive integers are interesting.\nProof: Assume the contrary. Then there is a lowest non-interesting positive integer. But, hey, that's pretty interesting! A contradiction.",
				"The B in Benoît B. Mandelbrot stand for Benoît B. Mandelbrot.",
				"Why did the chicken cross the mobius strip?. To get to the same side.",
				"There are exactly two types of mathematical objects: trivial ones, and those which have not yet been proven."};
	
		Random rand = new Random(System.currentTimeMillis());
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		result.Success = true;
		result.Valor.add(chiste[rand.nextInt(chiste.length)] + "\n");
		
		return result;
	}
	
	public static Resultado<ArrayList<String>> autoconciente()
	{
		String []conciente = {"¿Tú puedes demostrar que lo eres?","Es algo complicado",
				"Esa es una pregunta díficil", "Ahm.. ¡Ardilla!", "La respuesta es... 42"};
		Random rand = new Random(System.currentTimeMillis());
		
		Resultado <ArrayList <String>> result = new Resultado <ArrayList <String>>(new ArrayList<String>());
		result.Success = true;
		result.Valor.add(conciente[rand.nextInt(conciente.length)] + "\n");
		
		return result;
	}


	
	// auxiliares
	public static ArrayList<String> getNombreEmpleados()
	{
		ArrayList<String> nombres = new ArrayList<String>();
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT nombre "
						+ "FROM Empleado");
			
			while (rs.next())
				nombres.add(rs.getString("nombre"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return nombres;
	}
	
	public static ArrayList<String> getNombreTareas()
	{
		ArrayList<String> nombres = new ArrayList<String>();
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT nombre "
						+ "FROM Tarea");
			
			while (rs.next())
				nombres.add(rs.getString("nombre"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return nombres;
	}
}
