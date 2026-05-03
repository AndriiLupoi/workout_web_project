import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth'
import {forkJoin} from 'rxjs';

interface AdminUser {
  id:        string;
  email:     string;
  firstName: string;
  lastName:  string;
  role:      string;
  createdAt: string;
}

interface Stats {
  totalUsers:  number;
  totalPlans:  number;
  totalAdmins: number;
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

  users     = signal<AdminUser[]>([]);
  stats     = signal<Stats | null>(null);
  loading   = signal(false);
  error     = signal<string | null>(null);
  actionMsg = signal<string | null>(null);

  isOwner = () => this.authService.isOwner();

  ngOnInit(): void {
    this.loading.set(true);
    forkJoin({
      users: this.http.get<AdminUser[]>('/api/v1/admin/users'),
      stats: this.http.get<Stats>('/api/v1/admin/stats')
    }).subscribe({
      next: ({ users, stats }) => {
        this.users.set(users);
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка завантаження');
        this.loading.set(false);
      }
    });
  }

  assignRole(userId: string, role: string): void {
    this.http.put<AdminUser>(`/api/v1/admin/roles/${userId}?role=${role}`, {}).subscribe({
      next: (updated) => {
        this.users.update(list =>
          list.map(u => u.id === userId ? { ...u, role: updated.role } : u)
        );
        // Оновлюємо лічильник адмінів
        const adminCount = this.users().filter(u => u.role === 'ADMIN').length;
        this.stats.update(s => s ? { ...s, totalAdmins: adminCount } : s);
        this.showAction(`Роль змінено на ${role}`);
      },
      error: () => this.error.set('Помилка зміни ролі')
    });
  }

  deleteUser(userId: string, email: string): void {
    if (!confirm(`Видалити користувача ${email}?`)) return;
    this.http.delete(`/api/v1/admin/users/${userId}`).subscribe({
      next: () => {
        this.users.update(list => list.filter(u => u.id !== userId));
        this.stats.update(s => s ? { ...s, totalUsers: s.totalUsers - 1 } : s);
        this.showAction('Користувача видалено');
      },
      error: () => this.error.set('Помилка видалення')
    });
  }

  private showAction(msg: string): void {
    this.actionMsg.set(msg);
    setTimeout(() => this.actionMsg.set(null), 3000);
  }

  getRoleBadgeClass(role: string): string {
    return ({ OWNER: 'badge-owner', ADMIN: 'badge-admin', USER: 'badge-user' } as any)[role] ?? 'badge-user';
  }
}

