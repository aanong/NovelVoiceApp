package com.app.novelvoice.common;

import lombok.Data;
import java.util.Date;

@Data
public class BaseEntity {
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
}
