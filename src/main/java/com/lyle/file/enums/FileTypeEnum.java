package com.lyle.file.enums;

/**
 * @ClassName: FileTypeEnum
 * @Description: 文件类型枚举
 * @author: Lyle
 * @date: 2018年1月26日 下午2:57:42
 */
public enum FileTypeEnum {
	/**
	 * JPEG_JPG
	 */
	JPEG("JPEG_JPG", "FFD8FF"),
	/**
	 * PNG
	 */
	PNG("PNG", "89504E470D0A1A0A"),
	/**
	 * GIF
	 */
	GIF("GIF", "47494638"),
	/**
	 * Windows bitmap
	 */
	BMP("BMP", "424D"),
	/**
	 * CAD
	 */
	DWG("DWG", "41433130"),
	/**
	 * Adobe photoshop
	 */
	PSD("PSD", "38425053"),
	/**
	 * Rich Text Format
	 */
	RTF("RTF", "7B5C727466"),
	/**
	 * XML
	 */
	XML("XML", "3C3F786D6C"),
	/**
	 * Outlook Express
	 */
	DBX("DBX", "CFAD12FEC5FD746F "),
	/**
	 * Outlook
	 */
	PST("PST", "2142444E"),
	/**
	 * doc;xls;dot;ppt;xla;ppa;pps;pot;msi;sdw;db
	 */
	OLE2("OLE2", "D0CF11E0A1B11AE1"),
	/**
	 * docx;pptx;xlsx
	 */
	DPX("DPX", "504B030414000600"),
	/**
	 * Microsoft Word/Excel
	 */
	XLS_DOC("XLS_DOC", "D0CF11E0"),
	/**
	 * Microsoft Access
	 */
	MDB("MDB", "5374616E64617264204A"),
	/**
	 * Word Perfect
	 */
	WPB("WPB", "FF575043"),
	/**
	 * Postscript
	 */
	EPS_PS("EPS_PS", "252150532D41646F6265"),
	/**
	 * Adobe Acrobat
	 */
	PDF("PDF", "255044462D312E"),
	/**
	 * Windows Password
	 */
	PWL("PWL", "E3828596"),
	/**
	 * ZIP Archive
	 */
	ZIP("ZIP", "504B0304140000"),
	/**
	 * ARAR Archive
	 */
	RAR("RAR", "52617221"),
	/**
	 * WAV
	 */
	WAV("WAV", "52494646"),
	/**
	 * WAVE
	 */
	WMV("WMV", "3026B2758E66CF11"),
	/**
	 * AVI
	 */
	AVI("AVI", "52494646"),
	/**
	 * Real Audio
	 */
	RAM("RAM", "2E7261FD"),
	/**
	 * SWF
	 */
	SWF("SWF", "465753"),
	/**
	 * Real Media
	 */
	RM("RM_RMVB", "2E524D46"),
	/**
	 * Quicktime
	 */
	MOV("MOV", "6D6F6F76"),
	/**
	 * Windows Media
	 */
	ASF("ASF", "3026B2758E66CF11"),
	/**
	 * MIDI
	 */
	MID("MID", "4D546864");

	/**
	 * 文件类型
	 */
	private String type;

	private String magic;

	private FileTypeEnum(String type, String magic) {
		this.type = type;
		this.magic = magic;
	}

	public String getType() {
		return type;
	}

	public String getMagic() {
		return magic;
	}
}
