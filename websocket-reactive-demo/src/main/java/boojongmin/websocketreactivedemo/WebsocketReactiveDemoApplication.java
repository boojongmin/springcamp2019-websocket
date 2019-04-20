package boojongmin.websocketreactivedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class WebsocketReactiveDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketReactiveDemoApplication.class, args);
	}

}

class SpringWebSocketHandler implements WebSocketHandler {
	private Set<WebSocketSession> sessions = new ConcurrentHashMap().newKeySet();

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<WebSocketMessage> out = session.receive()
				.doOnSubscribe(x -> {
					sessions.add(session);
					System.out.println("new session: " + sessions.size());
				})
//				.doOnNext(message -> System.out.println("recieved message from client : " + message.getPayloadAsText()))
				.map(message -> session.textMessage("hello client: " + (Math.random() * 10)))
				.doOnCancel(() -> System.out.println("canceled"))
				.doOnError(e -> e.printStackTrace())
				.doOnComplete(() -> {
					sessions.remove(session);
					System.out.println("completed: " +
							"" + sessions.size());
				});
		return session.send(out);
	}
}

@Configuration
class WebConfig {

	@Bean
	public HandlerMapping handlerMapping() {
		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put("/websocket", new SpringWebSocketHandler());

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(map);
		mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return mapping;
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}
}

