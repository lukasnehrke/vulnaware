import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { Project } from "../../../../shared/models/project.model";
import { MatDialog } from "@angular/material/dialog";
import { ProjectService } from "../../../../shared/services/projects.service";
import { FormControl } from "@angular/forms";
import { startWith, switchMap } from "rxjs";
import { MatSnackBar } from "@angular/material/snack-bar";
import { NewProjectDialogComponent } from "../../components/new-project-dialog/new-project-dialog.component";

@Component({
    selector: "va-project-dashboard",
    templateUrl: "./dashboard.component.html",
    styleUrls: ["./dashboard.component.scss"],
    encapsulation: ViewEncapsulation.None,
})
export class DashboardComponent implements OnInit {
    isLoading = true;
    filter = new FormControl("");
    projects: Project[] | null;

    constructor(
        private projectService: ProjectService,
        private _dialog: MatDialog,
        private _snackBar: MatSnackBar,
    ) {}

    ngOnInit(): void {
        this.filter.valueChanges
            .pipe(
                startWith(null),
                switchMap((filter) => this.projectService.findAll(filter)),
            )
            .subscribe({
                next: (projects) => {
                    this.projects = projects;
                    this.isLoading = false;
                },
                error: () => {
                    this._snackBar.open("Failed to load projects", "Dismiss");
                },
            });
    }

    openNewProjectDialog() {
        this._dialog.open(NewProjectDialogComponent);
    }

    getProjectLink(project: Project) {
        if (!project.main) {
            return `/projects/${project.slug}/intro`;
        }
        return `/projects/${project.slug}`;
    }

    formatDate(text: string) {
        return new Date(text).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour12: false,
        });
    }
}
