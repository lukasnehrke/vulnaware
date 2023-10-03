import { Component } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";
import { AuthService } from "../../../../shared/services/auth.service";

@Component({
    selector: "va-auth-register",
    templateUrl: "./register.component.html",
    styleUrls: ["./register.component.scss"],
})
export class RegisterComponent {
    hidePassword = true;
    form = new FormGroup({
        name: new FormControl("", [Validators.required]),
        email: new FormControl("", [Validators.required, Validators.email]),
        password: new FormControl("", [Validators.required]),
    });

    constructor(
        private _router: Router,
        private _authService: AuthService,
        private _snackbar: MatSnackBar,
    ) {}

    onSubmit() {
        const name = this.form.get("name")?.value;
        const email = this.form.get("email")?.value;
        const password = this.form.get("password")?.value;
        if (!name || !email || !password) return;

        this._authService.register(name, email, password).subscribe({
            next: (res) => {
                this._authService.setToken(res.jwt);
                this._snackbar.open("Registration successful", "Dismiss");
                return this._router.navigate(["/"]);
            },
            error: ({ error }) => {
                if (error.title) {
                    this._snackbar.open(error.title, "Dismiss");
                } else {
                    this._snackbar.open("Registration failed", "Dismiss");
                }
            },
        });
    }
}
