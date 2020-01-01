package com.mrivanplays.rakija;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mrivanplays.binclient.servers.IvanBinServer;
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
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot
{

    private final RakijaConfig config;
    private final RakijaEventListener rakijaEventListener;
    private final EventWaiter eventWaiter;
    private final ScheduledExecutorService executor;
    private final PlayerManager playerManager;
    private final JDA jda;
    public static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private final IvanBinServer pasteServer;
    private final OkHttpClient httpClient;

    private Bot() throws LoginException, InterruptedException
    {
        config = new RakijaConfig();
        LOGGER.info("Booting");
        rakijaEventListener = new RakijaEventListener();
        executor = Executors.newScheduledThreadPool(2);
        eventWaiter = new EventWaiter(executor, false);
        httpClient = new OkHttpClient.Builder().dispatcher(new Dispatcher(executor)).build();
        jda = new JDABuilder()
                .setToken(config.getString("token"))
                .setAutoReconnect(true)
                .setActivity(Activity.listening("@Rakija help"))
                .addEventListeners(rakijaEventListener, eventWaiter)
                .setHttpClient(httpClient)
                .setCallbackPool(executor)
                .setRateLimitPool(executor)
                .setGatewayPool(executor)
                .build()
                .awaitReady();
        EmbedUtil.setDefaultEmbed(() -> new EmbedBuilder()
                .setFooter("https://mrivanplays.com", jda.getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.now()).setColor(Color.CYAN));
        playerManager = new PlayerManager(); // music stuff
        pasteServer = new IvanBinServer(httpClient);
        CommandRegistrar.registerCommands(jda, this);
        LOGGER.info("Boot successful. Loaded as: " + jda.getSelfUser().getAsTag());
    }

    public static void main(String[] args) throws LoginException, InterruptedException
    {
        new Bot();
    }

    public RakijaConfig getConfig()
    {
        return config;
    }

    public EventWaiter getEventWaiter()
    {
        return eventWaiter;
    }

    public ScheduledExecutorService getExecutor()
    {
        return executor;
    }

    public RakijaEventListener getRakijaEventListener()
    {
        return rakijaEventListener;
    }

    public PlayerManager getPlayerManager()
    {
        return playerManager;
    }

    public JDA getJda()
    {
        return jda;
    }

    public IvanBinServer getPasteServer()
    {
        return pasteServer;
    }

    public OkHttpClient getHttpClient()
    {
        return httpClient;
    }
}
