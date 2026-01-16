package com.app.novelvoice.excel.model;

import lombok.Data;

/**
 * 业主信息导入模型
 * 对应配置文件中的 ormClass: com.app.novelvoice.excel.model.HouseUserImportModel
 */
@Data
public class HouseUserImportModel {

    /**
     * 楼栋
     */
    private String number;

    /**
     * 单元
     */
    private String unit;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 房号
     */
    private String houseNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份: 租客/业主/亲属
     */
    private String type;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 联系手机
     */
    private String phone;

    /**
     * 与业主关系: 业主本人/租客/丈夫/妻子/女儿/儿子/父子/母亲/岳父/岳母/亲友/其他/祖孙/夫妻/租赁
     */
    private String relation;

    /**
     * 性别: 男/女
     */
    private String sex;

    /**
     * 证件类型: 身份证/护照/其它组织证件/港澳台通行证/警官证/军官证/营业执照
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idCard;

    /**
     * 常住人口: 是/否
     */
    private String oftenLive;

    /**
     * 人员标签
     */
    private String tags;
}
