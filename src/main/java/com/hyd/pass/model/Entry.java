package com.hyd.pass.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yiding.he
 */
public class Entry extends OrderedItem implements Cloneable {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private String location;

    private String comment;

    private String note;

    private String createTime = now();

    private List<Authentication> authentications = new ArrayList<>();

    public Entry() {
    }

    public Entry(String name, String location, String comment) {
        this.setName(name);
        this.location = location;
        this.comment = comment;
    }

    private static String now() {
        return DateFormatUtils.format(new Date(), DATE_PATTERN);
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    @Override
    public final Entry clone() {
        try {
            return (Entry) super.clone();
        } catch (CloneNotSupportedException e) {
            // ignore this
            return null;
        }
    }
}
