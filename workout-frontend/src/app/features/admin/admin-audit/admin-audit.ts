import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface AuditLog {
  id:          string;
  actorId:     string;
  actorEmail:  string;
  actorRole:   string;
  action:      string;
  targetId:    string;
  targetType:  string;
  details:     string;
  createdAt:   string;
}

interface PageResponse<T> {
  content:       T[];
  currentPage:   number;
  totalPages:    number;
  totalElements: number;
  pageSize:      number;
  first:         boolean;
  last:          boolean;
}

const ACTION_LABELS: Record<string, string> = {
  USER_CREATED:              'Реєстрація',
  USER_DELETED:              'Видалення юзера',
  ROLE_CHANGED:              'Зміна ролі',
  PLAN_GENERATED:            'Генерація плану',
  PLAN_DELETED:              'Видалення плану',
  EXERCISE_CREATED:          'Створення вправи',
  EXERCISE_UPDATED:          'Редагування вправи',
  EXERCISE_DELETED:          'Видалення вправи',
  WORKOUT_LOGGED:            'Тренування збережено',
  PASSWORD_RESET_REQUESTED:  'Скидання пароля',
  PASSWORD_RESET_COMPLETED:  'Пароль змінено',
  PROFILE_UPDATED:           'Оновлення профілю',
};

const ACTION_ICONS: Record<string, string> = {
  USER_CREATED:              '👤',
  USER_DELETED:              '🗑',
  ROLE_CHANGED:              '🛡️',
  PLAN_GENERATED:            '📋',
  PLAN_DELETED:              '🗑',
  EXERCISE_CREATED:          '➕',
  EXERCISE_UPDATED:          '✏️',
  EXERCISE_DELETED:          '🗑',
  WORKOUT_LOGGED:            '💪',
  PASSWORD_RESET_REQUESTED:  '🔑',
  PASSWORD_RESET_COMPLETED:  '✅',
  PROFILE_UPDATED:           '👤',
};

const ACTION_COLORS: Record<string, string> = {
  USER_CREATED:              'audit-green',
  USER_DELETED:              'audit-red',
  ROLE_CHANGED:              'audit-yellow',
  PLAN_GENERATED:            'audit-blue',
  PLAN_DELETED:              'audit-red',
  EXERCISE_CREATED:          'audit-green',
  EXERCISE_UPDATED:          'audit-blue',
  EXERCISE_DELETED:          'audit-red',
  WORKOUT_LOGGED:            'audit-purple',
  PASSWORD_RESET_REQUESTED:  'audit-yellow',
  PASSWORD_RESET_COMPLETED:  'audit-green',
  PROFILE_UPDATED:           'audit-blue',
};

@Component({
  selector: 'app-admin-audit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-audit.html',
  styleUrl: './admin-audit.css'
})
export class AdminAuditComponent implements OnInit {
  private http = inject(HttpClient);

  logs          = signal<AuditLog[]>([]);
  loading       = signal(false);
  error         = signal<string | null>(null);
  currentPage   = signal(0);
  totalPages    = signal(0);
  totalElements = signal(0);
  pageSize      = signal(50);

  // Фільтри
  selectedAction = '';
  dateFrom       = '';
  dateTo         = '';

  readonly allActions = Object.keys(ACTION_LABELS);
  readonly Math = Math;

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.loading.set(true);
    this.error.set(null);

    let url = `/api/v1/admin/audit?page=${page}&size=${this.pageSize()}`;
    if (this.selectedAction) url += `&action=${this.selectedAction}`;
    if (this.dateFrom && this.dateTo) url += `&from=${this.dateFrom}&to=${this.dateTo}`;

    this.http.get<PageResponse<AuditLog>>(url).subscribe({
      next: (res) => {
        this.logs.set(res.content);
        this.currentPage.set(res.currentPage);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка завантаження журналу');
        this.loading.set(false);
      }
    });
  }

  applyFilters(): void {
    this.loadPage(0);
  }

  clearFilters(): void {
    this.selectedAction = '';
    this.dateFrom       = '';
    this.dateTo         = '';
    this.loadPage(0);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) return;
    this.loadPage(page);
  }

  getPageNumbers(): (number | '...')[] {
    const total   = this.totalPages();
    const current = this.currentPage();
    if (total <= 7) return Array.from({ length: total }, (_, i) => i);

    const pages: (number | '...')[] = [0];
    if (current > 3) pages.push('...');
    const start = Math.max(1, current - 2);
    const end   = Math.min(total - 2, current + 2);
    for (let i = start; i <= end; i++) pages.push(i);
    if (current < total - 4) pages.push('...');
    pages.push(total - 1);
    return pages;
  }

  actionLabel(action: string): string {
    return ACTION_LABELS[action] ?? action;
  }

  actionIcon(action: string): string {
    return ACTION_ICONS[action] ?? '📝';
  }

  actionColor(action: string): string {
    return ACTION_COLORS[action] ?? 'audit-blue';
  }

  formatDate(dt: string): string {
    if (!dt) return '—';
    const d = new Date(dt);
    return d.toLocaleString('uk-UA', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }
}
