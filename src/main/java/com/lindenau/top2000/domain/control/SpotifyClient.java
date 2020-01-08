package com.lindenau.top2000.domain.control;

import com.lindenau.top2000.config.control.ConfigLoader;
import com.lindenau.top2000.domain.entity.EntrySong;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyClient {

    Pattern playListId = Pattern.compile(".*spotify:playlist:([a-zA-Z0-9]*)\".*");
    Pattern tracksNumber = Pattern.compile(".*\"total\" : (\\d+).*},.*");
    Pattern trackPattern = Pattern.compile(".*spotify:track:([a-zA-Z0-9]*)\".*");
    private static final String spotifyApi = "api.spotify.com";
    HttpClient httpClient = new HttpClient();
    ConfigLoader configLoader = new ConfigLoader();

    public SpotifyClient() throws IOException {
    }

    public List<String> getSongsFromPlaylist(String playListId, int offset) throws URISyntaxException, ParseException {
        List<String> ids = new ArrayList<>();
        URI uri = getSpotifyUriBuilder()
                .setPath("/v1/playlists/" + playListId + "/tracks")
                .setParameter("fields", "items(track(id))")
                .setParameter("limit", "100")
                .setParameter("offset", String.valueOf(offset))
                .build();
        String response = httpClient.sendGet(uri.toString());
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");
        for (Object o : jsonArray) {
            JSONObject item = (JSONObject) o;
            JSONObject track = (JSONObject) item.get("track");
            ids.add(String.valueOf(track.get("id")));
        }
        return ids;
    }

    public String deleteTrackFromPlaylist(String trackId, String playlistId) throws URISyntaxException, IOException {
        URI uri = getSpotifyUriBuilder()
                .setPath("/v1/playlists/" + playlistId + "/tracks")
                .build();
        return httpClient.sendDelete(uri, Collections.singletonList(trackId));
    }

    public String getPlaylistID() throws URISyntaxException {
        String response = getPlaylist().replaceAll("\n", "-");
        Matcher matcher = playListId.matcher(response);
        String val = "ERROR";
        if (matcher.matches()) {
            val = matcher.group(1);
        }
        System.out.println("Returning playlist id: " + val);
        return val;
    }

    public int getTracksNumberInPlaylist(String playListId) throws URISyntaxException, ParseException {
        URI uri = getSpotifyUriBuilder()
                .setPath("/v1/playlists/" + playListId + "/tracks")
                .setParameter("fields", "total")
                .build();
        String response = httpClient.sendGet(uri.toString());//getPlaylist().replaceAll("\n", "-");
        JSONObject object = (JSONObject) new JSONParser().parse(response);
        int value = Integer.parseInt(String.valueOf(object.get("total")));
        System.out.println(String.format("There are %s tracks in playlist", value));
        return value;
    }

    public String getPlaylist() throws URISyntaxException {
        URI uri = getSpotifyUriBuilder()
                .setPath("/v1/users/" + configLoader.getUserId() + "/playlists")
                .setParameter("limit", "1")
                .build();
        String response = httpClient.sendGet(uri.toString());
        return response;
    }

    public String getTrack(EntrySong song) throws URISyntaxException {
        URI uri = getSpotifyUriBuilder()
                .setPath("v1/search")
                .setParameter("q", song.getSearchString())
                .setParameter("type", "track")
                .setParameter("limit", "1")
                .build();
        String val = retrieveData(uri, trackPattern);
        System.out.println("Returning track id: " + val + " for song " + song.getNumber());
        return val;
    }

    public String addTrackToPlaylist(String playlistId, String trackId) throws URISyntaxException {
        URI uri = getSpotifyUriBuilder()
                .setPath("v1/playlists/" + playlistId + "/tracks")
                .setParameter("uris", "spotify:track:" + trackId)
                .build();
        String response = httpClient.sendPost(uri.toString());
        return response;
    }

    private String retrieveData(URI uri, Pattern pattern) {
        String result = httpClient.sendGet(uri.toString());
        Matcher matcher = pattern.matcher(result);
        String val = "ERROR";
        if (matcher.matches()) {
            val = matcher.group(1);
        }
        return val;
    }

    private URIBuilder getSpotifyUriBuilder() {
        return new URIBuilder().setScheme("https")
                .setHost(spotifyApi);
    }

}