import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { AppService } from "../../../../shared/services/app.service";
import { ActivityService } from "../../../../shared/services/activity.service";
import { Activity } from "../../../../shared/models/activity.model";
import { formatRelative } from "date-fns";

@Component({
    selector: "va-project-activities",
    templateUrl: "./activities.component.html",
    styleUrls: ["./activities.component.scss"],
    encapsulation: ViewEncapsulation.None,
})
export class ActivitiesComponent implements OnInit {
    activities: Activity[] = [];
    selectedType: string;

    types = [
        { value: "Upload", label: "Upload" },
        { value: "vulnerabilities", label: "Vulnerability Analysis" },
        { value: "components", label: "Component Analysis" },
    ];

    constructor(
        private appService: AppService,
        private activityService: ActivityService,
    ) {}

    ngOnInit() {
        this.appService.getActiveProject().subscribe((project) => {
            if (!project) {
                this.activities = [];
                return;
            }
            this.activityService.findAll(project.slug).subscribe((activities) => {
                this.activities = activities;
            });
        });
    }

    dateTime(date: string) {
        const str = formatRelative(new Date(date), new Date());
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
}
