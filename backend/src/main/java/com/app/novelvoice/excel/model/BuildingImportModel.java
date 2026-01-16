package com.app.novelvoice.excel.model;

import lombok.Data;

/**
 * 楼栋导入模型
 * 对应配置文件中的 ormClass: com.app.novelvoice.excel.model.BuildingImportModel
 */
@Data
public class BuildingImportModel {

    /**
     * 楼栋
     */
    private String number;

    /**
     * 单元
     */
    private String unit;

    /**
     * 楼栋性质
     */
    private String buildingType;

    /**
     * 有无电梯: 有/无
     */
    private String haveElevator;

    /**
     * 地下层数
     */
    private Long basementFloors;

    /**
     * 地面层数
     */
    private Long groundFloors;

    /**
     * 楼层高度(米)
     */
    private Double floorHeight;

    /**
     * 楼栋高度(米)
     */
    private Double buildingHeight;
}
