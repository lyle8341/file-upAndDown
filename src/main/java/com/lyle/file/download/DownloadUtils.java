package com.lyle.file.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyle.file.utils.IOUtils;

/**
 * @ClassName: DownloadUtils
 * @Description: 下载工具类
 * @author: Lyle
 * @date: 2018年1月23日 下午3:56:08
 */
public final class DownloadUtils implements Serializable {

	private static final long serialVersionUID = -6736538612831104971L;

	public static final int CAPACITY = 1024;

	public static final String CHARSET = "UTF-8";

	private static final String TMPDIR = "java.io.tmpdir";

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadUtils.class);

	/**
	 * @Title: packageDown
	 * @Description: 打包下载
	 * @param response
	 * @param urls 下载路径集合
	 * @param zipName 压缩包名（不带后缀）
	 * @return: void
	 */
	public static void packageDown(HttpServletResponse response, Set<String> urls, String zipName) {
		File temp = new File(System.getProperty(TMPDIR) + File.separator + zipName + ".zip");
		FileOutputStream fous = null;
		ZipOutputStream zipOut = null;
		if (null == urls || urls.size() == 0) {
			LOGGER.warn("文件路径为空，无法下载!!");
			return;
		}
		try {
			if (!temp.exists()) {
				temp.createNewFile();
			}
			fous = new FileOutputStream(temp);
			zipOut = new ZipOutputStream(fous);
			for (String url : urls) {
				if (null == url || "".equals(url.trim()))
					continue;
				ZipEntry entry = new ZipEntry(new File(url).getName());
				zipOut.putNextEntry(entry);
				HttpURLConnection httpUrl = null;
				URL fileUrl = null;
				fileUrl = new URL(url);
				httpUrl = (HttpURLConnection) fileUrl.openConnection();
				httpUrl.connect();
				InputStream downloadFile = httpUrl.getInputStream();
				int len = 0;
				byte[] buf = new byte[CAPACITY];
				while ((len = downloadFile.read(buf)) != -1) {
					zipOut.write(buf, 0, len);
				}
			}
			zipOut.flush();
			zipOut.closeEntry();
			zipOut.close();
			fous.close();
			// 删除临时文件
			downLoadFile(temp, response);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		} finally {
			IOUtils.close(zipOut, fous);
			deleteFile(temp);
		}
	}

	/**
	 * @Title: packageDown
	 * @Description: 打包并下载
	 * @param request
	 * @param response
	 * @param map <k,v> k是url，v是不带后缀的文件名（适合重命名）。 eg:{http://localhost/2018/01/19/创业.jpg=创业}
	 * @param zipName 压缩文件名(不带后缀)
	 * @return: void
	 */
	public static void packageDown(HttpServletResponse response, Map<String, String> map, String zipName) {
		File temp = new File(System.getProperty(TMPDIR) + File.separator + zipName + ".zip");
		FileOutputStream fous = null;
		ZipOutputStream zipOut = null;
		if (null == map || map.size() == 0) {
			LOGGER.warn("文件路径为空，无法下载!!");
			return;
		}
		try {
			if (!temp.exists()) {
				temp.createNewFile();
			}
			fous = new FileOutputStream(temp);
			zipOut = new ZipOutputStream(fous);
			for (Entry<String, String> mentry : map.entrySet()) {
				String key = mentry.getKey();
				if (null == key || "".equals(key.trim()))
					continue;
				String value = mentry.getValue();
				String resultFileName = (null == value || "".equals(value.trim())) ? new File(key).getName()
						: (value + key.substring(key.lastIndexOf(".")));
				ZipEntry entry = new ZipEntry(resultFileName);
				zipOut.putNextEntry(entry);
				HttpURLConnection httpUrl = null;
				URL fileUrl = null;
				fileUrl = new URL(key);
				httpUrl = (HttpURLConnection) fileUrl.openConnection();
				httpUrl.connect();
				InputStream downloadFile = httpUrl.getInputStream();
				int len = 0;
				byte[] buf = new byte[CAPACITY];
				while ((len = downloadFile.read(buf)) != -1) {
					zipOut.write(buf, 0, len);
				}
			}
			zipOut.flush();
			zipOut.closeEntry();
			zipOut.close();
			fous.close();
			// 删除临时文件
			downLoadFile(temp, response);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		} finally {
			IOUtils.close(zipOut, fous);
			deleteFile(temp);
		}
	}

	/**
	 * @Title: deleteTemp
	 * @Description: 删除临时文件
	 * @param temp 临时文件
	 * @return: void
	 */
	private static void deleteFile(File temp) {
		try {
			if (temp.exists()) {
				temp.delete();
			}
		} catch (Exception e) {
			LOGGER.error("删除文件出错", e);
		}
	}

	/**
	 * @Title: downLoadZip
	 * @Description: 下载
	 * @param file 临时压缩文件
	 * @param response
	 * @return: void
	 */
	public static void downLoadFile(File file, HttpServletResponse response) {
		InputStream fis = null;
		OutputStream bos = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			bos = new BufferedOutputStream(response.getOutputStream());
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(file.getName(), CHARSET));
			int len = 0;
			byte[] buff = new byte[CAPACITY];
			while ((len = fis.read(buff)) != -1) {
				bos.write(buff, 0, len);
			}
			bos.flush();
		} catch (FileNotFoundException e) {
			LOGGER.error("找不到文件{},文件路径是{}", file.getName(), file.getPath());
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("-------不支持的Encode编码-------");
		} catch (IOException e) {
			LOGGER.error("下载出现IO异常", e);
		} finally {
			IOUtils.close(bos, fis);
		}
	}

	/**
	 * @Title: singleDownload
	 * @Description: 不打包下载
	 * @param response
	 * @param url 文件url
	 * @param rename 重命名 为null或者""时候则使用原名
	 * @return: void
	 */
	public static void singleDownload(HttpServletResponse response, String url, String rename) {
		HttpURLConnection httpUrl = null;
		URL fileUrl = null;
		InputStream is = null;
		OutputStream os = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		if (url == null || "".equals(url.trim())) {
			LOGGER.warn("文件路径为空，无法下载!!");
			return;
		}
		try {
			response.reset();
			response.setContentType("application/octet-stream");
			// new File(url).getName 为了取出文件名。new File(url)并不创建文件实体，只是创建一个指向url的引用
			String resultFileName = (null == rename || "".equals(rename.trim())) ? new File(url).getName()
					: (rename + url.substring(url.lastIndexOf(".")));
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(resultFileName, CHARSET));// 文件名.jpg
			fileUrl = new URL(url);
			httpUrl = (HttpURLConnection) fileUrl.openConnection();
			httpUrl.connect();
			is = httpUrl.getInputStream();
			os = response.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);
			int len = 0;
			byte[] buf = new byte[CAPACITY];
			while ((len = bis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			bos.flush();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("-------不支持的Encode编码-------", e);
		} catch (IOException e) {
			LOGGER.error("IO异常", e);
		} finally {
			IOUtils.close(os, is);
		}
	}

	/**
	 * @Title: singleDownload
	 * @Description: 不打包下载
	 * @param request
	 * @param response
	 * @param map<k,v> key是url，v是不带后缀的文件名。 eg:{http://localhost/2018/01/19/创业.jpg=创业}
	 * @return: void
	 */
	public static void singleDownload(HttpServletResponse response, Map<String, String> map) {
		for (Entry<String, String> entry : map.entrySet()) {
			String filepath = entry.getKey();
			String rename = entry.getValue();
			singleDownload(response, filepath, rename);
		}
	}
}
