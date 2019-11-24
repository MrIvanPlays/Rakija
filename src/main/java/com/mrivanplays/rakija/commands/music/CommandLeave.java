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
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Makes the bot leave your channel")
@CommandUsage("leave")
@CommandAliases("l|q|quit|disconnect|d")
public class CommandLeave extends Command
{

    private Bot bot;

    public CommandLeave(Bot bot)
    {
        super("leave");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel channel = context.getChannel();
        AudioManager audioManager = context.getGuild().getAudioManager();
        User author = context.getAuthor();
        Member member = context.getMember();

        if (!audioManager.isConnected())
        {
            channel.sendMessage(EmbedUtil.errorEmbed(author).setTitle("Error")
                    .setDescription("I'm not connected to a voice channel")
                    .build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }

        VoiceChannel voiceChannel = audioManager.getConnectedChannel();
        List<Member> channelMembers = new ArrayList<>(voiceChannel.getMembers());
        channelMembers.remove(context.getGuild().getSelfMember());
        if (!channelMembers.contains(context.getMember()))
        {
            channel.sendMessage(EmbedUtil.errorEmbed(author).setTitle("Error")
                    .setDescription("You have to be in the same channel as me to use this command!").build())
                    .complete().delete().queueAfter(15, TimeUnit.SECONDS);
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }
        if (channelMembers.size() > 1)
        {
            Role dj = context.getGuild().getRolesByName("DJ", true).get(0);
            if (dj == null)
            {
                context.getGuild().createRole().setColor(Color.ORANGE).setName("DJ").queue();
            }
            if (!member.getRoles().contains(dj))
            {
                channel.sendMessage(EmbedUtil.errorEmbed(author).setTitle("Error")
                        .setDescription("You need to have the DJ role to be able to do that (being alone with the bot also works)")
                        .build()).queue();
                return false;
            }
        }

        audioManager.closeAudioConnection();
        bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getPlayer().stopTrack();
        bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getScheduler().getQueue().clear();
        return true;
    }
}
