package com.jk.alienplayer.metadata;

public class NetworkArtistInfo extends NetworkSearchResult {

    public String avatar;

    public NetworkArtistInfo(long id, String name, String avatar) {
        super(id, TYPE_ARTISTS, name);
        this.avatar = avatar;
    }

}
