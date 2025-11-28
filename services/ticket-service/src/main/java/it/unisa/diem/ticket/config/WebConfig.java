package it.unisa.diem.ticket.config;

import it.unisa.diem.ticket.config.multitenancy.SchemaMultiTenantConnectionProvider;
import it.unisa.diem.ticket.config.multitenancy.TenantIdentifierResolver;
import it.unisa.diem.ticket.config.multitenancy.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Autowired
    private SchemaMultiTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private TenantIdentifierResolver tenantIdentifierResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor);
    }

    // --- QUESTA È LA SOLUZIONE AL TUO ERRORE ---
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            // Iniettiamo le istanze vere dei bean (che hanno il DataSource) invece delle stringhe
            hibernateProperties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
            hibernateProperties.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);
        };
    }
}