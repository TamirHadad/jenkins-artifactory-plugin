package org.jfrog.hudson.pipeline;

import net.sf.json.JSONObject;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by romang on 4/26/16.
 */
public class ArtifactoryAqlJson {

    private JSONObject find;

    public String getFind() {
        if (find != null) {
            return "items.find(" + find.toString() + ")";
        }
        return null;
    }

    @JsonProperty("items.find")
    public void setFind(JSONObject find) {
        this.find = find;
    }
}
