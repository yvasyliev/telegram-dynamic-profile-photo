package app.telegram.suppliers;

import api.deezer.objects.Track;

import java.util.function.Supplier;

/**
 * Supplies track to print.
 */
public class TrackToPrint implements Supplier<Track> {
    /**
     * Track to print.
     */
    private Track track;

    @Override
    public Track get() {
        return track;
    }

    /**
     * Sets track to print.
     *
     * @param track track to print.
     */
    public void setTrack(Track track) {
        this.track = track;
    }
}
