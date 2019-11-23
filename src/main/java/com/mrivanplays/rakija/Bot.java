/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.rakija;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mrivanplays.rakija.event.RakijaEventListener;
import com.mrivanplays.rakija.music.PlayerManager;
import com.mrivanplays.rakija.util.CommandRegistrar;
import com.mrivanplays.rakija.util.EmbedUtil;
import com.mrivanplays.rakija.util.RakijaConfig;
import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {

  private final RakijaConfig config;
  private final RakijaEventListener rakijaEventListener;
  private final EventWaiter eventWaiter;
  private final ScheduledExecutorService executor;
  private final PlayerManager playerManager;
  private final JDA jda;
  public static Logger LOGGER = LoggerFactory.getLogger(Bot.class);

  private Bot() throws LoginException, InterruptedException {
    config = new RakijaConfig();
    LOGGER.info("Booting");
    rakijaEventListener = new RakijaEventListener();
    executor = Executors.newSingleThreadScheduledExecutor();
    eventWaiter = new EventWaiter(executor, false);
    jda =
        new JDABuilder()
            .setToken(config.getString("token"))
            .setAutoReconnect(true)
            .setActivity(Activity.listening("@Rakija help"))
            .addEventListeners(rakijaEventListener, eventWaiter)
            .build()
            .awaitReady();
    EmbedUtil.setDefaultEmbed(
        () ->
            new EmbedBuilder()
                .setFooter("https://mrivanplays.com", jda.getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.CYAN));
    playerManager = new PlayerManager(); // music stuff
    CommandRegistrar.registerCommands(jda, this);
    LOGGER.info("Boot successful. Loaded as: " + jda.getSelfUser().getAsTag());
  }

  public static void main(String[] args) throws LoginException, InterruptedException {
    new Bot();
  }

  public RakijaConfig getConfig() {
    return config;
  }

  public EventWaiter getEventWaiter() {
    return eventWaiter;
  }

  public ScheduledExecutorService getExecutor() {
    return executor;
  }

  public RakijaEventListener getRakijaEventListener() {
    return rakijaEventListener;
  }

  public PlayerManager getPlayerManager() {
    return playerManager;
  }

  public JDA getJda() {
    return jda;
  }
}
