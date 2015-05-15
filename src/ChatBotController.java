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
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
	}
	
	static ArrayList<String> getMiembros(String rol)
	{
		ArrayList<String> result = new ArrayList<String>();
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT Empleado.nombre, Empleado.paterno, Empleado.materno "
					+ "FROM Empleado, Puesto "
					+ "WHERE Empleado.puesto_id = Puesto.id AND Puesto.nombre ='" + rol + "'");
			
			while (rs.next())
				result.add(rs.getString("nombre") + ' ' + rs.getString("paterno") + ' ' + rs.getString("materno"));
		}
		catch (SQLException e)
		{
			result.add("ERROR");
			result.add(e.toString());
		}
		
		return result;
	}
	
	static void Prueba()
	{
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM puesto");
			while (rs.next())
				System.out.println(rs.getString("nombre"));
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
	}
	
}
