package com.mrivanplays.rakija.commands.moderation;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Clears the messages specified")
@CommandUsage("purge [number of messages]")
@CommandAliases("clear|rm|removemessages")
public class CommandPurge extends Command
{

    private Bot bot;

    public CommandPurge(Bot bot)
    {
        super("purge", Permission.MESSAGE_MANAGE);
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel channel = context.getChannel();
        args.nextInt().ifPresent(messageCount ->
        {
            List<String> blacklistedChannels = bot.getConfig().getStringArray("purgeBlacklist");
            if (blacklistedChannels.contains(channel.getName()))
            {
                channel.sendMessage(EmbedUtil.errorEmbed(context.getAuthor()).setDescription("You can't purge messages here!").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            channel.getIterableHistory().takeAsync(messageCount).thenApplyAsync(messages ->
            {
                List<Message> goodMessages = messages.stream()
                        .filter(message -> message.getTimeCreated().isBefore(OffsetDateTime.now().plusWeeks(2)))
                        .collect(Collectors.toList());

                channel.purgeMessages(goodMessages);

                return goodMessages.size();
            }).whenCompleteAsync((size, error) ->
            {
                channel.sendMessage(EmbedUtil.successEmbed(context.getAuthor()).setDescription("Cleared " + size + " messages").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
            }).exceptionally(error -> {
                String cause = "";
                if (error.getCause() != null)
                {
                    cause = " caused by: " + error.getCause().getMessage();
                }

                channel.sendMessageFormat("Error: %s%s", error.getMessage(), cause).queue();

                return 0;
            });
        }).orElse(failReason ->
        {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED)
            {
                channel.sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                        .setDescription("You should also specify messages to delete!").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE)
            {
                channel.sendMessage(EmbedUtil.errorEmbed(context.getAuthor()).setDescription("You should type a number!").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            }
        });
        return true;
    }
}
