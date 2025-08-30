package com.example.musicdemoapp;

import java.io.Serializable;
import java.util.Objects;

public class AudioModel implements Serializable {
    String path;
    String title;
    String artist;
    String album;
    String albumId;
    String albumArtPath;
    String duration;

    public AudioModel(){}

    public AudioModel(String path, String title, String artist, String album, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public AudioModel(String path, String title, String artist, String album, String albumId, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
    public String getAlbumArtPath() {
        return albumArtPath;
    }
    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = albumArtPath;
    }
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AudioModel that = (AudioModel) o;
        return Objects.equals(path, that.path) && Objects.equals(title, that.title) && Objects.equals(artist, that.artist) && Objects.equals(album, that.album) && Objects.equals(albumId, that.albumId) && Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, title, artist, album, albumId, duration);
    }

    @Override
    public String toString() {
        return "AudioModel{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId='" + albumId + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
