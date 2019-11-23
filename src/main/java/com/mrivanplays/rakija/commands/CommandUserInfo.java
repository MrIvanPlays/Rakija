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
package com.mrivanplays.rakija.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.ArgumentResolvers;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@CommandUsage("userinfo (user mention|user id)")
@CommandDescription(
    "Shows various data about the user, who have run the command, or if arguments specified, the user mentioned/user with the specified id")
public class CommandUserInfo extends Command {

  public CommandUserInfo() {
    super("userinfo");
  }

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    args.next(ArgumentResolvers.USER)
        .ifPresent(
            user -> {
              Member member = context.getGuild().getMember(user);
              if (member == null) {
                context
                    .getChannel()
                    .sendMessage(
                        EmbedUtil.errorEmbed(context.getAuthor())
                            .setDescription("That user isn't in this server!")
                            .build())
                    .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
              }
              context.getChannel().sendMessage(gatherUserInfo(member, user).build()).queue();
            })
        .orElse(
            failReason -> {
              if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                context
                    .getChannel()
                    .sendMessage(gatherUserInfo(context.getMember(), context.getAuthor()).build())
                    .queue();
                return;
              }
              if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE) {
                context
                    .getChannel()
                    .sendMessage(
                        EmbedUtil.errorEmbed(context.getAuthor())
                            .setDescription("Invalid user")
                            .build())
                    .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
              }
            });
    return true;
  }

  private EmbedBuilder gatherUserInfo(Member member, User user) {
    return EmbedUtil.defaultEmbed()
        .setColor(member.getColor())
        .setThumbnail(user.getEffectiveAvatarUrl().replaceFirst("gif", "png"))
        .addField("Username#Discriminator", String.format("%#s", user), false)
        .addField("DisplayName", member.getEffectiveName(), false)
        .addField(
            "User Id + Mention",
            String.format("%s (%s)", user.getId(), member.getAsMention()),
            true)
        .addField(
            "Account Created",
            user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
            true)
        .addField(
            "Server Joined",
            member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME),
            true)
        .addField(
            "Online Status",
            member.getOnlineStatus().name().toLowerCase().replaceAll("_", " "),
            true)
        .addField("Bot Account", user.isBot() ? "Yes" : "No", true);
  }
}
