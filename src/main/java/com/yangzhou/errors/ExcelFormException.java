package com.yangzhou.errors;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExcelFormException extends RuntimeException {

    Object errorRowList;

    public ExcelFormException(Object errorRowList) {
        super("excel文件内容格式不合法！");
        this.errorRowList = errorRowList;
    }

    public ExcelFormException(String message, Object errorRowList) {
        super(message);
        this.errorRowList = errorRowList;
    }
}
