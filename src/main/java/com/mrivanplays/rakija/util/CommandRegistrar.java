package com.mrivanplays.rakija.util;

import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.builtin.CommandShutdown;
import com.mrivanplays.jdcf.builtin.DefaultFailReasonHandler;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.settings.prefix.PrefixHandler;
import com.mrivanplays.jdcf.translation.TranslationCollector;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.commands.*;
import com.mrivanplays.rakija.commands.image.CommandCat;
import com.mrivanplays.rakija.commands.image.CommandDog;
import com.mrivanplays.rakija.commands.image.CommandMeme;
import com.mrivanplays.rakija.commands.moderation.CommandPurge;
import com.mrivanplays.rakija.commands.music.*;
import com.mrivanplays.rakija.commands.ticket.CommandCloseTicket;
import com.mrivanplays.rakija.commands.ticket.CommandCreateTicket;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class CommandRegistrar
{

    private static CommandManager commandManager;

    public static void registerCommands(JDA jda, Bot bot)
    {
        CommandSettings settings = new CommandSettings();
        settings.setEnableHelpCommand(true);
        settings.setHelpCommandEmbed(() -> EmbedUtil.defaultEmbed().setTitle("`()` - optional ; `[]` - required"));
        settings.setEnablePrefixCommand(true);
        settings.setPrefixCommandEmbed(() -> EmbedUtil.defaultEmbed().setColor(Color.BLUE).setTitle("Prefix"));
        settings.setEnableMentionInsteadPrefix(true);
        settings.setExecutorService(bot.getExecutor());
        settings.setCommandsPerHelpPage(10);
        settings.setErrorEmbed(() -> EmbedUtil.defaultEmbed().setColor(Color.RED).setTitle("Error"));
        settings.setNoPermissionEmbed(() -> EmbedUtil.defaultEmbed()
                .setColor(Color.RED)
                .setTitle("Error")
                .setDescription("You don't have permission to perform this command."));
        settings.setSuccessEmbed(() -> EmbedUtil.defaultEmbed().setColor(Color.GREEN).setTitle("Success"));
        settings.setPrefixHandler(PrefixHandler.defaultHandler(BotUtils.JSON_MAPPER));
        settings.getPrefixHandler().setDefaultPrefix("r!");
        try
        {
            settings.setTranslations(TranslationCollector.getInstance().getTranslations("en"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        settings.setLogExecutedCommands(bot.getConfig().getBoolean("logExecutedCommands"));
        settings.setCommandExecuteChannel(jda.getTextChannelById(bot.getConfig().getLong("commandsChannel")));
        settings.setAllowDMSCommands(true);
        settings.setFailReasonHandler(new DefaultFailReasonHandler());
        CommandRegistrar.commandManager = new CommandManager(jda, settings);

        commandManager.registerCommands(
                new CommandRakija(bot.getEventWaiter()),
                new CommandServerInfo(),
                new CommandUserInfo(),
                new Command8Ball(),
                new CommandCreatePaste(bot.getPasteServer()),
                new CommandMeme(),
                new CommandDog(),
                new CommandCat(),
                new CommandBroadcast(),
                new CommandEval(bot),
                new CommandCreateTicket(bot),
                new CommandCloseTicket(bot),
                new CommandPurge(bot),
                new CommandShutdown(bot.getConfig().getString("owner")),
                new CommandPing());

        musicCommands(bot, settings);
    }

    public static boolean dispatchCommand(CommandExecutionContext context, String commandLine)
    {
        // people would ask me why you are killing your bot smh
        // like I am the lead developer of JDCF, and i'm against dispatching commands like this
        // but still doing it in my own bot
        // well, the ecosystem of Rakija is like that, and so I can't just copy paste the command smh
        // plus the fact i use this method in 2 of Rakija's commands to execute 2 different commands
        // what i'm doing no one should do
        String[] lineSplit = commandLine.split(" ");
        String name = lineSplit[0];
        String[] args = Arrays.copyOfRange(lineSplit, 1, lineSplit.length);
        Optional<RegisteredCommand> commandOptional = commandManager.getCommand(name);
        return commandOptional.map(registeredCommand ->
                registeredCommand.execute(context, new CommandArguments(context, args))).orElse(false);
    }

    private static void musicCommands(Bot bot, CommandSettings settings)
    {
        commandManager.registerCommands(
                new CommandJoin(),
                new CommandLeave(bot),
                new CommandNowPlaying(bot),
                new CommandPause(bot, settings),
                new CommandPlay(bot, settings),
                new CommandQueue(bot),
                new CommandSkip(bot),
                new CommandVolume(bot),
                new CommandRepeat(bot));
    }

    public static String getPrefix(Guild guild)
    {
        return commandManager.getSettings().getPrefixHandler().getPrefix(guild.getIdLong());
    }
}
