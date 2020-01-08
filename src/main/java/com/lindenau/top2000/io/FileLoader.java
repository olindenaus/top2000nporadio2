package com.lindenau.top2000.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLoader {

    public static List<String> readSongsFromFile(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(Objects.requireNonNull(FileLoader.class.getClassLoader().getResource(fileName + ".txt")).toURI());
        Stream<String> lines = Files.lines(path).filter(line -> line.contains(":")).map(line -> line.split(":")[0].trim());
        List<String> data = lines.collect(Collectors.toList());
        lines.close();
        return data;
    }
}
