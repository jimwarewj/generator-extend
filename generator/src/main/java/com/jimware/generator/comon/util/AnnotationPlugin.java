package com.jimware.generator.comon.util;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Properties;

/**
 *
 * @author wangjing0131
 * @date 2017/12/29
 */
public class AnnotationPlugin extends PluginAdapter {
    private final static String selectPage = "selectPage";
    private final static String selectOne = "selectOne";
    private final static String selectAll = "selectAll";
    private boolean condition;
    private boolean conditionDate;
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        condition = Boolean.valueOf(properties.getProperty("condition"));
        conditionDate = Boolean.valueOf(properties.getProperty("conditionDate"));
    }
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        if(interfaze!=null&&interfaze.getType()!=null&&interfaze.getType().getShortName().endsWith("Dao")){
            interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
            interfaze.addAnnotation("@Repository");
            FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
            String pageClazz = getProperties().getProperty("pageClass");
            FullyQualifiedJavaType pageClazzType = new FullyQualifiedJavaType(pageClazz);
            interfaze.addImportedType(pageClazzType);
            Method m1 = new Method();
            m1.setName(selectPage);
            //Parameter p1 = new Parameter(baseRecordType, "record");
            Parameter p2 = new Parameter(pageClazzType , "pageable");
          //  p1.addAnnotation("@Param(\"record\")");
           // p2.addAnnotation("@Param(\"pageable\")");
          //  m1.addParameter(p1);
            m1.addParameter(p2);
            FullyQualifiedJavaType interfaceReturnType = FullyQualifiedJavaType.getNewListInstance();
            interfaceReturnType.addTypeArgument(baseRecordType);
            m1.setReturnType(interfaceReturnType);
            m1.addJavaDocLine("/**\r\n\t* 分页查询 <b>若无任何条件则不会返回数据</b><br>\r\n\t* \r\n\t*/");
            interfaze.addMethod(m1);
            Method m2 = new Method();
            m2.setName(selectOne);
            // E selectByExample(@Param("example") C example);
            p2 = new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example");
            m2.addParameter(p2);
            m2.setReturnType(baseRecordType);
            m2.addJavaDocLine("/**\r\n\t* 查一条 <b>查一条</b><br>\r\n\t* \r\n\t*/");
            interfaze.addMethod(m2);
        }

        return true;
    }
    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        parentElement.addElement(genSelectPage(document, introspectedTable));
        parentElement.addElement(genSelectOne(document, introspectedTable));
        return true;
    }
    private XmlElement genSelectPage(Document document,
                                     IntrospectedTable introspectedTable){

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", selectPage)); //$NON-NLS-1$
        if(introspectedTable.hasBLOBColumns()){
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getResultMapWithBLOBsId()));
        }else {
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getBaseResultMapId()));
        }

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                super.getProperties().getProperty("pageClass")));
        StringBuilder sb = new StringBuilder();

        sb.append("select"); //$NON-NLS-1$
