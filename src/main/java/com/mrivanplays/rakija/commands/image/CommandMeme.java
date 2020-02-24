package com.mrivanplays.rakija.commands.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.rakija.util.BotUtils;
import com.mrivanplays.rakija.util.EmbedUtil;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@CommandUsage("meme")
@CommandDescription("Shows a random reddit meme")
@CommandAliases("meme")
public class CommandMeme extends Command
{

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        context.getJda().getHttpClient().newCall(
                new Request.Builder()
                        .url("https://apis.duncte123.me/meme")
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
                JsonNode node = BotUtils.JSON_MAPPER.readTree(response.body().charStream());
                if (node.get("success").asBoolean())
                {
                    context.getChannel().sendMessage(EmbedUtil.embedWithAuthor(context.getAuthor())
                            .setTitle(node.get("data").get("title").asText(), node.get("data").get("url").asText())
                            .setImage(node.get("data").get("image").asText())
                            .build()).queue();
                }
                else
                {
                    context.getChannel().sendMessage(EmbedUtil.errorEmbed(context.getAuthor())
                            .setDescription("We're being rate limited. Try again after some time.")
                            .build()).queue();
                }
            }
        });
        return true;
    }
}
