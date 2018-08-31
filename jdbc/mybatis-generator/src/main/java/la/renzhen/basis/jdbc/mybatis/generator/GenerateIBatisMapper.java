package la.renzhen.basis.jdbc.mybatis.generator;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import kotlin.Triple;
import la.renzhen.basis.jdbc.mybatis.plugins.*;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.plugins.SerializablePlugin;
import org.mybatis.generator.plugins.ToStringPlugin;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * 自动生成mapper工具类
 */
@Data
public class GenerateIBatisMapper {

    private String module;
    private String database;
    private JDBCConnectionConfiguration jdbcConnectionConfiguration;

    List<Class<? extends Plugin>> plugins = Lists.newArrayList(
            SerializablePlugin.class,
            ToStringPlugin.class,
            MapperAnnotationPlugin.class,
            MapperXmlOverWritePlugin.class,
            OffsetLimitPlugin.class,
            ExampleWithPlugin.class,
            ConditionalJudgmentPlugin.class
    );

    public String modulePath = "src/main/java";
    public String mapperXMLPath = "src/main/resources";
    public String mapperPath = "src/main/java";


    protected List<String> configFiles() {
        return Lists.newArrayList(
                "/application.properties",
                "/application-jdbc.properties",
                "/application-datasource.properties",

                "/application.yaml", "/application.yml",
                "/application-jdbc.yaml", "/application-jdbc.yml",
                "/application-datasource.yaml", "/application-datasource.yml",

                "/jdbc.properties",
                "/datasource.properties",

                "/mybatis.properties",
                "/generator.properties"

        );
    }

    protected List<String> urlKey() {
        return Lists.newArrayList("spring.datasource.druid.url", "spring.datasource.url", "jdbc.url");
    }

    protected List<String> userNameKey() {
        return Lists.newArrayList("spring.datasource.druid.username", "spring.datasource.username", "jdbc.username");
    }

    protected List<String> passwordKey() {
        return Lists.newArrayList("spring.datasource.druid.password", "spring.datasource.password", "jdbc.password");
    }


    protected kotlin.Triple<String, String, String> jdbc() {
        Properties config = new Properties();
        Yaml yaml = new Yaml();
        List<String> configFiles = configFiles();
        for (String configFile : configFiles) {
            try {
                if (configFile.endsWith(".yaml") || configFile.endsWith(".yml")) {
                    config.putAll(new Yaml2Prop().getProperties(GenerateIBatisMapper.class.getResourceAsStream(configFile)));
                } else {
                    config.load(GenerateIBatisMapper.class.getResourceAsStream(configFile));
                }
            } catch (Exception e) {
                System.out.println("load " + configFile + " is error : " + e.getMessage());
            }
        }
        return new kotlin.Triple<String,String,String>(
                configValue(config, urlKey()),
                configValue(config, userNameKey()),
                configValue(config, passwordKey())
        );
    }

    private String configValue(Properties config, List<String> keys) {
        for (String key : keys) {
            String value = config.getProperty(key);
            if (value != null && !"".equals(value)) {
                return value;
            }
        }
        return null;
    }

    public JDBCConnectionConfiguration getJdbcConnectionConfiguration() {
        if (jdbcConnectionConfiguration == null) {
            Triple<String, String, String> jdbc = jdbc();
            if (jdbc != null) {
                jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
                jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
                jdbcConnectionConfiguration.setConnectionURL(jdbc.getFirst());
                jdbcConnectionConfiguration.setUserId(jdbc.getSecond());
                jdbcConnectionConfiguration.setPassword(jdbc.getThird());
            }else{
                throw new RuntimeException("can't found the jdbc config");
            }
        }
        return jdbcConnectionConfiguration;
    }

    public void addPlugin(Class<? extends Plugin> plugin) {
        plugins.add(plugin);
    }

    public JavaModelGeneratorConfiguration getJavaModelGeneratorConfiguration() {
        JavaModelGeneratorConfiguration configuration = new JavaModelGeneratorConfiguration();
        configuration.setTargetPackage(String.format("%s.entry", getModule()));
        configuration.setTargetProject(getModulePath());
        configuration.addProperty("enableSubPackages", "true");
        configuration.addProperty("trimStrings", "true");
        return configuration;
    }

    public SqlMapGeneratorConfiguration getSqlMapGeneratorConfiguration() {
        SqlMapGeneratorConfiguration configuration = new SqlMapGeneratorConfiguration();
        configuration.setTargetPackage(String.format("%s.mapper", getModule()));
        configuration.setTargetProject(getMapperXMLPath());
        configuration.addProperty("enableSubPackages", "true");
        return configuration;
    }

