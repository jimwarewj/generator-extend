package com.yao.invoice.comon.util;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author wangjing0131
 */
public class RenamePlugin extends PluginAdapter {
    private Pattern pattern1=Pattern.compile("Mapper$");
    private Pattern pattern2=Pattern.compile("Example$");
    private Pattern pattern3=Pattern.compile("Entity$");
    private boolean onlyGenForVO = false;
    private FullyQualifiedJavaType integer = new FullyQualifiedJavaType("java.lang.Integer");
    private FullyQualifiedJavaType string = new FullyQualifiedJavaType("java.lang.String");
    
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        onlyGenForVO = Boolean.valueOf(properties.getProperty("onlyGenForVO"));
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
    	for (IntrospectedColumn col : introspectedTable.getAllColumns()){
    		if (col.getFullyQualifiedJavaType().getFullyQualifiedName().equals("java.lang.Byte") 
    				|| col.getFullyQualifiedJavaType().getFullyQualifiedName().equals("java.lang.Short"))
    			col.setFullyQualifiedJavaType(integer);
    	}
        String oldType = null;
        if (onlyGenForVO){
        	oldType = introspectedTable.getBaseRecordType();
	        oldType = pattern3.matcher(oldType).replaceAll("VO");
	        if (!oldType.endsWith("VO")) oldType += "VO";
	        introspectedTable.setBaseRecordType(oldType);
	    	for (IntrospectedColumn col : introspectedTable.getAllColumns()){
	    		if (col.getFullyQualifiedJavaType().getFullyQualifiedName().equals("java.math.BigDecimal"))
	    			col.setFullyQualifiedJavaType(string);
	    	}
        }else{
	        oldType = introspectedTable.getMyBatis3JavaMapperType();
	        oldType = pattern1.matcher(oldType).replaceAll("Dao");
	        introspectedTable.setMyBatis3JavaMapperType(oldType);
	        
	        oldType = introspectedTable.getExampleType();
	        oldType = pattern2.matcher(oldType).replaceAll("Criteria");
	        introspectedTable.setExampleType(oldType);
	        
	        oldType = introspectedTable.getBaseRecordType();
	        if (!oldType.endsWith("Bean")) oldType += "Bean";
	        introspectedTable.setBaseRecordType(oldType);
        }
    }
}
