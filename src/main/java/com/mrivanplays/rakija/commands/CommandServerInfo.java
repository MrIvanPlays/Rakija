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
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

@CommandDescription("Shows various data about the current server")
@CommandUsage("serverinfo")
public class CommandServerInfo extends Command
{

    public CommandServerInfo()
    {
        super("serverinfo");
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        Guild guild = context.getGuild();
        String generalInfo = String.format(
                "**Owner**: <@%s>\n**Region**: %s\n**Creation Date**: %s\n**Verification Level**: %s",
                guild.getOwnerId(),
                guild.getRegion().getName(),
                guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                convertVerificationLevel(guild.getVerificationLevel()));

        String memberInfo = String.format(
                "**Total Roles**: %s\n**Total Members**: %s\n**Online Members**: %s\n**Offline Members**: %s\n**Bot Count**: %s",
                guild.getRoleCache().size(),
                guild.getMemberCache().size(),
                guild.getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.ONLINE).count(),
                guild.getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.OFFLINE).count(),
                guild.getMemberCache().stream().filter((m) -> m.getUser().isBot()).count());

        EmbedBuilder embed = EmbedUtil.defaultEmbed()
                .setTitle("Server info for " + guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField("General Info", generalInfo, false)
                .addField("Role And Member Counts", memberInfo, false);

        context.getChannel().sendMessage(embed.build()).queue();
        return true;
    }

    private String convertVerificationLevel(Guild.VerificationLevel lvl)
    {
        String[] names = lvl.name().toLowerCase().split("_");
        StringBuilder out = new StringBuilder();

        for (String name : names)
        {
            out.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1)).append(" ");
        }

        return out.toString().trim();
    }
}
