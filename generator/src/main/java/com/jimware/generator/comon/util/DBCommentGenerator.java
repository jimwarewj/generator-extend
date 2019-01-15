package com.jimware.generator.comon.util;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.internal.DefaultCommentGenerator;

/**
 * @author wangjing0131
 */
public class DBCommentGenerator extends DefaultCommentGenerator {

	@Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + getColumnDbTypeInfo(field, introspectedColumn) + "<br>");
        field.addJavaDocLine(" * " + (introspectedColumn.getDefaultValue() != null ? "默认值[" + introspectedColumn.getDefaultValue() + "]<br>" : ""));
        field.addJavaDocLine(" * " + (introspectedColumn.isNullable() ? "" : "必填<br>"));
        field.addJavaDocLine(" * " + (introspectedColumn.getRemarks() != null ? introspectedColumn.getRemarks()+"<br>" : ""));
        field.addJavaDocLine(" */");

    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        if (introspectedColumn.getRemarks() != null) {
            method.addJavaDocLine(" * 获得 " + introspectedColumn.getRemarks() + "<br>");
        }
        method.addJavaDocLine(" * " + getColumnDbTypeInfo(introspectedColumn));
        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * " + getColumnDbTypeInfo(introspectedColumn) + "<br>");
        if (introspectedColumn.getRemarks() != null) {
            method.addJavaDocLine(" * 设置 " + introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(" */");
    }
    public String getColumnDbTypeInfo(IntrospectedColumn introspectedColumn){
    	return getColumnDbTypeInfo(null, introspectedColumn);
    }
    
    public void addAnnotations(Field field, IntrospectedColumn col, boolean markId) {
    }
    
    private String getColumnDbTypeInfo(Field field, IntrospectedColumn introspectedColumn) {
    	addAnnotations(field, introspectedColumn, true);
        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJdbcTypeName());
        if (introspectedColumn.getLength() > 0) {
            sb.append("(");
            sb.append(introspectedColumn.getLength());
            if (introspectedColumn.getScale() > 0) {
                sb.append(",").append(introspectedColumn.getScale());
            }
            sb.append(")");
            
        }
        if (introspectedColumn.getDefaultValue() != null) {
            sb.append(" 默认值[" + introspectedColumn.getDefaultValue() + "]");
        }
        if (!introspectedColumn.isNullable()) {
            sb.append(" 必填");
        }
        return sb.toString();
    }
}
