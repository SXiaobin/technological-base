# DynamicQuery && Paramter

用于动态拼接SQL，填充数据

```java
package com.inca.util;
import java.util.ArrayList;
import com.inca.np.util.SelectHelper;
/**
 * 动态查询工具类，用于拼接SQL、填充selectSqlHelper
 *
 * @author suxiaobin
 *
 */
public class DynamicQuery {
     private String templet = " AND %s %s ?";
     private String dateTemplet = " AND %s %s to_date(?, '%s')";
     private String baseSql;
     private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
     public DynamicQuery() {
     }
     /**
      * 要求baseSql带有where条件
      *
      * @param baseSql
      *            基础的 sql条件
      */
     public void setBaseSql(String baseSql) {
            this.baseSql = baseSql;
     }
     /**
      * @param parameter
      *            要在 sql条件上添加的where参数
      * */
     public void addParameter(Parameter parameter) {
            parameters.add(parameter);
     }
     /**
      * @return 动态添加where条件后的最终sql
      * */
     public String generateSql() {
           StringBuffer buffer = new StringBuffer( baseSql);
            for (Parameter p : parameters) {
                 if (p instanceof DateParameter) {
                     DateParameter dp = (DateParameter) p;
                     buffer.append(String. format(dateTemplet, dp.getField(),
                                dp.getOperator(), dp.getDateFormate()));
                      continue;
                }
                buffer.append(String. format(templet, p.getField(), p.getOperator()));
           }
            return buffer.toString();
     }
     /**
      * 将已动态添加到DynamicQuery中的parameter中的值，对应的添加到SelectHelper的参数中
      *
      * @param sh
      *            执行 sql的SelectHelper
      * */
     public void fillPreparedStatement(SelectHelper sh) {
            for (Parameter p : parameters) {
                sh.bindParam(p.getValue());
           }
     }
}



package com.inca.util;
/**
 * 查询参数类，用于表示条件参数对象
 *
 * @author suxiaobin
 *
 */
public class Parameter {
     private String field;
     private String value;
     private String operator;
     /**
      *
      * @param field
      *            数据库字段名
      * @param operator
      *            数据库操作符 =、>=、<、like etc...
      * @param value
      *            参数值 Object
      */
     public Parameter(String field, String operator, String value) {
            super();
            this.field = field;
            this.value = value;
            this.operator = operator;
     }
     public String getField() {
            return field ;
     }
     public String getValue() {
            return value ;
     }
     public String getOperator() {
            return operator ;
     }
}



package com.inca.util;
/**
 * 因为平台不支持date类型的参数传递，所以date类型where条件的拼接，请使用该类
 * @author suxiaobin
 * */
public class DateParameter extends Parameter {
     private String dateFormate;
     public DateParameter (String field, String operator, String value,
                String dateFormate) {
            super(field, operator, value);
            this.dateFormate = dateFormate;
     }
     public String getDateFormate() {
            return dateFormate ;
     }
}

```

