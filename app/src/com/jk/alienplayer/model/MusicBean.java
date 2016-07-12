package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by junjie.qu on 6/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicBean {
    @JsonProperty("dfsId")
    private long dfsId = 0;

    @JsonProperty("extension")
    private String extension = "";

    @JsonProperty("dfsId")
    public long getDfsId() {
        return dfsId;
    }

    @JsonProperty("dfsId")
    public void setDfsId(long dfsId) {
        this.dfsId = dfsId;
    }

    @JsonProperty("extension")
    public String getExtension() {
        return extension;
    }

    @JsonProperty("extension")
    public void setExtension(String extension) {
        this.extension = extension;
    }
}
