import java.util.*;

public class Vicky {

	public static void main(String[] args)
	{
		ChatBotController.Conecta();
		
		Resultado<ArrayList<String>> programadores = ChatBotController.getEmpleados("programador");
		for (int i = 0; i < programadores.Valor.size(); ++i)
			System.out.println(programadores.Valor.get(i));
		
		Resultado<ArrayList<String>> tareas = ChatBotController.getTareas(1);
		for (int i = 0; i < tareas.Valor.size(); ++i)
			System.out.println(tareas.Valor.get(i));
		
		Resultado <String> ok = ChatBotController.setTarea(1, "Ir por las frituras");
		System.out.println(ok.Valor);
		
		Resultado <String> jefe = ChatBotController.getBoss(4);
		System.out.println(jefe.Valor);
		
		System.out.println(ChatBotController.tellJoke().Valor);
		
		System.out.println(ChatBotController.terminarTareaEmpleado(1).Valor);
	}
	
}
