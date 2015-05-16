import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Vicky {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		ChatBotController.Conecta();
		
		/*Resultado<ArrayList<String>> programadores = ChatBotController.getEmpleados("programador");
		for (int i = 0; i < programadores.Valor.size(); ++i)
			System.out.println(programadores.Valor.get(i));
		
		Resultado<ArrayList<String>> tareas = ChatBotController.getTareas(1);
		for (int i = 0; i < tareas.Valor.size(); ++i)
			System.out.println(tareas.Valor.get(i));
		
		Resultado <String> ok = ChatBotController.setTarea(1, "Ir por las frituras");
		System.out.println(ok.Valor);
		
		Resultado <String> jefe = ChatBotController.getBoss(4);
		System.out.println(jefe.Valor);
		
		
		System.out.println(ChatBotController.terminarTareaEmpleado(1).Valor);

		String unPuesto = "Luke";
		Resultado <ArrayList <String> > persona = ChatBotController.getPuesto(unPuesto);
		
		System.out.println("Ahora vamos a ver que hace " +unPuesto);
		System.out.println("Él es el: ");
		
		for (int i = 0; i < persona.Valor.size(); ++i)
			System.out.println(persona.Valor.get(i));
		
<<<<<<< HEAD
		String cad = "mama";
		
		Boolean logo  = ChatBotController.logIn(4, cad);
		
		if(logo == true)
			System.out.println("Logueo exitoso");
		else
			System.out.println("Contraseña o id incorrecto, verfique");
		
		System.out.println(ChatBotController.tellJoke().Valor);
=======
		System.out.println(ChatBotController.tellJoke().Valor);*/
		
		Scanner sc = new Scanner(System.in);
		DBVicky.initSinonimos();
		DBVicky.initGrafo();
		
		while (true)
		{
			String terminal = VickyHelper.procesaQuery(sc.nextLine());
			//System.out.println(terminal);
			
			if (terminal != null)
			{
				Firma firma = DBVicky.Terminales.get(terminal);
				//System.out.println(firma.method.toString());
				
				if (firma.method.equals(ChatBotController.class.getMethod("getEmpleados", String.class)))
				{
					if (firma.args.get(0) == "Lider")
					{
						Resultado <ArrayList <String>> result = (Resultado<ArrayList<String>>)firma.method.invoke(null, firma.args.toArray()); 
						System.out.println("El líder del proyecto es: " + result.Valor.get(0));
					}
				}
				else if (firma.method.equals(ChatBotController.class.getMethod("tellJoke")))
				{
					Resultado <String> result = (Resultado <String>)firma.method.invoke(null);
					System.out.println(result.Valor);
				}
			}
			else
			{
				System.out.println("Lo siento, no te entiendo");
			}
		}
	}
	
}
