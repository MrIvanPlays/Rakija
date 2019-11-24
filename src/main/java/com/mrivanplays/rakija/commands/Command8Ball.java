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
        super("8ball");
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
