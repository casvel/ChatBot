import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Vicky 
{
	static Scanner sc = new Scanner(System.in);
	static int empleado_id = -1;
	public static int confused = 1;
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		ChatBotController.Conecta();
		DBVicky.init();
			
		while (true)
		{
			String query = sc.nextLine();
			Firma firma = VickyHelper.procesaQuery(query);
			//System.out.println(terminal);
			
			if (firma != null)
			{
				//System.out.println(firma.method.toString());
				
				if (empleado_id == -1)
				{
					if (firma.method.equals(ChatBotController.class.getMethod("saludo")))
						ChatBotController.saludo();
					else
						System.out.println("No seas maleducado, saluda primero");
				}
				else
				{
					Resultado <ArrayList <String>> result;
					if (firma.args.get(0) != null)
					{
						if (firma.args.get(0).charAt(0) == '%')
						{
							ArrayList<String> nargs = new ArrayList<String>();
							nargs.add(DBVicky.param);
							for (int i = 1; i < firma.args.size(); ++i)
								nargs.add(firma.args.get(i));
							result = (Resultado<ArrayList<String>>)firma.method.invoke(null, nargs.toArray()); 
						}
						else
							result = (Resultado<ArrayList<String>>)firma.method.invoke(null, firma.args.toArray()); 
					}
					else
						result = (Resultado <ArrayList<String>>)firma.method.invoke(null);
					
					if (result.Success == false)
						System.out.println("Ups, hubo un error con tu petición:");
					
					for (String r : result.Valor)
						System.out.print(r);
				}
				confused = 0;
			}
			else
			{
				confused += 1;
				
				String []tempo ={"Lo siento, no te entiendo, vuelve a preguntar", "No comprendo, pregunta de nuevo",
						"Mmm, pregunta otra vez","puedes volver a preguntar?","jeje, no entiendo, puedes preguntar de nuevo?",
						"¿quieres preguntar nuevamente?","reformula tu pregunta, please ;)","soy una chica lista pero"
								+ " no te estoy entendiendo"};
				Random rand = new Random(System.currentTimeMillis());
				if(confused == 3)
					System.out.println(tempo[tempo.length-1]);
				else
				System.out.println(tempo[rand.nextInt(tempo.length)]) ;
				
			}
		}
	}
	
}
