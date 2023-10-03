import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { BomComponent } from "./pages/bom.component";

const routes: Routes = [
    {
        path: "",
        component: BomComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class BomRoutingModule {}
