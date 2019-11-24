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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BotUtils
{

    public static DecimalFormat DECIMAL_FORMAT_NUMBER;
    public static DecimalFormat DECIMAL_FORMAT_PERCENTAGE;
    public static ObjectMapper JSON_MAPPER;

    static
    {
        DECIMAL_FORMAT_NUMBER = new DecimalFormat("###,###,###.##");
        DECIMAL_FORMAT_PERCENTAGE = new DecimalFormat("###.##");
        JSON_MAPPER = new ObjectMapper();
    }

    public static boolean checkVoiceStates(TextChannel responseChannel, GuildVoiceState botState, GuildVoiceState memberState,
                                           User author, Message triggeringMessage, Runnable checksPassed)
    {
        if (botState.inVoiceChannel())
        {
            if (memberState.inVoiceChannel())
            {
                if (!botState.getChannel().equals(memberState.getChannel()))
                {
                    responseChannel.sendMessage(EmbedUtil.errorEmbed(author)
                            .setDescription("You should be in the same voice channel with the bot in order to perform this command.").build())
                            .queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                    triggeringMessage.delete().queueAfter(15, TimeUnit.SECONDS);
                    return true;
                }
                else
                {
                    checksPassed.run();
                    return true;
                }
            }
            else
            {
                responseChannel.sendMessage(EmbedUtil.errorEmbed(author)
                        .setDescription("You should be in the same voice channel with the bot in order to perform this command.").build())
                        .queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                triggeringMessage.delete().queueAfter(15, TimeUnit.SECONDS);
                return true;
            }
        }
        else
        {
            responseChannel.sendMessage(EmbedUtil.errorEmbed(author)
                    .setDescription("Bot isn't connected to a voice channel.").build())
                    .queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            triggeringMessage.delete().queueAfter(15, TimeUnit.SECONDS);
            return true;
        }
    }
}
