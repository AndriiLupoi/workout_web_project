import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from './profile.service';
import { UserService } from './user.service';

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
  private userService = inject(UserService);

  isEditMode = signal(false);
  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  profileForm: FormGroup;

  goals = [
    { value: 'MASS',                label: 'Набір м\'язової маси' },
    { value: 'LOSS',                label: 'Схуднення / спалення жиру' },
    { value: 'ENDURANCE',           label: 'Розвиток витривалості' },
    { value: 'STRENGTH',            label: 'Розвиток сили' },
    { value: 'STRENGTH_AND_MASS',   label: 'Сила + Маса' },
  ];

  experienceLevels = [
    { value: 'BEGINNER',     label: 'Початківець' },
    { value: 'RETURNING',    label: 'Повертаюсь після паузи' },
    { value: 'INTERMEDIATE', label: 'Середній' },
    { value: 'ADVANCED',     label: 'Просунутий' },
  ];

  planTypes = [
    { value: 'HYPERTROPHY',           label: 'Гіпертрофія — 8 тижнів' },
    { value: 'STRENGTH',              label: 'Сила — 10 тижнів' },
    { value: 'STRENGTH_HYPERTROPHY',  label: 'Сила + Маса — 9 тижнів' },
    { value: 'FAT_LOSS',              label: 'Спалення жиру — 8 тижнів' },
    { value: 'ENDURANCE',             label: 'Витривалість — 8 тижнів' },
  ];

  equipmentOptions = [
    { value: 'BARBELL',    label: 'Штанга' },
    { value: 'DUMBBELL',   label: 'Гантелі' },
    { value: 'MACHINE',    label: 'Тренажери' },
    { value: 'BODYWEIGHT', label: 'Вага тіла' },
    { value: 'PULL_UP',    label: 'Турнік' },
    { value: 'CABLE',      label: 'Блоки / кабелі' },
    { value: 'BENCH',      label: 'Лава для жиму' },
  ];

  constructor() {
    this.profileForm = this.fb.group({
      firstName:          ['', [Validators.required, Validators.minLength(2)]],
      lastName:           ['', [Validators.required, Validators.minLength(2)]],
      age:                [null],
      height:             [null],
      currentWeight:      [null],
      targetWeight:       [null],
      goal:               ['MASS', Validators.required],
      experienceLevel:    ['BEGINNER', Validators.required],
      planType:           ['HYPERTROPHY', Validators.required],
      trainingDaysPerWeek:[3, [Validators.required, Validators.min(2), Validators.max(6)]],
      availableEquipment: [[] as string[]]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading.set(true);
    forkJoin({
      user:    this.userService.getUser(),
      profile: this.profileService.getProfile()
    }).subscribe({
      next: ({ user, profile }: any) => {
        this.profileForm.patchValue({
          firstName:           user.firstName || '',
          lastName:            user.lastName  || '',
          age:                 profile.age,
          height:              profile.height,
          currentWeight:       profile.currentWeight,
          targetWeight:        profile.targetWeight,
          goal:                profile.goal     || 'MASS',
          experienceLevel:     profile.level    || 'BEGINNER',
          planType:            profile.planType || 'HYPERTROPHY',
          trainingDaysPerWeek: profile.workoutsPerWeek || 3,
        });
        this.profileForm.get('availableEquipment')
          ?.setValue(profile.availableEquipment || []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Не вдалося завантажити профіль');
        this.isLoading.set(false);
      }
    });
  }

  toggleEditMode(): void {
    this.isEditMode.update(v => !v);
    if (!this.isEditMode()) this.loadProfile();
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;

    this.isLoading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    const v = this.profileForm.value;

    forkJoin({
      user: this.userService.updateUser({
        firstName: v.firstName,
        lastName:  v.lastName
      }),
      profile: this.profileService.updateProfile({
        goal:               v.goal,
        level:              v.experienceLevel,
        planType:           v.planType,
        workoutsPerWeek:    v.trainingDaysPerWeek || 3,
        currentWeight:      v.currentWeight  || null,
        targetWeight:       v.targetWeight   || null,
        height:             v.height         || null,
        age:                v.age            || null,
        availableEquipment: v.availableEquipment || []
      })
    }).subscribe({
      next: () => {
        this.successMessage.set('Профіль успішно збережено!');
        this.isEditMode.set(false);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Помилка збереження профілю');
        this.isLoading.set(false);
      }
    });
  }

  onEquipmentChange(event: any): void {
    const value = event.target.value;
    let current: string[] = this.profileForm.get('availableEquipment')?.value || [];
    current = event.target.checked
      ? [...current.filter(i => i !== value), value]
      : current.filter(i => i !== value);
    this.profileForm.get('availableEquipment')?.setValue(current);
  }

  isEquipmentSelected(equipment: string): boolean {
    return (this.profileForm.get('availableEquipment')?.value as string[] || [])
      .includes(equipment);
  }
}
