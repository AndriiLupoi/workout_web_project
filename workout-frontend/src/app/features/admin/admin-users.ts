import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth';
import { forkJoin } from 'rxjs';

interface AdminUser {
  id:        string;
  email:     string;
  firstName: string;
  lastName:  string;
  role:      string;
  createdAt: string;
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

interface Stats {
  totalUsers:  number;
  totalPlans:  number;
  totalAdmins: number;
}

interface UserProfile {
  goal:               string;
  level:              string;
  planType:           string;
  workoutsPerWeek:    number;
  currentWeight:      number | null;
  targetWeight:       number | null;
  height:             number | null;
  age:                number | null;
  availableEquipment: string[];
}

interface WorkoutExercise {
  exerciseName:  string;
  sets:          number;
  reps:          string;
  restSeconds:   number;
  plannedWeight: number | null;
}

interface WorkoutDay {
  weekNumber:    number;
  dayNumber:     number;
  focus:         string;
  intensityType: string;
  exercises:     WorkoutExercise[];
}

interface WorkoutPlan {
  id:            string;
  title:         string;
  goal:          string;
  planType:      string;
  durationWeeks: number;
  status:        string;
  days:          WorkoutDay[];
  createdAt:     string;
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-users.html',
  styleUrls: ['./admin-users.css', './admin-users-modal.css', 'admin-users-pagination.css']
})
export class AdminUsersComponent implements OnInit {
  private http        = inject(HttpClient);
  private authService = inject(AuthService);

  // ── Table state ──
  users         = signal<AdminUser[]>([]);
  stats         = signal<Stats | null>(null);
  loading       = signal(false);
  error         = signal<string | null>(null);
  actionMsg     = signal<string | null>(null);

  // ── Pagination ──
  currentPage   = signal(0);
  totalPages    = signal(0);
  totalElements = signal(0);
  pageSize      = signal(20);

  // ── Modal state ──
  selectedUser    = signal<AdminUser | null>(null);
  selectedProfile = signal<UserProfile | null>(null);
  selectedPlans   = signal<WorkoutPlan[]>([]);
  modalLoading    = signal(false);
  modalError      = signal<string | null>(null);
  activeTab       = signal<'profile' | 'plans'>('profile');
  expandedPlanId  = signal<string | null>(null);

  isOwner = () => this.authService.isOwner();

  ngOnInit(): void {
    this.loadPage(0);
    this.loadStats();
  }

  loadStats(): void {
    this.http.get<Stats>('/api/v1/admin/stats').subscribe({
      next: (stats) => this.stats.set(stats),
      error: () => {}
    });
  }

