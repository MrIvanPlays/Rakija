package com.mrivanplays.rakija.commands.ticket;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.CommandRegistrar;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
        if (args.size() == 0)
        {
            context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                    .setDescription("You should specify a reason for your ticket!").build())
                    .queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }
        String reason = args.joinArgumentsSpace(0);
        int number = BotUtils.generateRandomNumber(7);
        context.getGuild().createTextChannel("ticket-" + number)
                .setParent(context.getGuild().getCategoriesByName(
                        bot.getConfig().getString("ticketsCategoryName"), true).get(0))
                .queue(channel ->
                {
                    channel.createPermissionOverride(context.getMember())
                            .setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
                            .queue();
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
        return true;
    }
}
