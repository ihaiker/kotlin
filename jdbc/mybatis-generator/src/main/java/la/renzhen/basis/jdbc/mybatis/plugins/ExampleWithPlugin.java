package la.renzhen.basis.jdbc.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 *
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 23/06/2018 2:44 PM
 */
public class ExampleWithPlugin extends PluginAdapterEnhance {


    /**
     * Example的方法添加
     * @param topLevelClass example class
     * @param introspectedTable table info
     * @return true
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(Consumer.class.getName());
        topLevelClass.addAnnotation("@lombok.experimental.Accessors(fluent = true)");
        topLevelClass.addAnnotation("@lombok.Setter");

        {
            Method method = new Method("withCriteria");
            method.setVisibility(JavaVisibility.PUBLIC);

            method.addParameter(new Parameter(new FullyQualifiedJavaType(Consumer.class.getName()) {{
                addTypeArgument(FullyQualifiedJavaType.getCriteriaInstance());
            }}, "criteria"));

            method.setReturnType(topLevelClass.getType());
            method.addBodyLine("criteria.accept(createCriteria());");
            method.addBodyLine("return this;");

            topLevelClass.addMethod(method);
        }

        {
            Method example = new Method("example");
            example.setVisibility(JavaVisibility.PUBLIC);
            example.setStatic(true);
            example.setReturnType(topLevelClass.getType());
            example.addBodyLine(String.format("return new %s();", topLevelClass.getType().toString()));
            topLevelClass.addMethod(example);
        }

        {
            Method with = new Method("with");
            with.setVisibility(JavaVisibility.PUBLIC);
            with.setStatic(true);

            with.addParameter(new Parameter(new FullyQualifiedJavaType(Consumer.class.getName()) {{
                addTypeArgument(FullyQualifiedJavaType.getCriteriaInstance());
            }}, "criteria"));

            with.setReturnType(topLevelClass.getType());

            with.addBodyLine("return example().withCriteria(criteria);");
            topLevelClass.addMethod(with);
        }
        return true;
    }


    /**
     * mapper with 等方法
     * @param method 方法
     * @param interfaze Mapper接口
     * @param introspectedTable 表信息
     * @return true
     */
    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        String exampleName = introspectedTable.getExampleType();
        String entryName = introspectedTable.getBaseRecordType();

        {
            Method with = new Method("with");
            with.setDefault(true);

            with.addParameter(new Parameter(new FullyQualifiedJavaType(Consumer.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(
                        exampleName + "." + FullyQualifiedJavaType.getCriteriaInstance()
                ));
            }}, "criteria"));

            with.setReturnType(new FullyQualifiedJavaType(List.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(entryName));
            }});

            with.addBodyLine("return selectByExample(new " + exampleName + "().withCriteria(criteria));");
            interfaze.addMethod(with);
        }

        {
            Method withLimit = new Method("with");
            withLimit.setDefault(true);

            withLimit.addParameter(new Parameter(new FullyQualifiedJavaType(Consumer.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(
                        exampleName + "." + FullyQualifiedJavaType.getCriteriaInstance()
                ));
            }}, "criteria"));

            withLimit.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "limit"));

            withLimit.setReturnType(new FullyQualifiedJavaType(List.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(entryName));
            }});
            withLimit.addBodyLine("return selectByExample(new " + exampleName + "().withCriteria(criteria).limit(limit));");
            interfaze.addMethod(withLimit);
        }

        {
            Method withOffsetLimit = new Method("with");
            withOffsetLimit.setDefault(true);

            withOffsetLimit.addParameter(new Parameter(new FullyQualifiedJavaType(Consumer.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(
                        exampleName + "." + FullyQualifiedJavaType.getCriteriaInstance()
                ));
            }}, "criteria"));
            withOffsetLimit.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "offset"));
            withOffsetLimit.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "limit"));

            withOffsetLimit.setReturnType(new FullyQualifiedJavaType(List.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(entryName));
            }});
            withOffsetLimit.addBodyLine("return selectByExample(new " + exampleName + "().withCriteria(criteria).offset(offset).limit(limit));");
            interfaze.addMethod(withOffsetLimit);
        }

        {
            Method example = new Method("selectByExample");
            example.setDefault(true);
            example.addParameter(new Parameter(new FullyQualifiedJavaType(Consumer.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(exampleName));
            }}, "consumer"));
            example.setReturnType(new FullyQualifiedJavaType(List.class.getName()) {{
                addTypeArgument(new FullyQualifiedJavaType(entryName));
            }});
            example.addBodyLine(String.format("%s example = new %s();",exampleName,exampleName));
            example.addBodyLine("consumer.accept(example);");
            example.addBodyLine("return selectByExample(example);");
            interfaze.addMethod(example);
        }
        return true;
    }

}
