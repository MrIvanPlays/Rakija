package com.mrivanplays.rakija.commands.music;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Makes the bot join into your voice channel if you are in a voice channel")
@CommandUsage("join")
@CommandAliases("j")
public class CommandJoin extends Command
{

    public CommandJoin()
    {
        super("join");
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        MessageChannel channel = context.getChannel();
        User author = context.getAuthor();
        AudioManager audioManager = context.getGuild().getAudioManager();
        if (audioManager.isConnected())
        {
            channel.sendMessage(EmbedUtil.errorEmbed(author)
                    .setDescription("Bot is already connected to voice channel")
                    .build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return true;
        }

        GuildVoiceState memberVoiceState = context.getMember().getVoiceState();
        if (!memberVoiceState.inVoiceChannel())
        {
            channel.sendMessage(EmbedUtil.errorEmbed(author).setDescription("You need to be in a voice channel").build())
                    .complete().delete().queueAfter(15, TimeUnit.SECONDS);
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }

        VoiceChannel voiceChannel = memberVoiceState.getChannel();
        Member self = context.getGuild().getSelfMember();
        if (!self.hasPermission(voiceChannel, Permission.VOICE_CONNECT))
        {
            channel.sendMessage(EmbedUtil.errorEmbed(author)
                    .setDescription("Bot doesn't have permission to connect to the voice channel you are connected")
                    .build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }
        audioManager.openAudioConnection(voiceChannel);
        return true;
    }
}
