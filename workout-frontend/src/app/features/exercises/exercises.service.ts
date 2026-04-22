// exercises.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Exercise {
  id: string;
  name: string;
  muscleGroup: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  equipment: string;
  description?: string;
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
}
