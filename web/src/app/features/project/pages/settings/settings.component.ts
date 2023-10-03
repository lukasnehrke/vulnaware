import { Component, OnInit } from "@angular/core";
import { filter, switchMap } from "rxjs";
import { FormControl, Validators } from "@angular/forms";
import { AppService } from "../../../../shared/services/app.service";
import { ProjectService } from "../../services/project.service";
import { ProjectMember } from "../../models/member.model";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
    selector: "va-project-settings",
    templateUrl: "./settings.component.html",
    styleUrls: ["./settings.component.scss"],
})
export class SettingsComponent implements OnInit {
    project = "";
    email = new FormControl("", [Validators.email]);
    displayedColumns = ["name", "email", "role", "actions"];
    dataSource: ProjectMember[] = [];

    constructor(
        private snackbar: MatSnackBar,
        private appService: AppService,
        private projectService: ProjectService,
    ) {}

    ngOnInit() {
        this.appService.activeProject$
            .pipe(
                filter((project) => !!project),
                switchMap((project) => {
                    this.project = project!.slug;
                    return this.projectService.findMembers(project!.slug);
                }),
            )
            .subscribe((members) => {
                this.dataSource = members;
            });
    }

    addUser() {
        if (this.email.invalid || !this.project) return;

        this.projectService.addMember(this.project, this.email.value!).subscribe({
            next: (members) => {
                this.dataSource = members;
                this.email.reset();
                this.snackbar.open("Member added.", "Dismiss");
            },
            error: (error) => {
                if (error.status == 404) {
                    this.snackbar.open("User not found.", "Dismiss");
                    return;
                }
                console.error(error);
                this.snackbar.open("Failed to add member.", "Dismiss");
            },
        });
    }

    deleteUser(email: string) {
        this.projectService.removeMember(this.project, email).subscribe({
            next: (members) => {
                this.dataSource = members;
                this.email.reset();
                this.snackbar.open("Member has been removed.", "Dismiss");
            },
            error: (error) => {
                console.error(error);
                this.snackbar.open("Failed to remove member.", "Dismiss");
            },
        });
    }
}
