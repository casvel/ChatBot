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
}
