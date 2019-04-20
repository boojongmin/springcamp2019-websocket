package boojongmin.websocketservletdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class WebsocketServletDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketServletDemoApplication.class, args);
	}

}



class WebsocketHandler extends TextWebSocketHandler {
	private Set<WebSocketSession> sessions = new ConcurrentHashMap().newKeySet();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			session.sendMessage(new TextMessage("hello client: " + (Math.random() * 10)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
	}
}


@Configuration
@EnableWebSocket
class WebSocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(websocketHandler(), "/websocket");
	}

	@Bean
	public WebSocketHandler websocketHandler() {
		return new WebsocketHandler();
	}
}

@Component
class WebSocketEvent {

	@EventListener
	public void sessionConnectedEvent(SessionConnectedEvent event) {
		System.out.println(event);

	}
}

@Component
class CustomSpringEventListener implements ApplicationListener<SessionConnectedEvent> {
	@Override
	public void onApplicationEvent(SessionConnectedEvent event) {
		System.out.println("Received spring custom event - " + event.getMessage());
	}
}
