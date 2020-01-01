package com.mrivanplays.rakija.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.api.entities.Member;

public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer player;
    private final BlockingQueue<RequestedAudioTrack> queue;
    private boolean repeating;
    private AudioTrack lastTrack;

    public TrackScheduler(AudioPlayer player)
    {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member requester)
    {
        if (!player.startTrack(track, true))
        {
            RequestedAudioTrack requestedAduioTrack = new RequestedAudioTrack(track, requester.getEffectiveName());
            queue.offer(requestedAduioTrack);
        }
    }

    public BlockingQueue<RequestedAudioTrack> getQueue()
    {
        return queue;
    }

    public boolean nextTrack()
    {
        RequestedAudioTrack next = queue.poll();
        if (next == null)
        {
            return false;
        }
        player.startTrack(next.getTrack(), false);
        return true;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        this.lastTrack = track;
        if (endReason.mayStartNext)
        {
            if (repeating)
            {
                player.startTrack(lastTrack.makeClone(), false);
            }
            else
            {
                nextTrack();
            }
        }
    }

    public boolean isRepating()
    {
        return repeating;
    }

    public void setRepeating(boolean repeating)
    {
        this.repeating = repeating;
    }
}
