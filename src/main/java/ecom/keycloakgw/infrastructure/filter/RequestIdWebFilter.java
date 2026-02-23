package ecom.keycloakgw.infrastructure.filter;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdWebFilter implements WebFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_KEY = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String requestId = Optional.ofNullable(request.getHeaders().getFirst(REQUEST_ID_HEADER))
                .filter(id -> !id.isBlank())
                .orElse(UUID.randomUUID().toString().replace("-", ""));

        // Set response header
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set(REQUEST_ID_HEADER, requestId);

        // Put requestId into exchange attributes for easy access in controllers
        exchange.getAttributes().put(REQUEST_ID_KEY, requestId);

        return chain.filter(exchange)
                .contextWrite(Context.of(REQUEST_ID_KEY, requestId))
                .doFirst(() -> MDC.put(REQUEST_ID_KEY, requestId))
                .doFinally(signalType -> MDC.remove(REQUEST_ID_KEY));
    }
}
