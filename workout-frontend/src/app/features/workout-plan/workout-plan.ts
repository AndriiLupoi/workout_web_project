import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WorkoutPlanService, WorkoutPlan, WorkoutDay, WorkoutExercise } from './workout-plan.service';
import {
  WorkoutLogResponse,
  WorkoutLogService,
  PlanProgressResponse
} from '../../core/services/workout-log/workout-log.service';

interface LiveExercise extends WorkoutExercise {
  liveWeight: number | null;
  previousWeight: number | null;
  feltEasy: boolean | null;
}

@Component({
  selector: 'app-workout-plan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './workout-plan.html',
  styleUrls: ['./workout-plan.css', './workout-plan-live-mode.css']
})
export class WorkoutPlanComponent implements OnInit {

  private service    = inject(WorkoutPlanService);
  private logService = inject(WorkoutLogService);

  plans       = signal<WorkoutPlan[]>([]);
  activePlan  = signal<WorkoutPlan | null>(null);
  activeWeek  = signal<number>(1);
  loading     = signal(false);
  generating  = signal(false);
  saving      = signal(false);
  error       = signal<string | null>(null);
  saveSuccess = signal(false);

  isLiveMode    = signal(false);
  liveDay       = signal<WorkoutDay | null>(null);
  liveExercises = signal<LiveExercise[]>([]);

  planLogs  = signal<WorkoutLogResponse[]>([]);
  planStats = signal<PlanProgressResponse | null>(null);

  weeks = computed(() => {
    const plan = this.activePlan();
    if (!plan) return [];
    return [...new Set(plan.days.map(d => d.weekNumber))].sort((a, b) => a - b);
  });

