package com.mrivanplays.rakija.commands.ticket;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.CommandRegistrar;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

@CommandUsage("createticket")
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
                    channel.sendMessage(
                            "To close this ticket, type " + CommandRegistrar.getPrefix(context.getGuild()) + "closeticket in this channel.")
                            .queue(message -> message.pin().queue());
                    context.getChannel().sendMessage(context.getAuthor().getAsMention() + " , ticket has been created! "
                            + channel.getAsMention()).queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                });
        return true;
    }
}
