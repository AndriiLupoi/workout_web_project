import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { Eye, EyeOff, LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    LucideAngularModule
  ],
  templateUrl: './reset-password.html',
  styleUrls: [ './reset-password.css', '../login/login.css']
})
export class ResetPasswordComponent implements OnInit {

  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);

  readonly Eye = Eye;
  readonly EyeOff = EyeOff;

  form = inject(FormBuilder).group({
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  loading = signal(false);
  error = signal('');
  done = signal(false);
  showPassword = signal(false);
  token = '';

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'] ?? '';
  }

  onSubmit(): void {
    if (this.form.invalid || !this.token) return;

    this.loading.set(true);

    this.authService.resetPassword(
      this.token,
      this.form.value.password!
    ).subscribe({
      next: () => {
        this.done.set(true);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(
          e.error?.message || 'Невалідний або прострочений токен'
        );
        this.loading.set(false);
      }
    });
  }
}
