import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(private router: Router) {}

  onLogin() {
    // Simulate login logic (replace with real backend call)
    if (this.email && this.password) {
      console.log('Login:', { email: this.email, password: this.password });
      this.router.navigate(['/chat']);
    } else {
      alert('Please fill in all fields');
    }
  }
}