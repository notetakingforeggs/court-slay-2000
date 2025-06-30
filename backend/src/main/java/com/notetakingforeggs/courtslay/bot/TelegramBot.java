package com.notetakingforeggs.courtslay.bot;

import com.notetakingforeggs.courtslay.model.Subscription;
import com.notetakingforeggs.courtslay.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final SubscriptionService subs;
    private final String username;

    public TelegramBot(@Value("${bot.token}") String token, SubscriptionService subs, @Value("${bot.username}") String username) {
        super(token);
        this.subs = subs;
        this.username = username;

    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            System.out.println(chatId);
            String messageText = update.getMessage().getText().trim().toLowerCase();

            if (messageText.startsWith("/claimant:")) {
                // TODO ide suggested removing non capturing group, come back and replace maybe?
                Pattern pattern = Pattern.compile("/claimant: *(.+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(messageText);
                if (matcher.find()) {
                    String claimant = matcher.group(1);
                    sendMessage(chatId, "Claimant (" + claimant + ") added to alert subs");
                    subs.addOrUpdateClaimant(Long.parseLong(chatId), claimant);
                }

            } else if (messageText.startsWith("/defendant:")) {
                Pattern pattern = Pattern.compile("/defendant: *(.+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(messageText);
                if (matcher.find()) {
                    String defendant = matcher.group(1);
                    sendMessage(chatId, "Defendant (" + defendant + ") added to alert subs");
                    subs.addOrUpdateDefendant(Long.parseLong(chatId), defendant);
                }
            } else if (messageText.startsWith("/view")) {
                Subscription sub = subs.getSub(Long.parseLong(chatId));
                if (sub == null) {
                    sendMessage(chatId, "You have no active subscribed alerts");
                } else {
                    StringBuilder sb = new StringBuilder("»»» listing all your subscriptions «««\n\n");
                    sb.append("Claimants\n");
                    sub.getAlertTermsClaimant().forEach(d -> sb.append("• " + d + "\n"));
                    sb.append("\nDefendants:\n");
                    sub.getAlertTermsDefendant().forEach(d -> sb.append("• " + d + "\n"));
                    sendMessage(chatId, sb.toString());
                }
            } else if (messageText.startsWith("/clear")) {
                subs.deleteAll();
                // TODO probably dont need to flush?
                subs.flush();

            } else {
                String helpText = """ 
                        Welcome to the court slay 2000 Telegram Bot!
                        
                        to sign up for alerts based on claimaint, type /claimaint: followed by the name or names of the claimant you wish to be alerted for, eg "/claimaint: joe blogs". To sign up for alerts based on names of defendants send a message like "/defendant: persons unknown". 
                        To check your current alerts type "/view".
                        To remove your existing alerts, type "/clear"
                        
                        This service is not reliable. Please double check your court dates on the courtserve.net website.
                        
                        """;
                sendMessage(chatId, helpText);
            }
        }
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

}
