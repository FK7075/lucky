package com.lucky.web.enums;

import org.apache.commons.text.StringEscapeUtils;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/11 0011 15:08
 */
public enum EscapeType implements EscapeString {

    JAVA("JAVA"),SCRIPT("SCRIPT"),JSON("JSON"),HTML4("HTML4"),
    HTML3("HTML3"),XML11("XML11"),XML10("XML10"),CSV("CSV"),XSI("XSI");

    private String type;

    EscapeType(String type) {
        this.type = type;
    }

    @Override
    public String escape(String in) {
        switch (type){
            case "JAVA"   :return StringEscapeUtils.escapeJava(in);
            case "SCRIPT" :return StringEscapeUtils.escapeEcmaScript(in);
            case "JSON"   :return StringEscapeUtils.escapeJson(in);
            case "HTML4"  :return StringEscapeUtils.escapeHtml4(in);
            case "HTML3"  :return StringEscapeUtils.escapeHtml3(in);
            case "XML11"  :return StringEscapeUtils.escapeXml11(in);
            case "XML10"  :return StringEscapeUtils.escapeXml10(in);
            case "CSV"    :return StringEscapeUtils.escapeCsv(in);
            case "XSI"    :return StringEscapeUtils.escapeXSI(in);
            default: return in;
        }
    }

}
