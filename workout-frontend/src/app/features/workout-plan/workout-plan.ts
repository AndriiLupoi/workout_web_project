import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WorkoutPlanService, WorkoutPlan, WorkoutDay, WorkoutExercise } from './workout-plan.service';
import {WorkoutLogResponse, WorkoutLogService, LogWorkoutRequest, LoggedExerciseResponse, LoggedExerciseRequest} from '../../core/services/workout-log/workout-log.service';

// Розширена вправа з живою вагою для поточного тренування
interface LiveExercise extends WorkoutExercise {
  liveWeight: number | null;  // вага яку юзер вводить під час тренування
  previousWeight: number | null;  // вага з попереднього лога (для підказки)
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

  plans      = signal<WorkoutPlan[]>([]);
  activePlan = signal<WorkoutPlan | null>(null);
  activeWeek = signal<number>(1);
  loading    = signal(false);
  generating = signal(false);
  saving     = signal(false);
  error      = signal<string | null>(null);
  saveSuccess = signal(false);

  // Режим "Live тренування" — коли юзер активно тренується і вводить ваги
  isLiveMode    = signal(false);
  liveDay       = signal<WorkoutDay | null>(null);
  liveExercises = signal<LiveExercise[]>([]);

  // Попередні логи по активному плану (для підказок щодо ваги)
  planLogs = signal<WorkoutLogResponse[]>([]);

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
        if (active) this.loadPlanLogs(active.id);
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
        if (next) this.loadPlanLogs(next.id);
      },
      error: () => this.error.set('Помилка видалення плану')
    });
  }

  selectPlan(plan: WorkoutPlan): void {
    this.activePlan.set(plan);
    this.activeWeek.set(1);
    this.stopLiveMode();
    this.loadPlanLogs(plan.id);
  }

  selectWeek(week: number): void {
    this.activeWeek.set(week);
    this.stopLiveMode();
  }

  // Запуск live-режиму для конкретного дня
  startLiveMode(day: WorkoutDay): void {
    this.liveDay.set(day);
    // Беремо попередню вагу з логів для кожної вправи
    const live: LiveExercise[] = day.exercises.map(ex => ({
      ...ex,
      liveWeight: ex.plannedWeight ?? null,
      previousWeight: this.getPreviousWeight(ex.exerciseId)
    }));
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

  // Знайти останню використану вагу для вправи з попередніх логів
  getPreviousWeight(exerciseId: string): number | null {
    const logs = this.planLogs();
    for (let i = logs.length - 1; i >= 0; i--) {
      const found = logs[i].exercises.find(e => e.exerciseId === exerciseId);
      if (found?.actualWeight != null) return found.actualWeight;
    }
    return null;
  }

  // Зміна ваги в live-режимі
  updateLiveWeight(exerciseId: string, weight: number | null): void {
    this.liveExercises.update(list =>
      list.map(ex => ex.exerciseId === exerciseId ? { ...ex, liveWeight: weight } : ex)
    );
  }

  // Збереження тренування
  saveWorkout(): void {
    const plan = this.activePlan();
    const day  = this.liveDay();
    if (!plan || !day) return;

    this.saving.set(true);
    this.error.set(null);

    const exercises = this.liveExercises().map(ex => ({
      exerciseId:    ex.exerciseId,
      exerciseName:  ex.exerciseName,
      plannedSets:   ex.sets,                // int — беремо з плану
      plannedReps:   ex.reps,
      plannedWeight: ex.plannedWeight ?? null,
      actualSets:    ex.sets,                // int — той самий план (можна розширити пізніше)
      actualReps:    ex.reps,
      actualWeight:  ex.liveWeight ?? null,  // Double — вага яку ввів юзер
      feltEasy:      false,
      notes:         ''
    }));

    this.logService.saveLog({
      planId:     plan.id,
      weekNumber: day.weekNumber,  // int — НЕ null
      dayNumber:  day.dayNumber,   // int — НЕ null
      exercises,
      notes:      ''
    }).subscribe({
      next: (log) => {
        this.planLogs.update(logs => [...logs, log]);
        this.saving.set(false);
        this.saveSuccess.set(true);
        setTimeout(() => this.stopLiveMode(), 2000);
      },
      error: () => {
        this.error.set('Помилка збереження тренування');
        this.saving.set(false);
      }
    });
  }

  // Утиліти лейблів
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
}