  loadPage(page: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<PageResponse<AdminUser>>(
      `/api/v1/admin/users?page=${page}&size=${this.pageSize()}`
    ).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.currentPage.set(res.currentPage);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка завантаження');
        this.loading.set(false);
      }
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) return;
    this.loadPage(page);
  }

  // Генеруємо масив номерів сторінок для відображення (макс 7 кнопок)
  getPageNumbers(): (number | '...')[] {
    const total = this.totalPages();
    const current = this.currentPage();

    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i);
    }

    const pages: (number | '...')[] = [];

    // Завжди показуємо першу
    pages.push(0);

    if (current > 3) pages.push('...');

    // Сусідні сторінки навколо поточної
    const start = Math.max(1, current - 2);
    const end   = Math.min(total - 2, current + 2);
    for (let i = start; i <= end; i++) pages.push(i);

    if (current < total - 4) pages.push('...');

    // Завжди показуємо останню
    pages.push(total - 1);

    return pages;
  }

  // ── Modal ──
  openUserModal(user: AdminUser): void {
    if (!this.isOwner()) return;
    this.selectedUser.set(user);
    this.selectedProfile.set(null);
    this.selectedPlans.set([]);
    this.modalLoading.set(true);
    this.modalError.set(null);
    this.activeTab.set('profile');
    this.expandedPlanId.set(null);

    forkJoin({
      profile: this.http.get<UserProfile>(`/api/v1/admin/users/${user.id}/profile`),
      plans:   this.http.get<WorkoutPlan[]>(`/api/v1/admin/users/${user.id}/plans`)
    }).subscribe({
      next: ({ profile, plans }) => {
        this.selectedProfile.set(profile);
        this.selectedPlans.set(plans);
        this.modalLoading.set(false);
      },
      error: () => {
        this.modalError.set('Не вдалося завантажити дані юзера');
        this.modalLoading.set(false);
      }
    });
  }

  closeModal(): void {
    this.selectedUser.set(null);
    this.selectedProfile.set(null);
    this.selectedPlans.set([]);
    this.expandedPlanId.set(null);
  }

  togglePlan(planId: string): void {
    this.expandedPlanId.set(this.expandedPlanId() === planId ? null : planId);
  }

  assignRole(userId: string, role: string): void {
    this.http.put<AdminUser>(`/api/v1/admin/roles/${userId}?role=${role}`, {}).subscribe({
      next: (updated) => {
        this.users.update(list =>
          list.map(u => u.id === userId ? { ...u, role: updated.role } : u)
        );
        if (this.selectedUser()?.id === userId) {
          this.selectedUser.update(u => u ? { ...u, role: updated.role } : u);
        }
        this.loadStats();
        this.showAction(`Роль змінено на ${role}`);
      },
      error: () => this.error.set('Помилка зміни ролі')
    });
  }

  deleteUser(userId: string, email: string): void {
    if (!confirm(`Видалити користувача ${email}?`)) return;
    this.http.delete(`/api/v1/admin/users/${userId}`).subscribe({
      next: () => {
        // Якщо після видалення сторінка порожня — переходимо на попередню
        const newTotal = this.totalElements() - 1;
        const maxPage  = Math.max(0, Math.ceil(newTotal / this.pageSize()) - 1);
        const targetPage = Math.min(this.currentPage(), maxPage);
        this.loadPage(targetPage);
        this.loadStats();
        if (this.selectedUser()?.id === userId) this.closeModal();
        this.showAction('Користувача видалено');
      },
      error: () => this.error.set('Помилка видалення')
    });
  }

  deleteUserPlan(userId: string, planId: string): void {
    if (!confirm('Видалити цей план?')) return;
    this.http.delete(`/api/v1/admin/users/${userId}/plans/${planId}`).subscribe({
      next: () => {
        this.selectedPlans.update(list => list.filter(p => p.id !== planId));
        this.loadStats();
        this.showAction('План видалено');
      },
      error: () => this.error.set('Помилка видалення плану')
    });
  }

  private showAction(msg: string): void {
    this.actionMsg.set(msg);
    setTimeout(() => this.actionMsg.set(null), 3000);
  }

  getRoleBadgeClass(role: string): string {
    return ({ OWNER: 'badge-owner', ADMIN: 'badge-admin', USER: 'badge-user' } as any)[role] ?? 'badge-user';
  }

  goalLabel(v: string): string {
    return ({ MASS: 'Набір маси', LOSS: 'Схуднення', ENDURANCE: 'Витривалість', STRENGTH: 'Сила', STRENGTH_AND_MASS: 'Сила + Маса' } as any)[v] ?? v;
  }
  levelLabel(v: string): string {
    return ({ BEGINNER: 'Початківець', RETURNING: 'Після паузи', INTERMEDIATE: 'Середній', ADVANCED: 'Просунутий' } as any)[v] ?? v;
  }
  planTypeLabel(v: string): string {
    return ({ HYPERTROPHY: 'Гіпертрофія', STRENGTH: 'Сила', STRENGTH_HYPERTROPHY: 'Сила + Маса', FAT_LOSS: 'Спалення жиру', ENDURANCE: 'Витривалість' } as any)[v] ?? v;
  }
  equipmentLabel(v: string): string {
    return ({ BARBELL: 'Штанга', DUMBBELL: 'Гантелі', MACHINE: 'Тренажери', BODYWEIGHT: 'Вага тіла', PULL_UP: 'Турнік', CABLE: 'Блоки/кабелі', BENCH: 'Лава' } as any)[v] ?? v;
  }

  getWeekNumbers(plan: WorkoutPlan): number[] {
    return [...new Set(plan.days.map(d => d.weekNumber))].sort((a, b) => a - b);
  }

  getDaysForWeek(plan: WorkoutPlan, week: number): WorkoutDay[] {
    return plan.days.filter(d => d.weekNumber === week);
  }

  protected readonly Math = Math;
}
