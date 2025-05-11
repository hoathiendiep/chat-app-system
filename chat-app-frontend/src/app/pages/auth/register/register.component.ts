import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  imports: [FormsModule],
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  username: string = '';
  email: string = '';
  password: string = '';

  constructor(private router: Router) {}

  onRegister() {
    // Simulate registration logic (replace with real backend call)
    if (this.username && this.email && this.password) {
      console.log('Register:', { username: this.username, email: this.email, password: this.password });
      this.router.navigate(['/login']);
    } else {
      alert('Please fill in all fields');
    }
  }
}