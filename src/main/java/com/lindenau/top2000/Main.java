package com.lindenau.top2000;

import com.lindenau.top2000.domain.control.SpotifyClient;
import com.lindenau.top2000.domain.entity.EntrySong;
import com.lindenau.top2000.excel.control.ExcelReader;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        ExcelReader excelReader = new ExcelReader();
        List<EntrySong> songs = excelReader.readSongsFromExcel();
        SpotifyClient spotifyClient = new SpotifyClient();
        String playlistId = spotifyClient.getPlaylistID();
        for (EntrySong song : songs) {
            String trackId = spotifyClient.getTrack(song);
            String response = spotifyClient.addTrackToPlaylist(playlistId, trackId);
            if(response.contains("snapshot")) {
                System.out.println("Success for: " + song.getNumber());
            } else {
                System.out.println("Problems for: " + song.getNumber());
            }
        }
    }
}
