package com.apcc4m.sdoc.bean;

import java.util.List;

public class Tag {

    private String tagId;
    private String tagName;
    private String tagMethod;
    private String tagSummary;
    private List<Tag> children;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagMethod() {
        return tagMethod;
    }

    public void setTagMethod(String tagMethod) {
        this.tagMethod = tagMethod;
    }

    public String getTagSummary() {
        return tagSummary;
    }

    public void setTagSummary(String tagSummary) {
        this.tagSummary = tagSummary;
    }

    public List<Tag> getChildren() {
        return children;
    }

    public void setChildren(List<Tag> children) {
        this.children = children;
    }

}
