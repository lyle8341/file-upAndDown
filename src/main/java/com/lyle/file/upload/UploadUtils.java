package com.lyle.file.upload;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.lyle.file.download.DownloadUtils;
import com.lyle.file.utils.IOUtils;

/**
 * @ClassName: UploadUtils
 * @Description: 文件上传工具类
 * @author: Lyle
 * @date: 2018年1月23日 下午3:56:45
 */
public final class UploadUtils implements Serializable {

	private static final long serialVersionUID = -2602912574927815346L;

	private static final String DOT = ".";

	private static final String RN = "\r\n";

	private static final String DOTTEDLINE6 = "------";

	private static final String DOTTEDLINE4 = "----";

	private static final String DOTTEDLINE2 = "--";

	private static final String REQUESTMETHOD = "POST";

	private static final String REQUESTCONNECTION = "Keep-Alive";

	private static final String REQUESTUSER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64)";

	private static final String REQUESTCONTENT_TYPE = "multipart/form-data; boundary=";

	private static final String DATACONTENT_DISPOSITION = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s";

	private static final String DATACONTENT_TYPE = "Content-Type: application/octet-stream";

	private static final String DEFAULTNAMEOFFILEFIELD = "file";

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadUtils.class);

	/**
	 * 单文件上传
	 * @param multipartFile 文件
	 * @param saveObject 保存bean的接口
	 * @param beanInfo 文件bean
	 * @param savePath 保存路径(/结尾)
	 * @param newFileName 生成新文件名
	 * @return 如有异常返回null,空文件返回空字符串
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static <T extends BaseFileBean> String uploadOneFile(MultipartFile multipartFile, SaveObject<T> saveObject,
			T beanInfo, String savePath, NewFileName newFileName) {
		if (multipartFile.isEmpty()) {
			LOGGER.info("----------------文件为空---------------");
			return "";
		}
		String originalFilename = multipartFile.getOriginalFilename();// 图片上传.zip
		String baseName = FilenameUtils.getBaseName(originalFilename);
		String extension = FilenameUtils.getExtension(originalFilename);
		String generateFileName = newFileName.generateFileName(baseName);
		// 保存文件
		File savePathFile = new File(savePath);
		if (!savePathFile.exists())
			savePathFile.mkdirs();
		try {
			multipartFile.transferTo(new File(savePath, generateFileName + DOT + extension));
			// 保存到数据库
			beanInfo.setOriginalFileName(baseName);// 图片上传
			beanInfo.setSavePath(savePath);
			beanInfo.setNewFileName(generateFileName);
			beanInfo.setFileSize(multipartFile.getSize());
			beanInfo.setExtension(extension);
			saveObject.insert(beanInfo);
			return savePath + generateFileName + DOT + extension;
		} catch (IllegalStateException e) {
			LOGGER.error("临时文件夹里面的内容已经在第一次传输完后删除了，不能再次调用transferTo", e);
		} catch (IOException e) {
			LOGGER.error("读写文件时发生异常", e);
		}
		return null;
	}

	/**
	 * 多文件上传
	 * @param multipartFile 文件
	 * @param saveObject 保存bean的接口
	 * @param beanInfo 文件bean
	 * @param savePath 保存路径(/结尾)
	 * @param newFileName 生成新文件名
	 * @return 文件路径集合
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static <T extends BaseFileBean> List<String> uploadMoreFile(MultipartFile[] multipartFile,
			SaveObject<T> saveObject, T beanInfo, String savePath, NewFileName newFileName) {
		List<String> paths = new ArrayList<>();
		for (MultipartFile file : multipartFile) {
			String path = uploadOneFile(file, saveObject, beanInfo, savePath, newFileName);
			paths.add(path);
		}
		return paths;
	}

	/**
	 * 远程上传文件
	 * @Title: remoteUploadOneFile
	 * @param request
	 * @param remoteUrl 远程地址
	 * @param nameOfFileField 远程服务器接收文件的字段名（默认为file）
	 * @throws IOException
	 * @return: String
	 */
	public static String remoteUpload(HttpServletRequest request, String remoteUrl, String nameOfFileField)
			throws IOException {
		Map<String, InputStream> map = new HashMap<>();
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getServletContext());
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> iter = multiRequest.getFileNames();
			if (commonsMultipartResolver.isMultipart(request)) {
				while (iter.hasNext()) {
					List<MultipartFile> fileRows = multiRequest.getFiles(iter.next());
					if (fileRows != null && fileRows.size() != 0) {
						for (MultipartFile file : fileRows) {
							if (file != null && !file.isEmpty()) {
								map.put(file.getOriginalFilename(), file.getInputStream());
							}
						}
					}
				}
			}
		}
		return uploadToRemoteServer(map, remoteUrl, nameOfFileField);
	}

	/**
	 * 上传到远端服务器
	 * @Title: uploadToRemoteServer
	 * @param files key为文件名a.png value为输入流
	 * @param remoteUrl 远端地址
	 * @param nameOfFileField 远端服务器接收文件的字段名
	 * @return: String
	 */
	private static String uploadToRemoteServer(Map<String, InputStream> files, String remoteUrl,
			String nameOfFileField) {
		String Boundary = UUID.randomUUID().toString();
		URL url = null;
		HttpURLConnection conn = null;
		StringBuilder response = new StringBuilder();
		try {
			url = new URL(remoteUrl);
			conn = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(REQUESTMETHOD);
			conn.setRequestProperty("Connection", REQUESTCONNECTION);
			conn.setRequestProperty("User-Agent", REQUESTUSER_AGENT);
			conn.setRequestProperty("Charsert", DownloadUtils.CHARSET);
			conn.setRequestProperty("Content-Type", REQUESTCONTENT_TYPE + DOTTEDLINE4 + Boundary);
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			byte[] end_data = (DOTTEDLINE6 + Boundary + DOTTEDLINE2 + RN).getBytes(DownloadUtils.CHARSET);// 定义最后数据分隔线
			Iterator<Entry<String, InputStream>> iter = files.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, InputStream> entry = iter.next();
				String key = entry.getKey();
				InputStream val = entry.getValue();
				StringBuilder sb = new StringBuilder();
				sb.append(DOTTEDLINE6);
				sb.append(Boundary);
				sb.append(RN);
				sb.append(String.format(DATACONTENT_DISPOSITION,
						((null == nameOfFileField || "".equals(nameOfFileField.trim())) ? DEFAULTNAMEOFFILEFIELD
								: nameOfFileField),
						key, RN));
				sb.append(DATACONTENT_TYPE + RN + RN);
				System.out.println(sb.toString());
				byte[] data = sb.toString().getBytes(DownloadUtils.CHARSET);
				out.write(data);
				DataInputStream in = new DataInputStream(val);
				int bytes = 0;
				byte[] bufferOut = new byte[DownloadUtils.CAPACITY];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				out.write(RN.getBytes(DownloadUtils.CHARSET)); // 多个文件时，二个文件之间加入这个
				IOUtils.close(in);
			}
			out.write(end_data);
			out.flush();
			IOUtils.close(out);
			// 定义BufferedReader输入流来读取URL的响应
			InputStream inputStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, DownloadUtils.CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (MalformedURLException e) {
			LOGGER.error("url地址不正确:" + remoteUrl, e);
		} catch (ProtocolException e) {
			LOGGER.error("请求协议有误：{}", conn != null ? conn.getRequestMethod() : null);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("不支持的编码：{}", DownloadUtils.CHARSET);
		} catch (IOException e) {
			LOGGER.error("读写错误", e);
		}
		return response.toString();
	}
}
