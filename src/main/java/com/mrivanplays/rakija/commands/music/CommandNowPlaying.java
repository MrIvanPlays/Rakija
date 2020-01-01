package com.mrivanplays.rakija.commands.music;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.music.GuildMusicManager;
import com.mrivanplays.rakija.music.PlayerManager;
import com.mrivanplays.rakija.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Shows the currently playing track")
@CommandUsage("nowplaying")
@CommandAliases("np")
public class CommandNowPlaying extends Command
{

    private Bot bot;

    public CommandNowPlaying(Bot bot)
    {
        super("nowplaying");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel channel = context.getChannel();
        PlayerManager playerManager = bot.getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(context.getGuild());
        AudioPlayer player = musicManager.getPlayer();
        if (player.getPlayingTrack() == null)
        {
            context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor()).setTitle("Now playing")
                    .setDescription("Nothing is currently played").build()).queue();
            return true;
        }

        AudioTrackInfo info = player.getPlayingTrack().getInfo();
        channel.sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor()).setTitle("Now playing")
                .setDescription(String.format("[%s] (%s)\n%s %s - %s",
                        info.title, info.uri, player.isPaused() ? "\u23F8" : "▶",
                        formatTime(player.getPlayingTrack().getPosition()),
                        formatTime(player.getPlayingTrack().getDuration())))
                .build()).queue();
        return true;
    }

    private String formatTime(long timeInMillis)
    {
        long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
