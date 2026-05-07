import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WorkoutPlanService, WorkoutPlan, WorkoutDay, WorkoutExercise } from './workout-plan.service';
import {
  WorkoutLogResponse,
  WorkoutLogService,
  PlanProgressResponse, PersonalRecordResponse
} from '../../core/services/workout-log/workout-log.service';

interface LiveExercise extends WorkoutExercise {
  liveWeight: number | null;
  previousWeight: number | null;
  feltEasy: boolean | null;
  skipped: boolean;

  recommendationLabel?: string;
  recommendationHint?: string;
}

export interface WorkoutCompletedSummary {
  weekNumber: number;
  dayNumber: number;
  focus: string;
  durationMs: number;
  totalVolume: number;
  prCount: number;
  exercises: ExerciseSummary[];
}

export interface ExerciseSummary {
  exerciseId: string;
  exerciseName: string;
  sets: number;
  reps: string;
  actualWeight: number | null;
  volume: number;
  isPR: boolean;
  prDelta: number | null;
}


@Component({
  selector: 'app-workout-plan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './workout-plan.html',
  styleUrls: ['./workout-plan.css', './workout-plan-live-mode.css', "./workout-plan-completed.css"]
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

  // Workout completed summary
  completedSummary = signal<WorkoutCompletedSummary | null>(null);

  // Timer
  private liveStartTime: number | null = null;
  private timerInterval: ReturnType<typeof setInterval> | null = null;
  elapsedMs = signal<number>(0);


  ngOnInit(): void {
    this.loadPlans();
  }

  private clearTimer(): void {
    if (this.timerInterval !== null) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  private startTimer(): void {
    this.clearTimer();
    this.liveStartTime = Date.now();
    this.elapsedMs.set(0);
    this.timerInterval = setInterval(() => {
      this.elapsedMs.set(Date.now() - (this.liveStartTime ?? Date.now()));
    }, 1000);
  }

  formatElapsed(ms: number): string {
    const totalSeconds = Math.floor(ms / 1000);
    const h = Math.floor(totalSeconds / 3600);
    const m = Math.floor((totalSeconds % 3600) / 60);
    const s = totalSeconds % 60;
    if (h > 0) return `${h}г ${m}хв`;
    if (m > 0) return `${m} хв ${s < 10 ? '0' : ''}${s}с`;
    return `${s}с`;
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
    const plan = this.activePlan();
    if (!plan) return;

    this.liveDay.set(day);

    const live: LiveExercise[] = day.exercises.map(ex => ({
      ...ex,
      liveWeight: ex.plannedWeight ?? null,
      previousWeight: this.getPreviousWeight(ex.exerciseId),
      feltEasy: null,
      skipped: false,
      recommendationLabel: '',
      recommendationHint: ''
    }));

    this.liveExercises.set(live);

    // завантаження рекомендацій
    live.forEach(ex => {
      this.logService.getRecommendation(
        plan.id,
        ex.exerciseId,
        ex.plannedWeight
      ).subscribe({
        next: (rec) => {
          this.liveExercises.update(list =>
            list.map(item =>
              item.exerciseId === ex.exerciseId
                ? {
                  ...item,
                  liveWeight: rec.recommendedWeight,
                  recommendationLabel: rec.label,
                  recommendationHint: rec.hint
                }
                : item
            )
          );
        },
        error: () => {
          // fallback якщо API впало
          this.liveExercises.update(list =>
            list.map(item =>
              item.exerciseId === ex.exerciseId
                ? {
                  ...item,
                  liveWeight: ex.plannedWeight ?? null
                }
                : item
            )
          );
        }
      });
    });

    this.isLiveMode.set(true);
    this.saveSuccess.set(false);
    this.completedSummary.set(null);

    this.startTimer();
  }

  stopLiveMode(): void {
    this.isLiveMode.set(false);
    this.liveDay.set(null);
    this.liveExercises.set([]);
    this.saveSuccess.set(false);
    this.liveStartTime = null;
  }

  closeCompletedScreen(): void {
    this.completedSummary.set(null);
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

  private parseReps(repsStr: string): number {
    if (!repsStr) return 1;
    if (repsStr.includes('-')) {
      const parts = repsStr.split('-').map(Number);
      return Math.round((parts[0] + parts[1]) / 2);
    }
    return parseInt(repsStr, 10) || 1;
  }


  getPreviousWeight(exerciseId: string): number | null {
    return this.getPreviousLog(exerciseId).weight;
  }

  private buildCompletedSummary(
    durationMs: number,
    prs: PersonalRecordResponse[]
  ): WorkoutCompletedSummary {

    const day = this.liveDay()!;
    const exercises = this.liveExercises();

    const exerciseSummaries: ExerciseSummary[] = exercises
      .filter(ex => !ex.skipped)
      .map(ex => {

        const pr = prs.find(p => p.exerciseId === ex.exerciseId);

        return {
          exerciseId: ex.exerciseId,
          exerciseName: ex.exerciseName,
          sets: ex.sets,
          reps: ex.reps,
          actualWeight: ex.liveWeight,
          volume: 0,

          isPR: !!pr,

          prDelta: pr?.delta ?? null
        };
      });

    return {
      weekNumber: day.weekNumber,
      dayNumber: day.dayNumber,
      focus: day.focus,
      durationMs,
      totalVolume: 0,

      prCount: prs.length,

      exercises: exerciseSummaries
    };
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

    const durationMs = this.liveStartTime ? Date.now() - this.liveStartTime : 0

    const exercises = this.liveExercises()
      .filter(ex => !ex.skipped)
      .map(ex => ({
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
      next: (response) => {

        this.planLogs.update(logs => [
          ...logs,
          response.log
        ]);

        const summary = this.buildCompletedSummary(
          durationMs,
          response.personalRecords
        );

        this.saving.set(false);
        this.saveSuccess.set(true);

        this.clearTimer();

        this.isLiveMode.set(false);
        this.liveDay.set(null);
        this.liveExercises.set([]);
        this.liveStartTime = null;

        this.completedSummary.set(summary);

        this.loadPlanStats(plan.id);
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

  formatDuration(ms: number): string {
    const totalSeconds = Math.floor(ms / 1000);
    const h = Math.floor(totalSeconds / 3600);
    const m = Math.floor((totalSeconds % 3600) / 60);
    const s = totalSeconds % 60;
    if (h > 0) return `${h}г ${m}хв`;
    if (m > 0) return `${m} хв`;
    return `${s}с`;
  }

  toggleSkip(exerciseId: string): void {
    this.liveExercises.update(list =>
      list.map(ex => ex.exerciseId === exerciseId
        ? { ...ex, skipped: !ex.skipped }
        : ex)
    );
  }

  updateLiveReps(exerciseId: string, reps: string): void {
    this.liveExercises.update(list =>
      list.map(ex => ex.exerciseId === exerciseId ? { ...ex, reps } : ex)
    );
  }

}
