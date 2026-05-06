// exercises.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Exercise {
  id: string;
  name: string;
  muscleGroup: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  equipmentType: string;
  description?: string;
  videoUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExercisesService {

  private readonly apiUrl = '/api/v1/exercises';

  constructor(private http: HttpClient) {}

  getAllExercises(): Observable<Exercise[]> {
    return this.http.get<Exercise[]>(this.apiUrl);
  }

  updateVideoUrl(exerciseId: string, videoUrl: string): Observable<Exercise> {
    return this.http.patch<Exercise>(
      `/api/v1/admin/exercises/${exerciseId}/video`,
      { videoUrl }
    );
  }

  createExercise(data: Partial<Exercise>): Observable<Exercise> {
    return this.http.post<Exercise>('/api/v1/admin/exercises', data);
  }

  updateExercise(id: string, data: Partial<Exercise>): Observable<Exercise> {
    return this.http.put<Exercise>(`/api/v1/admin/exercises/${id}`, data);
  }

  deleteExercise(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/admin/exercises/${id}`);
  }
}
