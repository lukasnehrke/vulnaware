import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";

import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { ProjectComponent } from "./pages/project.component";
import { IntroComponent } from "./pages/intro/intro.component";
import { IssuesComponent } from "./pages/issues/issues.component";
import { IssueDetailsComponent } from "./pages/issue-details/issue-details.component";
import { ActivitiesComponent } from "./pages/activities/activities.component";
import { SettingsComponent } from "./pages/settings/settings.component";

const routes: Routes = [
    {
        path: "",
        component: DashboardComponent,
    },
    {
        path: ":slug",
        component: ProjectComponent,
        children: [
            {
                path: "",
                redirectTo: "issues",
                pathMatch: "full",
            },
            {
                path: "intro",
                component: IntroComponent,
            },
            {
                path: "issues",
                component: IssuesComponent,
            },
            {
                path: "issues/:vulnId",
                component: IssueDetailsComponent,
            },
            {
                path: "bom",
                loadChildren: () => import("./features/bom/bom.module").then((m) => m.BomModule),
            },
            {
                path: "activity",
                component: ActivitiesComponent,
            },
            {
                path: "settings",
                component: SettingsComponent,
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ProjectRoutingModule {}
