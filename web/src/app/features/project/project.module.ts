import { NgModule } from "@angular/core";
import { CommonModule, NgIf, NgOptimizedImage } from "@angular/common";

import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatCardModule } from "@angular/material/card";
import { RouterLink } from "@angular/router";
import { MatListModule } from "@angular/material/list";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { CircularMetricsComponent } from "../../shared/components/circular-metrics/circular-metrics.component";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatSelectModule } from "@angular/material/select";
import { MatSnackBarModule } from "@angular/material/snack-bar";

import { BadgeComponent } from "../../components/badge/badge.component";
import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { IntroComponent } from "./pages/intro/intro.component";
import { IssuesComponent } from "./pages/issues/issues.component";
import { IssueDetailsComponent } from "./pages/issue-details/issue-details.component";
import { ActivitiesComponent } from "./pages/activities/activities.component";
import { HeaderComponent } from "./components/header/header.component";
import { SettingsComponent } from "./pages/settings/settings.component";
import { ProjectService } from "./services/project.service";
import { ProjectRoutingModule } from "./project-routing.module";
import { NewProjectDialogComponent } from "./components/new-project-dialog/new-project-dialog.component";
import { ProjectComponent } from "./pages/project.component";
import { MatDialogModule } from "@angular/material/dialog";
import { BomSelectComponent } from "./components/bom-select/bom-select.component";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatPaginatorModule } from "@angular/material/paginator";
import { VulnCardComponent } from "../../components/vuln-card/vuln-card.component";
import { MatNativeDateModule } from "@angular/material/core";
import { MatTableModule } from "@angular/material/table";
import { VulnEditComponent } from "./components/vuln-edit/vuln-edit.component";

@NgModule({
    imports: [
        ProjectRoutingModule,
        NgIf,
        CommonModule,
        NgOptimizedImage,
        MatProgressSpinnerModule,
        MatCardModule,
        RouterLink,
        MatListModule,
        MatFormFieldModule,
        MatIconModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatInputModule,
        MatButtonModule,
        CircularMetricsComponent,
        MatDatepickerModule,
        MatSelectModule,
        FormsModule,
        MatSnackBarModule,
        MatDialogModule,
        BadgeComponent,
        BomSelectComponent,
        MatCheckboxModule,
        MatPaginatorModule,
        VulnCardComponent,
        MatNativeDateModule,
        MatTableModule,
    ],
    declarations: [
        HeaderComponent,
        NewProjectDialogComponent,
        ProjectComponent,
        DashboardComponent,
        IntroComponent,
        IssuesComponent,
        IssueDetailsComponent,
        ActivitiesComponent,
        SettingsComponent,
        VulnEditComponent,
    ],
    providers: [ProjectService],
    exports: [HeaderComponent],
})
export class ProjectModule {}
