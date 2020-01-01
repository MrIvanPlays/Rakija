package com.mrivanplays.rakija.commands.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@CommandUsage("cat")
@CommandDescription("Shows a random cat image, took from imgur")
public class CommandCat extends Command
{

    public CommandCat()
    {
        super("cat");
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        context.getJda().getHttpClient().newCall(
                new Request.Builder()
                        .url("https://www.imgur.com/r/cat/hot.json")
                        .addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1")
                        .get()
                        .build()
        ).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                JsonNode dataArray = BotUtils.JSON_MAPPER.readTree(response.body().charStream()).get("data");
                int random = ThreadLocalRandom.current().nextInt(0, dataArray.size());
                JsonNode node = dataArray.get(random);
                context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor())
                        .setTitle(node.get("title").asText(), "https://imgur.com/" + node.get("hash").asText())
                        .setImage("https://imgur.com/" + node.get("hash").asText() + node.get("ext").asText())
                        .build()).queue();
            }
        });
        return true;
    }
}
