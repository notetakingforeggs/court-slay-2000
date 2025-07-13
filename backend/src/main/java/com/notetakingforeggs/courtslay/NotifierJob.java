package com.notetakingforeggs.courtslay;

import com.notetakingforeggs.courtslay.bot.TelegramBot;
import com.notetakingforeggs.courtslay.model.CourtCase;
import com.notetakingforeggs.courtslay.model.Subscription;
import com.notetakingforeggs.courtslay.repository.CourtCaseRepository;
import com.notetakingforeggs.courtslay.repository.SubscriptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(NotifierJob.class);


    @PostConstruct
    public void init() {
        log.info("NotifierJob initialized");
    }
        //  run at 730 every morning
        @Scheduled(cron =  "0 30 7 * * *")
//    @Scheduled(cron =  "0 * * * * *")
//    @Scheduled(fixedRate = 50000) // every 10 seconds
    public void run(){
        log.info("Scheduler running: checking suscriptions");
        // iterating thru all subscriptions
        for (Subscription s : subs.findAll()){
            List<CourtCase> claimantHits = new ArrayList<>();
            List<CourtCase> defendantHits = new ArrayList<>();

            // getting all claimants/defendants that are returned with alert search terms
            // TODO need to create methods for finding cases created after a certain date and feed this in here
            log.debug("last notified{}", s.getLastNotifiedTimestamp());

            for (String claimant : s.getAlertTermsClaimant()){
                log.debug("Alert Terms for Claimant");
                log.debug(claimant);
                claimantHits.addAll(cases.findByClaimantContainingIgnoreCaseAndCreatedAtAfter(claimant, s.getLastNotifiedTimestamp()));
            }for (String defendant : s.getAlertTermsDefendant()){
                log.debug("Alert terms for Defendant");
                log.debug(defendant);
                defendantHits.addAll(cases.findByDefendantContainingIgnoreCaseAndCreatedAtAfter(defendant, s.getLastNotifiedTimestamp()));
            }

            if(!claimantHits.isEmpty() || !defendantHits.isEmpty()){
                log.debug("not empty condish");
                log.info("sending message to user");
                bot.sendMessage(s.getChatId().toString(),format(claimantHits, defendantHits));
                s.setLastNotifiedTimestamp(Instant.now().getEpochSecond());
                subs.save(s);
                log.debug("timestamp of last notification{}", s.getLastNotifiedTimestamp());
            }else{
                log.debug("empteeeeeeeeeeeeeee");
                //TODO i think the issue is in the last notified time or smth?
            }
        }
    }

    public String format(List<CourtCase> claimantHits, List<CourtCase> defendantHits){
        StringBuilder sb = new StringBuilder("»» New Hits for your subscribed alerts «« \n\n");

        if(!claimantHits.isEmpty()){
            sb.append("Hits for your claimant subscriptions: \n\n");
            claimantHits.forEach(c -> sb.append("• Start-time: ").append(epochSecondsToString(c.getStartTimeEpoch()))
                    .append("\n")
                    .append("• Details: ").append(c.getCaseDetails())
                    .append("\n\n"));
        }if(!defendantHits.isEmpty()){
            sb.append("Hits for your defendants subscriptions: \n\n");

            for(CourtCase c : defendantHits){
                log.debug("Start time: {}", epochSecondsToString(c.getStartTimeEpoch()));

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
