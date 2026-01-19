package com.gmrfid.file.storage.annotation;

import com.gmrfid.file.storage.config.FileStorageAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FileStorageAutoConfiguration.class)
public @interface EnableFileStorage {
}
