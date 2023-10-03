import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { authGuard } from "./core/guards/auth.guard";

const routes: Routes = [
    { path: "", redirectTo: "/projects", pathMatch: "full" },
    {
        path: "auth",
        loadChildren: () => import("./features/auth/auth.module").then((mod) => mod.AuthModule),
    },
    {
        path: "profile",
        canActivate: [authGuard],
        loadChildren: () => import("./features/user-profile/user-profile.module").then((mod) => mod.UserProfileModule),
    },
    {
        path: "notifications",
        canActivate: [authGuard],
        loadChildren: () => import("./features/notifications/notifications.module").then((mod) => mod.NotificationsModule),
    },
    {
        path: "projects",
        canActivate: [authGuard],
        loadChildren: () => import("./features/project/project.module").then((mod) => mod.ProjectModule),
    },
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {
            scrollPositionRestoration: "enabled",
            anchorScrolling: "enabled",
        }),
    ],
    exports: [RouterModule],
})
export class AppRouting {}
