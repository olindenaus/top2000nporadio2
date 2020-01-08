package com.lindenau.top2000.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileSaver {

    public static <K,V> void saveMapToFile(Map<K, V> map, String fileName) throws IOException {
        List<String> lines = map.entrySet().stream()
                .map(kvEntry -> kvEntry.getKey().toString() + " : " + kvEntry.getValue().toString())
                .collect(Collectors.toList());
        Path path = Paths.get(fileName + ".txt");
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
