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
import com.mrivanplays.rakija.util.EmbedUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Pauses/Plays the currently playing song")
@CommandUsage("pause")
@CommandAliases("resume")
public class CommandPause extends Command {

  private Bot bot;
  private CommandSettings settings;

  public CommandPause(Bot bot, CommandSettings settings) {
    super("pause");
    this.bot = bot;
    this.settings = settings;
  }

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    TextChannel channel = context.getChannel();
    User author = context.getAuthor();
    Guild guild = context.getGuild();
    Member selfMember = guild.getSelfMember();

    PlayerManager playerManager = bot.getPlayerManager();
    GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);

    VoiceChannel voiceChannel = selfMember.getVoiceState().getChannel();
    List<Member> voiceChannelMembers = new ArrayList<>(voiceChannel.getMembers());
    voiceChannelMembers.remove(selfMember);

    if (voiceChannelMembers.size() == 1) {
      musicManager.getPlayer().setPaused(!musicManager.getPlayer().isPaused());
      channel
          .sendMessage(
              getMessage(
                      musicManager.getPlayer().isPaused(), author, context.getGuild().getIdLong())
                  .build())
          .queue();
      return true;
    } else {
      Role dj = guild.getRolesByName("DJ", true).get(0);
      if (dj == null) {
        context.getGuild().createRole().setColor(Color.ORANGE).setName("DJ").queue();
      }
      if (context.getMember().getRoles().contains(dj)) {
        musicManager.getPlayer().setPaused(!musicManager.getPlayer().isPaused());
        channel
            .sendMessage(
                getMessage(
                        musicManager.getPlayer().isPaused(), author, context.getGuild().getIdLong())
                    .build())
            .queue();
        return true;
      } else {
        channel
            .sendMessage(
                EmbedUtil.errorEmbed(author)
                    .setTitle("Error")
                    .setDescription(
                        "You need to have the DJ role to be able to do that (being alone with the bot also works)")
                    .build())
            .queue();
      }
    }
    return false;
  }

  private EmbedBuilder getMessage(boolean isPaused, User author, long guildId) {
    if (isPaused) {
      return EmbedUtil.successEmbed(author)
          .setDescription(
              "Track paused. Use `"
                  + settings.getPrefixHandler().getPrefix(guildId)
                  + "play` or this command again to resume");
    } else {
      return EmbedUtil.successEmbed(author).setDescription("Track resumed.");
    }
  }
}
