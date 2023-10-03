package dev.lukasnehrke.vulnaware.activity.model.type;

import dev.lukasnehrke.vulnaware.activity.model.Activity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VULN")
public class VulnActivity extends Activity {

    @Column(name = "tag")
    private String tag;

    @Column(name = "amount")
    private Integer amount;

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(final Integer amount) {
        this.amount = amount;
    }
}
