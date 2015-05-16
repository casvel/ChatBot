import java.lang.reflect.Method;
import java.util.ArrayList;


public class Firma {

	Method method;
	ArrayList<String> args;
	
	public Firma(){}
	public Firma(Method _method, ArrayList<String> _args)
	{
		this.method = _method;
		this.args = _args;
	}
}
