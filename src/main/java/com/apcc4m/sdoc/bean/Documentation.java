package com.apcc4m.sdoc.bean;

import java.util.List;
import java.util.Map;

public class Documentation {

    private Integer groupId;
    private ApiInfo ApiInfo;
    private String groupName;
    private String basePath;
    private List<Tag>tags;
    private Map<String, List<Options>> optionsMap;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public ApiInfo getApiInfo() {
        return ApiInfo;
    }

    public void setApiInfo(ApiInfo apiInfo) {
        ApiInfo = apiInfo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Map<String, List<Options>> getOptionsMap() {
        return optionsMap;
    }

    public void setOptionsMap(Map<String, List<Options>> optionsMap) {
        this.optionsMap = optionsMap;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}