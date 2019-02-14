package com.azureip.tmspider.util;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.xssf.usermodel.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ExcelUtil {

    private static final String DATE_REGEX = "yyyy-mm-dd";
    private static final String FONT_NAME = "等线";
    private static final double FONT_SIZE = 10;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * 设置单元格文本
     */
    public static void setText(XSSFWorkbook workbook, XSSFCell cell, String value) {
        // 设置字体：字体集3（等线）、10号
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFamily(FontFamily.MODERN);
        font.setFontName(FONT_NAME);
        font.setFontHeight(FONT_SIZE);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
        // 设置文本
        cell.setCellValue(value);
    }

    /**
     * 设置单元格超链接
     */
    public static void setHyperLink(XSSFWorkbook workbook, XSSFCell cell, String url) {
        // 设置字体：字体集3（等线）、10号、蓝色、下划线
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont tarFont = workbook.createFont();
        tarFont.setFamily(FontFamily.MODERN);
        tarFont.setFontName(FONT_NAME);
        tarFont.setFontHeight(FONT_SIZE);
        tarFont.setThemeColor((short) 10);
        tarFont.setUnderline((byte) 1);
        cellStyle.setFont(tarFont);
        cell.setCellStyle(cellStyle);
        // 设置超链接
        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        XSSFHyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.URL);
        hyperlink.setAddress(url);
        cell.setHyperlink(hyperlink);
    }

    /**
     * 设置单元格日期
     */
    public static void setDate(XSSFWorkbook workbook, XSSFCell cell, String dateStr) {
        // 设置日期字体：字体集3（等线）、10号
        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFamily(FontFamily.MODERN);
        font.setFontName(FONT_NAME);
        font.setFontHeight(FONT_SIZE);
        cellStyle.setFont(font);
        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(DATE_REGEX));
        cell.setCellStyle(cellStyle);
        // 设置日期
        try {
            cell.setCellValue(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            cell.setCellValue(dateStr);
        }
    }
}
