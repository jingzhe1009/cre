package com.bonc.frame.config;

import com.bonc.frame.security.interceptor.mybatis.AuthInterceptor;
import com.bonc.frame.security.offer.AuthInterceptorOffer;
import com.github.pagehelper.PageInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class MyBatisConfig implements TransactionManagementConfigurer{
	
	@Autowired
    DataSource dataSource;
	
	@Value("${datasource.dbtype}")
	private String dbType;
	
	@Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("tk.mybatis.springboot.model");
        AuthInterceptor authInterceptor = new AuthInterceptor();
        AuthInterceptorOffer authInterceptorOffer = new AuthInterceptorOffer();
        //分页插件配置
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("pageSizeZero", "true");
        properties.setProperty("reasonable", "false");
        properties.setProperty("params", "pageNum=start;pageSize=limit;");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        pageInterceptor.setProperties(properties);
        //设置分页插件
        bean.setPlugins(new Interceptor[]{pageInterceptor, authInterceptor, authInterceptorOffer});
        //配置扫描xml路径
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
        	Resource [] r1 = resolver.getResources("classpath*:com/bonc/*/mapper/"+dbType+"/**/*.xml");
        	Resource [] r2 = resolver.getResources("classpath*:com/bonc/*/engine/mapper/**/*.xml");
        	Resource [] r = (Resource[]) ArrayUtils.addAll(r1, r2);
            bean.setMapperLocations(r);
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
	

	@Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
