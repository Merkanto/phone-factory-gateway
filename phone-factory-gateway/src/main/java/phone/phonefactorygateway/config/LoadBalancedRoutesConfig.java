package phone.phonefactorygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery")
@Configuration
public class LoadBalancedRoutesConfig {
    @Bean
    public RouteLocator localBalancedRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r -> r.path("/api/v1/phone*", "/api/v1/phone/*", "/api/v1/phoneImei/*")
                        .uri("lb://phone-service")
                        .id("phone-service"))
                .route(r -> r.path("/api/v1/customers/**")
                        .uri("lb://order-service")
                        .id("order-service"))
                .route(r -> r.path("/api/v1/phone/*/inventory")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventoryCB")
                                .setFallbackUri("forward:/inventory-failover")
                                .setRouteId("inv-failover")
                        ))
                        .uri("lb://inventory-service")
                        .id("inventory-service"))
                .route(r -> r.path("/inventory-failover/**")
                        .uri("lb://inventory-failover")
                        .id("inventory-failover-service"))
                .build();
    }
}
