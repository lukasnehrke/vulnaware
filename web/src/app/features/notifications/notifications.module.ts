import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDividerModule } from "@angular/material/divider";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatCardModule } from "@angular/material/card";
import { RouterLink } from "@angular/router";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { DetailsComponent } from "./pages/details/details.component";
import { NotificationsRoutingModule } from "./notifications-routing.module";
import { MatSnackBarModule } from "@angular/material/snack-bar";

@NgModule({
    imports: [
        NotificationsRoutingModule,
        CommonModule,
        MatTableModule,
        MatCheckboxModule,
        MatDividerModule,
        MatPaginatorModule,
        MatCardModule,
        RouterLink,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
    ],
    declarations: [DashboardComponent, DetailsComponent],
})
export class NotificationsModule {}
