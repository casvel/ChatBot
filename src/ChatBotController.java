import java.sql.*;

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
