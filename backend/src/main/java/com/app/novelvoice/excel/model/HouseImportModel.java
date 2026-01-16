package com.app.novelvoice.excel.model;

import lombok.Data;

/**
 * 房屋信息导入模型
 * 对应配置文件中的 ormClass: com.app.novelvoice.excel.model.HouseImportModel
 */
@Data
public class HouseImportModel {

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
     * 房屋用途: 住宅/公寓/车位/商业
     */
    private String purpose;

    /**
     * 建筑面积(平方米)
     */
    private Double buildArea;

    /**
     * 专有建筑面积(平方米)
     */
    private Double realArea;

    /**
     * 房屋状态: 未售出/已售出
     */
    private String houseStatus;

    /**
     * 数据来源: 住建同步/物业企业/社区街道/自治组织
     */
    private String dataSource;

    /**
     * 备注(公开业主)
     */
    private String openRemark;

    /**
     * 备注(非公开)
     */
    private String privateRemark;
}
