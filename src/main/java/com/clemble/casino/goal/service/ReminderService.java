package com.clemble.casino.goal.service;

import com.clemble.casino.server.ServerService;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;

/**
 * Created by mavarazy on 12/12/14.
 */
public interface ReminderService extends ServerService {

    public void scheduleReminder(String player, String goalKey, String template, Map<String, String> params, DateTime breachTime);

    public void cancelReminder(String player, String goalKey);

}
