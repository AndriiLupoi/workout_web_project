import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth'

interface AdminUser {
  id:        string;
  email:     string;
  firstName: string;
  lastName:  string;
  role:      string;
  createdAt: string;
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-users.html',
  styleUrl:    './admin-users.css'
})
export class AdminUsersComponent implements OnInit {
  private http        = inject(HttpClient);
  private authService = inject(AuthService);

  users      = signal<AdminUser[]>([]);
  loading    = signal(false);
  error      = signal<string | null>(null);
  actionMsg  = signal<string | null>(null);

  isOwner = () => this.authService.isOwner();

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.http.get<AdminUser[]>('/api/v1/admin/users').subscribe({
      next: (data) => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка завантаження користувачів');
        this.loading.set(false);
      }
    });
  }

  // Призначити роль — тільки OWNER
  assignRole(userId: string, role: string): void {
    this.http.put<AdminUser>(`/api/v1/admin/roles/${userId}?role=${role}`, {}).subscribe({
      next: (updated) => {
        this.users.update(list =>
          list.map(u => u.id === userId ? { ...u, role: updated.role } : u)
        );
        this.actionMsg.set(`Роль змінено на ${role}`);
        setTimeout(() => this.actionMsg.set(null), 3000);
      },
      error: () => this.error.set('Помилка зміни ролі')
    });
  }

  // Видалити юзера — тільки OWNER
  deleteUser(userId: string, email: string): void {
    if (!confirm(`Видалити користувача ${email}? Цю дію не можна скасувати.`)) return;

    this.http.delete(`/api/v1/admin/users/${userId}`).subscribe({
      next: () => {
        this.users.update(list => list.filter(u => u.id !== userId));
        this.actionMsg.set('Користувача видалено');
        setTimeout(() => this.actionMsg.set(null), 3000);
      },
      error: () => this.error.set('Помилка видалення')
    });
  }

  getRoleBadgeClass(role: string): string {
    return {
      OWNER: 'badge-owner',
      ADMIN: 'badge-admin',
      USER:  'badge-user'
    }[role] ?? 'badge-user';
  }
}
