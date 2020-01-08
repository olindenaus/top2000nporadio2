package com.lindenau.top2000;

import com.lindenau.top2000.domain.control.SpotifyClient;
import com.lindenau.top2000.domain.entity.EntrySong;
import com.lindenau.top2000.excel.control.ExcelReader;
import com.lindenau.top2000.io.FileLoader;
import com.lindenau.top2000.io.FileSaver;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose option");
        System.out.println("1. Add tracks to playlist");
        System.out.println("2. Download duplicates in playlist");
        System.out.println("3. Delete duplicates from playlist");
        System.out.println("4. Calculate how many duplicated songs there are.");
        String s = scanner.nextLine();
        switch (s) {
            case "1":
                addTracks();
                break;
            case "2":
                downloadDuplicates();
                break;
            case "3":
                deleteDuplicates();
                addDuplicatedTracks();
                break;
            case "4":
                calculateHowManyDuplicates();
                break;
        }
    }

    public static void deleteDuplicates() throws IOException, URISyntaxException {
        List<String> data = FileLoader.readSongsFromFile("duplicateSongs");
        SpotifyClient spotifyClient = new SpotifyClient();
        String playlistId = spotifyClient.getPlaylistID();
        int counter = 0;
        System.out.println("Deleting duplicates: ");
        for (String trackId : data) {
            String response = spotifyClient.deleteTrackFromPlaylist(trackId, playlistId);
            if(response.contains("snapshot")) {
                System.out.print("\r"+ ++counter);
            }
        }
    }

    public static void downloadDuplicates() throws Exception {
        SpotifyClient spotifyClient = new SpotifyClient();
        String playListId = spotifyClient.getPlaylistID();
        int playlistLength = spotifyClient.getTracksNumberInPlaylist(playListId);
        int lastOffset = playlistLength - playlistLength % 100;
        List<String> ids = new ArrayList<>();
        for (int offset = 0; offset <= lastOffset; offset += 100) {
            ids.addAll(spotifyClient.getSongsFromPlaylist(playListId, offset));
        }
        Map<String, Integer> songs = calculateDuplicates(ids);
        Map<String, Integer> uniqueSongs = getUnique(songs);
        FileSaver.saveMapToFile(uniqueSongs, "uniqueSongs");
        Map<String, Integer> duplicateSongs = getDuplicates(songs);
        FileSaver.saveMapToFile(duplicateSongs, "duplicateSongs");
        System.out.println("finished");
    }

    public static void calculateHowManyDuplicates() throws Exception {
        SpotifyClient spotifyClient = new SpotifyClient();
        String playListId = spotifyClient.getPlaylistID();
        int playlistLength = spotifyClient.getTracksNumberInPlaylist(playListId);
        int lastOffset = playlistLength - playlistLength % 100;
        List<String> ids = new ArrayList<>();
        for (int offset = 0; offset <= lastOffset; offset += 100) {
            ids.addAll(spotifyClient.getSongsFromPlaylist(playListId, offset));
        }
        Map<String, Integer> songs = calculateDuplicates(ids);
        Map<String, Integer> duplicateSongs = getDuplicates(songs);
        FileSaver.saveMapToFile(duplicateSongs, "duplicateSongs");
        System.out.println("finished: " + duplicateSongs.entrySet().size());
    }

    public static Map<String, Integer> calculateDuplicates(List<String> ids) throws IOException {
        Map<String, Integer> duplicates = new HashMap<>();
        for (String id : ids) {
            Integer count = duplicates.get(id);
            duplicates.put(id, (count == null) ? 1 : count + 1);
        }
        FileSaver.saveMapToFile(duplicates, "allSongs");
        return duplicates;
    }

    public static Map<String, Integer> getUnique(Map<String, Integer> songs) {
        return songs.entrySet().stream()
                .filter(x -> x.getValue() == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Integer> getDuplicates(Map<String, Integer> songs) {
        return songs.entrySet().stream()
                .filter(x -> x.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void addDuplicatedTracks() throws Exception {
        List<String> data = FileLoader.readSongsFromFile("duplicateSongs");
        SpotifyClient spotifyClient = new SpotifyClient();
        String playlistId = spotifyClient.getPlaylistID();
        int counter = 0;
        for (String songId : data) {
            String response = spotifyClient.addTrackToPlaylist(playlistId, songId);
            if (response.contains("snapshot")) {
                System.out.print("\r Added " + ++counter + " out of " + data.size());
            } else {
                System.out.println("Problems for: " + songId);
            }
        }
    }

    public static void addTracks() throws Exception {
        ExcelReader excelReader = new ExcelReader();
        List<EntrySong> songs = excelReader.readSongsFromExcel();
        SpotifyClient spotifyClient = new SpotifyClient();
        String playlistId = spotifyClient.getPlaylistID();
        for (EntrySong song : songs) {
            String trackId = spotifyClient.getTrack(song);
            String response = spotifyClient.addTrackToPlaylist(playlistId, trackId);
            if (response.contains("snapshot")) {
                System.out.println("Success for: " + song.getNumber());
            } else {
                System.out.println("Problems for: " + song.getNumber());
            }
        }
    }
}
