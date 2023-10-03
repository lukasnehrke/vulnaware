import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { AuthRoutingModule } from "./auth-routing.module";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";

@NgModule({
    imports: [
        AuthRoutingModule,
        CommonModule,
        ReactiveFormsModule,
        MatSelectModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        AuthRoutingModule,
        MatSnackBarModule,
    ],
    declarations: [LoginComponent, RegisterComponent],
})
export class AuthModule {}
