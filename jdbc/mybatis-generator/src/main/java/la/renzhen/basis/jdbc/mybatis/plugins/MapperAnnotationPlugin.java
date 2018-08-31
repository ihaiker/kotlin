package la.renzhen.basis.jdbc.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;


public class MapperAnnotationPlugin extends PluginAdapterEnhance {

    @Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		interfaze.addAnnotation("@Mapper");
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
		return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
	}

}