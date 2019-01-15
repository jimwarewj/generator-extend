package com.test.jimware.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 *
 * @author wangjing0131
 * @date 2017/11/23
 */
public class ReflectHelper {
    private final static Logger logger= LoggerFactory.getLogger(ReflectHelper.class);

        public static Object getFieldValue(Object obj , String fieldName ){

            if(obj == null){
                return null ;
            }

            Field targetField = getTargetField(obj.getClass(), fieldName);

            try {
                return FieldUtils.readField(targetField, obj, true ) ;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null ;
        }

        public static Field getTargetField(Class<?> targetClass, String fieldName) {
            Field field = null;

            try {
                if (targetClass == null) {
                    return field;
                }

                if (Object.class.equals(targetClass)) {
                    return field;
                }

                field = FieldUtils.getDeclaredField(targetClass, fieldName, true);
                if (field == null) {
                    field = getTargetField(targetClass.getSuperclass(), fieldName);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }

            return field;
        }

        public static void setFieldValue(Object obj , String fieldName , Object value ){
            if(null == obj){return;}
            Field targetField = getTargetField(obj.getClass(), fieldName);
            try {
                FieldUtils.writeField(targetField, obj, value) ;
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(),e);
            }
        }

}
