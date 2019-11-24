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
