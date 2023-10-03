import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { switchMap } from "rxjs";
import { Notification } from "../../../../shared/models/notification.model";
import { NotificationService } from "../../../../shared/services/notification.service";

@Component({
    selector: "va-notifications-details",
    templateUrl: "./details.component.html",
    styleUrls: ["./details.component.scss"],
})
export class DetailsComponent implements OnInit {
    notification: Notification | null;
    loading = true;

    constructor(
        private route: ActivatedRoute,
        private notificationService: NotificationService,
    ) {}

    ngOnInit() {
        this.route.paramMap
            .pipe(
                switchMap((params: ParamMap) => {
                    const id = params.get("id");
                    return this.notificationService.find(id!);
                }),
            )
            .subscribe((notification: Notification) => {
                this.notification = notification;
                this.loading = false;

                if (!notification.read) {
                    this.notificationService.update(notification.id, { read: true }).subscribe((notification) => {
                        this.notification = notification;
                    });
                }
            });
    }

    formatDate(text: string) {
        return this.notificationService.formatDate(text);
    }
}
