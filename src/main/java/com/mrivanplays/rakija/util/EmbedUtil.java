package com.mrivanplays.rakija.util;

import java.awt.Color;
import java.util.function.Supplier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class EmbedUtil
{

    private static Supplier<EmbedBuilder> defaultEmbed;

    public static EmbedBuilder defaultEmbed()
    {
        return defaultEmbed.get();
    }

    public static EmbedBuilder embedWithAuthor(User author)
    {
        return defaultEmbed().setAuthor(author.getName(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl());
    }

    public static void setDefaultEmbed(Supplier<EmbedBuilder> supplier)
    {
        if (EmbedUtil.defaultEmbed == null)
        {
            EmbedUtil.defaultEmbed = supplier;
        }
    }

    public static EmbedBuilder errorEmbed(User author)
    {
        return embedWithAuthor(author).setColor(Color.RED).setTitle("Error");
    }

    public static MessageEmbed noPermissionEmbed(User author)
    {
        return errorEmbed(author).setDescription("You don't have permission to perform this command.").build();
    }

    public static EmbedBuilder successEmbed(User author)
    {
        return embedWithAuthor(author).setColor(Color.GREEN).setTitle("Success!");
    }
}
