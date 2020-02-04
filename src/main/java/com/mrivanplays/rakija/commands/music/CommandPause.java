package com.mrivanplays.rakija.commands.music;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.music.GuildMusicManager;
import com.mrivanplays.rakija.music.PlayerManager;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Pauses/Plays the currently playing song")
@CommandUsage("pause")
@CommandAliases("resume")
public class CommandPause extends Command
{

    private Bot bot;
    private CommandSettings settings;

    public CommandPause(Bot bot, CommandSettings settings)
    {
        super("pause");
        this.bot = bot;
        this.settings = settings;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        MessageChannel channel = context.getChannel();
        User author = context.getAuthor();
        Guild guild = context.getGuild();
        Member selfMember = guild.getSelfMember();

        PlayerManager playerManager = bot.getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);

        GuildVoiceState botState = selfMember.getVoiceState();
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
            String prefix = settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong());
            if (members.size() == 1)
            {
                musicManager.getPlayer().setPaused(!musicManager.getPlayer().isPaused());
                channel.sendMessage(getMessage(musicManager.getPlayer().isPaused(), author, prefix)).queue();
                return;
            }
            if (members.size() > 1)
            {
                Role dj = guild.getRolesByName("DJ", true).get(0);
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
                    musicManager.getPlayer().setPaused(!musicManager.getPlayer().isPaused());
                    channel.sendMessage(getMessage(musicManager.getPlayer().isPaused(), author, prefix)).queue();
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

    private MessageEmbed getMessage(boolean isPaused, User author, String prefix)
    {
        if (isPaused)
        {
            return EmbedUtil.successEmbed(author)
                    .setDescription("Track paused. Use `" + prefix + "play` or this command again to resume").build();
        }
        else
        {
            return EmbedUtil.successEmbed(author).setDescription("Track resumed.").build();
        }
    }
}
