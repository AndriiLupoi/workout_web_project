package org.lupoi.workoutapp.infrastructure.seed;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.infrastructure.document.workout.ExerciseDocument;
import org.lupoi.workoutapp.infrastructure.repository.MongoExerciseRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExerciseDataSeeder implements ApplicationRunner {

    private final MongoExerciseRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() == 0) {
            repository.saveAll(defaultExercises());
        }
    }

    private List<ExerciseDocument> defaultExercises() {
        return List.of(
                // ── ГРУДИ ──
                build("Жим штанги лежачи",           "CHEST", "INTERMEDIATE", "BARBELL",    "Класичний жим на горизонтальній лаві"),
                build("Жим гантелей на похилій лаві", "CHEST", "INTERMEDIATE", "DUMBBELL",   "Жим для верхньої частини грудей"),
                build("Віджимання",                   "CHEST", "BEGINNER",     "BODYWEIGHT", "Класична вправа з вагою тіла"),
                build("Зведення рук в тренажері",     "CHEST", "BEGINNER",     "MACHINE",    "Ізоляція грудей (метелик)"),
                build("Кросовер на блоках",           "CHEST", "INTERMEDIATE", "CABLE",      "Зведення рук на блоках під різними кутами"),
                build("Жим у тренажері Сміта",        "CHEST", "INTERMEDIATE", "MACHINE",    "Жим лежачи в тренажері Сміта"),
                build("Віджимання на брусах",         "CHEST", "INTERMEDIATE", "BODYWEIGHT", "Віджимання з акцентом на груди"),
                build("Жим Хаммер",                   "CHEST", "INTERMEDIATE", "MACHINE",    "Жим на тренажері з блинами"),
                build("Похилий жим Хаммер",           "CHEST", "INTERMEDIATE", "MACHINE",    "Похилий жим на тренажері з блинами"),

                // ── СПИНА ──
                build("Підтягування",                 "BACK", "INTERMEDIATE", "BODYWEIGHT", "Вертикальна тяга для ширини спини"),
                build("Тяга штанги в нахилі",         "BACK", "INTERMEDIATE", "BARBELL",    "Горизонтальна тяга для товщини спини"),
                build("Тяга верхнього блоку",         "BACK", "BEGINNER",     "MACHINE",    "Блочна тяга для ширини спини"),
                build("Тяга нижнього блоку сидячи",   "BACK", "BEGINNER",     "CABLE",      "Горизонтальна тяга на блоці"),
                build("Тяга Т-грифа",                 "BACK", "INTERMEDIATE", "BARBELL",    "Тяга Т-подібного грифа"),
                build("Тяга гантелі однією рукою",    "BACK", "BEGINNER",     "DUMBBELL",   "Тяга однією рукою в упорі"),
                build("Пуловер з гантеллю",           "BACK", "INTERMEDIATE", "DUMBBELL",   "Ізоляція широкого м'яза спини"),
                build("Тяга в тренажері",             "BACK", "BEGINNER",     "MACHINE",    "Горизонтальна тяга в тренажері"),
                build("Тяга верхнього блоку широким хватом", "BACK", "BEGINNER", "MACHINE", "Тяга широким хватом"),

                // ── НОГИ ──
                build("Присідання зі штангою",        "LEGS", "INTERMEDIATE", "BARBELL",    "Базова вправа для ніг"),
                build("Жим ногами",                   "LEGS", "BEGINNER",     "MACHINE",    "Альтернатива присіданням"),
                build("Румунська тяга",               "LEGS", "INTERMEDIATE", "BARBELL",    "Тяга з акцентом на біцепс стегна"),
                build("Розгинання ніг у тренажері",   "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція чотириголового м'яза"),
                build("Згинання ніг у тренажері",     "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція біцепса стегна"),
                build("Випади з гантелями",           "LEGS", "INTERMEDIATE", "DUMBBELL",   "Ходячі або статичні випади"),
                build("Підйом на носки стоячи",       "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція литкових м'язів"),

                // ── ПЛЕЧІ ──
                build("Жим штанги стоячи",            "SHOULDERS", "INTERMEDIATE", "BARBELL", "Вертикальний жим для маси плечей"),
                build("Розведення гантелей в сторони","SHOULDERS", "BEGINNER",     "DUMBBELL","Ізоляція середньої дельти"),
                build("Зворотній метелик",            "SHOULDERS", "BEGINNER",     "MACHINE", "Ізоляція задньої дельти"),
                build("Тяга до обличчя",              "SHOULDERS", "BEGINNER",     "CABLE",   "Задня дельта і верхня спина"),
                build("Тяга штанги до підборіддя",    "SHOULDERS", "INTERMEDIATE", "BARBELL", "Плечі і трапеції"),
                build("Жим гантелей сидячи",          "SHOULDERS", "INTERMEDIATE", "DUMBBELL","Сидячий або стоячий жим"),
                build("Підйом диска перед собою",     "SHOULDERS", "BEGINNER",     "BODYWEIGHT","Підйом диска перед собою"),

                // ── БІЦЕПС ──
                build("Підйом штанги на біцепс",      "BICEPS", "BEGINNER",     "BARBELL",   "Класична вправа для біцепса"),
                build("Молоткові згинання",           "BICEPS", "BEGINNER",     "DUMBBELL",  "Згинання для брахіаліса та біцепса"),
                build("Згинання на лаві Скотта",      "BICEPS", "BEGINNER",     "BARBELL",   "Згинання на лаві Скотта"),
                build("Згинання на блоці",            "BICEPS", "BEGINNER",     "CABLE",     "Згинання рук на блоці"),
                build("Концентроване згинання",       "BICEPS", "BEGINNER",     "DUMBBELL",  "Ізоляційне згинання однією рукою"),
                build("Згинання Арнольда",            "BICEPS", "BEGINNER",     "DUMBBELL",  "Варіація згинання з обертанням"),
                build("Згинання на блоці через тіло", "BICEPS", "BEGINNER",     "CABLE",     "Тяга блоку через тіло"),
                build("Згинання гантелей сидячи",     "BICEPS", "BEGINNER",     "DUMBBELL",  "Сидяче згинання на біцепс"),

                // ── ТРИЦЕПС ──
                build("Розгинання на блоці вниз",          "TRICEPS", "BEGINNER",     "CABLE",   "Ізоляція трицепса на блоці"),
                build("Французький жим",                   "TRICEPS", "INTERMEDIATE", "BARBELL", "Розгинання лежачи"),
                build("Розгинання гантелі з-за голови",    "TRICEPS", "BEGINNER",     "DUMBBELL","Розгинання над головою"),
                build("Розгинання на блоці над головою",   "TRICEPS", "BEGINNER",     "CABLE",   "Блочне розгинання над головою"),
                build("Жим вузьким хватом",                "TRICEPS", "INTERMEDIATE", "BARBELL", "Жим з акцентом на трицепс"),
                build("Розгинання однією рукою з-за голови","TRICEPS", "BEGINNER",    "DUMBBELL","Розгинання однією рукою"),
                build("Розгинання в тренажері",            "TRICEPS", "BEGINNER",     "MACHINE", "Ізоляція трицепса в тренажері"),

                // ── ПЕРЕДПЛІЧЧЯ ──
                build("Згинання зворотнім хватом",    "FOREARMS", "BEGINNER", "BARBELL",  "Згинання зворотнім хватом"),
                build("Згинання зап'ястків",          "FOREARMS", "BEGINNER", "DUMBBELL", "Ізоляція згиначів передпліччя"),

                // ── ТРАПЕЦІЇ ──
                build("Шраги зі штангою",             "TRAPS", "BEGINNER",     "BARBELL",  "Класичні шраги зі штангою"),
                build("Шраги з гантелями",            "TRAPS", "BEGINNER",     "DUMBBELL", "Шраги з гантелями"),
                build("Шраги в тренажері Сміта",      "TRAPS", "BEGINNER",     "MACHINE",  "Шраги в тренажері Сміта"),
                build("Шраги за спиною",              "TRAPS", "INTERMEDIATE", "BARBELL",  "Шраги за спиною"),
                build("Прогулянка фермера",           "TRAPS", "INTERMEDIATE", "DUMBBELL", "Важка хода для трапецій і хвату"),
                build("Тяга до колін",                "TRAPS", "ADVANCED",     "BARBELL",  "Часткова тяга з акцентом на трапеції"),

                // ── ЛИТКИ ──
                build("Підйом на носки стоячи",       "CALVES", "BEGINNER",     "MACHINE",   "Підйом на носки стоячи"),
                build("Підйом на носки сидячи",       "CALVES", "BEGINNER",     "MACHINE",   "Ізоляція литкових м'язів сидячи"),
                build("Підйом на носки в жимі ногами","CALVES", "BEGINNER",     "MACHINE",   "Підйом на носки в тренажері"),
                build("Підйом на носки осляча",       "CALVES", "INTERMEDIATE", "BODYWEIGHT","Класична вправа для литок"),
                build("Підйом на носки на одній нозі","CALVES", "BEGINNER",     "BODYWEIGHT","Однонога робота для литок"),
                build("Скакалка",                     "CALVES", "BEGINNER",     "BODYWEIGHT","Динамічна витривалість литок"),

                // ── ПРЕС ──
                build("Планка",                       "ABS", "BEGINNER", "BODYWEIGHT", "Статична вправа для кора"),
                build("Скручування",                  "ABS", "BEGINNER", "BODYWEIGHT", "Базова вправа для преса"),
                build("Альпініст",                    "ABS", "BEGINNER", "BODYWEIGHT", "Динамічна вправа для кора"),
                build("Скручування на блоці",         "ABS", "BEGINNER", "CABLE",      "Скручування з вагою на блоці"),

                // ── КАРДІО ──
                build("Бігова доріжка",               "CARDIO", "BEGINNER", "MACHINE", "Кардіо тренування на витривалість")
        );
    }

    private ExerciseDocument build(String name, String muscle,
                                   String difficulty, String equipment, String desc) {
        return ExerciseDocument.builder()
                .name(name)
                .muscleGroup(muscle)
                .difficulty(difficulty)
                .equipmentType(equipment)
                .description(desc)
                .build();
    }
}