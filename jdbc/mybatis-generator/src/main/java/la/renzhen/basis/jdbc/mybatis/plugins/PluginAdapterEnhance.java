package la.renzhen.basis.jdbc.mybatis.plugins;

import com.google.common.base.CaseFormat;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * PluginAdapter的扩展类
 *
 * <pre>
 * 主要包括了:
 * -获取系统分割符
 * -添加字段同时也添加get,set方法
 * </pre>
 *
 * @author Patrick
 */
public abstract class PluginAdapterEnhance extends PluginAdapter {

    /**
     * 取消验证
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 获取系统分隔符
     *
     * @return 系统分隔符
     */
    protected String getSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 添加字段，同时也添加get,set方法
     *
     * @param topLevelClass 上层类
     * @param introspectedTable 表
     * @param field 字段
     */
    protected void addField(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, Field field) {
        String fieldName = field.getName();
        // 添加Java字段
        topLevelClass.addField(field);

        // 生成Set方法
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + MethodName(fieldName));
        method.addParameter(new Parameter(field.getType(), fieldName));
        method.addBodyLine("this." + fieldName + "=" + fieldName + ";");
        topLevelClass.addMethod(method);

        // 生成Get方法
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName("get" + MethodName(fieldName));
        method.addBodyLine("return " + fieldName + ";");
        topLevelClass.addMethod(method);
    }

    protected static String MethodName(String fieldName) {
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(fieldName);
    }
    protected static String methodName(String fieldName) {
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(fieldName);
    }
}