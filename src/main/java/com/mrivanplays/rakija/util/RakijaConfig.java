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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mrivanplays.rakija.Bot;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class RakijaConfig {

  private final JsonObject object;

  public RakijaConfig() {
    Bot.LOGGER.info("Loading config");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    try (Reader reader =
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.json"))) {
      object = gson.fromJson(reader, JsonObject.class);
    } catch (IOException ignored) {
      throw new IllegalArgumentException("a");
    }
  }

  public String getString(String member) {
    return object.get(member).getAsString();
  }

  public int getInt(String member) {
    return object.get(member).getAsInt();
  }
}
