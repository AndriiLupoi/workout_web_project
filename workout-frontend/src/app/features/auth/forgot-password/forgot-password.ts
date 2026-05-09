import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: '../login/login.css'
})
export class ForgotPasswordComponent {

  private authService = inject(AuthService);

  form = inject(FormBuilder).group({
    email: ['', [Validators.required, Validators.email]]
  });

  loading = signal(false);
  error = signal('');
  sent = signal(false);

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);

    this.authService.forgotPassword(
      this.form.value.email!
    ).subscribe({
      next: () => {
        this.sent.set(true);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка. Спробуй пізніше.');
        this.loading.set(false);
      }
    });
  }
}
