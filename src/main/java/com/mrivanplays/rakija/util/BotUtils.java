package com.mrivanplays.rakija.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.*;

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

    public static boolean checkVoiceStates(MessageChannel responseChannel, GuildVoiceState botState, GuildVoiceState memberState,
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

    public static int generateRandomNumber(int length)
    {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int random = ThreadLocalRandom.current().nextInt(0, 9);
            number.append(random);
        }
        return Integer.parseInt(number.toString());
    }
}
