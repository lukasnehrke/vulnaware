import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { ProjectService } from "../../../../shared/services/projects.service";
import { first } from "rxjs";
import { FormControl, Validators } from "@angular/forms";

@Component({
    selector: "va-project-new-project-dialog",
    templateUrl: "./new-project-dialog.component.html",
    styleUrls: ["./new-project-dialog.component.scss"],
})
export class NewProjectDialogComponent {
    name = new FormControl("", [Validators.required, Validators.minLength(3)]);

    constructor(
        private _dialog: MatDialogRef<NewProjectDialogComponent>,
        private _router: Router,
        private _projectService: ProjectService,
    ) {}

    get error() {
        if (this.name.hasError("required")) return "Name is required";
        if (this.name.hasError("minlength")) return "Name must be at least 3 characters";
        return "Name is invalid";
    }

    createProject() {
        if (!this.name.valid || !this.name.value) return;

        const slug = this.slugify(this.name.value) + "-" + Math.floor(Math.random() * 1000000);
        this._projectService
            .create(this.name.value, slug)
            .pipe(first())
            .subscribe((project) => {
                this._dialog.close();
                return this._router.navigate(["projects/", project.slug]);
            });
    }

    private slugify(text: string): string {
        return text
            .toLowerCase()
            .replace(/ /g, "-")
            .replace(/[^\w-]+/g, "");
    }
}
