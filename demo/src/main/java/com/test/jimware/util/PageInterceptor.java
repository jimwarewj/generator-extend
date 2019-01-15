package com.test.jimware.util;

import com.alibaba.druid.sql.PagerUtils;
import com.test.jimware.vo.Pager;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.beans.IntrospectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author wangjing0131
 * @date 2017/11/23
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class })
})
public class PageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
            MappedStatement mappedStatement=(MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
            BoundSql boundSql = statementHandler.getBoundSql();
            Object obj = boundSql.getParameterObject();
            if (obj instanceof Pager<?>) {
                /*Pager<?> page = (Pager<?>) obj;*/
                //拦截到的prepare方法参数是一个Connection对象
                Connection connection = (Connection)invocation.getArgs()[0];
                String sql = boundSql.getSql();
                //给当前的page参数对象设置总记录数
                this.setTotalRecord(obj,  mappedStatement, connection);
                //获取分页Sql语句
                String pageSql = this.getPageSql(obj, sql);
                //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
                ReflectHelper.setFieldValue(boundSql, "sql", pageSql);
            }
        }

        return invocation.proceed();
    }

    /**
     * 给当前的参数对象page设置总记录数
     *
     * @param page Mapper映射语句对应的参数对象
     * @param mappedStatement Mapper映射语句
     * @param connection 当前的数据库连接
     */
    private void setTotalRecord(Object page,
                                MappedStatement mappedStatement,Connection connection) throws IntrospectionException {
        //获取对应的BoundSql，这个BoundSql其实跟我们利用StatementHandler获取到的BoundSql是同一个对象。
        //delegate里面的boundSql也是通过mappedStatement.getBoundSql(paramObj)方法获取到的。
        BoundSql boundSql = mappedStatement.getBoundSql(page);
        //获取到我们自己写在Mapper映射语句中对应的Sql语句
        String sql = boundSql.getSql();
        //通过查询Sql语句获取到对应的计算总记录数的sql语句
        String countSql = this.getCountSql(sql);
        //通过BoundSql获取对应的参数映射
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        List<ParameterMapping> paras=new ArrayList<>();
        for(ParameterMapping para:parameterMappings){
            if("startNum".equals(para.getProperty())){
                continue;
            }
            if("endNum".equals(para.getProperty())){
                continue;
            }
            if("orderBy".equals(para.getProperty())){
                continue;
            }
            if("orderType".equals(para.getProperty())){
                continue;
            }
            paras.add(para);
        }
        //利用Configuration、查询记录数的Sql语句countSql、参数映射关系parameterMappings和参数对象page建立查询记录数对应的BoundSql对象。
        BoundSql countBoundSql = boundSql ;
        //通过mappedStatement、参数对象page和BoundSql对象countBoundSql建立一个用于设定参数的ParameterHandler对象
        //通过connection建立一个countSql对应的PreparedStatement对象。
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            ReflectHelper.setFieldValue(countBoundSql, "sql", countSql);
            ReflectHelper.setFieldValue(countBoundSql, "parameterMappings", paras);
            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, page, countBoundSql);


            pstmt = connection.prepareStatement(countSql);
            //通过parameterHandler给PreparedStatement对象设置参数
            parameterHandler.setParameters(pstmt);
            //之后就是执行获取总记录数的Sql语句和获取结果了。
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalRecord = rs.getInt(1);
                //给当前的参数page对象设置总记录数
                ReflectHelper.setFieldValue(page, "totalCount", totalRecord);
                // page.setTotalCount(totalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据原Sql语句获取对应的查询总记录数的Sql语句
     * @param sql
     * @return
     */
    private String getCountSql(String sql) {
        /*sql=sql.replaceAll("\n"," ").replaceAll(" {2,}?"," ").replaceAll("\t"," ").replaceAll(" {2,}?"," ");
        Matcher matcher=FROM.matcher(sql);
        Matcher matcherORDER=ORDER.matcher(sql);
        Matcher matcherGROUP=GROUP.matcher(sql);
        Matcher matcherLIMIT=LIMIT.matcher(sql);
        if(matcher.find()){
            int index = sql.lastIndexOf(matcher.group());
            String from=sql.substring(index);
            if(matcherORDER.find()){
                int orderbyIndex=sql.lastIndexOf(matcherORDER.group());
                if(orderbyIndex>0){
                    from=sql.substring(index,orderbyIndex);
                    if(matcherGROUP.find()){
                        int groupIdx=from.indexOf(matcherGROUP.group());
                        if(groupIdx>0){
                            from=from.substring(0,groupIdx);
                        }
                    }
                }

            }
            if(matcherLIMIT.find()){
                int limitIndex=from.lastIndexOf(matcherLIMIT.group());
                if(limitIndex>0){
                    from= from.substring(0,limitIndex);
                }
                return "select count(1) " + from;
            }
            return "select count(1) " + from;

        }*/
        return PagerUtils.count(sql,"mysql");
    }

    public static void main(String[] args) {
        String sql="select childOrderid from invoice_info a where a.invoiceStatus=3 group by childOrderid order by id limit 0,50";
//        String sql="select childOrderid from\ninvoice_info as a where a.invoiceStatus=3 limit 0 ,50";
//        System.out.println(new PageInterceptor().getCountSql(sql));
//        String str = " Select orderId from dual ORDer by id";
      /*  Matcher m = Pattern.compile("\\sorder\\s",Pattern.CASE_INSENSITIVE).matcher(str);
        while (m.find()) {
            if (!"".equals(m.group())) {
                System.out.println(m.group());
            }
        }*/
        String str="select * from `invoice_info` where 1=1";
        System.out.println(PagerUtils.limit(str,"mysql",2,50));
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }
    /**
     * 获取Mysql数据库的分页查询语句
     * @param page 分页对象
     * @param sqlBuffer 包含原sql语句的StringBuffer对象
     * @return Mysql数据库分页语句
     */
    private String getMysqlPageSql(Object page, StringBuffer sqlBuffer) {
        //计算第一条记录的位置，Mysql中记录的位置是从0开始的。
//     System.out.println("page:"+page.getPage()+"-------"+page.getRows());
        /*sqlBuffer.append(" limit ").append((page.getPageNumber() - 1) * page.getPageSize()).append(",").append(page.getPageSize());
        return sqlBuffer.toString();*/
        Integer pageNumber=(Integer) ReflectHelper.getFieldValue(page, "pageNumber");
        Integer pageSize=(Integer) ReflectHelper.getFieldValue(page, "pageSize");
        int offset=(pageNumber-1)*pageSize;
        return PagerUtils.limit(sqlBuffer.toString(),"mysql",offset,pageSize);
    }
    /**
     * 根据page对象获取对应的分页查询Sql语句
     *
     * @param page 分页对象
     * @param sql 原sql语句
     * @return
     */
    private String getPageSql(Object page, String sql) {
       /* Matcher matcher= LIMIT.matcher(sql);
        if(matcher.find()){
            int idex=sql.lastIndexOf(matcher.group());
            sql=idex<0?sql:sql.substring(0,idex-1);
            StringBuffer sqlBuffer = new StringBuffer(sql);

            return getMysqlPageSql(page, sqlBuffer);
        }
        return getMysqlPageSql(page,  new StringBuffer(sql));*/
        return getMysqlPageSql(page,new StringBuffer(sql));
    }
}
