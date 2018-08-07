package com.lyle.file.utils;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: IOUtils
 * @Description: 关闭流工具类
 * @author: Lyle
 * @date: 2018年1月26日 下午2:59:55
 */
public final class IOUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

	@SafeVarargs
	public static <T extends Closeable> void close(T... stream) {
		for (T temp : stream) {
			try {
				if (null != temp) {
					temp.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭流时发生错误{}", e);
			} finally {
				temp = null;
			}
		}
	}
}
