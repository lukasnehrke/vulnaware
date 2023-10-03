import { Component, EventEmitter, OnDestroy, OnInit, Output } from "@angular/core";
import { Project } from "../../shared/models/project.model";
import { AppService } from "../../shared/services/app.service";
import { AuthService } from "../../shared/services/auth.service";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { NgClass, NgIf } from "@angular/common";
import { Router, RouterLink } from "@angular/router";
import { MatMenuModule } from "@angular/material/menu";
import { MatBadgeModule } from "@angular/material/badge";
import { NotificationService } from "../../shared/services/notification.service";
import { UserDetails } from "../../shared/models/auth.model";
import { Subscription, combineLatest } from "rxjs";

@Component({
    standalone: true,
    selector: "va-navbar",
    templateUrl: "./navbar.component.html",
    styleUrls: ["./navbar.component.scss"],
    imports: [NgIf, RouterLink, MatButtonModule, MatIconModule, MatMenuModule, MatBadgeModule, NgClass],
})
export class NavbarComponent implements OnInit, OnDestroy {
    @Output() toggleEvent = new EventEmitter<boolean>();

    showMenu = false;
    project: Project | null;
    user: UserDetails | null;
    unread: number = 0;

    private sub: Subscription;

    constructor(
        private router: Router,
        private appService: AppService,
        private authService: AuthService,
        private notificationService: NotificationService,
    ) {}

    ngOnInit(): void {
        this.sub = combineLatest([this.appService.activeProject$, this.authService.currentUser, this.notificationService.status]).subscribe(
            ([project, user, status]) => {
                this.project = project;
                this.user = user;
                this.unread = status.unread;
            },
        );

        this.sub.add(
            this.appService.getNavItems().subscribe((items) => {
                this.showMenu = items.length > 0;
            }),
        );
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }

    getName() {
        if (!this.user?.sub) return "";
        return this.user.sub[0].toUpperCase();
    }

    logout() {
        this.authService.setToken(null);
        return this.router.navigateByUrl("/auth/login");
    }

    sideNavToggle() {
        this.appService.toggleSideBar();
    }
}
