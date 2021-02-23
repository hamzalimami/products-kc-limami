package ma.enset.productsapp.sec;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//pour configurer keycloak dans le fichier application properties de spring boot au lieu du fichier keycloak.json
@Configuration
public class KeycloakAdapterConfig {
    @Bean
    public KeycloakSpringBootConfigResolver springBootConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public KeycloakRestTemplate keycloakRestTemplate(KeycloakClientRequestFactory keycloakClientRequestFactory) {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }
}
