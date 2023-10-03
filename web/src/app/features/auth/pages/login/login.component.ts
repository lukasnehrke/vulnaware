import { Component } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AuthService } from "../../../../shared/services/auth.service";

@Component({
    selector: "va-auth-login",
    templateUrl: "./login.component.html",
    styleUrls: ["./login.component.scss"],
})
export class LoginComponent {
    form = new FormGroup({
        email: new FormControl("", [Validators.required, Validators.email]),
        password: new FormControl("", [Validators.required]),
    });

    constructor(
        private _router: Router,
        private _authService: AuthService,
        private _snackbar: MatSnackBar,
    ) {}

    onSubmit() {
        const email = this.form.get("email")?.value;
        const password = this.form.get("password")?.value;
        if (!email || !password) return;

        this._authService.login(email, password).subscribe({
            next: (res) => {
                this._authService.setToken(res.jwt);
                this._snackbar.open("Login successful", "Dismiss");
                return this._router.navigate(["/"]);
            },
            error: ({ error }) => {
                if (error.detail === "Bad credentials") {
                    this._snackbar.open("Invalid Email/Password", "Dismiss");
                } else {
                    this._snackbar.open("Authentication failed", "Dismiss");
                }
                this.form.controls["password"].setValue("");
            },
        });
    }
}
