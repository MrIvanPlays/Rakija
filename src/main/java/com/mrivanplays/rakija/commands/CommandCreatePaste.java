package com.mrivanplays.rakija.commands;

import com.mrivanplays.binclient.servers.IvanBinServer;
import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.EmbedUtil;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Creates a paste with the specified code")
@CommandUsage("createpaste [code]")
public class CommandCreatePaste extends Command
{

    private IvanBinServer server;

    public CommandCreatePaste(IvanBinServer server)
    {
        super("createPaste", false);
        this.server = server;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        String code = args.joinArgumentsSpace(0);
        if (code.isEmpty())
        {
            context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor()).setDescription("Cannot make empty bin").build()).queue();
            return true;
        }
        server.createPaste(code).async(binId -> context.getChannel().sendMessage(EmbedUtil.successEmbed(context.getAuthor())
                .setDescription("Paste url: https://bin.mrivanplays.com/" + binId).build()).queue());
        return true;
    }
}
