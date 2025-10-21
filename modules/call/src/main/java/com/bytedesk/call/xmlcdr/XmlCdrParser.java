package com.bytedesk.call.xmlcdr;

import lombok.extern.slf4j.Slf4j;

// import com.bytedesk.call.cdr.CallCdrEntity;


/**
 * 解析 FreeSWITCH mod_xml_cdr 的 XML 为 CallCdrEntity
 * 尝试优先使用 *_epoch 字段，回退到 *_stamp 文本时间
 */
@Slf4j
public class XmlCdrParser {

    // private static final List<DateTimeFormatter> DATE_FORMATS = Arrays.asList(
    //         DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssVV"),
    //         DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    //         DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"),
    //         DateTimeFormatter.ISO_ZONED_DATE_TIME,
    //         DateTimeFormatter.ISO_OFFSET_DATE_TIME
    // );

    // public CallCdrEntity parse(String xml) {
    //     try {
    //         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //         factory.setNamespaceAware(true);
    //         factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    //         factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    //         factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

    //         DocumentBuilder builder = factory.newDocumentBuilder();
    //         Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    //         XPath xpath = XPathFactory.newInstance().newXPath();

    //         String uuid = pick(xpath, doc,
    //                 "//uuid",
    //                 "//variable_uuid",
    //                 "//Unique-ID",
    //                 "//variables/uuid");

    //         String callerIdName = pick(xpath, doc, "//caller_id_name", "//Caller-Caller-ID-Name");
    //         String callerIdNumber = pick(xpath, doc, "//caller_id_number", "//Caller-Caller-ID-Number");
    //         String destinationNumber = pick(xpath, doc, "//destination_number", "//Caller-Destination-Number");
    //         String context = pick(xpath, doc, "//context", "//variable_user_context", "//variables/user_context");
    //         String direction = pick(xpath, doc, "//direction");

    //         // 时间优先取 epoch
    //         ZonedDateTime start = parseTime(
    //                 pick(xpath, doc, "//start_epoch", "//variables/start_epoch"),
    //                 pick(xpath, doc, "//start_stamp", "//variables/start_stamp"));
    //         ZonedDateTime answer = parseTime(
    //                 pick(xpath, doc, "//answer_epoch", "//variables/answer_epoch"),
    //                 pick(xpath, doc, "//answer_stamp", "//variables/answer_stamp"));
    //         ZonedDateTime end = parseTime(
    //                 pick(xpath, doc, "//end_epoch", "//variables/end_epoch"),
    //                 pick(xpath, doc, "//end_stamp", "//variables/end_stamp"));

    //         Integer duration = parseInt(pick(xpath, doc, "//duration", "//variables/duration"));
    //         Integer billsec = parseInt(pick(xpath, doc, "//billsec", "//variables/billsec"));

    //         String hangupCause = pick(xpath, doc, "//hangup_cause", "//variables/hangup_cause");
    //         String accountcode = pick(xpath, doc, "//accountcode", "//variables/accountcode", "//account_code");
    //         String readCodec = pick(xpath, doc, "//read_codec", "//variables/read_codec");
    //         String writeCodec = pick(xpath, doc, "//write_codec", "//variables/write_codec");
    //         String sipHangupDisposition = pick(xpath, doc, "//sip_hangup_disposition", "//variables/sip_hangup_disposition");

    //         // 录音路径常见变量名：record_session / RECORD_FILE // 尽量多尝试
    //         String recordFile = pick(xpath, doc,
    //                 "//record_file",
    //                 "//variables/record_file",
    //                 "//variables/record_session",
    //                 "//variables/shout_record_file",
    //                 "//variables/RECORD_FILE");

    //     String blegUuid = pick(xpath, doc, "//bleg_uuid", "//variables/bridge_uuid", "//variables/bleg_uuid");
    //     String domainName = pick(xpath, doc, "//domain_name", "//variables/domain_name");
    //     String ani = pick(xpath, doc, "//ani", "//variables/ani");
    //     String aniii = pick(xpath, doc, "//aniii", "//variables/aniii");
    //     String networkAddr = pick(xpath, doc, "//network_addr", "//variables/network_addr");

    //         CallCdrEntity cdr = CallCdrEntity.builder()
    //                 .uid(nullSafe(uuid))
    //                 .callerIdName(nullSafe(callerIdName))
    //                 .callerIdNumber(nullSafe(callerIdNumber))
    //                 .destinationNumber(nullSafe(destinationNumber))
    //                 .context(nullSafe(context))
    //                 .startStamp(start)
    //                 .answerStamp(answer)
    //                 .endStamp(end)
    //                 .duration(duration)
    //                 .billsec(billsec)
    //                 .hangupCause(nullSafe(hangupCause))
    //                 .accountcode(nullSafe(accountcode))
    //                 .readCodec(nullSafe(readCodec))
    //                 .writeCodec(nullSafe(writeCodec))
    //                 .sipHangupDisposition(nullSafe(sipHangupDisposition))
    //                 .recordFile(nullSafe(recordFile))
    //                 .direction(nullSafe(direction))
    //                 .blegUuid(nullSafe(blegUuid))
    //                 .domainName(nullSafe(domainName))
    //                 .ani(nullSafe(ani))
    //                 .aniii(nullSafe(aniii))
    //                 .networkAddr(nullSafe(networkAddr))
    //                 .build();

    //         return cdr;
    //     } catch (Exception e) {
    //         log.error("解析 XML CDR 失败", e);
    //         throw new IllegalArgumentException("Invalid XML CDR", e);
    //     }
    // }

    // private static String pick(XPath xpath, Document doc, String... expressions) {
    //     for (String expr : expressions) {
    //         String val = text(xpath, doc, expr);
    //         if (val != null && !val.isBlank()) return val;
    //     }
    //     return null;
    // }

    // private static String text(XPath xpath, Document doc, String expr) {
    //     try {
    //         Node node = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
    //         if (node != null) return node.getTextContent();
    //         NodeList list = (NodeList) xpath.evaluate(expr, doc, XPathConstants.NODESET);
    //         if (list != null && list.getLength() > 0) return list.item(0).getTextContent();
    //         return null;
    //     } catch (Exception e) {
    //         return null;
    //     }
    // }

    // private static Integer parseInt(String s) {
    //     try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); } catch (Exception e) { return null; }
    // }

    // private static ZonedDateTime parseTime(String epoch, String stamp) {
    //     // 优先 epoch（秒）
    //     try {
    //         if (epoch != null && !epoch.isBlank()) {
    //             long sec = Long.parseLong(epoch.trim());
    //             return ZonedDateTime.ofInstant(Instant.ofEpochSecond(sec), ZoneId.systemDefault());
    //         }
    //     } catch (Exception ignored) {}

    //     if (stamp != null && !stamp.isBlank()) {
    //         String s = stamp.trim();
    //         for (DateTimeFormatter fmt : DATE_FORMATS) {
    //             try {
    //                 // 若没有时区，按系统默认
    //                 if (fmt == DATE_FORMATS.get(1)) { // yyyy-MM-dd HH:mm:ss
    //                     LocalDateTime ldt = LocalDateTime.parse(s, fmt);
    //                     return ldt.atZone(ZoneId.systemDefault());
    //                 } else {
    //                     return ZonedDateTime.parse(s, fmt);
    //                 }
    //             } catch (Exception ignored) {}
    //         }
    //     }
    //     return null;
    // }

    // private static String nullSafe(String s) {
    //     return (s == null || s.isBlank()) ? null : s;
    // }
}
