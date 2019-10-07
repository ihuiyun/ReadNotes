public interface ITeacher{
	public void teach();
}

public class TeacherImpl implements ITeacher{
	@Override
	public void teach(){
		System.out.println("Method teach");
	}
}

public class ProxyFactory{
	//维护一个目标对象，Object
	private Object target;
	//构造器，对target进行初始化
	public ProxyFactory(Object target){
		this.target = target;
	}
	//给目标对象生成一个代理对象
	public Object getProxyInstance(){
		//ClassLoader 目标类的类加载器
		//Class<?>[] interfaces目标对象的接口类型，使用泛型的方式确认类型
		//InvocationHandler 事件处理，执行目标对象时，会触发目标对象的事件处理方法
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
			new InvocationHandler(){
				
				@Override
				public Object invoke(Object proxy, Method method, Object[] args){
					System.out.println("JDK代理开始");
					//反射机制调用目标对象方法
					Object re = method.invoke(target, args);
					System.out.println("JDK代理提交");
					return re;
				}
			}
		);
	}
}

public class Client{
	
	public static void main(String[] args){
		ITeacher target = new TeacherImpl();
		ITeacher proxyInstance = (ITeacher)new ProxyFactory(target).getProxyInstance();
		
		proxyInstance.teach();
	}
}