package com.lyle.file.validate;

import java.io.InputStream;
import java.util.Collection;

import com.lyle.file.enums.FileTypeEnum;
import com.lyle.file.utils.FileMagicUtil;

/**
 * @ClassName: ValidateFile
 * @Description: 上传文件时候用于校验
 * @author: Lyle
 * @date: 2018年1月26日 下午3:00:13
 */
public final class ValidateFile {

	/**
	 * @Description: 校验文件大小
	 * @param actual 实际大小
	 * @param limit 限制大小
	 * @return 符合限制返回true
	 */
	public static boolean validateSize(long actual, long limit) {
		return Long.compare(actual, limit) > 0 ? false : true;
	}

	/**
	 * @Description: 通过扩展名校验文件
	 * @param actual 实际扩展名
	 * @param limit 允许扩展名
	 * @return boolean
	 */
	public static boolean validateTypeByExtension(String actual, Collection<String> limit) {
		return limit.contains(actual);
	}

	/**
	 * @Title: validateTypeByMagic
	 * @Description: 通过魔数校验文件类型
	 * @param is
	 * @param limit
	 * @return: boolean
	 */
	public static boolean validateTypeByMagic(InputStream is, Collection<FileTypeEnum> limit) {
		FileTypeEnum type = FileMagicUtil.getType(is);
		return (null != type && limit.contains(type)) ? true : false;
	}
}
