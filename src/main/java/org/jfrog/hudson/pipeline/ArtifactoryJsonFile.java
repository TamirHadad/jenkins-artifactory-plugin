package org.jfrog.hudson.pipeline;

import net.sf.json.JSONObject;

/**
 * Created by romang on 4/20/16.
 */
public class ArtifactoryJsonFile {
    private JSONObject aql;
    private String pattern;
    private String target;

    public String getAql() {
        if (aql != null) {
            return aql.toString();
        }
        return null;
    }

    public String getPattern() {
        return pattern;
    }

    public String getTarget() {
        return target;
    }

    public void setAql(JSONObject aql) {
        this.aql = aql;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
