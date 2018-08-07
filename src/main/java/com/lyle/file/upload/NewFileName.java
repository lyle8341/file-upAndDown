package com.lyle.file.upload;

public interface NewFileName {

	/**
	 * 生成新文件名
	 * @param originalFileName
	 * @return
	 */
	default String generateFileName(String originalFileName) {
		return originalFileName;
	};
}
