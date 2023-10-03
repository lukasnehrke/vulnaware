import { Component } from "@angular/core";
import { UploadDialogComponent } from "../../features/bom/components/upload-dialog/upload-dialog.component";
import { MatDialog } from "@angular/material/dialog";

@Component({
    selector: "va-project-intro",
    templateUrl: "./intro.component.html",
    styleUrls: ["./intro.component.scss"],
})
export class IntroComponent {
    constructor(private _dialog: MatDialog) {}

    openUploadDialog() {
        this._dialog.open(UploadDialogComponent);
    }
}
