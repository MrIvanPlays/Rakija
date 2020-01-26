package com.mrivanplays.rakija.commands.ticket;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.Bot;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@CommandDescription("Closes the ticket where it was ran")
@CommandUsage("closeticket")
public class CommandCloseTicket extends Command
{

    private Bot bot;

    public CommandCloseTicket(Bot bot)
    {
        super("closeticket");
        this.bot = bot;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        TextChannel ticketChannel = context.getChannel();
        if (!ticketChannel.getName().contains("ticket"))
        {
            context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor()).setDescription("This is not a ticket!").build())
                    .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }
        context.getMessage().delete().queue();
        Guild guild = context.getGuild();
        String ticketId = ticketChannel.getName().replace("ticket-", "");
        TextChannel transcriptChannel = context.getGuild().getTextChannelById(bot.getConfig().getLong("transcriptsChannel"));
        if (transcriptChannel == null)
        {
            ticketChannel.sendMessage("Error: no transcript channel specified, no transcript will be saved for this ticket.").queue();
        }
        else
        {
            ticketChannel.getHistoryFromBeginning(100).queue(messageHistory ->
            {
                List<Message> messages = new ArrayList<>(messageHistory.getRetrievedHistory());
                messages.sort(Comparator.comparing(ISnowflake::getTimeCreated));
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("template.html"))
                {
                    Document document = Jsoup.parse(in, "UTF-8", "");
                    Element div = document.createElement("div").addClass("guild-info");
                    Element p = document.createElement("span").text(guild.getName());
                    Element guildImage = document.createElement("img");
                    guildImage.attr("src", guild.getIconUrl())
                            .attr("width", "150");
                    div.appendChild(guildImage);
                    div.appendChild(p);
                    document.appendChild(div);
                    for (Message message : messages)
                    {
                        String messageContent = message.getContentRaw();
                        if (messageContent.isEmpty())
                        {
                            continue;
                        }
                        Element parentContainer = document.createElement("div");
                        parentContainer.addClass("parent-container");

                        Element avatarDiv = document.createElement("div");
                        avatarDiv.addClass("avatar-container");
                        Element img = document.createElement("img");
                        img.attr("src", message.getAuthor().getEffectiveAvatarUrl());
                        img.addClass("avatar");
                        avatarDiv.appendChild(img);

                        parentContainer.appendChild(avatarDiv);

                        Element messageContainer = document.createElement("div");
                        messageContainer.addClass("message-container");
                        Element nameContainer = document.createElement("span")
                                .text(message.getAuthor().getAsTag() + " " +
                                        message.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME));
                        messageContainer.appendChild(nameContainer);

                        if (messageContent.contains("```"))
                        {
                            // todo: fix this
                            String code = messageContent.replace("```", "");
                            Element codeElement = document.createElement("code").text(code);
                            messageContainer.appendChild(codeElement);
                        }
                        else
                        {
                            Element messageElement = document.createElement("span").text(messageContent);
                            messageContainer.appendChild(messageElement);
                        }
                        parentContainer.appendChild(messageContainer);
                        document.appendChild(parentContainer);
                    }
                    File temp = new File(".", ticketId + ".html");
                    try (Writer writer = Files.newBufferedWriter(temp.toPath(), StandardCharsets.UTF_8))
                    {
                        writer.write(document.normalise().html().replace("{title}", "Ticket #" + ticketId));
                    }
                    EmbedBuilder embed = EmbedUtil.successEmbed(context.getAuthor())
                            .setTitle("Ticket #" + ticketId + " was closed")
                            .setDescription("Closed by: " + context.getAuthor().getAsTag());
                    transcriptChannel.sendMessage(embed.build()).queue();
                    transcriptChannel.sendFile(temp, "transcript-" + ticketId + ".html").queue(message -> temp.delete());
                }
                catch (IOException e4)
                {
                    e4.printStackTrace();
                }
            });
        }
        ticketChannel.sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor())
                .setTitle("Ticket deletion")
                .setDescription("Ticket will be deleted in 5 seconds").build()).queue();
        ticketChannel.delete().queueAfter(5, TimeUnit.SECONDS);
        return true;
    }
}
