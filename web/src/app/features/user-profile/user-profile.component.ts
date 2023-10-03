import { Component } from "@angular/core";
import { FormControl } from "@angular/forms";
import { UserService } from "../../shared/services/user.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
    selector: "user-profile",
    templateUrl: "./user-profile.component.html",
    styleUrls: ["./user-profile.component.scss"],
})
export class UserProfileComponent {
    apiKey = new FormControl({ value: "", disabled: true });

    constructor(
        private usersService: UserService,
        private snackbar: MatSnackBar,
    ) {}

    generateKey() {
        this.usersService.generateApiKey().subscribe({
            next: (response) => {
                this.apiKey.setValue(response.apiKey);
            },
            error: () => {
                this.snackbar.open("Failed to generate API-Key", "Dismiss");
            },
        });
    }
}
