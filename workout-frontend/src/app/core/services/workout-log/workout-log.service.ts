import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoggedExerciseRequest {
  exerciseId:    string;
  exerciseName:  string;
  plannedSets:   number;
  plannedReps:   string;
  plannedWeight: number | null;
  actualSets:    number;
  actualReps:    string;
  actualWeight:  number | null;
  feltEasy:      boolean;
  notes:         string;
}

export interface LogWorkoutRequest {
  planId:     string;
  weekNumber: number;
  dayNumber:  number;
  exercises:  LoggedExerciseRequest[];
  notes:      string;
}

export interface WorkoutLogResponse {
  id:          string;
  planId:      string;
  weekNumber:  number;
  dayNumber:   number;
  exercises:   LoggedExerciseResponse[];
  notes:       string;
  completedAt: string;
}

export interface LoggedExerciseResponse {
  exerciseId:    string;
  exerciseName:  string;
  plannedSets:   number;
  plannedReps:   string;
  plannedWeight: number | null;
  actualSets:    number;
  actualReps:    string;
  actualWeight:  number | null;
  feltEasy:      boolean;
  notes:         string;
}

export interface PlanProgressResponse {
  totalDays:     number;
  completedDays: number;
  currentStreak: number;
}

export interface WeightRecommendationResponse {
  recommendedWeight: number;
  label: string;
  hint: string;
}

export interface PersonalRecordResponse {
  exerciseId: string;
  exerciseName: string;
  previousWeight: number | null;
  newWeight: number;
  delta: number | null;
}

export interface WorkoutLogResultResponse {
  log: WorkoutLogResponse;
  personalRecords: PersonalRecordResponse[];
}

export interface TrainingDaysResponse {
  trainedDays: number[]; // індекси: 0=Пн, 1=Вт, ..., 6=Нд
}

@Injectable({ providedIn: 'root' })
export class WorkoutLogService {
  private readonly api = '/api/v1/logs';

  constructor(private http: HttpClient) {}

  saveLog(payload: LogWorkoutRequest): Observable<WorkoutLogResultResponse> {
    return this.http.post<WorkoutLogResultResponse>(this.api, payload);
  }

  getLogsByPlan(planId: string): Observable<WorkoutLogResponse[]> {
    return this.http.get<WorkoutLogResponse[]>(this.api, { params: { planId } });
  }

  getLogForDay(planId: string, week: number, day: number): Observable<WorkoutLogResponse> {
    return this.http.get<WorkoutLogResponse>(`${this.api}/day`, {
      params: { planId, week, day }
    });
  }

  getPlanStats(planId: string): Observable<PlanProgressResponse> {
    return this.http.get<PlanProgressResponse>(`${this.api}/stats?planId=${planId}`);
  }

  getRecommendation(
    planId: string,
    exerciseId: string,
    plannedWeight: number | null
  ): Observable<WeightRecommendationResponse> {
    return this.http.get<WeightRecommendationResponse>(`${this.api}/recommendation`, {
      params: { planId, exerciseId, plannedWeight: plannedWeight?.toString() ?? '0' }
    });
  }

  // GET /api/v1/logs/week → { trainedDays: [0, 2, 4] }
  getTrainingDaysThisWeek(): Observable<TrainingDaysResponse> {
    return this.http.get<TrainingDaysResponse>(`${this.api}/week`);
  }
}
