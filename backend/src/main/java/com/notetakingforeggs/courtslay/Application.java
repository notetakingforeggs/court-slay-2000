package com.notetakingforeggs.courtslay;

import com.notetakingforeggs.courtslay.bot.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

	@SpringBootApplication
	@EnableScheduling
	public class Application {

		public static void main(String[] args) {
			var context = SpringApplication.run(Application.class, args);

			try{
				TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
				botsApi.registerBot(context.getBean(TelegramBot.class));
			} catch (TelegramApiException e) {
				throw new RuntimeException(e);
			}
		}

}
