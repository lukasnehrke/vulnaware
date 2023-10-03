import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { UserProfileComponent } from "./user-profile.component";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { ReactiveFormsModule } from "@angular/forms";
import { UserProfileRoutingModule } from "./user-profile-routing.module";
import { MatSnackBarModule } from "@angular/material/snack-bar";

@NgModule({
    imports: [
        UserProfileRoutingModule,
        MatSnackBarModule,
        CommonModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        ReactiveFormsModule,
        MatSnackBarModule,
    ],
    declarations: [UserProfileComponent],
})
export class UserProfileModule {}
