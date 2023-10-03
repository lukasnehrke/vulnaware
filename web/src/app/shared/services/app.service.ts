import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { NavItem } from "../models/app.model";
import { Project } from "../models/project.model";
import { Bom } from "../models/bom.model";

@Injectable({ providedIn: "root" })
export class AppService {
    private _activeProject = new BehaviorSubject<Project | null>(null);
    private _activeBom = new BehaviorSubject<Bom | null>(null);
    private navItems = new BehaviorSubject<NavItem[]>([]);
    private showSideBar = new BehaviorSubject<boolean>(true);

    constructor() {}

    get activeProject$(): Observable<Project | null> {
        return this._activeProject.asObservable();
    }

    get activeBom$(): Observable<Bom | null> {
        return this._activeBom.asObservable();
    }

    get showSideBar$(): Observable<boolean> {
        return this.showSideBar.asObservable();
    }

    toggleSideBar() {
        this.showSideBar.next(!this.showSideBar.value);
    }

    getNavItems(): Observable<NavItem[]> {
        return this.navItems.asObservable();
    }

    setNavItems(items: NavItem[]) {
        this.navItems.next(items);
    }

    getActiveProject(): Observable<Project | null> {
        return this._activeProject.asObservable();
    }

    setActiveProject(project: Project | null) {
        this._activeProject.next(project);
    }

    getActiveBom(): Observable<Bom | null> {
        return this._activeBom.asObservable();
    }

    setActiveBom(bom: Bom | null) {
        this._activeBom.next(bom);
    }
}
