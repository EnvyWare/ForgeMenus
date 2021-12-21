package com.envyful.menus.forge.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Configurable {

    private final String name;
    private final Path file;

    private CommentedConfigurationNode node;
    private YamlConfigurationLoader loader;

    public Configurable(String name) {
        this.name = name;
        this.file = Paths.get(name);
        this.loader = YamlConfigurationLoader.builder().path(this.file).build();

        this.setup();
    }

    public String getName() {
        return this.name;
    }

    public YamlConfigurationLoader getLoader() {
        return this.loader;
    }

    public CommentedConfigurationNode getNode() {
        return this.node;
    }

    public void setNode(CommentedConfigurationNode node) {
        this.node = node;
    }

    public void setup() {
        File configDirectory = Paths.get("config").toFile();

        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        if (!Files.exists(this.file)) {
            this.populateFiles();
        } else {
            this.load();
        }
    }

    private void populateFiles() {
        try {
            Files.createFile(this.file);
            this.load();
            this.populate();
            this.save();
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public void load() {
        try {
            this.node = this.loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + this.name, e);
        }
    }

    public abstract void populate() throws SerializationException;

    public void save() {
        try {
            this.loader.save(this.node);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }
}
