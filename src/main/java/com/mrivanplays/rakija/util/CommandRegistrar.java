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
package com.mrivanplays.rakija.util;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.settings.prefix.DefaultPrefixHandler;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.commands.*;
import com.mrivanplays.rakija.commands.music.*;
import com.mrivanplays.teamtreesclient.FullGoalData;
import com.mrivanplays.teamtreesclient.SiteResponse;
import com.mrivanplays.teamtreesclient.TeamTreesClient;
import java.awt.Color;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
        settings.setPrefixHandler(new DefaultPrefixHandler(bot.getExecutor(), BotUtils.JSON_MAPPER));
        settings.getPrefixHandler().setDefaultPrefix("r!");
        CommandRegistrar.commandManager = new CommandManager(jda, settings);

        commandManager.registerCommands(
                new CommandRakija(bot.getEventWaiter()),
                new CommandServerInfo(),
                new CommandUserInfo(),
                new Command8Ball(),
                new CommandCreatePaste(bot.getPasteServer()));

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

    private static void commandsUsingBuilder(Bot bot)
    {
        TeamTreesClient teamTrees =
                new TeamTreesClient(bot.getHttpClient(), commandManager.getSettings().getExecutorService());
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

        Command.builder()
                .name("shutdown")
                .executor((context, args) ->
                {
                    User author = context.getAuthor();
                    if (!author.getId().equalsIgnoreCase(bot.getConfig().getString("owner")))
                    {
                        context.getChannel().sendMessage(EmbedUtil.noPermissionEmbed(author))
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                        return false;
                    }
                    Bot.LOGGER.info("Bot shutting down");
                    context.getJda().shutdownNow();
                    context.getJda().getHttpClient().connectionPool().evictAll();
                    context.getJda().getHttpClient().dispatcher().executorService().shutdown();
                    return true;
                })
                .buildAndRegister(commandManager);

        Command.builder()
                .name("teamtrees")
                .usage("teamtrees")
                .aliases("trees", "teamtreesinfo", "treesinfo")
                .description("Shows stats about the teamtrees goal")
                .executor((context, args) ->
                {
                    SiteResponse<FullGoalData> siteData = teamTrees.retrieveFullData().join();
                    Optional<FullGoalData> data = siteData.getData();
                    if (data.isPresent())
                    {
                        FullGoalData actualData = data.get();
                        EmbedBuilder embedBuilder = EmbedUtil.successEmbed(context.getAuthor()).setTitle("Let's go TeamTrees!");
                        embedBuilder.addField("Trees: ", BotUtils.DECIMAL_FORMAT_NUMBER.format(actualData.getTrees()), true);
                        embedBuilder.addField(
                                "Trees left: ",
                                BotUtils.DECIMAL_FORMAT_NUMBER.format(actualData.getTreesLeft()),
                                true);
                        embedBuilder.addField(
                                "Percentage done: ",
                                BotUtils.DECIMAL_FORMAT_PERCENTAGE.format(actualData.getPercentDone()),
                                true);
                        embedBuilder.addField("Days left: ", String.valueOf(actualData.getDaysLeft()), true);
                        context.getChannel().sendMessage(embedBuilder.build()).queue();
                        return true;
                    }
                    return false;
                })
                .buildAndRegister(commandManager);
    }
}
