package com.bonc.frame.ext;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
/**
 * 自定义类加载器
 * @author qxl
 * @date 2018年5月2日 下午2:38:32
 * @version 1.0
 */
public class MyClassLoader extends ClassLoader {
	
	public static void main(String[] args) throws Exception {
		//通过自定义类加载器加载类，（任意指定目录和名称）
		Class<?> clazz = new MyClassLoader("ClassLoaderLib").loadClass("Person");
		//获取构造方法
		Constructor<?> con = clazz.getConstructor(String.class, int.class);
		//创建对象，成功加载则执行静态代码块中的打印语句打印信息
		Object obj = con.newInstance("张三",15);
		//打印该类的类加载器
		System.out.println(obj.getClass().getClassLoader());
	}

	/**
	 * 构造函数传递待加载类所在的目录
	 * @param classDir	待加载的类所在的目录
	 */
	public MyClassLoader(String classDir) {
		if(classDir == null || classDir.isEmpty()){
			throw new IllegalArgumentException();
		}
		this.classDir = classDir;
	}
	
	//classDir记录类所在目录
	private String classDir;

	/**
	 * 复写findClass方法
	 * 因为自定义的类加载器MyClassLoader继承了ClassLoader类，继承了其所有方法
	 * 通过loadClass(String name)方法加载类的时候：
	 * 1、会检查该类是否已经加载，如果没有，会依次委托MyClassLoader的父类、父类的父类
	 *    等类加载器依次尝试加载，都失败则执行findClass(String name)试图获取待加载的类的Class对象
	 * 2、而ClassLoader中findClass(String name)方法直接抛出ClassNotFoundException(name)
	 * 3、因此复写findClass(String name)方法就是以自定义的方式获取类（在父类都无法加载的情况）
	 * 4、通过将某个.class文件读入一个byte类型的数组中，然后通过defineClass方法
	 *    获取该字节码文件（Class对象）并返回
	 * 5、因此，当1中的情况出现时，本类即可通过findClass(String name)获取Class对象给
	 *    loadClass(String name)方法，使其返回同一个Class对象
	 * 	    
	 *    本类主函数内：
	 *    new MyClassLoader("ClassLoaderLib").loadClass("Person");
	 *    new MyClassLoader("ClassLoaderLib").findClass("Person");
	 * 
	 *    因为protected权限修饰符，其他类调用本类加载某个类只能用loadClass方法
	 * 
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		
		//获取字节码文件的完整路径
		String classPath = null;
		if(classDir!=null && classDir.endsWith("/")) {
			classPath = classDir + name + ".class";
		}else {
			classPath = classDir + File.separator + name + ".class";
		}
		
		//定义Class对象引用
		Class<?> clazz = null;
		//设置字节输入流引用
		BufferedInputStream bos = null;
		//定义字节数组输出流引用
		ByteArrayOutputStream baos = null;
		try {
			//建立字节输入流
			bos = new BufferedInputStream(new FileInputStream(classPath)); 
			//定义缓冲数组buf，len记录结束标记
			byte[] buf = new byte[1024];
			int len = -1;
			//新建字节数组输出流（内部维护了一个字节数组）
			baos = new ByteArrayOutputStream();
			//循环读取，写入baos中的字节数组
			while ((len = bos.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			//获取baos中的字节数组
			byte[] bytes = baos.toByteArray();
			if(bytes!=null && bytes.length>0){
				//将该字节数组变成字节码文件
				clazz = defineClass(null, bytes, 0, bytes.length);
			}
		} catch (IOException e) {
			throw new ClassNotFoundException("The class path is:"+classPath);
		} finally {//关闭流
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//加载过程中失败则终止程序，否则返回该类
		if (clazz == null) {
			throw new ClassNotFoundException("The class path is:"+classPath);
		}
		return clazz;
	}

}
