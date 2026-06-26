import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  rememberMe = false;
  errorMessage = '';
  isLoginMode = true;

  constructor(private authService: AuthService, private router: Router) {}

  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';
  }

  onSubmit() {
    if (this.isLoginMode) {
      this.authService.login(this.credentials).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
        },
        error: err => {
          this.errorMessage = 'Invalid username or password';
        }
      });
    } else {
      this.authService.register(this.credentials).subscribe({
        next: (res) => {
          // Registration success, login automatically or switch to login mode
          this.errorMessage = '';
          alert('Registered successfully! Logging in...');
          this.isLoginMode = true;
          this.onSubmit(); // Log them in right away
        },
        error: err => {
          this.errorMessage = err.error || 'Failed to register. Username might exist.';
        }
      });
    }
  }
}
