package com.mrivanplays.rakija.util;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.builtin.CommandShutdown;
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
import com.mrivanplays.teamtreesclient.FullGoalData;
import com.mrivanplays.teamtreesclient.SiteResponse;
import com.mrivanplays.teamtreesclient.TeamTreesClient;
import java.awt.Color;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

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
                new CommandShutdown(bot.getConfig().getString("owner")));

        commandsUsingBuilder(bot);
        musicCommands(bot, settings);
    }

    public static boolean dispatchCommand(CommandExecutionContext context, String commandLine)
    {
        return commandManager.dispatchCommand(context.getJda(), context.getGuild(), context.getChannel(), context.getMember(), commandLine);
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

    private static void commandsUsingBuilder(Bot bot)
    {
        Command.builder()
                .name("ping")
                .usage("ping")
                .description("Shows the latency of the bot")
                .executor((context, args) ->
                {
                    long gatewayPing = context.getJda().getGatewayPing();
                    long restPing = context.getJda().getRestPing().complete();
                    context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor())
                            .setTitle("Latency")
                            .setDescription("Gateway ping: " + gatewayPing + " \n Rest ping: " + restPing).build())
                            .queue();
                    return true;
                })
                .buildAndRegister(commandManager);
    }
}
