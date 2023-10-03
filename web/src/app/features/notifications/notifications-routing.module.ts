import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";

import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { DetailsComponent } from "./pages/details/details.component";

const routes: Routes = [
    { path: "", component: DashboardComponent },
    { path: ":id", component: DetailsComponent },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class NotificationsRoutingModule {}
