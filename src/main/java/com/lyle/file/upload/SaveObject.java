package com.lyle.file.upload;

/**
 * 把图片，文件等信息存入数据库
 * @author Lyle
 * @param <T>
 */
public interface SaveObject<T> {

	/**
	 * 存入数据库
	 * @param t
	 * @return
	 */
	boolean insert(T t);
}
