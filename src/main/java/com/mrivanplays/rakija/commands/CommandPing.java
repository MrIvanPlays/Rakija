package com.mrivanplays.rakija.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.EmbedUtil;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Shows the latency of the bot")
@CommandUsage("ping")
@CommandAliases("ping")
public class CommandPing extends Command
{

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        long gatewayPing = context.getJda().getGatewayPing();
        long restPing = context.getJda().getRestPing().complete();
        context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor())
                .setTitle("Latency")
                .setDescription("Gateway ping: " + gatewayPing + " \n Rest ping: " + restPing).build())
                .queue();
        return true;
    }
}
