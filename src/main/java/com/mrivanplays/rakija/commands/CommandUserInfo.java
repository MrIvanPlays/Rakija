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
public class CommandUserInfo extends Command
{

    public CommandUserInfo()
    {
        super("userinfo");
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        args.next(ArgumentResolvers.USER).ifPresent(user ->
        {
            Member member = context.getGuild().getMember(user);
            if (member == null)
            {
                context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                        .setDescription("That user isn't in this server!").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            context.getChannel().sendMessage(gatherUserInfo(member, user).build()).queue();
        }).orElse(failReason ->
        {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED)
            {
                context.getChannel().sendMessage(gatherUserInfo(context.getMember(), context.getAuthor()).build()).queue();
                return;
            }
            if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE)
            {
                context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor()).setDescription("Invalid user").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            }
        });
        return true;
    }

    private EmbedBuilder gatherUserInfo(Member member, User user)
    {
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
