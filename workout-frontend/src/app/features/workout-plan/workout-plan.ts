import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorkoutPlanService, WorkoutPlan, WorkoutDay } from './workout-plan.service';

@Component({
  selector: 'app-workout-plan',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workout-plan.html',
  styleUrl: './workout-plan.css'
})
export class WorkoutPlanComponent implements OnInit {

  private service = inject(WorkoutPlanService);

  plans      = signal<WorkoutPlan[]>([]);
  activePlan = signal<WorkoutPlan | null>(null);
  activeWeek = signal<number>(1);
  loading    = signal(false);
  generating = signal(false);
  error      = signal<string | null>(null);

  // унікальні тижні активного плану
  weeks = computed(() => {
    const plan = this.activePlan();
    if (!plan) return [];
    return [...new Set(plan.days.map(d => d.weekNumber))].sort((a, b) => a - b);
  });

  // дні поточного тижня
  currentDays = computed(() => {
    const plan = this.activePlan();
    if (!plan) return [];
    return plan.days.filter(d => d.weekNumber === this.activeWeek());
  });

  ngOnInit(): void { this.loadPlans(); }

  loadPlans(): void {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (data) => {
        this.plans.set(data);
        const active = data.find(p => p.status === 'ACTIVE') ?? data[0] ?? null;
        this.activePlan.set(active);
        this.activeWeek.set(1);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка завантаження планів');
        this.loading.set(false);
      }
    });
  }

  generate(): void {
    this.generating.set(true);
    this.error.set(null);
    this.service.generate().subscribe({
      next: (plan) => {
        this.activePlan.set(plan);
        this.activeWeek.set(1);
        this.plans.update(list => [plan, ...list]);
        this.generating.set(false);
      },
      error: () => {
        this.error.set('Помилка генерації плану. Переконайся що профіль заповнений.');
        this.generating.set(false);
      }
    });
  }

  selectPlan(plan: WorkoutPlan): void {
    this.activePlan.set(plan);
    this.activeWeek.set(1);
  }

  selectWeek(week: number): void {
    this.activeWeek.set(week);
  }

  goalLabel(goal: string): string {
    const map: Record<string, string> = {
      MASS:             'Набір маси',
      LOSS:             'Схуднення',
      ENDURANCE:        'Витривалість',
      STRENGTH:         'Сила',
      STRENGTH_AND_MASS:'Сила + Маса'
    };
    return map[goal] ?? goal;
  }

  planTypeLabel(planType: string): string {
    const map: Record<string, string> = {
      HYPERTROPHY:          'Гіпертрофія',
      STRENGTH:             'Сила',
      STRENGTH_HYPERTROPHY: 'Сила + Маса',
      FAT_LOSS:             'Спалення жиру',
      ENDURANCE:            'Витривалість'
    };
    return map[planType] ?? planType;
  }

  intensityLabel(intensity: string): string {
    const map: Record<string, string> = {
      FULL_BODY: 'Full Body',
      HEAVY:     'Важкий',
      MEDIUM:    'Середній',
      SETS:      'Сети'
    };
    return map[intensity] ?? intensity;
  }
}
