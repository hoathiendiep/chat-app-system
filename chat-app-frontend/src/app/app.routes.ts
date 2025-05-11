import { Routes } from '@angular/router';
import { MainComponent } from './pages/main/main.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';

export const routes: Routes = [
  {
    path: '',
    component: MainComponent
  },
  // { path: 'login', component: LoginComponent },
  // { path: 'register', component: RegisterComponent },
];
