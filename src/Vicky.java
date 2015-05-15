import java.util.*;

public class Vicky {

	public static void main(String[] args)
	{
		ChatBotController.Conecta();
		
		Resultado<ArrayList<String>> programadores = ChatBotController.getEmpleados("programador");
		for (int i = 0; i < programadores.Valor.size(); ++i)
			System.out.println(programadores.Valor.get(i));
		
		Resultado<ArrayList<String>> tareas = ChatBotController.getTareas("1");
		for (int i = 0; i < tareas.Valor.size(); ++i)
			System.out.println(tareas.Valor.get(i));
		
		Resultado <String> ok = ChatBotController.setTarea("1", "Ir por las frituras");
		System.out.println(ok.Valor);
		
		Resultado <String> jefe = ChatBotController.getBoss("4");
		System.out.println(jefe.Valor);
		
		String unPuesto = "Luke";
		Resultado <ArrayList <String> > persona = ChatBotController.getPuesto(unPuesto);
		
		System.out.println("Ahora vamos a ver que hace " +unPuesto);
		System.out.println("El es el: ");
		
		for (int i = 0; i < persona.Valor.size(); ++i)
			System.out.println(persona.Valor.get(i));
		
		System.out.println(ChatBotController.TellJoke().Valor);
	}
	
}
