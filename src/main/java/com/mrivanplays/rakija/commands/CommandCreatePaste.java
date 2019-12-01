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
        super("createPaste");
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
