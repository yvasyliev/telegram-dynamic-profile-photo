package app.telegram.suppliers;

import api.deezer.objects.Track;

import java.util.function.Supplier;

public class TrackToPrint implements Supplier<Track> {
    private Track track;

    @Override
    public Track get() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
