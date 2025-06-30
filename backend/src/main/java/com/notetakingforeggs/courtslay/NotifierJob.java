package com.notetakingforeggs.courtslay;

import com.notetakingforeggs.courtslay.bot.TelegramBot;
import com.notetakingforeggs.courtslay.model.CourtCase;
import com.notetakingforeggs.courtslay.model.Subscription;
import com.notetakingforeggs.courtslay.repository.CourtCaseRepository;
import com.notetakingforeggs.courtslay.repository.SubscriptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifierJob {

    private final CourtCaseRepository cases;
    private final SubscriptionRepository subs;
    private final TelegramBot bot;


    @PostConstruct
    public void init() {
        System.out.println("NotifierJob initialized");
    }
        //  run at 730 every morning
        @Scheduled(cron =  "0 30 7 * * *")
//    @Scheduled(cron =  "0 * * * * *")
//    @Scheduled(fixedRate = 50000) // every 10 seconds
    public void run(){
        System.out.println("Scheduler running");
        // iterating thru all subscriptions
        for (Subscription s : subs.findAll()){
            List<CourtCase> claimantHits = new ArrayList<>();
            List<CourtCase> defendantHits = new ArrayList<>();

            // getting all claimants/defendants that are returned with alert search terms
            // TODO need to create methods for finding cases created after a certain date and feed this in here
            for (String claimaint : s.getAlertTermsClaimant()){
                System.out.println("Alert Terms for Claimaint");
                System.out.println(claimaint);
                System.out.println("last notified" + s.getLastNotifiedTimestamp());
                claimantHits.addAll(cases.findByClaimantContainingIgnoreCaseAndCreatedAtAfter(claimaint, s.getLastNotifiedTimestamp()));
            }for (String defendant : s.getAlertTermsDefendant()){
                System.out.println("Alert terms for defendant");
                System.out.println(defendant);
                System.out.println("last notified" + s.getLastNotifiedTimestamp());
                defendantHits.addAll(cases.findByDefendantContainingIgnoreCaseAndCreatedAtAfter(defendant, s.getLastNotifiedTimestamp()));
            }

            if(!claimantHits.isEmpty() || !defendantHits.isEmpty()){
                System.out.println("not empty condish");
                bot.sendMessage(s.getChatId().toString(),format(claimantHits, defendantHits));
                s.setLastNotifiedTimestamp(Instant.now().getEpochSecond());
                subs.save(s);
                System.out.println("timestamp of last notification" + s.getLastNotifiedTimestamp());
            }else{
                System.out.println("empteeeeeeeeeeeeeee");
                //TODO i think the issue is in the last notified time or smth?
            }
        }
    }

    public String format(List<CourtCase> claimaintHits, List<CourtCase> defendantHits){
        StringBuilder sb = new StringBuilder("»» New Hits for your subscribed alerts «« \n\n");

        if(!claimaintHits.isEmpty()){
            sb.append("Hits for your claimaint subscriptions: \n\n");
            claimaintHits.forEach(c -> sb.append("• Start-time: ").append(epochSecondsToString(c.getStartTimeEpoch()))
                    .append("\n")
                    .append("• Details: ").append(c.getCaseDetails())
                    .append("\n\n"));
        }if(!defendantHits.isEmpty()){
            sb.append("Hits for your defendants subscriptions: \n\n");

            for(CourtCase c : defendantHits){
                System.out.println("Start time: " + epochSecondsToString(c.getStartTimeEpoch()));

                sb.append("• Start-time: ").append(epochSecondsToString(c.getStartTimeEpoch()))
                                    .append("\n")
                                    .append("• Details: ").append(c.getCaseDetails())
                                    .append("\n\n");
            }
// TODO this anon function not working. understand why?

//            defendantHits.forEach(c ->
//                            sb.append("• Start-time").append(epochSecondsToString(c.getStartTimeEpoch()))
//                                    .append("\n")
//                                    .append("Details:").append(c.getCaseDetails())
//                    );
        }
        return sb.toString();
    }

    public String epochSecondsToString(Long startTimeEpochSeconds){
        ZonedDateTime dateTime = Instant.ofEpochSecond(startTimeEpochSeconds).atZone(ZoneOffset.UTC);
        DateTimeFormatter ukFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");
        return dateTime.format(ukFormatter);
    }
}
