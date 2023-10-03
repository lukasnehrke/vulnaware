import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Notification } from "../../../../shared/models/notification.model";
import { MatPaginator } from "@angular/material/paginator";
import { map, startWith, switchMap } from "rxjs";
import { NotificationService } from "../../../../shared/services/notification.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
    selector: "va-notifications-dashboard",
    templateUrl: "./dashboard.component.html",
    styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;

    isLoading = true;
    displayedColumns: string[] = ["date", "summary"];
    notifications: Notification[] = [];
    length: number = 0;

    constructor(
        private _snackbar: MatSnackBar,
        private _notificationsService: NotificationService,
    ) {}

    ngAfterViewInit(): void {
        this.paginator.page
            .pipe(
                startWith({}),
                switchMap(() => this._notificationsService.findAll(this.paginator.pageIndex, this.paginator.pageSize)),
                map((data) => {
                    if (data === null) return [];
                    this.length = data.totalElements;
                    return data.content;
                }),
            )
            .subscribe({
                next: (notifications) => {
                    this.notifications = notifications;
                    this.isLoading = false;
                },
                error: (err) => {
                    this.notifications = [];
                    console.error(err);
                    this._snackbar.open("Failed to load notifications", "Dismiss");
                },
            });
    }

    getSummary(notification: Notification): string {
        if (notification.type === "welcome") {
            return "Welcome to VulnAware";
        }
        if (notification.type === "vuln") {
            return notification.project!.name + " has new vulnerabilities";
        }
        return "(empty)";
    }

    formatDate(text: string) {
        return this._notificationsService.formatDate(text);
    }
}
