package com.mrivanplays.rakija.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.CommandRegistrar;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Run it and see what it will do \uD83E\uDD14")
@CommandUsage("rakija")
@CommandAliases("rakija")
public class CommandRakija extends Command
{

    private EventWaiter eventWaiter;
    private final String playSongEmoji = "\uD83C\uDD70";
    private final String pasteInChat = "\uD83C\uDD71";

    public CommandRakija(EventWaiter eventWaiter)
    {
        this.eventWaiter = eventWaiter;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor()).setTitle("Please react")
                .setDescription("You should react with \"" + playSongEmoji
                        + "\" to play the song (should be in voice channel) or react with \"" + pasteInChat
                        + "\" to paste the song in the chat. Quick, you have 30 seconds to decide!").build())
                .queue(message ->
                {
                    message.addReaction(playSongEmoji).queue();
                    message.addReaction(pasteInChat).queue();
                    initWaiter(context, message, context.getAuthor());
                });
        return true;
    }

    private void initWaiter(CommandExecutionContext context, Message message, User author)
    {
        eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class,
                reactEvent ->
                {
                    ReactionEmote emote = reactEvent.getReactionEmote();

                    return (!reactEvent.getUser().isBot()
                            && reactEvent.getMessageIdLong() == message.getIdLong()
                            && !emote.isEmote())
                            && (playSongEmoji.equals(emote.getName()) || pasteInChat.equals(emote.getName()));
                },
                reactEvent ->
                {
                    ReactionEmote emote = reactEvent.getReactionEmote();

                    if (emote.getName().equals(playSongEmoji))
                    {
                        message.delete().queue();
                        CommandRegistrar.dispatchCommand(context, "play https://www.youtube.com/watch?v=m8uVSJL0nBc");
                    }
                    if (emote.getName().equals(pasteInChat))
                    {
                        message.clearReactions().queue();
                        message.editMessage("https://www.youtube.com/watch?v=m8uVSJL0nBc").override(true).queue();
                    }
                }, 30, TimeUnit.SECONDS,
                () ->
                {
                    message.clearReactions().queue();
                    message.editMessage(EmbedUtil.embedWithAuthor(author).setTitle("React time expired!").setColor(Color.YELLOW).build())
                            .queue();
                });
    }
}
