package dev.lukasnehrke.vulnaware.activity.model.type;

import dev.lukasnehrke.vulnaware.activity.model.Activity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BOM_UPLOAD")
public class UploadActivity extends Activity {

    @Column(name = "tag")
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }
}
