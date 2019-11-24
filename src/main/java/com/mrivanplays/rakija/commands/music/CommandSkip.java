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
package com.mrivanplays.rakija.commands.music;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.music.GuildMusicManager;
import com.mrivanplays.rakija.music.PlayerManager;
import com.mrivanplays.rakija.music.TrackScheduler;
import com.mrivanplays.rakija.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Skips the current song")
@CommandUsage("skip")
public class CommandSkip extends Command
{

    private Bot bot;
    private Map<AudioTrack, Integer> skipVotes = new ConcurrentHashMap<>();

    public CommandSkip(Bot bot)
    {
        super("skip");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel channel = context.getChannel();
        User author = context.getAuthor();
        PlayerManager playerManager = bot.getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(context.getGuild());
        TrackScheduler scheduler = musicManager.getScheduler();
        AudioPlayer player = musicManager.getPlayer();
        if (player.getPlayingTrack() == null)
        {
            channel.sendMessage(EmbedUtil.errorEmbed(author).setTitle("Error")
                    .setDescription("The track isn't playing anything.").build())
                    .complete().delete().queueAfter(15, TimeUnit.SECONDS);
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }
        VoiceChannel voiceChannel = context.getGuild().getSelfMember().getVoiceState().getChannel();
        List<Member> voiceChannelMembers = new ArrayList<>(voiceChannel.getMembers());
        voiceChannelMembers.remove(context.getGuild().getSelfMember());
        if (voiceChannelMembers.size() == 1)
        {
            if (!scheduler.nextTrack())
            {
                player.stopTrack();
                player.setPaused(false);
            }
            channel.sendMessage(stctMessage(author).build()).queue();
            return true;
        }
        else
        {
            Role dj = context.getGuild().getRolesByName("DJ", true).get(0);
            if (dj == null)
            {
                context.getGuild().createRole().setColor(Color.ORANGE).setName("DJ").queue();
            }
            if (context.getMember().getRoles().contains(dj))
            {
                if (!scheduler.nextTrack())
                {
                    player.stopTrack();
                    player.setPaused(false);
                }
                channel.sendMessage(stctMessage(author).build()).queue();
                return true;
            }
            Integer votes = skipVotes.get(player.getPlayingTrack());
            if (votes == null)
            {
                votes = 1;
                skipVotes.put(player.getPlayingTrack(), votes);
            }
            else
            {
                votes++;
            }
            if (votes >= voiceChannelMembers.size())
            {
                if (!scheduler.nextTrack())
                {
                    player.stopTrack();
                    player.setPaused(false);
                }
                channel.sendMessage(stctMessage(author).build()).queue();
                return true;
            }
            else
            {
                channel.sendMessage(EmbedUtil.embedWithAuthor(author)
                        .setColor(Color.YELLOW)
                        .setTitle("There's one more problem...")
                        .setDescription("To skip the current track we need `" + (voiceChannelMembers.size() - votes) + "` more vote(s)")
                        .build()).queue();
                return false;
            }
        }
    }

    private EmbedBuilder stctMessage(User author)
    {
        return EmbedUtil.successEmbed(author).setDescription("Skipping the current track");
    }
}
