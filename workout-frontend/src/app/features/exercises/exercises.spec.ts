import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExercisesComponent } from './exercises';

describe('Exercises', () => {
  let component: ExercisesComponent;
  let fixture: ComponentFixture<ExercisesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExercisesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ExercisesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
