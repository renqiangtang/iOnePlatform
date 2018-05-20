package test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.base.utils.ParaMap;

@DevClass(author = "朱金亮2")
public class ReadAnnotationInfoTest {

	@DevMethod(priority = 1,  author = "朱金亮",content = "测试函数")
	public ParaMap t1(ParaMap in) throws Exception {
		return null;
	}

	public static void main(String[] args) throws Exception {
		Class c = Class.forName("test.ReadAnnotationInfoTest");
		Annotation[] classAnnotations=c.getDeclaredAnnotations();
		for (Annotation an : classAnnotations) {
			Method[] meths = an.annotationType().getDeclaredMethods();
			// 遍历每个注解的所有变量
			for (Method meth : meths) {
				System.out.println("注解的变量名为：" + meth.getName() + ":"
						+ meth.invoke(an, null));
			}
		}
		
		
		Method[] methods = c.getDeclaredMethods();
		for (Method method : methods) {
			// 获取每个方法上面所声明的所有注解信息
			Annotation[] annotations = method.getDeclaredAnnotations();
			for (Annotation an : annotations) {
				System.out.println("方法名为：" + method.getName() + " 其上面的注解为："
						+ an.annotationType().getSimpleName());
				Method[] meths = an.annotationType().getDeclaredMethods();
				// 遍历每个注解的所有变量
				for (Method meth : meths) {
					System.out.println("注解的变量名为：" + meth.getName() + ":"
							+ meth.invoke(an, null));
				}

			}
		}

	}

}