    public JavaClientGeneratorConfiguration getJavaClientGeneratorConfiguration() {
        JavaClientGeneratorConfiguration configuration = new JavaClientGeneratorConfiguration();
        configuration.setTargetPackage(String.format("%s.mapper", getModule()));
        configuration.setTargetProject(getMapperPath());
        configuration.setConfigurationType("XMLMAPPER");
        configuration.addProperty("enableSubPackages", "true");
        return configuration;
    }

    public JavaTypeResolverConfiguration getJavaTypeResolverConfiguration() {
        JavaTypeResolverConfiguration configuration = new JavaTypeResolverConfiguration();
        configuration.addProperty("forceBigDecimals", "false");
        return configuration;
    }

    @SneakyThrows
    public void GenerateTable(String table, Consumer<TableConfiguration> consumer) {
        generate(context -> {
            String javaName = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(table);
            TableConfiguration tableConfiguration = new TableConfiguration(context);
            String database = getDatabase();
            if (database != null) {
                tableConfiguration.setSchema(database);
            }
            tableConfiguration.setTableName(table);
            tableConfiguration.setDomainObjectName(javaName);
            consumer.accept(tableConfiguration);
            context.addTableConfiguration(tableConfiguration);
        });
    }

    @SneakyThrows
    public void GenerateTables(String... tables) {
        generate(context -> {
            for (String table : tables) {
                String javaName = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(table);
                TableConfiguration tableConfiguration = new TableConfiguration(context);
                String database = getDatabase();
                if (database != null) {
                    tableConfiguration.setSchema(database);
                }
                tableConfiguration.setTableName(table);
                tableConfiguration.setDomainObjectName(javaName);
                context.addTableConfiguration(tableConfiguration);
            }
        });
    }

    public ColumnOverride columnOverwrite(String column, Class<?> javaType, Class<? extends TypeHandler> typeHandler) {
        ColumnOverride columnOverride = new ColumnOverride(column);
        columnOverride.setJavaType(javaType.getName());
        columnOverride.setTypeHandler(typeHandler.getName());
        return columnOverride;
    }


    /**
     * 设置Entry列的上父类
     *
     * @param configuration 表配置
     * @param rootClass     父类
     */
    public void setEntryRootClass(TableConfiguration configuration, Class rootClass) {
        configuration.addProperty("rootClass", rootClass.getName());
    }

    /**
     * 设置mapper的上层类
     *
     * @param configuration 表配置
     * @param rootInterface 父接口
     */
    public void setMapperInterface(TableConfiguration configuration, Class rootInterface) {
        assert rootInterface.isInterface();
        configuration.addProperty("rootInterface", rootInterface.getName());
    }

    public String getModule() {
        if (module == null) {
            return this.getClass().getPackage().getName();
        }
        return module;
    }

    @SneakyThrows
    private void generate(Consumer<Context> consumer) {
        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();

        Context context = new Context(ModelType.FLAT);
        context.setId("DB2Tables");
        context.setTargetRuntime("MyBatis3");

        for (Class<? extends Plugin> plugin : plugins) {
            PluginConfiguration configuration = new PluginConfiguration();
            configuration.setConfigurationType(plugin.getName());
            context.addPluginConfiguration(configuration);
        }
        config.addContext(context);

        context.addProperty("javaFileEncoding", "UTF-8");

        CommentGeneratorConfiguration comment = new CommentGeneratorConfiguration();
        comment.addProperty("suppressAllComments", "true");
        context.setCommentGeneratorConfiguration(comment);

        context.setJdbcConnectionConfiguration(getJdbcConnectionConfiguration());
        context.setJavaModelGeneratorConfiguration(getJavaModelGeneratorConfiguration());
        context.setSqlMapGeneratorConfiguration(getSqlMapGeneratorConfiguration());
        context.setJavaClientGeneratorConfiguration(getJavaClientGeneratorConfiguration());
        context.setJavaTypeResolverConfiguration(getJavaTypeResolverConfiguration());

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType(SqlCommentGenerator.class.getName());
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        consumer.accept(context);

        DefaultShellCallback callback = new DefaultShellCallback(true);
        new MyBatisGenerator(config, callback, warnings).generate(null);
        warnings.forEach(System.out::println);
    }
}