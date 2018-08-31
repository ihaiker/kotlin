package la.renzhen.basis.jdbc.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * <p>
 *
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 23/06/2018 2:18 PM
 */
public class OffsetLimitPlugin extends PluginAdapterEnhance {

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        {
            Field offset = new Field();
            offset.setVisibility(JavaVisibility.PROTECTED);
            offset.setType(PrimitiveTypeWrapper.getIntegerInstance());
            offset.setName("offset");
            addField(topLevelClass, introspectedTable, offset);
        }
        {
            Field limit = new Field();
            limit.setVisibility(JavaVisibility.PROTECTED);
            limit.setType(PrimitiveTypeWrapper.getIntegerInstance());
            limit.setName("limit");
            addField(topLevelClass, introspectedTable, limit);
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return addOffsetLimit(element);
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return addOffsetLimit(element);
    }

    private boolean addOffsetLimit(XmlElement element){
        XmlElement pageElement = new XmlElement("if");
        pageElement.addAttribute(new Attribute("test", "limit != null and limit > 0"));
        pageElement.addElement(new TextElement("limit "));

        XmlElement offset = new XmlElement("if");
        offset.addAttribute(new Attribute("test", "offset != null"));
        offset.addElement(new TextElement("#{offset},"));
        pageElement.addElement(offset);

        pageElement.addElement(new TextElement("#{limit}"));

        element.addElement(pageElement);
        return true;
    }
}