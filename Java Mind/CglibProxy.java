public class Teacher{
	public void teach(){
		System.out.println("Method teach");
	}
}

public class ProxyFactory implements MethodIntercepter{
	//维护一个目标对象，Object
	private Object target;
	//构造器，对target进行初始化
	public ProxyFactory(Object target){
		this.target = target;
	}
	//给目标对象生成一个代理对象
	public Object getProxyInstance(){
		//创建一个工具类
		Enhancer enhancer = new Enhancer();
		//设置父类
		enhancer.setSuperclass(target.getClass());
		//设置回调函数
		enhancer.setCallback(this);
		//创建子类对象，即代理对象
		return enhancer.create();
	}
	
	@Override
	public Object intercept(Object arg0, Method method, Object[] args, MethodProxy arg3){
		System.out.println("Cglib代理模式开始");
		Object re = method.invoke(target, args);
		System.out.println("Cglib代理模式提交");
		return re;
	}
}

public class Client{
	
	public static void main(String[] args){
		ITeacher target = new TeacherImpl();
		ITeacher proxyInstance = (ITeacher)new ProxyFactory(target).getProxyInstance();
		
		proxyInstance.teach();
	}
}