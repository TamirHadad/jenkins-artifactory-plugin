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
        return aql.toString();
    }

    public String getPattern() {
        return pattern;
    }

    public String getTarget() {
        return target;
    }
}
