package com.lyle.file.utils;

import java.io.InputStream;
import java.io.Serializable;

import com.lyle.file.enums.FileTypeEnum;

/**
 * @ClassName: FileMagicUtil
 * @Description: 通过文件魔数获取文件真实类型
 * @author: Lyle
 * @date: 2018年1月26日 下午2:59:05
 */
public final class FileMagicUtil implements Serializable {

	private static final long serialVersionUID = 4166788188614143796L;

	/**
	 * @Title: getType
	 * @Description: 获取文件类型
	 * @param is
	 * @return: FileTypeEnum
	 */
	public static FileTypeEnum getType(InputStream is) {
		String fileHead = getFileHeader(is);
		if (null != fileHead && fileHead.length() > 0) {
			fileHead = fileHead.toUpperCase();
			FileTypeEnum[] fileTypes = FileTypeEnum.values();
			for (FileTypeEnum type : fileTypes) {
				if (fileHead.startsWith(type.getMagic()))
					return type;
			}
		}
		return null;
	}

	/**
	 * @Title: getFileHeader
	 * @Description: 获取文件头部
	 * @param is
	 * @return: String
	 */
	private static String getFileHeader(InputStream is) {
		byte[] buff = new byte[28];
		try {
			is.read(buff, 0, 28);
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.close(is);
		}
		return bytesToHex(buff);
	}

	/**
	 * @Title: bytesToHex
	 * @Description: 字节数组转为十六进制字符串
	 * @param buff
	 * @return: void
	 */
	private static String bytesToHex(byte[] buff) {
		StringBuilder sb = new StringBuilder();
		if (buff == null || buff.length <= 0) {
			return null;
		}
		for (int i = 0; i < buff.length; i++) {
			int v = buff[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
		}
		return sb.toString();
	}
}
