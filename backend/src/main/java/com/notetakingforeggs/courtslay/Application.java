package com.notetakingforeggs.courtslay;

import com.notetakingforeggs.courtslay.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


	@SpringBootApplication
	@EnableScheduling
	public class Application {




		private static final Logger log = LoggerFactory.getLogger(Application.class);
		@Value("${bot.toke}")
		private static String botToken;

		public static void main(String[] args) {
			var context = SpringApplication.run(Application.class, args);

			log.info("000000000000000000000000000000000000000000000000000000000000000000000000000000");
			log.info("main running");
			log.info(botToken);
			try{
				TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
				botsApi.registerBot(context.getBean(TelegramBot.class));
			} catch (TelegramApiException e) {
				throw new RuntimeException(e);
			}
		}

}
