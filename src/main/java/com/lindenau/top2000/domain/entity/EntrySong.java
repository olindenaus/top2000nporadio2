package com.lindenau.top2000.domain.entity;

public class EntrySong {

    private int number;
    private String title;
    private String artist;
    private int year;

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getYear() {
        return year;
    }

    public String getSearchString() {
        return getTitle() + " " + getArtist().replaceAll("&", ",");
    }

    public static Builder builder() {
        return new Builder();
    }

    private EntrySong(Builder builder) {
        this.number = builder.number;
        this.title = builder.title;
        this.artist = builder.artist;
        this.year = builder.year;
    }

    public static class Builder {
        private int number;
        private String title;
        private String artist;
        private int year;

        public Builder setNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public EntrySong build() {
            return new EntrySong(this);
        }
    }
}
