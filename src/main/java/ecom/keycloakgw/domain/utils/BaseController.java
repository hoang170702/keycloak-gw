package ecom.keycloakgw.domain.utils;

import ecom.keycloakgw.infrastructure.filter.RequestIdWebFilter;
import org.springframework.web.server.ServerWebExchange;

public class BaseController {
    public static String getRequestId(ServerWebExchange exchange) {
        return (String) exchange.getAttributes()
                .getOrDefault(RequestIdWebFilter.REQUEST_ID_KEY, "");
    }
}
