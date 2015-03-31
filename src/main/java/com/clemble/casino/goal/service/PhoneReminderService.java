package com.clemble.casino.goal.service;

import com.clemble.casino.server.event.email.SystemEmailSendRequestEvent;
import com.clemble.casino.server.event.phone.SystemPhoneSMSSendRequestEvent;
import com.clemble.casino.server.event.schedule.SystemAddJobScheduleEvent;
import com.clemble.casino.server.event.schedule.SystemRemoveJobScheduleEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;

/**
 * Created by mavarazy on 12/12/14.
 */
public class PhoneReminderService implements ReminderService {

    final private SystemNotificationService notificationService;

    public PhoneReminderService(SystemNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void scheduleReminder(String player, String goalKey, String template, Map<String, String> params, DateTime breachTime) {
        String key = toKey(player);
        // Step 1. Cancel reminder
        // WARNING Since this is asynchronous, this might cause problems, it's better to do it in reminder service notificationService.send(new SystemRemoveJobScheduleEvent(goalKey, key));
        // Step 2. Schedule notification for a new breach time
        // Step 2.1 Generate email notification
        SystemPhoneSMSSendRequestEvent smsRequest = new SystemPhoneSMSSendRequestEvent(player, template, params);
        // Step 2.2 Schedule email notification
        notificationService.send(new SystemAddJobScheduleEvent(goalKey, key, smsRequest, breachTime));
    }

    @Override
    public void cancelReminder(String player, String goalKey) {
        String key = toKey(player);
        // Step 1. Cancel reminder
        notificationService.send(new SystemRemoveJobScheduleEvent(goalKey, key));
    }

    private String toKey(String player) {
        return "reminder:phone:" + player;
    }

}
