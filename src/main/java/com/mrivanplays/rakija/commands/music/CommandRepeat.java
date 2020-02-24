package com.mrivanplays.rakija.commands.music;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.data.MarkGuildOnly;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.music.GuildMusicManager;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Makes the player repeat the currently playing song")
@CommandUsage("repeat")
@CommandAliases("repeat|loop")
@MarkGuildOnly
public class CommandRepeat extends Command
{

    private Bot bot;

    public CommandRepeat(Bot bot)
    {
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        GuildMusicManager musicManager = bot.getPlayerManager().getGuildMusicManager(context.getGuild());
        GuildVoiceState botVoiceState = context.getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberVoiceState = context.getMember().getVoiceState();
        MessageChannel channel = context.getChannel();
        User author = context.getAuthor();
        return BotUtils.checkVoiceStates(channel, botVoiceState, memberVoiceState, author, context.getMessage(), () ->
        {
            VoiceChannel voiceChannel = memberVoiceState.getChannel();
            List<Member> members = new ArrayList<Member>(voiceChannel.getMembers())
            {
                {
                    remove(context.getGuild().getSelfMember());
                }
            };
            if (members.size() == 1)
            {
                musicManager.getScheduler().setRepeating(!musicManager.getScheduler().isRepating());
                String setTo = musicManager.getScheduler().isRepating() ? "repeat" : "not repeat";
                channel.sendMessage(EmbedUtil.successEmbed(author).setDescription("Player was set to " + setTo).build()).queue();
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
                if (context.getMember().getRoles().contains(dj))
                {
                    musicManager.getScheduler().setRepeating(!musicManager.getScheduler().isRepating());
                    String setTo = musicManager.getScheduler().isRepating() ? "repeat" : "not repeat";
                    channel.sendMessage(EmbedUtil.successEmbed(author).setDescription("Player was set to " + setTo).build()).queue();
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
