package boojongmin.websocketservletdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;

import static java.lang.String.format;

@SpringBootApplication
public class WebsocketStompDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebsocketStompDemoApplication.class, args);
	}

}


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setPathMatcher(new AntPathMatcher("."));
		config.setApplicationDestinationPrefixes("/app");
		config.enableSimpleBroker("/topic", "/queue");
	}
}

@Controller
class StompController {
	private SimpMessagingTemplate template;

	@MessageMapping("/event")
	public void stomp(Map map) {
		this.template.convertAndSend("/topic/message", "hello");
	}
}

@Controller
@MessageMapping("ksug")
class HelloController {
	@MessageMapping("springcamp.{year}")
	@SendTo("/topic/message")
	public String message1(@DestinationVariable int year) {
		return format("welcome ksug %d springcamp", year);
	}

	@MessageMapping("echo")
	@SendToUser("/queue/message")
	public String message2(String message) {
        return format("echo '%s'", message);
	}

	@MessageExceptionHandler
	@SendToUser("/queue/error")
	public String handleException(Exception exception) {
		return exception.toString();
	}
}

@Controller
class EchoController {

	private SimpMessagingTemplate template;

	@Autowired
	public EchoController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@MessageMapping("echo-every")
	public void echoToEvery(String echo) {
		this.template.convertAndSend("/topic/message", echo);
	}

	@MessageMapping("echo-one")
	public void echoToOne(Principal principal, String echo) {
		this.template.convertAndSendToUser(principal.getName(), "/queue/message", echo);
	}
}

@Controller
class SecurityController {

	@MessageMapping("user")
	@SendToUser("/queue/message")
	public Principal echo(Principal principal) {
		return principal;
	}
}


@Component
class Listener {

	@EventListener
	public void sessionConnectedEvent(SessionConnectedEvent event) {
		System.out.println(event);
	}

	@EventListener
	public void sessionSubscribeEvent(SessionSubscribeEvent event) {
		System.out.println(event);
	}

	@EventListener
	public void sessionDisconnectEvent(SessionDisconnectEvent event) {
		System.out.println(event);
	}

}

@EnableWebSecurity
class MultiHttpSecurityConfig {
	@Bean
	public UserDetailsService userDetailsService() throws Exception {
		User.UserBuilder users = User.withDefaultPasswordEncoder();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(users.username("user").password("password").roles("USER").build());
		return manager;
	}

	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		protected void configure(HttpSecurity http) throws Exception {
			http
					.antMatcher("/**")
					.authorizeRequests()
					.anyRequest().hasRole("USER")
					.and()
					.httpBasic();
		}
	}

	@Configuration
	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.formLogin()
                    .and()
                    .httpBasic();
		}
	}
}


