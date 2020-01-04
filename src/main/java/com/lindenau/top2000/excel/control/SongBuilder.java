package com.lindenau.top2000.excel.control;

import com.lindenau.top2000.domain.entity.EntrySong;

public class SongBuilder {

    public static EntrySong buildFromRow(String row) {
        String[] values = row.split(";");
        return EntrySong.builder()
                .setNumber(Integer.parseInt(values[0]))
                .setTitle(values[1])
                .setArtist(values[2])
                .setYear(Integer.parseInt(values[3]))
                .build();
    }
}
