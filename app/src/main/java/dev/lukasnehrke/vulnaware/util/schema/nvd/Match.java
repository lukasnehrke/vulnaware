package dev.lukasnehrke.vulnaware.util.schema.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {

    @JsonProperty("vulnerable")
    boolean vulnerable;

    @JsonProperty("criteria")
    String criteria;

    @JsonProperty("versionStartIncluding")
    String versionStartIncluding;

    @JsonProperty("versionStartExcluding")
    String versionStartExcluding;

    @JsonProperty("versionEndIncluding")
    String versionEndIncluding;

    @JsonProperty("versionEndExcluding")
    String versionEndExcluding;
}
