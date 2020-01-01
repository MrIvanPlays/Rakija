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
import com.mrivanplays.rakija.util.BotUtils;
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

        GuildVoiceState botState = context.getGuild().getSelfMember().getVoiceState();
        GuildVoiceState memberState = context.getMember().getVoiceState();
        return BotUtils.checkVoiceStates(channel, botState, memberState, author, context.getMessage(), () ->
        {
            AudioPlayer player = musicManager.getPlayer();
            if (player.getPlayingTrack() == null)
            {
                channel.sendMessage(EmbedUtil.errorEmbed(author)
                        .setDescription("The track isn't playing anything.").build())
                        .complete().delete().queueAfter(15, TimeUnit.SECONDS);
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            VoiceChannel voiceChannel = memberState.getChannel();
            List<Member> members = new ArrayList<Member>(voiceChannel.getMembers())
            {
                {
                    remove(context.getGuild().getSelfMember());
                }
            };
            if (members.size() == 1)
            {
                if (!scheduler.nextTrack())
                {
                    player.stopTrack();
                    player.setPaused(false);
                }
                channel.sendMessage(stctMessage(author).build()).queue();
                return;
            }
            if (members.size() > 1)
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
                }
                else
                {
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
                    if (votes >= members.size())
                    {
                        if (!scheduler.nextTrack())
                        {
                            player.stopTrack();
                            player.setPaused(false);
                        }
                        channel.sendMessage(stctMessage(author).build()).queue();
                    }
                    else
                    {
                        channel.sendMessage(EmbedUtil.embedWithAuthor(author).setColor(Color.YELLOW)
                                .setTitle("There's one more problem...")
                                .setDescription("To skip the current track we need `" + (members.size() - votes) + "` more vote(s)")
                                .build()).queue();
                    }
                }
            }
        });
    }

    private EmbedBuilder stctMessage(User author)
    {
        return EmbedUtil.successEmbed(author).setDescription("Skipping the current track");
    }
}
