import java.util.*;

public class Vicky {

	public static void main(String[] args)
	{
		ChatBotController.Conecta();
		
		/*ArrayList<String> programadores = ChatBotController.getMiembros("Programador");
		if (programadores.get(0) == "ERROR")
			System.out.println("ERROR:" +  programadores.get(1));
		else
			for (int i = 0; i < programadores.size(); ++i)
				System.out.println(programadores.get(i));*/
		
		ArrayList<String> tareas = ChatBotController.getTareas("0");
		if (tareas.get(0) == "ERROR")
			System.out.println("ERROR:" +  tareas.get(1));
		else
			for (int i = 0; i < tareas.size(); ++i)
				System.out.println(tareas.get(i));
	}
	
}
