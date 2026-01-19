package com.gmrfid.excel.annotation;

import com.gmrfid.excel.ExcelAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ExcelAutoConfiguration.class)
public @interface EnableExcel {
}
