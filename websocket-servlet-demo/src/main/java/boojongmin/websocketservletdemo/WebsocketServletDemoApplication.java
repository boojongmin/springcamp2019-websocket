package boojongmin.websocketservletdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
		System.out.println("Here comes new challenger! total challenger is " + sessions.size() );
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		System.out.println(">> recieved: " + message.getPayload());
		try {
			session.sendMessage(new TextMessage("hello client: " + (Math.random() * 10)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		System.out.println("challenger is gone! total challenger is " + sessions.size() );
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