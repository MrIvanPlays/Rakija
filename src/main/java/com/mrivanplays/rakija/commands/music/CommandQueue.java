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
import com.mrivanplays.rakija.music.RequestedAudioTrack;
import com.mrivanplays.rakija.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Shows queue")
@CommandUsage("queue")
public class CommandQueue extends Command
{

    private Bot bot;

    public CommandQueue(Bot bot)
    {
        super("queue");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel channel = context.getChannel();
        EmbedBuilder builder = EmbedUtil.embedWithAuthor(context.getAuthor()).setColor(Color.BLUE).setTitle("Showing tracks in queue");
        GuildMusicManager musicManager = bot.getPlayerManager().getGuildMusicManager(context.getGuild());
        if (musicManager.getPlayer().getPlayingTrack() != null)
        {
            AudioTrackInfo info = musicManager.getPlayer().getPlayingTrack().getInfo();
            builder.setDescription("Now playing: " + String.format("`%s` - %s", info.title, info.uri) + "\n");
        }
        else
        {
            builder.setDescription("Nothing is currently playing. \n");
        }
        if (musicManager.getScheduler().getQueue().isEmpty())
        {
            builder.appendDescription("The queue is empty");
            channel.sendMessage(builder.build()).queue();
            return true;
        }
        builder.appendDescription("\u2B07" + " UP NEXT " + "\u2B07" + "\n");
        int pos = 0;
        for (RequestedAudioTrack rqTrack : musicManager.getScheduler().getQueue())
        {
            pos++;
            AudioTrack track = rqTrack.getTrack();
            String name = rqTrack.getRequester();
            builder.appendDescription("`" + pos + ".` " + track.getInfo().title + " `|` Requested by: `" + name + "` \n");
        }
        channel.sendMessage(builder.build()).queue();
        return true;
    }
}
