package io.github.mattidragon.extendeddrawers.datagen;

import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.io.IOException;
import java.nio.file.Files;

public class ReadmeDataProvider implements DataProvider {
    private static final String README = """
            # Generated data
            The data here is automatically generated and published only for reference for modpack and resource pack development.
            Any changes made to it will be reverted next time data generation is run.
            """;
    private final DataGenerator root;
    
    public ReadmeDataProvider(DataGenerator root) {
        this.root = root;
    }
    
    @Override
    public void run(DataCache cache) throws IOException {
        Files.writeString(root.getOutput().resolve("README.md"), README);
    }
    
    @Override
    public String getName() {
        return "Readme";
    }
}
