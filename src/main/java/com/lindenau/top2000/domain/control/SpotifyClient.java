package com.lindenau.top2000.domain.control;

import com.lindenau.top2000.config.control.ConfigLoader;
import com.lindenau.top2000.domain.entity.EntrySong;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyClient {

    Pattern playlistPattern = Pattern.compile(".*spotify:playlist:([a-zA-Z0-9]*)\".*");
    Pattern trackPattern = Pattern.compile(".*spotify:track:([a-zA-Z0-9]*)\".*");
    HttpClient httpClient = new HttpClient();
    ConfigLoader configLoader = new ConfigLoader();

    public SpotifyClient() throws IOException {
    }

    public String getPlaylistID() throws URISyntaxException {
        URI uri = new URIBuilder().setScheme("https")
                .setHost("api.spotify.com")
                .setPath("/v1/users/" + configLoader.getUserId() + "/playlists")
                .setParameter("limit", "1")
                .build();
        String val = retrieveData(uri, playlistPattern);
        System.out.println("Returning playlist id: " + val);
        return val;
    }

    public String getTrack(EntrySong song) throws URISyntaxException {
        URI uri = new URIBuilder().setScheme("https")
                .setHost("api.spotify.com")
                .setPath("v1/search")
                .setParameter("q", song.getSearchString())
                .setParameter("type", "track")
                .setParameter("limit", "1")
                .build();
        String val = retrieveData(uri, trackPattern);
        System.out.println("Returning track id: " + val + " for song " + song.getNumber());
        return val;
    }

    public String addTrackToPlaylist( String playlistId, String trackId) throws URISyntaxException {
        URI uri = new URIBuilder().setScheme("https")
                .setHost("api.spotify.com")
                .setPath("v1/playlists/" + playlistId + "/tracks")
                .setParameter("uris", "spotify:track:" + trackId)
                .build();
        String response = httpClient.sendPost(uri.toString());
        return response;
    }

    private String retrieveData(URI uri, Pattern pattern) {
        String result = httpClient.sendGet(uri.toString()).replaceAll("\n", "-");
        Matcher matcher = pattern.matcher(result);
        String val = "ERROR";
        if (matcher.matches()) {
            val = matcher.group(1);
        }
        return val;
    }

}