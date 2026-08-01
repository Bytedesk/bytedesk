package com.bytedesk.core.base;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.utils.BdDateUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ExcelExportUtils {

    private ExcelExportUtils() {
    }

    public static <E> void writeExcel(HttpServletResponse response,
                                      Object request,
                                      List<E> excelList,
                                      Class<E> excelClass,
                                      String sheetName,
                                      String filePrefix) throws Exception {
        prepareExcelResponse(response, request, filePrefix);
        String resolvedSheetName = resolveSheetName(request, sheetName, filePrefix);

        List<ExcelFieldMeta> exportFields = resolveExportFields(excelClass);
        Map<String, String> headerOverrides = resolveHeaderOverrides(request);
        String lang = resolveRequestString(request, "getLang");

        var writer = EasyExcel.write(response.getOutputStream())
                .autoCloseStream(Boolean.FALSE)
                .head(buildHead(exportFields, headerOverrides, lang));

        SheetWriteHandler widthHandler = buildColumnWidthHandler(exportFields);
        if (widthHandler != null) {
            writer.registerWriteHandler(widthHandler);
        }

        writer.sheet(resolvedSheetName)
            .doWrite(buildRows(exportFields, excelList, lang));
    }

    public static void writeCustomExcel(HttpServletResponse response,
                                        String sheetName,
                                        String filePrefix,
                                        List<List<String>> head,
                                        List<List<Object>> rows,
                                        int[] columnWidths) throws Exception {
        prepareExcelResponse(response, filePrefix);

        var writer = EasyExcel.write(response.getOutputStream())
                .autoCloseStream(Boolean.FALSE)
                .head(head);

        SheetWriteHandler widthHandler = buildColumnWidthHandler(columnWidths);
        if (widthHandler != null) {
            writer.registerWriteHandler(widthHandler);
        }

        writer.sheet(sheetName)
                .doWrite(rows);
    }

    public static Locale resolveLocale(Object request) {
        String lang = resolveRequestString(request, "getLang");
        return Locale.forLanguageTag(lang == null || lang.isBlank() ? "zh-CN" : lang.replace('_', '-'));
    }

    private static void prepareExcelResponse(HttpServletResponse response, Object request, String filePrefix) {
        String exportFileName = resolveRequestString(request, "getExportFileName");
        prepareExcelResponse(response, StringUtils.hasText(exportFileName) ? exportFileName.trim() : filePrefix);
    }

    private static String resolveSheetName(Object request, String sheetName, String filePrefix) {
        String exportSheetName = resolveRequestString(request, "getExportSheetName");
        if (StringUtils.hasText(exportSheetName)) {
            return exportSheetName.trim();
        }

        String exportFileName = resolveRequestString(request, "getExportFileName");
        if (StringUtils.hasText(exportFileName)) {
            return exportFileName.trim();
        }

        if (StringUtils.hasText(sheetName)) {
            return sheetName;
        }
        return filePrefix;
    }

    private static void prepareExcelResponse(HttpServletResponse response, String filePrefix) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode(filePrefix + "-" + BdDateUtils.formatDatetimeUid() + ".xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
    }

    private static List<List<String>> buildHead(List<ExcelFieldMeta> exportFields,
                                                Map<String, String> headerOverrides,
                                                String lang) {
        List<List<String>> head = new ArrayList<>();
        for (ExcelFieldMeta field : exportFields) {
            String title = headerOverrides.get(field.name());
            if (title == null || title.isBlank()) {
                title = resolveCommonHeader(field.name(), lang);
            }
            if (title == null || title.isBlank()) {
                title = field.header();
            }
            head.add(List.of(title));
        }
        return head;
    }

    private static List<List<Object>> buildRows(List<ExcelFieldMeta> exportFields, List<?> excelList, String lang) throws IllegalAccessException {
        List<List<Object>> rows = new ArrayList<>();
        for (Object item : excelList) {
            List<Object> row = new ArrayList<>();
            for (ExcelFieldMeta field : exportFields) {
                Object value = field.field().get(item);
                row.add(formatValue(value, field.datePattern(), lang));
            }
            rows.add(row);
        }
        return rows;
    }

    private static Object formatValue(Object value, String datePattern, String lang) {
        if (value == null) {
            return "";
        }
        if (value instanceof TemporalAccessor temporalAccessor && datePattern != null && !datePattern.isBlank()) {
            return DateTimeFormatter.ofPattern(datePattern).format(temporalAccessor);
        }
        if (value instanceof String stringValue) {
            return localizeStringValue(stringValue, lang);
        }
        return value;
    }

    private static String localizeStringValue(String value, String lang) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        if (!value.startsWith(I18Consts.I18N_PREFIX) && !value.startsWith("ROLE_")) {
            return value;
        }
        if (!ApplicationContextHolder.isInitialized()) {
            return value;
        }
        try {
            MessageSource messageSource = ApplicationContextHolder.getBean(MessageSource.class);
            Locale locale = Locale.forLanguageTag(lang == null || lang.isBlank() ? "zh-CN" : lang.replace('_', '-'));
            return messageSource.getMessage(value, null, value, locale);
        } catch (Exception ignored) {
            return value;
        }
    }

    private static SheetWriteHandler buildColumnWidthHandler(List<ExcelFieldMeta> exportFields) {
        Map<Integer, Integer> widthMap = new LinkedHashMap<>();
        for (int index = 0; index < exportFields.size(); index++) {
            Integer width = exportFields.get(index).width();
            if (width != null && width > 0) {
                widthMap.put(index, width);
            }
        }
        if (widthMap.isEmpty()) {
            return null;
        }
        return new SheetWriteHandler() {
            @Override
            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                Sheet sheet = writeSheetHolder.getSheet();
                widthMap.forEach((column, width) -> sheet.setColumnWidth(column, width * 256));
            }
        };
    }

    private static SheetWriteHandler buildColumnWidthHandler(int[] columnWidths) {
        if (columnWidths == null || columnWidths.length == 0) {
            return null;
        }
        return new SheetWriteHandler() {
            @Override
            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                Sheet sheet = writeSheetHolder.getSheet();
                for (int columnIndex = 0; columnIndex < columnWidths.length; columnIndex++) {
                    if (columnWidths[columnIndex] > 0) {
                        sheet.setColumnWidth(columnIndex, columnWidths[columnIndex] * 256);
                    }
                }
            }
        };
    }

    private static Map<String, String> resolveHeaderOverrides(Object request) {
        String exportHeaders = resolveRequestString(request, "getExportHeaders");
        if (exportHeaders == null || exportHeaders.isBlank()) {
            return Map.of();
        }
        Object parsed = JSON.parse(exportHeaders);
        if (!(parsed instanceof Map<?, ?> parsedMap)) {
            return Map.of();
        }

        Map<String, String> headers = new LinkedHashMap<>();
        parsedMap.forEach((key, value) -> {
            if (key != null && value != null) {
                headers.put(String.valueOf(key), String.valueOf(value));
            }
        });
        return headers;
    }

    private static String resolveRequestString(Object request, String methodName) {
        try {
            Method method = request.getClass().getMethod(methodName);
            Object value = method.invoke(request);
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static List<ExcelFieldMeta> resolveExportFields(Class<?> excelClass) {
        List<ExcelFieldMeta> fields = new ArrayList<>();
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> current = excelClass; current != null && current != Object.class; current = current.getSuperclass()) {
            hierarchy.add(0, current);
        }

        int declarationOrder = 0;
        for (Class<?> current : hierarchy) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(ExcelIgnore.class)) {
                    continue;
                }
                field.setAccessible(true);
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                DateTimeFormat dateTimeFormat = field.getAnnotation(DateTimeFormat.class);
                ColumnWidth columnWidth = field.getAnnotation(ColumnWidth.class);
                String header = field.getName();
                int index = Integer.MAX_VALUE;
                int order = Integer.MAX_VALUE;
                if (excelProperty != null) {
                    if (excelProperty.value().length > 0 && !excelProperty.value()[0].isBlank()) {
                        header = excelProperty.value()[0];
                    }
                    if (excelProperty.index() >= 0) {
                        index = excelProperty.index();
                    }
                    order = excelProperty.order();
                }
                fields.add(new ExcelFieldMeta(
                        field.getName(),
                        header,
                        field,
                        index,
                        order,
                        declarationOrder++,
                        dateTimeFormat == null ? null : dateTimeFormat.value(),
                        columnWidth == null ? null : Math.round(columnWidth.value())
                ));
            }
        }

        fields.sort(Comparator
                .comparingInt(ExcelFieldMeta::index)
                .thenComparingInt(ExcelFieldMeta::order)
                .thenComparingInt(ExcelFieldMeta::declarationOrder));
        return fields;
    }

    private static String resolveCommonHeader(String fieldName, String lang) {
        if (fieldName == null) {
            return null;
        }
        Locale locale = Locale.forLanguageTag(lang == null || lang.isBlank() ? "zh-CN" : lang.replace('_', '-'));
        String language = locale.getLanguage();
        return switch (fieldName) {
            case "name" -> switch (language) {
                case "ja" -> "名称";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "名稱" : "名称";
                default -> "Name";
            };
            case "nickname" -> switch (language) {
                case "ja" -> "ニックネーム";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "暱稱" : "昵称";
                default -> "Nickname";
            };
            case "username" -> switch (language) {
                case "ja" -> "ユーザー名";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "使用者名稱" : "用户名";
                default -> "Username";
            };
            case "user" -> switch (language) {
                case "ja" -> "ユーザー";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "使用者" : "用户";
                default -> "User";
            };
            case "email" -> switch (language) {
                case "ja" -> "メール";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "郵箱" : "邮箱";
                default -> "Email";
            };
            case "mobile" -> switch (language) {
                case "ja" -> "携帯電話";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "手機" : "手机";
                default -> "Mobile";
            };
            case "telephone" -> switch (language) {
                case "ja" -> "電話";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "電話" : "电话";
                default -> "Telephone";
            };
            case "jobNo" -> switch (language) {
                case "ja" -> "社員番号";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "工號" : "工号";
                default -> "Job Number";
            };
            case "jobTitle" -> switch (language) {
                case "ja" -> "役職";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "職位" : "职位";
                default -> "Job Title";
            };
            case "departmentName" -> switch (language) {
                case "ja" -> "部署";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "部門" : "部门";
                default -> "Department";
            };
            case "seatNo" -> switch (language) {
                case "ja" -> "座席番号";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "座席號" : "座位号";
                default -> "Seat Number";
            };
            case "title" -> switch (language) {
                case "ja" -> "タイトル";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "標題" : "标题";
                default -> "Title";
            };
            case "action" -> switch (language) {
                case "ja" -> "操作";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "操作" : "操作";
                default -> "Action";
            };
            case "ip" -> switch (language) {
                case "ja" -> "IP";
                case "zh" -> "IP";
                default -> "IP";
            };
            case "ipLocation" -> switch (language) {
                case "ja" -> "IP所在地";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "IP位置" : "IP位置";
                default -> "IP Location";
            };
            case "type" -> switch (language) {
                case "ja" -> "種類";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "類型" : "类型";
                default -> "Type";
            };
            case "color" -> switch (language) {
                case "ja" -> "色";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "顏色" : "颜色";
                default -> "Color";
            };
            case "uid" -> switch (language) {
                case "ja" -> "UID";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "唯一 UID" : "唯一Uid";
                default -> "UID";
            };
            case "createdAt" -> switch (language) {
                case "ja" -> "作成時間";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "建立時間" : "创建时间";
                default -> "Created At";
            };
            case "updatedAt" -> switch (language) {
                case "ja" -> "更新時間";
                case "zh" -> locale.getCountry().equalsIgnoreCase("TW") ? "更新時間" : "更新时间";
                default -> "Updated At";
            };
            default -> null;
        };
    }

    private record ExcelFieldMeta(String name,
                                  String header,
                                  Field field,
                                  int index,
                                  int order,
                                  int declarationOrder,
                                  String datePattern,
                                  Integer width) {
    }
}