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
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.music.GuildMusicManager;
import com.mrivanplays.rakija.music.PlayerManager;
import com.mrivanplays.rakija.util.CommandRegistrar;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Plays a song")
@CommandUsage("play (song name or song url)")
@CommandAliases("p")
public class CommandPlay extends Command
{

    private Bot bot;
    private CommandSettings settings;

    public CommandPlay(Bot bot, CommandSettings settings)
    {
        super("play");
        this.bot = bot;
        this.settings = settings;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel channel = context.getChannel();
        AudioManager audioManager = context.getGuild().getAudioManager();
        if (!audioManager.isConnected())
        {
            if (!CommandRegistrar.dispatchCommand(context, "join"))
            {
                return false;
            }
        }
        else
        {
            if (!audioManager.getConnectedChannel().equals(context.getMember().getVoiceState().getChannel()))
            {
                context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                        .setDescription("You should be in the same voice channel with the bot in order to perform this command.").build())
                        .queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return true;
            }
        }
        PlayerManager playerManager = bot.getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(context.getGuild());
        if (args.size() == 0)
        {
            if (musicManager.getPlayer().isPaused())
            {
                musicManager.getPlayer().setPaused(false);
                return true;
            }
            else
            {
                String prefix = settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong());
                channel.sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                        .setDescription("Usage: `" + prefix + "play (ytsearch:)<song name or song url>`").build())
                        .complete().delete().queueAfter(15, TimeUnit.SECONDS);
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return false;
            }
        }
        String input = args.joinArgumentsSpace(0);
        String newInput;
        if (!isURL(input))
        {
            newInput = "ytsearch:" + input;
        }
        else
        {
            newInput = input;
        }
        playerManager.loadAndPlay(channel, newInput, context.getMember(), context.getAuthor());
        return true;
    }

    private boolean isURL(String input)
    {
        try
        {
            new URL(input);
            return true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
    }
}
