
public class Resultado<T> {

	public boolean Success;
	public T Valor;
	
	public Resultado(boolean _success, T _valor)
	{
		this.Success = _success;
		this.Valor = _valor;
	}
	public Resultado(T _valor)
	{
		this.Valor = _valor;
	}
	public Resultado(){}
	
}
