package com.hyd.pass.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiding.he
 */
public class Entry extends OrderedItem {

    private String location;

    private String comment;

    private String note;

    private List<Authentication> authentications = new ArrayList<>();

    public Entry() {
    }

    public Entry(String name, String location, String comment) {
        this.setName(name);
        this.location = location;
        this.comment = comment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Authentication> getAuthentications() {
        return authentications;
    }

    public void setAuthentications(List<Authentication> authentications) {
        this.authentications = authentications;
    }

    ///////////////////////////////////////////////

    public boolean matchKeyword(String keyword) {
        return StringUtils.containsIgnoreCase(getName(), keyword) ||
                StringUtils.containsIgnoreCase(location, keyword) ||
                StringUtils.containsIgnoreCase(comment, keyword) ||
                StringUtils.containsIgnoreCase(note, keyword);
    }
}
