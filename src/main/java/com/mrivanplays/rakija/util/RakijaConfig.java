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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mrivanplays.rakija.Bot;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
}
