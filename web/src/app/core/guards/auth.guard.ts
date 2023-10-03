import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "../../shared/services/auth.service";

export const authGuard = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated()) {
        // allow the user to access the route
        return true;
    }

    // redirect to the login page
    return router.parseUrl("/auth/login");
};
