# Excel Spring Boot Starter

基于EasyExcel的动态Excel导入导出工具，支持YML配置驱动。

## 功能特性

- 🔧 **YML配置驱动** - 通过YML配置定义Excel导入导出规则，无需编写代码
- 🔄 **双向转换** - Map形式和类型化对象双向转换
- ✅ **SpEL验证** - 支持丰富的表达式验证规则
- 📦 **批处理** - 支持大数据量批处理回调
- 🎨 **样式定制** - 支持自定义导出样式
- 🔌 **易集成** - Spring Boot Starter自动配置

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.app.tool</groupId>
    <artifactId>excel-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置任务

在`application.yml`中配置:

```yaml
excel:
  tasks:
    - type: user_import
      name: 用户导入
      sheets:
        - name: 用户数据
          sheetIndex: 0
          headRowNumber: 1
          ormClass: com.example.model.User  # 可选: 指定转换目标类
          columns:
            - title: 用户名
              field: username
              verifyExpression: "#notBlank(#val)"
            - title: 邮箱
              field: email
              verifyExpression: "#empty(#val) || #email(#val)"
```

### 3. 使用服务

```java
@Autowired
private ExcelService excelService;

// 导入Excel (Map形式)
ExcelImportResult<Map<String, Object>> result = excelService.importExcel(file, "user_import");

// 导入Excel (类型化形式)
ExcelImportResult<User> result = excelService.importExcelAsObject(file, "user_import");

// 直接获取对象列表
List<User> users = excelService.importExcelAsObjectList(file, "user_import");

// 导出Excel
ExcelExportRequest request = new ExcelExportRequest();
request.setTaskType("user_import");
request.setMapData(dataList);
excelService.exportExcel(response, request);
```

## 支持的验证表达式

| 表达式 | 说明 |
|--------|------|
| `#notBlank(#val)` | 非空验证 |
| `#empty(#val)` | 空值(允许为空) |
| `#lengthLessThan(#val, 64)` | 长度小于 |
| `#lengthBetween(#val, 5, 20)` | 长度范围 |
| `#options(#val, 'A', 'B')` | 选项验证 |
| `#dateFormat(#val, 'yyyy/MM/dd')` | 日期格式 |
| `#email(#val)` | 邮箱格式 |
| `#phone(#val)` | 手机号(中国大陆) |
| `#idCard(#val)` | 身份证号(18位) |
| `#regex(#val, '^[a-z]+$')` | 正则表达式 |
| `#doubleGreaterThan(#val, 0)` | 数值比较(double) |
| `#longBetween(#val, 1, 100)` | 数值范围(long) |

表达式可以使用 `&&` 和 `||` 组合。

## 配置项说明

### 任务配置 (ExcelTaskConfig)

| 属性 | 说明 | 默认值 |
|------|------|--------|
| type | 任务类型(唯一标识) | - |
| name | 任务名称 | - |
| uploadUseFileBuffer | 是否使用文件缓冲 | false |
| failureFileDescription | 失败文件描述 | 下载失败数据 |
| verifyDuplicateTask | 是否验证重复任务 | false |
| maxParallelRunNum | 最大并行运行数 | 10 |
| exportFileName | 导出文件名模板 | - |

### Sheet配置 (ExcelSheetConfig)

| 属性 | 说明 | 默认值 |
|------|------|--------|
| name | Sheet名称 | - |
| sheetIndex | Sheet索引(从0开始) | 0 |
| minRowLimit | 最小行数限制 | 1 |
| maxRowLimit | 最大行数限制 | 10000 |
| headRowNumber | 表头行数 | 1 |
| ormClass | ORM映射类全限定名 | - |

### 列配置 (ExcelColumnConfig)

| 属性 | 说明 | 默认值 |
|------|------|--------|
| title | 列标题(表头显示名称) | - |
| field | 对应的实体字段名 | - |
| fieldType | 字段类型 | string |
| dateFormat | 日期格式 | yyyy-MM-dd |
| verifyExpression | 验证表达式 | - |
| errorMessage | 验证失败提示 | - |
| required | 是否必填 | false |
| width | 列宽(导出时使用) | 20 |
| exportable | 是否导出 | true |
| importable | 是否导入 | true |

## License

MIT
