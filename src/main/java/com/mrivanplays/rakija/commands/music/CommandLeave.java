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
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Makes the bot leave your channel")
@CommandUsage("leave")
@CommandAliases("l|quit|disconnect|dc")
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
        User author = context.getAuthor();
        Member member = context.getMember();

        GuildVoiceState botState = context.getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberState = context.getMember().getVoiceState();
        return BotUtils.checkVoiceStates(channel, botState, memberState, author, context.getMessage(), () ->
        {
            VoiceChannel voiceChannel = memberState.getChannel();
            List<Member> members = new ArrayList<Member>(voiceChannel.getMembers())
            {
                {
                    remove(context.getGuild().getSelfMember());
                }
            };
            if (members.size() == 1)
            {
                context.getGuild().getAudioManager().closeAudioConnection();
                bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getPlayer().stopTrack();
                bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getScheduler().getQueue().clear();
                return;
            }
            if (members.size() > 1)
            {
                Role dj = context.getGuild().getRolesByName("DJ", true).get(0);
                if (dj == null)
                {
                    context.getGuild().createRole().setColor(Color.ORANGE).setName("DJ").queue();
                    channel.sendMessage(EmbedUtil.errorEmbed(author)
                            .setDescription("You need to have the DJ role to be able to do that (being alone with the bot also works)")
                            .build()).queue();
                    return;
                }
                if (member.getRoles().contains(dj))
                {
                    context.getGuild().getAudioManager().closeAudioConnection();
                    bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getPlayer().stopTrack();
                    bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getScheduler().getQueue().clear();
                }
                else
                {
                    channel.sendMessage(EmbedUtil.errorEmbed(author)
                            .setDescription("You need to have the DJ role to be able to do that (being alone with the bot also works)")
                            .build()).queue();
                }
            }
        });
    }
}
