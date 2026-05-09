package org.lupoi.workoutapp.application.strategy.plans;/*
    @author Andrii
    @project workout
    @class HypertrophyPlanStrategy
    @version 1.0.0
    @since 09.05.2026 - 18.50
*/

import org.lupoi.workoutapp.application.strategy.BasePlanStrategy;
import org.lupoi.workoutapp.application.strategy.config.PlanConfig;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Hypertrophy (MASS) plan — 8 weeks.
 *
 * Goal: maximize muscle growth via volume and mechanical tension.
 * Trainer logic:
 *   - Medium weight, moderate reps (8–12)
 *   - High weekly volume
 *   - Periodic intensity spikes to stimulate strength adaptation
 *   - Accumulation weeks (supersets) to handle fatigue
 */
@Component("HYPERTROPHY")
public class HypertrophyPlanStrategy extends BasePlanStrategy {

    @Override
    public int totalWeeks() { return PlanConfig.WEEKS_HYPERTROPHY; }

    @Override
    public String planTitle() { return "Hypertrophy Plan — 8 weeks"; }

    @Override
    public List<IntensityType> intensityCycle() {
        return PlanConfig.CYCLE_HYPERTROPHY;
    }
}

