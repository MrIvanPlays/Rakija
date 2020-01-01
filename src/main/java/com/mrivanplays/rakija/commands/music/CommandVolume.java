package com.mrivanplays.rakija.commands.music;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Changes the volume of the bot.")
@CommandUsage("volume (new volume)")
public class CommandVolume extends Command
{

    private Bot bot;

    public CommandVolume(Bot bot)
    {
        super("volume");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        AudioPlayer player = bot.getPlayerManager().getGuildMusicManager(context.getGuild()).getPlayer();
        if (args.size() == 0)
        {
            context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor()).setTitle("Volume")
                    .setDescription("Volume is " + player.getVolume()).build()).queue();
            return true;
        }
        GuildVoiceState botState = context.getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberState = context.getMember().getVoiceState();
        return BotUtils.checkVoiceStates(context.getChannel(), botState, memberState, context.getAuthor(), context.getMessage(), () ->
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
                performExecution(context.getChannel(), player, context.getAuthor(), args);
                return;
            }
            if (members.size() > 1)
            {
                Role dj = context.getGuild().getRolesByName("DJ", true).get(0);
                if (dj == null)
                {
                    context.getGuild().createRole().setColor(Color.ORANGE).setName("DJ").queue();
                    context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                            .setDescription("You need to have the DJ role to be able to do that (being alone with the bot also works)")
                            .build()).queue();
                    return;
                }
                if (context.getMember().getRoles().contains(dj))
                {
                    performExecution(context.getChannel(), player, context.getAuthor(), args);
                }
                else
                {
                    context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                            .setDescription("You need to have the DJ role to be able to do that (being alone with the bot also works)")
                            .build()).queue();
                }
            }
        });
    }

    private void performExecution(TextChannel channel, AudioPlayer player, User author, CommandArguments args)
    {
        args.next(argContext ->
        {
            try
            {
                return Math.max(10, Math.min(100, Integer.parseInt(argContext.getArgument())));
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException();
            }
        }).ifPresent(volume ->
        {
            int oldVolume = player.getVolume();
            player.setVolume(volume);
            channel.sendMessage(EmbedUtil.successEmbed(author)
                    .setDescription("Volume set to " + volume + " from " + oldVolume).build()).queue();
        }).orElse(failReason ->
        {
            if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE)
            {
                channel.sendMessage(EmbedUtil.errorEmbed(author)
                        .setDescription("Set volume is invalid (should be 10 - 100)").build()).queue();
            }
        });
    }
}
