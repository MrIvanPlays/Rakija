package com.mrivanplays.rakija.commands.ticket;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.CommandRegistrar;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

@CommandUsage("createticket [reason]")
@CommandDescription("Creates a new ticket")
public class CommandCreateTicket extends Command
{

    private Bot bot;

    public CommandCreateTicket(Bot bot)
    {
        super("createticket");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        args.nextString().ifPresent(reason ->
        {
            int number = BotUtils.generateRandomNumber(7);
            context.getGuild().createTextChannel("ticket-" + number)
                    .setParent(context.getGuild().getCategoriesByName("tickets", true).get(0))
                    .queue(channel ->
                    {
                        for (Member member : context.getGuild().getMembers())
                        {
                            for (Role moderator : bot.getConfig().getStringArrayAsRole("moderatorGroups", context.getGuild()))
                            {
                                if (member.getRoles().contains(moderator) || member.getId().equalsIgnoreCase(context.getMember().getId()))
                                {
                                    channel.createPermissionOverride(member)
                                            .setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE)
                                            .queue();
                                    continue;
                                }
                                channel.createPermissionOverride(member)
                                        .setDeny(Permission.VIEW_CHANNEL)
                                        .queue();
                            }
                        }
                        EmbedBuilder ticketChannelEmbed = EmbedUtil.embedWithAuthor(context.getAuthor())
                                .setColor(0xCF40FA)
                                .setTitle(context.getGuild().getName() + " ticket opened")
                                .addField("Reason", reason, false)
                                .addField("New ticket",
                                        "You've made a ticket in order to receive help from our staff team." +
                                                "Please note that our staff may need some time to give you full answer to fix your problem. ",
                                        false)
                                .addField("Closing the ticket", "In order to close the ticket, type " +
                                        CommandRegistrar.getPrefix(context.getGuild()) + "closeticket", false);

                        channel.sendMessage(ticketChannelEmbed.build()).queue(message -> message.pin().queue());
                        context.getChannel().sendMessage(EmbedUtil.successEmbed(context.getAuthor())
                                .setDescription(
                                        context.getAuthor().getAsMention() + " , ticket has been created! " + channel.getAsMention())
                                .build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    });
        }).orElse(failReason ->
        {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED)
            {
                context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                        .setDescription("You should specify a reason for your ticket!").build()).queue();
            }
        });
        return true;
    }
}
