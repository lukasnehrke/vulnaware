import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";

import { AppComponent } from "./app.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AppRouting } from "./app-routing";
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { NavItemComponent } from "./components/nav-item/nav-item.component";
import { AuthInterceptor } from "./core/interceptors/auth.interceptor";
import { NavbarComponent } from "./components/navbar/navbar.component";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatListModule } from "@angular/material/list";
import { MatIconModule } from "@angular/material/icon";
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from "@angular/material/snack-bar";

@NgModule({
    declarations: [AppComponent, NavItemComponent],
    imports: [BrowserModule, AppRouting, BrowserAnimationsModule, HttpClientModule, NavbarComponent, MatSidenavModule, MatListModule, MatIconModule],
    providers: [
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { horizontalPosition: "center", verticalPosition: "top", duration: 2000 } },
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}
