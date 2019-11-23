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
package com.mrivanplays.rakija.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class RequestedAudioTrack {

  private final AudioTrack track;
  private final String requester;

  public RequestedAudioTrack(AudioTrack track, String requester) {
    this.track = track;
    this.requester = requester;
  }

  public AudioTrack getTrack() {
    return track;
  }

  public String getRequester() {
    return requester;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RequestedAudioTrack that = (RequestedAudioTrack) o;

    if (getTrack() != null
        ? !getTrack().equals(that.getTrack())
        : that.getTrack() != null) {
      return false;
    }
    return getRequester() != null
        ? getRequester().equals(that.getRequester())
        : that.getRequester() == null;
  }

  @Override
  public int hashCode() {
    int result = getTrack() != null ? getTrack().hashCode() : 0;
    result = 31 * result + (getRequester() != null ? getRequester().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "RequestedAudioTrack{"
        + "audioTrack="
        + track
        + ", requester='"
        + requester
        + '\''
        + '}';
  }
}
