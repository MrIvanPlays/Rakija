package com.mrivanplays.rakija.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mrivanplays.rakija.Bot;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class RakijaConfig
{

    private final ObjectNode object;

    public RakijaConfig()
    {
        Bot.LOGGER.info("Loading config");
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.json")))
        {
            object = BotUtils.JSON_MAPPER.readValue(reader, ObjectNode.class);
            if (object == null)
            {
                throw new IllegalArgumentException("No config present");
            }
        }
        catch (IOException ignored)
        {
            throw new IllegalArgumentException("a");
        }
    }

    public String getString(String member)
    {
        if (object.get(member) == null)
        {
            return null;
        }
        return object.get(member).asText();
    }

    public long getLong(String member)
    {
        if (object.get(member) == null)
        {
            return 0;
        }
        return object.get(member).asLong();
    }

    public boolean getBoolean(String member)
    {
        if (object.get(member) == null)
        {
            return false;
        }
        return object.get(member).asBoolean();
    }

    public List<String> getStringArray(String member)
    {
        if (object.get(member) == null)
        {
            return Collections.emptyList();
        }
        ArrayNode array = (ArrayNode) object.get(member);
        List<String> list = new ArrayList<>();
        for (JsonNode node : array)
        {
            list.add(node.asText());
        }
        return list;
    }

    public List<Role> getStringArrayAsRole(String member, Guild guild)
    {
        List<String> array = getStringArray(member);
        List<Role> roles = new ArrayList<>();
        for (String roleName : array)
        {
            roles.add(guild.getRolesByName(roleName.toLowerCase(), true).get(0));
        }
        return roles;
    }
}
