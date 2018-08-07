package com.lyle.file.upload;

import java.io.Serializable;

/**
 * 文件信息基类
 * @author Lyle
 *
 */
public class BaseFileBean implements Serializable {

	private static final long serialVersionUID = 3242165720140494497L;

	/**
	 * 原本名称(.之前的内容)
	 */
	private String originalFileName;

	/**
	 * 新名称
	 */
	private String newFileName;

	/**
	 * 保存路径
	 */
	private String savePath;

	/**
	 * 扩展名(没有.)
	 */
	private String extension;

	/**
	 * 大小bytes
	 */
	private long fileSize;

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
