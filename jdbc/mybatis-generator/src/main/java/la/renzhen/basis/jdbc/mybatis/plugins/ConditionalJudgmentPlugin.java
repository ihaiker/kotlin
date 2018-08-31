package la.renzhen.basis.jdbc.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>
 *
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 23/06/2018 2:44 PM
 */
public class ConditionalJudgmentPlugin extends PluginAdapterEnhance {
    /**
     * 添加判断
     *
     * @param topLevelClass     example class
     * @param introspectedTable table info
     * @return true
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        InnerClass innerClass = topLevelClass.getInnerClasses()
                .stream().filter(s -> s.getType().getShortName().equals("Criteria")).findFirst().get();

        ifNotNull(innerClass);
        check(innerClass);

        ifEmpty(innerClass);
        ifNotEmpty(innerClass);

        equals(innerClass);
        return true;
    }

    private Method method(InnerClass innerClass, String name, boolean fn, BiConsumer<InnerClass, Method> methodConsumer) {
        Method method = new Method(name);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(innerClass.getType());

        methodConsumer.accept(innerClass, method);

        if (fn) {
            Parameter parameter = method.getParameters().get(0);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("java.util.function.Function") {{
                addTypeArgument(parameter.getType());
                addTypeArgument(innerClass.getType());
            }}, "fn"));
        } else {
            method.addParameter(new Parameter(new FullyQualifiedJavaType("java.util.function.Consumer") {{
                addTypeArgument(innerClass.getType());
            }}, "cr"));
        }
        method.addBodyLine("return this;");
        innerClass.addMethod(method);
        return method;
    }

    private void ifNotNull(InnerClass innerClass) {
        method(innerClass, "ifNotNull", false, (ic, method) -> {
            method.addParameter(0, new Parameter(new FullyQualifiedJavaType("Object"), "t"));
            method.addBodyLine("if(t != null){ cr.accept(this); }");
        });
    }

    private void check(InnerClass innerClass) {
        method(innerClass, "assertTrue", false, (ic, method) -> {
            method.addParameter(0, new Parameter(new FullyQualifiedJavaType("boolean"), "t"));
            method.addBodyLine("if(t){ cr.accept(this); }");
        });
    }

    private void ifEmpty(InnerClass innerClass) {
        method(innerClass, "ifEmpty", false, (ic, method) -> {
            method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "t"));
            method.addBodyLine("if(t == null || \"\".trim().equals(t)){ cr.accept(this); }");
        });
    }

    private void ifNotEmpty(InnerClass innerClass) {
        method(innerClass, "ifNotEmpty", true, (ic, method) -> {
            method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "t"));
            method.addBodyLine("if(t != null && !\"\".trim().equals(t)){ return fn.apply(t); }");
        });
        method(innerClass, "notEmpty", false, (ic, method) -> {
            method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "t"));
            method.addBodyLine("if(t != null && !\"\".trim().equals(t)){  cr.accept(this); }");
        });
    }

    private void equals(InnerClass innerClass) {
        method(innerClass, "ifEquals", false, (ic, method) -> {
            method.addTypeParameter(new TypeParameter("T"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("T"), "t1"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("T"), "t2"));
            method.addBodyLine("if(java.util.Objects.equals(t1,t2)){ cr.accept(this); }");
        });
    }
}
