package la.renzhen.basis.jdbc.mybatis.plugins;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

@Slf4j
public class TableSubfixPlugin extends PluginAdapterEnhance {

    /**
     * 在Exmaple类中添加tableName字段
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field table_subfix = new Field("table_subfix", PrimitiveTypeWrapper.getStringInstance());
        table_subfix.setVisibility(JavaVisibility.PRIVATE);
        addField(topLevelClass, introspectedTable, table_subfix);
        return true;
    }

    /**
     * 在object类中添加tableName字段
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field table_subfix = new Field("table_subfix", PrimitiveTypeWrapper.getStringInstance());
        table_subfix.setVisibility(JavaVisibility.PRIVATE);
        addField(topLevelClass, introspectedTable, table_subfix);
        return true;
    }

    /**
     * 这三个函数在分表中需要用其他函数替换，以为分表需要传入table名字，但是count函数需要处理
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetDeleteXmlElementTableName(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        TextElement subSentence = new TextElement("from " + tablePrefix + "_${table_subfix}");
        elements.set(2, subSentence);
        return true;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        //method.getParameters().get(0).addAnnotation("@Param(\"id\")");
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotations().size() == 0) {
                parameter.addAnnotation("@Param(\"" + parameter.getName() + "\")");
            }
        }

        Parameter parameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "table_subfix");
        parameter.addAnnotation("@Param(\"table_subfix\")");
        method.addParameter(parameter);
        return true;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotations().size() == 0) {
                parameter.addAnnotation("@Param(\"" + parameter.getName() + "\")");
            }
        }

        Parameter parameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "table_subfix");
        parameter.addAnnotation("@Param(\"table_subfix\")");
        method.addParameter(parameter);
        return true;
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetCountByExample(element, introspectedTable);
        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

    /**
     * 在xml的SelectByExample的SQL语句添加limit
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetSelectXmlElementTableName(element, introspectedTable);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetUpdateXmlElementTableName(element, introspectedTable);
        return super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetUpdateXmlElementTableName(element, introspectedTable);
        return super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetUpdateXmlElementTableNameNotMapType(element, introspectedTable);
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetUpdateXmlElementTableNameNotMapType(element, introspectedTable);
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetInsertXmlElementTableName(element, introspectedTable);
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetInsertXmlElementTableName(element, introspectedTable);
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        resetDeleteXmlElementTableName(element, introspectedTable);
        return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
    }

    private void resetSelectXmlElementTableName(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        TextElement subSentence = new TextElement("from " + tablePrefix + "_${table_subfix}");
        elements.set(3, subSentence);
    }

    private void resetInsertXmlElementTableName(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        String content = elements.get(0).getFormattedContent(0);
        String[] data = content.split(" ");
        data[2] = tablePrefix + "_${table_subfix}";
        TextElement subSentence = new TextElement(MysqlSplitingTablePlugin.join(" ", data));
        elements.set(0, subSentence);
    }

    private void resetDeleteXmlElementTableName(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        String content = elements.get(0).getFormattedContent(0);
        String[] data = content.split(" ");
        data[2] = tablePrefix + "_${table_subfix}";
        TextElement subSentence = new TextElement(MysqlSplitingTablePlugin.join(" ", data));
        elements.set(0, subSentence);
    }

    private void resetUpdateXmlElementTableName(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        TextElement subSentence = new TextElement("update " + tablePrefix + "_${record.tableSubfix}");
        elements.set(0, subSentence);
    }

    private void resetUpdateXmlElementTableNameNotMapType(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        TextElement subSentence = new TextElement("update " + tablePrefix + "_${table_subfix}");
        elements.set(0, subSentence);
    }

    private void resetCountByExample(XmlElement element, IntrospectedTable introspectedTable) {
        String tablePrefix = introspectedTable.getTableConfiguration().getTableName();
        List<Element> elements = element.getElements();
        String content = elements.get(0).getFormattedContent(0);
        String[] data = content.split(" ");
        data[3] = tablePrefix + "_${table_subfix}";
        TextElement subSentence = new TextElement(MysqlSplitingTablePlugin.join(" ", data));
        elements.set(0, subSentence);
    }


    public static String join(String join, String[] strAry) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.length; i++) {
            if (i == (strAry.length - 1)) {
                sb.append(strAry[i]);
            } else {
                sb.append(strAry[i]).append(join);
            }
        }
        return new String(sb);
    }
}