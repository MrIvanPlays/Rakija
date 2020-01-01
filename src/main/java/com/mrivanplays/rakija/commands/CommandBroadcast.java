package com.mrivanplays.rakija.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

@CommandUsage("broadcast [message]")
@CommandDescription("Broadcasts a message in the channel you currently are")
public class CommandBroadcast extends Command
{

    public CommandBroadcast()
    {
        super("broadcast", Permission.ADMINISTRATOR);
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        context.getMessage().delete().queue();
        String message = args.joinArgumentsSpace(0);
        context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor()).setDescription(message).build()).queue();
        return true;
    }
}
