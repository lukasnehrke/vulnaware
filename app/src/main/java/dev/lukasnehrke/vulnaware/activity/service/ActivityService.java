package dev.lukasnehrke.vulnaware.activity.service;

import dev.lukasnehrke.vulnaware.activity.model.Activity;
import dev.lukasnehrke.vulnaware.activity.repository.ActivityRepository;
import org.springframework.stereotype.Service;

@Service
public final class ActivityService {

    private final ActivityRepository activities;

    public ActivityService(final ActivityRepository activities) {
        this.activities = activities;
    }

    public void create(final Activity activity) {
        this.activities.save(activity);
    }
}
