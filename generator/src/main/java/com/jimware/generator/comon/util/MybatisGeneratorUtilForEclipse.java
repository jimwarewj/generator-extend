package com.jimware.generator.comon.util;


import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author wangjing0131
 * @date 2017/12/29
 */
public class MybatisGeneratorUtilForEclipse {
    public static void main(String[] args)
    {
        try
        {
            System.out.println("startgenerator ...");
            List<String> warnings = new ArrayList();
            boolean overwrite = true;
            System.setProperty("gen.config.runtime.properties.name", "generatorForEclipse.properties");
            ConfigurationParser cp = new ConfigurationParser(warnings);

            Configuration config =  cp.parseConfiguration(MybatisGeneratorUtilForIDEA.class.getClassLoader().getResourceAsStream("generatorConfig.xml"));
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            for (String string : warnings) {
                System.out.println(string);
            }
            System.out.println("endgenerator!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (XMLParserException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
