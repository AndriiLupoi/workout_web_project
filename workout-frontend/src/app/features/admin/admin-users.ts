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

interface Stats {
  totalUsers:  number;
  totalPlans:  number;
  totalAdmins: number;
}

interface UserProfile {
  goal:              string;
  level:             string;
  planType:          string;
  workoutsPerWeek:   number;
  currentWeight:     number | null;
  targetWeight:      number | null;
  height:            number | null;
  age:               number | null;
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
  styleUrls: ['./admin-users.css', './admin-users-modal.css']
})
export class AdminUsersComponent implements OnInit {
  private http        = inject(HttpClient);
  private authService = inject(AuthService);

  users     = signal<AdminUser[]>([]);
  stats     = signal<Stats | null>(null);
  loading   = signal(false);
  error     = signal<string | null>(null);
  actionMsg = signal<string | null>(null);

  // Modal state
  selectedUser    = signal<AdminUser | null>(null);
  selectedProfile = signal<UserProfile | null>(null);
  selectedPlans   = signal<WorkoutPlan[]>([]);
  modalLoading    = signal(false);
  modalError      = signal<string | null>(null);
  activeTab       = signal<'profile' | 'plans'>('profile');
  expandedPlanId  = signal<string | null>(null);

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
        if (this.selectedUser()?.id === userId) this.closeModal();
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

  // Labels
  goalLabel(v: string): string {
    return ({ MASS: 'Набір маси', LOSS: 'Схуднення', ENDURANCE: 'Витривалість', STRENGTH: 'Сила', STRENGTH_AND_MASS: 'Сила + Маса' } as any)[v] ?? v;
  }
  levelLabel(v: string): string {
    return ({ BEGINNER: 'Початківець', RETURNING: 'Після паузи', INTERMEDIATE: 'Середній', ADVANCED: 'Просунутий' } as any)[v] ?? v;
  }
  planTypeLabel(v: string): string {
    return ({ HYPERTROPHY: 'Гіпертрофія', STRENGTH: 'Сила', STRENGTH_HYPERTROPHY: 'Сила + Маса', FAT_LOSS: 'Спалення жиру', ENDURANCE: 'Витривалість' } as any)[v] ?? v;
  }
  intensityLabel(v: string): string {
    return ({ FULL_BODY: 'Full Body', HEAVY: 'Важкий', MEDIUM: 'Середній', SETS: 'Сети' } as any)[v] ?? v;
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

  deleteUserPlan(userId: string, planId: string): void {
    if (!confirm('Видалити цей план?')) return;
    this.http.delete(`/api/v1/admin/users/${userId}/plans/${planId}`).subscribe({
      next: () => {
        this.selectedPlans.update(list => list.filter(p => p.id !== planId));
        this.stats.update(s => s ? { ...s, totalPlans: s.totalPlans - 1 } : s);
        this.showAction('План видалено');
      },
      error: () => this.error.set('Помилка видалення плану')
    });
  }
}
