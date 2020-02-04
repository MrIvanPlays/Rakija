package com.mrivanplays.rakija.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

@CommandUsage("8ball (question)")
@CommandDescription("Answers your question")
public class Command8Ball extends Command
{

    private List<String> answers;

    public Command8Ball()
    {
        super("8ball", false);
        answers = new ArrayList<String>()
        {
            {
                add("Yes");
                add("No");
                add("Maybe");
                add("Probably");
                add("Probably not");
                add("I cannot answer this question right now");
            }
        };
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        if (args.size() == 0)
        {
            context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                    .setDescription("You should type a question").build())
                    .queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return false;
        }
        String question = args.joinArgumentsSpace(0);
        String answer = answers.get(ThreadLocalRandom.current().nextInt(0, answers.size()));
        context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor())
                .setTitle("The magic 8ball")
                .appendDescription("**Question:** " + question + "\n")
                .appendDescription("**Answer:** " + answer).build()).queue();
        return true;
    }
}