  currentDays = computed(() => {
    const plan = this.activePlan();
    if (!plan) return [];
    return plan.days.filter(d => d.weekNumber === this.activeWeek());
  });

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (data) => {
        this.plans.set(data);
        const active = data.find(p => p.status === 'ACTIVE') ?? data[0] ?? null;
        this.activePlan.set(active);
        this.activeWeek.set(1);
        this.loading.set(false);
        if (active) {
          this.loadPlanLogs(active.id);
          this.loadPlanStats(active.id);
        }
      },
      error: () => {
        this.error.set('Помилка завантаження планів');
        this.loading.set(false);
      }
    });
  }

  loadPlanLogs(planId: string): void {
    this.logService.getLogsByPlan(planId).subscribe({
      next: (logs) => this.planLogs.set(logs),
      error: () => {}
    });
  }

  loadPlanStats(planId: string): void {
    this.logService.getPlanStats(planId).subscribe({
      next: (stats) => this.planStats.set(stats),
      error: () => {}
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
        this.loadPlanLogs(plan.id);
        this.loadPlanStats(plan.id);
      },
      error: () => {
        this.error.set('Помилка генерації плану. Переконайся що профіль заповнений.');
        this.generating.set(false);
      }
    });
  }

  deletePlan(plan: WorkoutPlan): void {
    if (!confirm(`Видалити план "${plan.title}"?`)) return;
    this.service.delete(plan.id).subscribe({
      next: () => {
        this.plans.update(list => list.filter(p => p.id !== plan.id));
        const next = this.plans()[0] ?? null;
        this.activePlan.set(next);
        this.planStats.set(null);
        if (next) {
          this.loadPlanLogs(next.id);
          this.loadPlanStats(next.id);
        }
      },
      error: () => this.error.set('Помилка видалення плану')
    });
  }

  selectPlan(plan: WorkoutPlan): void {
    this.activePlan.set(plan);
    this.activeWeek.set(1);
    this.stopLiveMode();
    this.loadPlanLogs(plan.id);
    this.loadPlanStats(plan.id);
  }

  selectWeek(week: number): void {
    this.activeWeek.set(week);
    this.stopLiveMode();
  }

  startLiveMode(day: WorkoutDay): void {
    this.liveDay.set(day);
    const live: LiveExercise[] = day.exercises.map(ex => {
      const rec = this.getRecommendedWeight(ex);
      return {
        ...ex,
        liveWeight:     rec?.weight ?? ex.plannedWeight ?? null,
        previousWeight: this.getPreviousWeight(ex.exerciseId),
        feltEasy:       null
      };
    });
    this.liveExercises.set(live);
    this.isLiveMode.set(true);
    this.saveSuccess.set(false);
  }

  stopLiveMode(): void {
    this.isLiveMode.set(false);
    this.liveDay.set(null);
    this.liveExercises.set([]);
    this.saveSuccess.set(false);
  }

  updateFeltEasy(exerciseId: string, value: boolean): void {
    this.liveExercises.update(list =>
      list.map(ex => ex.exerciseId === exerciseId ? { ...ex, feltEasy: value } : ex)
    );
  }

  getPreviousLog(exerciseId: string): { weight: number | null; feltEasy: boolean | null } {
    const logs = this.planLogs();
    for (let i = logs.length - 1; i >= 0; i--) {
      const found = logs[i].exercises.find(e => e.exerciseId === exerciseId);
      if (found?.actualWeight != null) {
        return { weight: found.actualWeight, feltEasy: found.feltEasy };
      }
    }
    return { weight: null, feltEasy: null };
  }

  getPreviousWeight(exerciseId: string): number | null {
    return this.getPreviousLog(exerciseId).weight;
  }

  getRecommendedWeight(ex: WorkoutExercise): { weight: number; label: string; hint: string } | null {
    const log = this.getPreviousLog(ex.exerciseId);

    if (log.weight == null) {
      const base = ex.plannedWeight ?? 0;
      const w = Math.round((base + 2.5) * 10) / 10;
      return { weight: w, label: `${w} кг (+2.5)`, hint: 'перше тренування' };
    }

    if (log.feltEasy === true) {
      const w = Math.round((log.weight + 2.5) * 10) / 10;
      return { weight: w, label: `${w} кг (+2.5)`, hint: 'було легко' };
    }

    if (log.feltEasy === false) {
      const w = Math.max(0, Math.round((log.weight - 2.5) * 10) / 10);
      return { weight: w, label: `${w} кг (-2.5)`, hint: 'було важко' };
    }

    // feltEasy невідомо але лог є — оптимістично +2.5
    const w = Math.round((log.weight + 2.5) * 10) / 10;
    return { weight: w, label: `${w} кг (+2.5)`, hint: 'оптимістично' };
  }

  updateLiveWeight(exerciseId: string, weight: number | null): void {
    this.liveExercises.update(list =>
      list.map(ex => ex.exerciseId === exerciseId ? { ...ex, liveWeight: weight } : ex)
    );
  }

  saveWorkout(): void {
    const plan = this.activePlan();
    const day  = this.liveDay();
    if (!plan || !day) return;

    this.saving.set(true);
    this.error.set(null);

    const exercises = this.liveExercises().map(ex => ({
      exerciseId:    ex.exerciseId,
      exerciseName:  ex.exerciseName,
      plannedSets:   ex.sets,
      plannedReps:   ex.reps,
      plannedWeight: ex.plannedWeight ?? null,
      actualSets:    ex.sets,
      actualReps:    ex.reps,
      actualWeight:  ex.liveWeight ?? null,
      feltEasy: ex.feltEasy ?? false,
      notes:         ''
    }));

    this.logService.saveLog({
      planId:     plan.id,
      weekNumber: day.weekNumber,
      dayNumber:  day.dayNumber,
      exercises,
      notes:      ''
    }).subscribe({
      next: (log) => {
        this.planLogs.update(logs => [...logs, log]);
        this.saving.set(false);
        this.saveSuccess.set(true);
        // Оновлюємо статистику після збереження тренування
        this.loadPlanStats(plan.id);
        setTimeout(() => this.stopLiveMode(), 2000);
      },
      error: () => {
        this.error.set('Помилка збереження тренування');
        this.saving.set(false);
      }
    });
  }

  getProgressPercent(): number {
    const s = this.planStats();
    if (!s || s.totalDays === 0) return 0;
    return Math.round((s.completedDays / s.totalDays) * 100);
  }

  goalLabel(goal: string): string {
    const map: Record<string, string> = {
      MASS: 'Набір маси', LOSS: 'Схуднення',
      ENDURANCE: 'Витривалість', STRENGTH: 'Сила', STRENGTH_AND_MASS: 'Сила + Маса'
    };
    return map[goal] ?? goal;
  }

  planTypeLabel(t: string): string {
    const map: Record<string, string> = {
      HYPERTROPHY: 'Гіпертрофія', STRENGTH: 'Сила',
      STRENGTH_HYPERTROPHY: 'Сила + Маса', FAT_LOSS: 'Спалення жиру', ENDURANCE: 'Витривалість'
    };
    return map[t] ?? t;
  }

  intensityLabel(i: string): string {
    const map: Record<string, string> = {
      FULL_BODY: 'Full Body', HEAVY: 'Важкий', MEDIUM: 'Середній', SETS: 'Сети'
    };
    return map[i] ?? i;
  }

  isLoggedThisDay(weekNumber: number, dayNumber: number): boolean {
    return this.planLogs().some(
      l => l.weekNumber === weekNumber && l.dayNumber === dayNumber
    );
  }
}
