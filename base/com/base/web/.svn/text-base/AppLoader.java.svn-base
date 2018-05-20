package com.base.web;

import java.util.Hashtable;

public class AppLoader extends ClassLoader {
	public static AppLoader loader = null;
	public static Hashtable<String, Object> objPool = new Hashtable<String, Object>();

	public static AppLoader getInstance() {
		if (loader == null)
			loader = new AppLoader();
		return loader;
	}

	public AppLoader() {
		super(Thread.currentThread().getContextClassLoader());
	}

	public static Object getObj(String className) throws Exception {
		Object o = objPool.get(className);
		if (o == null) {
			o = getInstance().loadClass(className).newInstance();
			objPool.put(className, o);
		}
		return o;
	}

	public void l(byte[] bytes) {
		try {
			Class c = defineClass(null, bytes, 0, bytes.length, null);
			objPool.put(c.getName(), c.newInstance());
		} catch (Exception ex) {
		}
	}

}
