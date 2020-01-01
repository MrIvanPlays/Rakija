package com.mrivanplays.rakija.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class RequestedAudioTrack
{

    private final AudioTrack track;
    private final String requester;

    public RequestedAudioTrack(AudioTrack track, String requester)
    {
        this.track = track;
        this.requester = requester;
    }

    public AudioTrack getTrack()
    {
        return track;
    }

    public String getRequester()
    {
        return requester;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        RequestedAudioTrack that = (RequestedAudioTrack) o;

        if (getTrack() != null ? !getTrack().equals(that.getTrack()) : that.getTrack() != null)
        {
            return false;
        }
        return getRequester() != null ? getRequester().equals(that.getRequester()) : that.getRequester() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getTrack() != null ? getTrack().hashCode() : 0;
        result = 31 * result + (getRequester() != null ? getRequester().hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "RequestedAudioTrack{audioTrack=" + track + ", requester='" + requester + '\'' + '}';
    }
}
