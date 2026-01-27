package com.chatapp.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {
		runSpring(ChatApplication.class, args);
	}

	public static void runSpring(Class<?> clazz, String[] args) {
		SpringApplication.run(clazz, args);
	}

}
