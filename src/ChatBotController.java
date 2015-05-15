import java.sql.*;
import java.util.*;

public class ChatBotController 
{
	static Connection con;
	
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
	static Resultado<ArrayList<String>> getEmpleados(String puesto)
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
			
			while (rs.next())
				result.Valor.add(rs.getString("id") + ". " + rs.getString("nombre") + ' ' + rs.getString("paterno") + ' ' + rs.getString("materno"));
		
			result.Success = true;
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString());
		}
		
		return result;
	}

	/*Obtiene el puesto de un empleado dado un nombre*/
	static Resultado <ArrayList <String> > getPuesto(String nombre)
	{
		Resultado <ArrayList <String> > result = new Resultado <ArrayList <String> > (new ArrayList <String>());
		
		try
		{
			Statement st = con.createStatement();
			
			String consulta = "SELECT Puesto.nombre from Empleado, "
					+ 		"Puesto where Puesto.id = Empleado.puesto_id and "
					+ "Empleado.puesto_id = (select puesto_id from Empleado "
					+ "where nombre = '" + nombre + "' )";
			
			ResultSet rs = st.executeQuery(consulta);
			
			while(rs.next())
			{
				result.Valor.add(rs.getString("nombre"));
			}
			
			result.Success = true;
			
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString());
		}
		
		return result;
		
	}
	
	/* Obtiene todas las tareas con estado = estado
	 * Si estado == "" regresa todas las tareas */
	static Resultado<ArrayList<String>> getTareas(String estado)
	{
		Resultado<ArrayList<String>> result = new Resultado<ArrayList<String>>(new ArrayList<String>());
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT nombre "
					+ "FROM Tarea "
					+ "WHERE estado =" + estado + "");
			
			while (rs.next())
				result.Valor.add(rs.getString("nombre"));
			
			result.Success = true;
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor.add(e.toString());
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
					+ "WHERE id = '" + tarea_id + "'");
			
			if (rs.next())
				estado = rs.getInt("estado");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
		
		return estado;
	}
	
	/* Asigna una tarea a un empleado */
	static Resultado<String> setTarea(String empleado_id, String tarea_nombre)
	{
		int tarea_id = getIdTarea(tarea_nombre);
		
		if (tarea_id == -1)
			return new Resultado<String>(false, "No se encontró la tarea");
		
		int tarea_estado = getEstadoTarea(tarea_id);
		
		if (tarea_estado == 2)
			return new Resultado<String>(false, "Tarea ya completada");
		
		Resultado <String> result = new Resultado <String>();
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
			result.Valor = "Tarea asignada";
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor = e.toString();
		}
		
		return result;
	}

	
	
	
	/* Regresa el jefe del empleado */
	static Resultado<String> getBoss(String empleado_id)
	{
		Resultado <String> result = new Resultado <String>();
		try
		{
			int jefe_id = -1;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT jefe_id "
					+ "FROM Empleado "
					+ "WHERE id = " + empleado_id);
			
			while (rs.next())
				jefe_id = rs.getInt("jefe_id");
			
			if (jefe_id != -1)
			{		
				rs = st.executeQuery("SELECT id, nombre, paterno, materno "
						+ "FROM Empleado "
						+ "WHERE id = " + jefe_id);
				
				result.Success = true;
				while (rs.next())
					result.Valor = rs.getString("id") + ". " + rs.getString("nombre") + " " + rs.getString("paterno") + " " + rs.getString("materno");
			
				if (result.Valor == null)
				{
					result.Success = false;
					result.Valor = "No tiene jefe";
				}
			}
			else
			{
				result.Success = false;
				result.Valor = "No se encontró el empleado";
			}
		}
		catch (SQLException e)
		{
			result.Success = false;
			result.Valor = e.toString();
		}
		
		return result;
	}



	/* Tells a joke */
	static Resultado<String> TellJoke()
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
		return new Resultado<String>(true, chiste[rand.nextInt(chiste.length)]);
	}
}
