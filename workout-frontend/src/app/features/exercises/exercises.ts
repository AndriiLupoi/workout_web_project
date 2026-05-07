import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExercisesService } from './exercises.service';
import { AuthService } from '../../core/services/auth';
import { SafePipe } from '../../core/services/safe.pipe';

export interface Exercise {
  id: string;
  name: string;
  muscleGroup: string;
  difficulty: string;
  equipmentType: string;
  description?: string;
  videoUrl?: string;
}

const DIFFICULTY_ORDER: Record<string, number> = {
  BEGINNER: 1,
  INTERMEDIATE: 2,
  ADVANCED: 3
};

@Component({
  selector: 'app-exercises',
  standalone: true,
  imports: [CommonModule, FormsModule, SafePipe],
  templateUrl: './exercises.html',
  styleUrl: './exercises.css'
})
export class ExercisesComponent implements OnInit {

  private exercisesService = inject(ExercisesService);
  private authService = inject(AuthService);

  // ================= SIGNALS =================
  exercises = signal<Exercise[]>([]);

  // Модалка редагування / створення
  selectedExercise = signal<Exercise | null>(null);
  isNewExercise = signal(false);
  editForm = signal<Partial<Exercise>>({});
  savingExercise = signal(false);
  exerciseSaveSuccess = signal(false);

  // Модалка для перегляду відео
  videoModalExercise = signal<Exercise | null>(null);

  // Підтвердження видалення
  deleteConfirmId = signal<string | null>(null);

  isAdmin = () => this.authService.isAdmin();

  // ================= FILTER STATE =================
  searchTerm = '';
  selectedMuscleGroup = '';
  selectedDifficulty = '';
  selectedSortBy: 'name' | 'difficulty' | 'muscleGroup' = 'name';
  showFilters = false;

  private searchTimeout: any;

  // ================= DATA =================
  muscleGroups = [
    { label: 'Груди',       value: 'CHEST' },
    { label: 'Спина',       value: 'BACK' },
    { label: 'Ноги',        value: 'LEGS' },
    { label: 'Плечі',       value: 'SHOULDERS' },
    { label: 'Біцепс',      value: 'BICEPS' },
    { label: 'Трицепс',     value: 'TRICEPS' },
    { label: 'Прес',        value: 'ABS' },
    { label: 'Передпліччя', value: 'FOREARMS' },
    { label: 'Трапеція',    value: 'TRAPS' },
    { label: 'Ікри',        value: 'CALVES' },
    { label: 'Кардіо',      value: 'CARDIO' },
  ];

  // ================= LIFECYCLE =================
  ngOnInit(): void {
    this.loadExercises();
  }

  // ================= API =================
  loadExercises(): void {
    this.exercisesService.getAllExercises({
      muscleGroup: this.selectedMuscleGroup,
      difficulty: this.selectedDifficulty,
      search: this.searchTerm.trim() || undefined,
      sortBy: this.selectedSortBy
    }).subscribe({
      next: (data) => {
        this.exercises.set(data);
      },
      error: (err) => {
        console.error(err);
        this.exercises.set([]);
      }
    });
  }

  // ================= FILTER LOGIC =================

  onFilterChange(): void {
    clearTimeout(this.searchTimeout);

    this.searchTimeout = setTimeout(() => {
      this.loadExercises();
    }, 200);
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedMuscleGroup = '';
    this.selectedDifficulty = '';
    this.selectedSortBy = 'name';

    this.loadExercises();
  }

  // ================= MODAL =================
  openVideoModal(exercise: Exercise): void {
    this.videoModalExercise.set(exercise);
  }

  closeVideoModal(): void {
    this.videoModalExercise.set(null);
  }

  openEditModal(exercise: Exercise): void {
    console.log('openEditModal called', exercise);
    this.selectedExercise.set(exercise);

    console.log('selectedExercise after set:', this.selectedExercise());
    this.isNewExercise.set(false);
    this.editForm.set({ ...exercise });
    this.exerciseSaveSuccess.set(false);
  }

  openCreateModal(): void {
    this.selectedExercise.set({} as Exercise);
    this.isNewExercise.set(true);
    this.editForm.set({
      muscleGroup: 'CHEST',
      difficulty: 'BEGINNER',
      equipmentType: 'BARBELL',
    });
    this.exerciseSaveSuccess.set(false);
  }

  closeModal(): void {
    this.selectedExercise.set(null);
    this.editForm.set({});
    this.exerciseSaveSuccess.set(false);
  }

  updateField(field: keyof Exercise, value: string): void {
    this.editForm.update(f => ({ ...f, [field]: value }));
  }

  saveExercise(): void {
    const raw = this.editForm();

    const data = {
      ...raw,
      muscleGroup: raw.muscleGroup?.toUpperCase(),
      difficulty: raw.difficulty?.toUpperCase(),
      equipmentType: raw.equipmentType?.toUpperCase(),
    };

    if (!data.name?.trim()) return;

    this.savingExercise.set(true);

    const obs = this.isNewExercise()
      ? this.exercisesService.createExercise(data)
      : this.exercisesService.updateExercise(this.selectedExercise()!.id, data);

    obs.subscribe({
      next: (saved) => {
        if (this.isNewExercise()) {
          this.exercises.update(list => [...list, saved]);
        } else {
          this.exercises.update(list =>
            list.map(ex => ex.id === saved.id ? saved : ex)
          );
        }
        this.loadExercises();
        this.savingExercise.set(false);
        this.exerciseSaveSuccess.set(true);
        setTimeout(() => this.closeModal(), 1200);
      },
      error: () => this.savingExercise.set(false)
    });
  }

  // ================= DELETE =================
  confirmDelete(id: string): void {
    this.deleteConfirmId.set(id);
  }

  cancelDelete(): void {
    this.deleteConfirmId.set(null);
  }

  executeDelete(): void {
    const id = this.deleteConfirmId();
    if (!id) return;
    this.exercisesService.deleteExercise(id).subscribe({
      next: () => {
        this.exercises.update(list => list.filter(ex => ex.id !== id));
        this.loadExercises();
        this.deleteConfirmId.set(null);
        this.closeModal();
      }
    });
  }

  // ================= UI HELPERS =================
  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  getMuscleLabel(value: string): string {
    return this.muscleGroups.find(g => g.value === value)?.label || value;
  }

  getDifficultyLabel(value: string): string {
    switch (value) {
      case 'BEGINNER':     return 'Початківець';
      case 'INTERMEDIATE': return 'Середній';
      case 'ADVANCED':     return 'Просунутий';
      default:             return value;
    }
  }

  getMuscleIcon(group: string): string {
    const icons: Record<string, string> = {
      CHEST: '💪', BACK: '🏋️', LEGS: '🦵', SHOULDERS: '🏋️‍♂️',
      BICEPS: '💪', TRICEPS: '🔱', ABS: '🔥', FOREARMS: '✋',
      TRAPS: '🧱', CALVES: '🦶', CARDIO: '❤️'
    };
    return icons[group] || '🏋️';
  }

  getMuscleCount(group: string): number {
    return this.exercises().filter(ex => ex.muscleGroup === group).length;
  }

  extractYoutubeId(url: string): string | null {
    if (!url) return null;
    const match = url.match(
      /(?:youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/
    );
    return match ? match[1] : null;
  }
}
