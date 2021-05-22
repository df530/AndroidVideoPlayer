package Model;

import java.io.File;

public class VideoModel {
    private String str_path, str_thumb;
    private boolean boolean_selected;

    public String getStr_path() {
        return str_path;
    }

    public void setStr_path(String str_path) {
        this.str_path = str_path;
    }

    public String getStr_thumb() {
        return str_thumb;
    }

    public void setStr_thumb(String str_thumb) {
        this.str_thumb = str_thumb;
    }

    public boolean isBoolean_selected() {
        return boolean_selected;
    }

    public void setBoolean_selected(boolean boolean_selected) {
        this.boolean_selected = boolean_selected;
    }

    public String getFileSizeMegaBytes() {
        File file = new File(str_path);
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    public int getVideoDuration() {
        //TODO
        return 0;
    }
}
