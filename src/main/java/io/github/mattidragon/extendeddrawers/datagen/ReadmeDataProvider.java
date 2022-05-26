package io.github.mattidragon.extendeddrawers.datagen;

import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class ReadmeDataProvider implements DataProvider {
    private static final String README = """
            # Generated data
            The data here is automatically generated and published only for reference for modpack and resource pack development.
            Any changes made to it will be reverted next time data generation is run.
            """;
    private static final String HASH = SHA1.hashUnencodedChars(README).toString();
    
    private final DataGenerator root;
    
    public ReadmeDataProvider(DataGenerator root) {
        this.root = root;
    }
    
    @Override
    public void run(DataCache cache) throws IOException {
        var path = root.getOutput().resolve("README.md");
        
        if (!Objects.equals(cache.getOldSha1(path), HASH) || !Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, README);
        }
        cache.updateSha1(path, HASH);
    }
    
    @Override
    public String getName() {
        return "Readme";
    }
}
