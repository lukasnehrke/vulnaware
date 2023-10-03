import { Component, EventEmitter, Output } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: "app-vuln-edit",
    templateUrl: "./vuln-edit.component.html",
    styleUrls: ["./vuln-edit.component.scss"],
})
export class VulnEditComponent {
    @Output() update = new EventEmitter<{ status: string; comment?: string }>();

    statuses: string[] = ["OPEN", "RESOLVED", "IGNORED"];

    form = new FormGroup({
        status: new FormControl("OPEN", Validators.required),
        comment: new FormControl(""),
    });

    constructor(private dialog: MatDialogRef<VulnEditComponent>) {}

    onSave() {
        if (this.form.invalid) return;
        this.update.emit({ status: this.form.value.status!, comment: this.form.value.comment ?? "" });
        this.dialog.close();
    }
}
