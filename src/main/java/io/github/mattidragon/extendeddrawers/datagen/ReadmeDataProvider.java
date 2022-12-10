package io.github.mattidragon.extendeddrawers.datagen;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ReadmeDataProvider implements DataProvider {
    private static final String README = """
            # Generated data
            The data here is automatically generated and published only for reference for modpack and resource pack development.
            Any changes made to it will be reverted next time data generation is run.
            """;
    @SuppressWarnings({"deprecation"})
    private static final HashCode HASH = Hashing.sha1().hashUnencodedChars(README);
    
    private final FabricDataOutput output;
    
    public ReadmeDataProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        var path = output.getPath().resolve("README.md");
        return CompletableFuture.runAsync(() -> {
            try {
                writer.write(path, README.getBytes(StandardCharsets.UTF_8), HASH);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, Util.getMainWorkerExecutor());
    }
    
    @Override
    public String getName() {
        return "Readme";
    }
}
