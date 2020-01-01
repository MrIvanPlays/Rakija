package com.mrivanplays.rakija.music;

import com.mrivanplays.rakija.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PlayerManager
{
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public PlayerManager()
    {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild)
    {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null)
        {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(TextChannel channel, String trackUrl, Member requester, User author)
    {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler()
        {

            @Override
            public void trackLoaded(AudioTrack track)
            {
                channel.sendMessage(EmbedUtil.successEmbed(author)
                        .setDescription("Adding to queue `" + track.getInfo().title + "`")
                        .build()).queue();

                musicManager.getScheduler().queue(track, requester);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack track = playlist.getSelectedTrack();

                if (track == null)
                {
                    track = playlist.getTracks().remove(0);
                }

                channel.sendMessage(EmbedUtil.successEmbed(author)
                        .setDescription("Adding to queue `" + track.getInfo().title + "`")
                        .build()).queue();

                musicManager.getScheduler().queue(track, requester);
            }

            @Override
            public void noMatches()
            {
                channel.sendMessage(EmbedUtil.embedWithAuthor(author)
                        .setColor(Color.YELLOW)
                        .setTitle("Nothing found")
                        .setDescription("Nothing found by " + trackUrl)
                        .build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                channel.sendMessage(EmbedUtil.errorEmbed(author)
                        .setTitle("Could not play that link")
                        .setDescription("`" + exception.getMessage() + "`")
                        .build()).queue();
            }
        });
    }
}
