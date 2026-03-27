import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkoutPlanComponent } from './workout-plan';

describe('WorkoutPlan', () => {
  let component: WorkoutPlanComponent;
  let fixture: ComponentFixture<WorkoutPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkoutPlanComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkoutPlanComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
