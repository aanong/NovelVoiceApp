package com.app.novelvoice.excel.model;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 账单数据导入模型
 * 用于演示Excel导入转换为Java对象
 * 对应配置文件中的 ormClass: com.app.novelvoice.excel.model.BillDataImportModel
 */
@Data
public class BillDataImportModel {

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
     * 虚拟房号
     */
    private String roomNo;

    /**
     * 交款状态: 已交帐/未交帐
     */
    private String paymentStatus;

    /**
     * 使用人姓名
     */
    private String ownerName;

    /**
     * 科目名称
     */
    private String name;

    /**
     * 科目属性: 公共收入/非公共收入
     */
    private String incomeType;

    /**
     * 计量单位名称
     */
    private String meteringUnitName;

    /**
     * 账单生产日期
     */
    private String billDate;

    /**
     * 账期开始时间
     */
    private String billStartDate;

    /**
     * 账期结束时间
     */
    private String billEndDate;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Long numbers;

    /**
     * 应收金额
     */
    private BigDecimal billAmount;

    /**
     * 用量起数
     */
    private Long dosageStartNum;

    /**
     * 用量止数
     */
    private Long dosageEndNum;

    /**
     * 是否校验: 是/否
     */
    private String hasCheck;

    // ========== 已缴费数据额外字段 ==========

    /**
     * 违约金额
     */
    private BigDecimal defalutAmount;

    /**
     * 应缴金额
     */
    private BigDecimal payableAmount;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 实缴金额
     */
    private BigDecimal paidInAmount;

    /**
     * 缴费方式
     */
    private String payWay;

    /**
     * 编号
     */
    private String customPayNum;

    /**
     * 缴费日期
     */
    private String payDate;
}
