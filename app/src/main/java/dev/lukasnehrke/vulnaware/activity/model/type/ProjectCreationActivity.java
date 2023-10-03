package dev.lukasnehrke.vulnaware.activity.model.type;

import dev.lukasnehrke.vulnaware.activity.model.Activity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PROJECT_CREATION")
public class ProjectCreationActivity extends Activity {}
