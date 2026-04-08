import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from './profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {

  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);

  // Режими
  isEditMode = signal(false);

  // Дані
  profileForm: FormGroup;
  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  goals = [
    { value: 'MASS',      label: 'Набір м\'язової маси' },
    { value: 'LOSS',      label: 'Схуднення / спалення жиру' },
    { value: 'ENDURANCE', label: 'Розвиток витривалості' }
  ];

  experienceLevels = [
    { value: 'BEGINNER', label: 'Початківець' },
    { value: 'INTERMEDIATE', label: 'Середній' },
    { value: 'ADVANCED', label: 'Просунутий' }
  ];

  equipmentOptions = [
    { value: 'BARBELL', label: 'Штанга' },
    { value: 'DUMBBELL', label: 'Гантелі' },
    { value: 'MACHINE', label: 'Тренажери' },
    { value: 'BODYWEIGHT', label: 'Вага тіла' },
    { value: 'PULL_UP', label: 'Турнік' },
    { value: 'CABLE', label: 'Блоки / кабелі' },
    { value: 'BENCH', label: 'Лава для жиму' }
  ];

  constructor() {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      age: [null],
      height: [null],
      currentWeight: [null],
      targetWeight: [null],
      goal: ['MASS', Validators.required],
      experienceLevel: ['BEGINNER', Validators.required],
      trainingDaysPerWeek: [3, [Validators.required, Validators.min(2), Validators.max(6)]],
      availableEquipment: [[] as string[]]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading.set(true);
    this.profileService.getProfile().subscribe({
      next: (profile: any) => {
        this.profileForm.patchValue({
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          age: profile.age,
          height: profile.height,
          currentWeight: profile.currentWeight,
          targetWeight: profile.targetWeight,
          goal: profile.goal || 'MASS',
          experienceLevel: profile.level || 'BEGINNER',
          trainingDaysPerWeek: profile.workoutsPerWeek || 3,
        });

        const savedEquipment: string[] = profile.availableEquipment || [];
        this.profileForm.get('availableEquipment')?.setValue(savedEquipment);

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Не вдалося завантажити профіль', err);
        this.isLoading.set(false);
      }
    });
  }

  toggleEditMode(): void {
    this.isEditMode.update(v => !v);
    if (!this.isEditMode()) {
      this.loadProfile(); // перезавантажуємо дані при виході з режиму редагування
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;

    this.isLoading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    const formValue = this.profileForm.value;

    const payload = {
      goal: formValue.goal,
      level: formValue.experienceLevel,
      workoutsPerWeek: formValue.trainingDaysPerWeek || 3,
      currentWeight: formValue.currentWeight || null,
      targetWeight: formValue.targetWeight || null,
      height: formValue.height || null,
      age: formValue.age || null,
      availableEquipment: formValue.availableEquipment || []
    };

    this.profileService.updateProfile(payload).subscribe({
      next: () => {
        this.successMessage.set('Профіль успішно збережено!');
        this.isEditMode.set(false); // виходимо з режиму редагування
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage.set('Помилка збереження профілю');
        this.isLoading.set(false);
      }
    });
  }

  onEquipmentChange(event: any): void {
    const value = event.target.value;
    let current: string[] = this.profileForm.get('availableEquipment')?.value || [];

    if (event.target.checked) {
      if (!current.includes(value)) current = [...current, value];
    } else {
      current = current.filter(item => item !== value);
    }

    this.profileForm.get('availableEquipment')?.setValue(current);
  }

  isEquipmentSelected(equipment: string): boolean {
    const selected = this.profileForm.get('availableEquipment')?.value as string[] || [];
    return selected.includes(equipment);
  }
}
