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
import com.mrivanplays.rakija.util.EmbedUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Makes the bot join into your voice channel if you are in a voice channel")
@CommandUsage("join")
@CommandAliases("j")
public class CommandJoin extends Command {

  public CommandJoin() {
    super("join");
  }

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    TextChannel channel = context.getChannel();
    User author = context.getAuthor();
    AudioManager audioManager = context.getGuild().getAudioManager();
    if (audioManager.isConnected()) {
      channel
          .sendMessage(
              EmbedUtil.errorEmbed(author)
                  .setTitle("Error")
                  .setDescription("I am already connected to a voice channel.")
                  .build())
          .complete()
          .delete()
          .queueAfter(15, TimeUnit.SECONDS);
      context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
      return true;
    }

    GuildVoiceState memberVoiceState = context.getMember().getVoiceState();
    if (!memberVoiceState.inVoiceChannel()) {
      channel
          .sendMessage(
              EmbedUtil.errorEmbed(author)
                  .setTitle("Error")
                  .setDescription("You need to be in a voice channel")
                  .build())
          .complete()
          .delete()
          .queueAfter(15, TimeUnit.SECONDS);
      context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
      return false;
    }

    VoiceChannel voiceChannel = memberVoiceState.getChannel();
    Member self = context.getGuild().getSelfMember();
    if (!self.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
      channel
          .sendMessage(
              EmbedUtil.errorEmbed(author)
                  .setTitle("Error")
                  .setDescription("I don't have permission to join this voice channel")
                  .build())
          .complete()
          .delete()
          .queueAfter(15, TimeUnit.SECONDS);
      context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
      return false;
    }
    audioManager.openAudioConnection(voiceChannel);
    return true;
  }
}
