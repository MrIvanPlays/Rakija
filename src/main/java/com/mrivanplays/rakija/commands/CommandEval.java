package com.mrivanplays.rakija.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.PermissionCheckContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.data.MarkGuildOnly;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.EmbedUtil;
import groovy.lang.GroovyShell;
import org.jetbrains.annotations.NotNull;

@CommandUsage("eval [code]")
@CommandDescription("Takes groovy code and evaluates it")
@CommandAliases("eval")
@MarkGuildOnly
public class CommandEval extends Command
{

    private final Bot bot;
    private final GroovyShell engine;
    private final String imports;

    public CommandEval(Bot bot)
    {
        this.bot = bot;
        this.engine = new GroovyShell();
        this.imports = "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import java.util.stream.*\n" +
                "import net.dv8tion.jda.api.*\n" +
                "import net.dv8tion.jda.api.entities.*\n" +
                "import net.dv8tion.jda.api.managers.*\n";
    }

    @Override
    public boolean hasPermission(@NotNull PermissionCheckContext context)
    {
        return context.getUser().getId().equalsIgnoreCase(bot.getConfig().getString("owner"));
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        if (args.size() == 0)
        {
            context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor()).setDescription("Missing arguments.").build()).queue();
            return false;
        }
        String code = args.joinArgumentsSpace(0);
        try
        {
            engine.setProperty("args", args);
            engine.setProperty("context", context);
            engine.setProperty("message", context.getMessage());
            engine.setProperty("channel", context.getChannel());
            engine.setProperty("jda", context.getJda());
            engine.setProperty("guild", context.getGuild());
            engine.setProperty("member", context.getMember());

            String script = imports + code;
            Object out = engine.evaluate(script);

            context.getChannel().sendMessage(out == null ? "Executed without error" : out.toString()).queue();
        }
        catch (Throwable e)
        {
            context.getChannel().sendMessage(e.getMessage()).queue();
        }
        return true;
    }
}