//        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getBaseColumnListId()));
        answer.addElement(includeElement);
        if(introspectedTable.hasBLOBColumns()){
            sb.setLength(0);
            sb.append(",");
            answer.addElement(new TextElement(sb.toString()));
            includeElement = new XmlElement("include"); //$NON-NLS-1$
            includeElement.addAttribute(new Attribute("refid", introspectedTable.getBlobColumnListId()));
            answer.addElement(includeElement);
        }
        sb.setLength(0);
        sb.append("from ").append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()).append(" where 1=1 ");
        answer.addElement(new TextElement(sb.toString()));
       // answer.addElement(checkAllColumAndProtectForNoCondition(document, introspectedTable));

        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
            XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
            if("String".equals(((IntrospectedColumn) introspectedColumn).getFullyQualifiedJavaType().getShortName())){
                ifElement.addAttribute(new Attribute(
                        "test", introspectedColumn.getJavaProperty("condition.")+" != null and "
                        +introspectedColumn.getJavaProperty("condition.")+"!=''")); //$NON-NLS-1$
                sb.setLength(0);
                sb.append("and ").append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(" = "); //$NON-NLS-1$
                sb.append(MyBatis3FormattingUtilities
                        .getParameterClause(introspectedColumn, "condition."));

                ifElement.addElement(new TextElement(sb.toString()));
                answer.addElement(ifElement);
            }else{
                if("Date".equals(((IntrospectedColumn) introspectedColumn).getFullyQualifiedJavaType().getShortName())
                        &&conditionDate){
                    ifElement.addAttribute(new Attribute(
                            "test", "condition.start"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1)+" != null and "
                            +"condition.end"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1)+"!=null")); //$NON-NLS-1$
                    sb.setLength(0);
                    sb.append("and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn))
                            .append(" <![CDATA[>=]]> ").append("DATE_FORMAT(#{").append("condition.start"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1))
                            .append("},'%Y-%m-%d 00:00:00')");
                    sb.append("\n\t").append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn))
                            .append(" <![CDATA[<=]]> ").append("DATE_FORMAT(#{").append("condition.end").append(introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1))
                            .append("},'%Y-%m-%d 23:59:59')");
                    ifElement.addElement(new TextElement(sb.toString()));
                    answer.addElement(ifElement);
                    ifElement = new XmlElement("if");
                    ifElement.addAttribute(new Attribute(
                            "test", "condition.start"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1)+" != null and "
                            +"condition.end"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1)+"==null"));
                    sb.setLength(0);
                    sb.append("and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn))
                            .append(" <![CDATA[>=]]> ").append("DATE_FORMAT(#{").append("condition.start"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1))
                            .append("},'%Y-%m-%d 00:00:00')");
                    sb.append("\n\t").append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn))
                            .append(" <![CDATA[<=]]> ").append("DATE_FORMAT(").append("now(),'%Y-%m-%d 23:59:59')");
                    ifElement.addElement(new TextElement(sb.toString()));
                    answer.addElement(ifElement);
                    ifElement = new XmlElement("if");
                    ifElement.addAttribute(new Attribute(
                            "test", "condition.start"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1)+" == null and "
                            +"condition.end"+introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1)+"!=null"));
                    sb.setLength(0);
                    sb.append("and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn))
                            .append(" <![CDATA[<=]]> ").append("DATE_FORMAT(#{").append("condition.end").append(introspectedColumn.getJavaProperty().substring(0,1).toUpperCase()+introspectedColumn.getJavaProperty().substring(1))
                            .append("},'%Y-%m-%d 23:59:59')");
                    ifElement.addElement(new TextElement(sb.toString()));
                    answer.addElement(ifElement);
                }else {
                    ifElement.addAttribute(new Attribute(
                            "test", introspectedColumn.getJavaProperty("condition.") + " != null ")); //$NON-NLS-1$
                    sb.setLength(0);
                    sb.append("and ").append(MyBatis3FormattingUtilities
                            .getEscapedColumnName(introspectedColumn));
                    sb.append(" = "); //$NON-NLS-1$
                    sb.append(MyBatis3FormattingUtilities
                            .getParameterClause(introspectedColumn, "condition."));

                    ifElement.addElement(new TextElement(sb.toString()));
                    answer.addElement(ifElement);
                }
            }
        }

        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute(
                "test", "orderBy != null and orderBy!=''")); //$NON-NLS-1$

        sb.setLength(0);
        sb.append("order by");
        //sb.setLength(0);
       /* ifElement.addElement(new TextElement(sb.toString()));*/

      /*  XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection","pageable.sort"));
        foreachElement.addAttribute(new Attribute("item","order"));
        foreachElement.addAttribute(new Attribute("separator",","));*/

        sb.append(" ${orderBy} ${orderType}");
      //  foreachElement.addElement(new TextElement(sb.toString()));
        //  ifElement.addElement(foreachElement);

      /*  ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute("test","pageable.offset >= 0 and pageable.pageSize > 0"));

        sb.setLength(0);
        sb.append("limit ${pageable.offset}, ${pageable.pageSize}");*/
        ifElement.addElement(new TextElement(sb.toString()));
        answer.addElement(ifElement);

        return answer;
    }
    private XmlElement genSelectOne(Document document,
                                    IntrospectedTable introspectedTable){

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", selectOne)); //$NON-NLS-1$

        if(introspectedTable.hasBLOBColumns()){
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getResultMapWithBLOBsId()));
        }else {
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getBaseResultMapId()));
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select"); //$NON-NLS-1$
//        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getBaseColumnListId()));
        answer.addElement(includeElement);
        if(introspectedTable.hasBLOBColumns()){
            sb.setLength(0);
            sb.append(",");
            answer.addElement(new TextElement(sb.toString()));
             includeElement = new XmlElement("include"); //$NON-NLS-1$
            includeElement.addAttribute(new Attribute("refid", introspectedTable.getBlobColumnListId()));
            answer.addElement(includeElement);
        }
        sb.setLength(0);
        sb.append("from ").append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));


        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute(
                "test", "_parameter != null")); //$NON-NLS-1$

        includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getExampleWhereClauseId()));
        answer.addElement(ifElement);
        answer.addElement(includeElement);
        sb.setLength(0);
        sb.append(" order by id desc limit 1"); //$NON-NLS-1$
        answer.addElement(new TextElement(sb.toString()));
        return answer;
    }
}
