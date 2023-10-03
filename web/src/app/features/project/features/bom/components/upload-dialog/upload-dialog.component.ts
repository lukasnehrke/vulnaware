import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MatStepper } from "@angular/material/stepper";
import { BomService } from "../../../../../../shared/services/bom.service";
import { AppService } from "../../../../../../shared/services/app.service";
import { Project } from "../../../../../../shared/models/project.model";
import { Subscription } from "rxjs";

@Component({
    selector: "va-project-bom-upload-dialog",
    templateUrl: "./upload-dialog.component.html",
    styleUrls: ["./upload-dialog.component.scss"],
})
export class UploadDialogComponent implements OnInit, OnDestroy {
    project: Project | null;

    file: File | null = null;
    isUploading = false;
    error: string | null = null;

    detailsForm = new FormGroup({
        tag: new FormControl("", [Validators.required]),
        description: new FormControl("", []),
    });

    @ViewChild("stepper")
    stepper: MatStepper;

    _sub: Subscription;

    constructor(
        private dialog: MatDialogRef<UploadDialogComponent>,
        private router: Router,
        private appService: AppService,
        private bomService: BomService,
    ) {}

    ngOnInit(): void {
        this._sub = this.appService.activeProject$.subscribe((project) => (this.project = project));
    }

    ngOnDestroy(): void {
        this._sub.unsubscribe();
    }

    onFileChange(event: any) {
        this.file = event.target.files[0];
    }

    upload() {
        const projectSlug = this.project?.slug;
        const tag = this.detailsForm.get("tag")?.value;
        const description = this.detailsForm.get("description")?.value || "";
        if (!projectSlug || !tag || !this.file) return;

        this.isUploading = true;
        this.stepper.next();

        this.bomService.upload(projectSlug, tag, description, this.file).subscribe({
            next: () => {
                this.dialog.close();
                window.location.href = `/projects/${projectSlug}/bom?tag=${tag}`;
            },
            error: ({ error }) => {
                this.error = error.detail;
            },
        });
    }
}
