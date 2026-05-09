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
                build("Жим штанги лежачи",                  "CHEST", "INTERMEDIATE", "BARBELL",    "Класичний жим на горизонтальній лаві"),
                build("Жим гантелей на похилій лаві",        "CHEST", "INTERMEDIATE", "DUMBBELL",   "Жим для верхньої частини грудей"),
                build("Віджимання",                          "CHEST", "BEGINNER",     "BODYWEIGHT", "Класична вправа з вагою тіла"),
                build("Зведення рук в тренажері",            "CHEST", "BEGINNER",     "MACHINE",    "Ізоляція грудей (метелик)"),
                build("Кросовер на блоках",                  "CHEST", "INTERMEDIATE", "CABLE",      "Зведення рук на блоках під різними кутами"),
                build("Жим у тренажері Сміта",               "CHEST", "INTERMEDIATE", "MACHINE",    "Жим лежачи в тренажері Сміта"),
                build("Віджимання на брусах",                "CHEST", "INTERMEDIATE", "BODYWEIGHT", "Віджимання з акцентом на груди"),
                build("Жим Хаммер",                          "CHEST", "INTERMEDIATE", "MACHINE",    "Жим на тренажері з блинами"),
                build("Похилий жим Хаммер",                  "CHEST", "INTERMEDIATE", "MACHINE",    "Похилий жим на тренажері з блинами"),
                // НОВІ BEGINNER для CHEST
                build("Жим гантелей лежачи",                 "CHEST", "BEGINNER",     "DUMBBELL",   "Жим гантелей на горизонтальній лаві"),
                build("Зведення гантелей лежачи",            "CHEST", "BEGINNER",     "DUMBBELL",   "Ізоляція грудей з гантелями"),
                build("Жим у тренажері сидячи",              "CHEST", "BEGINNER",     "MACHINE",    "Горизонтальний жим в тренажері"),
                build("Віджимання від колін",                 "CHEST", "BEGINNER",     "BODYWEIGHT", "Полегшені віджимання від підлоги"),
                build("Зведення рук на блоці знизу",         "CHEST", "BEGINNER",     "CABLE",      "Нижня ізоляція грудей на кабелі"),

                // ── СПИНА ──
                build("Підтягування",                         "BACK", "INTERMEDIATE", "BODYWEIGHT", "Вертикальна тяга для ширини спини"),
                build("Тяга штанги в нахилі",                 "BACK", "INTERMEDIATE", "BARBELL",    "Горизонтальна тяга для товщини спини"),
                build("Тяга верхнього блоку",                 "BACK", "BEGINNER",     "MACHINE",    "Блочна тяга для ширини спини"),
                build("Тяга нижнього блоку сидячи",           "BACK", "BEGINNER",     "CABLE",      "Горизонтальна тяга на блоці"),
                build("Тяга Т-грифа",                         "BACK", "INTERMEDIATE", "BARBELL",    "Тяга Т-подібного грифа"),
                build("Тяга гантелі однією рукою",            "BACK", "BEGINNER",     "DUMBBELL",   "Тяга однією рукою в упорі"),
                build("Пуловер з гантеллю",                   "BACK", "INTERMEDIATE", "DUMBBELL",   "Ізоляція широкого м'яза спини"),
                build("Тяга в тренажері",                     "BACK", "BEGINNER",     "MACHINE",    "Горизонтальна тяга в тренажері"),
                build("Тяга верхнього блоку широким хватом",  "BACK", "BEGINNER",     "MACHINE",    "Тяга широким хватом"),
                // НОВІ BEGINNER для BACK
                build("Тяга верхнього блоку зворотнім хватом","BACK", "BEGINNER",     "MACHINE",    "Тяга вузьким зворотнім хватом"),
                build("Тяга гантелей в нахилі",               "BACK", "BEGINNER",     "DUMBBELL",   "Двостороння тяга гантелей"),
                build("Гіперекстензія",                       "BACK", "BEGINNER",     "BODYWEIGHT", "Розгинання спини на тренажері"),
                build("Тяга нижнього блоку вузьким хватом",   "BACK", "BEGINNER",     "CABLE",      "Вузький хват для товщини спини"),
                build("Підтягування в асисті",                "BACK", "BEGINNER",     "MACHINE",    "Підтягування з допомогою тренажера"),

                // ── НОГИ ──
                build("Присідання зі штангою",               "LEGS", "INTERMEDIATE", "BARBELL",    "Базова вправа для ніг"),
                build("Жим ногами",                          "LEGS", "BEGINNER",     "MACHINE",    "Альтернатива присіданням"),
                build("Румунська тяга",                      "LEGS", "INTERMEDIATE", "BARBELL",    "Тяга з акцентом на біцепс стегна"),
                build("Розгинання ніг у тренажері",          "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція чотириголового м'яза"),
                build("Згинання ніг у тренажері",            "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція біцепса стегна"),
                build("Випади з гантелями",                  "LEGS", "INTERMEDIATE", "DUMBBELL",   "Ходячі або статичні випади"),
                build("Підйом на носки стоячи",              "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція литкових м'язів"),
                // НОВІ BEGINNER для LEGS
                build("Присідання з вагою тіла",             "LEGS", "BEGINNER",     "BODYWEIGHT", "Базові присідання без обладнання"),
                build("Сумо-присідання з гантеллю",          "LEGS", "BEGINNER",     "DUMBBELL",   "Присідання широким хватом"),
                build("Зворотні випади",                     "LEGS", "BEGINNER",     "BODYWEIGHT", "Випади назад для балансу"),
                build("Підйом на носки сидячи",              "LEGS", "BEGINNER",     "MACHINE",    "Ізоляція камбалоподібного м'яза"),
                build("Жим ногами вузьким хватом",           "LEGS", "BEGINNER",     "MACHINE",    "Акцент на квадрицепс"),
                build("Румунська тяга з гантелями",          "LEGS", "BEGINNER",     "DUMBBELL",   "Тяга гантелей для біцепса стегна"),

                // ── ПЛЕЧІ ──
                build("Жим штанги стоячи",                   "SHOULDERS", "INTERMEDIATE", "BARBELL",    "Вертикальний жим для маси плечей"),
                build("Розведення гантелей в сторони",        "SHOULDERS", "BEGINNER",     "DUMBBELL",   "Ізоляція середньої дельти"),
                build("Зворотній метелик",                    "SHOULDERS", "BEGINNER",     "MACHINE",    "Ізоляція задньої дельти"),
                build("Тяга до обличчя",                      "SHOULDERS", "BEGINNER",     "CABLE",      "Задня дельта і верхня спина"),
                build("Тяга штанги до підборіддя",            "SHOULDERS", "INTERMEDIATE", "BARBELL",    "Плечі і трапеції"),
                build("Жим гантелей сидячи",                  "SHOULDERS", "INTERMEDIATE", "DUMBBELL",   "Сидячий або стоячий жим"),
                build("Підйом диска перед собою",             "SHOULDERS", "BEGINNER",     "BODYWEIGHT", "Підйом диска перед собою"),
                // НОВІ BEGINNER для SHOULDERS
                build("Жим гантелей стоячи",                  "SHOULDERS", "BEGINNER",     "DUMBBELL",   "Вертикальний жим гантелей стоячи"),
                build("Підйом гантелей перед собою",          "SHOULDERS", "BEGINNER",     "DUMBBELL",   "Ізоляція передньої дельти"),
                build("Жим у тренажері над головою",          "SHOULDERS", "BEGINNER",     "MACHINE",    "Жим в тренажері для плечей"),
                build("Розведення на блоці стоячи",           "SHOULDERS", "BEGINNER",     "CABLE",      "Ізоляція середньої дельти на кабелі"),
                build("Зворотні розведення на блоці",         "SHOULDERS", "BEGINNER",     "CABLE",      "Задня дельта на кабелі"),

                // ── БІЦЕПС ──
                build("Підйом штанги на біцепс",             "BICEPS", "BEGINNER",     "BARBELL",   "Класична вправа для біцепса"),
                build("Молоткові згинання",                  "BICEPS", "BEGINNER",     "DUMBBELL",  "Згинання для брахіаліса та біцепса"),
                build("Згинання на лаві Скотта",             "BICEPS", "BEGINNER",     "BARBELL",   "Згинання на лаві Скотта"),
                build("Згинання на блоці",                   "BICEPS", "BEGINNER",     "CABLE",     "Згинання рук на блоці"),
                build("Концентроване згинання",              "BICEPS", "BEGINNER",     "DUMBBELL",  "Ізоляційне згинання однією рукою"),
                build("Згинання Арнольда",                   "BICEPS", "BEGINNER",     "DUMBBELL",  "Варіація згинання з обертанням"),
                build("Згинання на блоці через тіло",        "BICEPS", "BEGINNER",     "CABLE",     "Тяга блоку через тіло"),
                build("Згинання з гантелями почергово",      "BICEPS", "BEGINNER",     "DUMBBELL",  "Почергове згинання рук"),
                build("Підйом EZ-штанги на біцепс",          "BICEPS", "BEGINNER",     "BARBELL",   "EZ-гриф для зниження навантаження на зап'ясток"),

                // ── ТРИЦЕПС ──
                build("Французький жим лежачи",              "TRICEPS", "INTERMEDIATE", "BARBELL",   "Ізоляція трицепса лежачи"),
                build("Жим вузьким хватом",                  "TRICEPS", "INTERMEDIATE", "BARBELL",   "Базова вправа для трицепса"),
                build("Розгинання на блоці",                  "TRICEPS", "BEGINNER",     "CABLE",     "Ізоляція трицепса на блоці"),
                build("Відведення гантелі назад",            "TRICEPS", "BEGINNER",     "DUMBBELL",  "Ізоляція трицепса в нахилі"),
                build("Віджимання на брусах для трицепса",   "TRICEPS", "INTERMEDIATE", "BODYWEIGHT","Акцент на трицепс"),
                build("Розгинання над головою на блоці",     "TRICEPS", "BEGINNER",     "CABLE",     "Довга головка трицепса"),
                build("Французький жим з гантеллю",          "TRICEPS", "BEGINNER",     "DUMBBELL",  "Французький жим однією рукою"),
                build("Розгинання на блоці зворотнім хватом","TRICEPS", "BEGINNER",     "CABLE",     "Варіація розгинання на блоці"),

                // ── ПРЕС ──
                build("Скручування лежачи",                  "ABS", "BEGINNER",     "BODYWEIGHT", "Базові скручування для прямого м'яза"),
                build("Підйом ніг лежачи",                   "ABS", "BEGINNER",     "BODYWEIGHT", "Нижній прес"),
                build("Планка",                              "ABS", "BEGINNER",     "BODYWEIGHT", "Статична вправа для кора"),
                build("Велосипед",                           "ABS", "BEGINNER",     "BODYWEIGHT", "Косі м'язи живота"),
                build("Скручування на блоці",                "ABS", "BEGINNER",     "CABLE",      "Скручування з обтяженням"),
                build("Підйом колін у висі",                 "ABS", "INTERMEDIATE", "BODYWEIGHT", "Нижній прес у висі"),
                build("Бічна планка",                        "ABS", "BEGINNER",     "BODYWEIGHT", "Косі м'язи і стабілізатори"),

                // ── ПЕРЕДПЛІЧЧЯ ──
                build("Згинання зап'ясть зі штангою",        "FOREARMS", "BEGINNER", "BARBELL",   "Флексори передпліччя"),
                build("Зворотні згинання зап'ясть",          "FOREARMS", "BEGINNER", "BARBELL",   "Екстензори передпліччя"),
                build("Фермерська хода",                     "FOREARMS", "BEGINNER", "DUMBBELL",  "Хват і передпліччя"),

                // ── ЛИТКИ ──
                build("Підйом на носки в тренажері",         "CALVES", "BEGINNER", "MACHINE",    "Ізоляція литок стоячи"),
                build("Підйом на носки сидячи",              "CALVES", "BEGINNER", "MACHINE",    "Ізоляція камбалоподібного м'яза"),
                build("Підйом на носки з гантелями",         "CALVES", "BEGINNER", "DUMBBELL",   "Литки з вільними вагами"),

                // ── ТРАПЕЦІЇ ──
                build("Шраги зі штангою",                    "TRAPS", "BEGINNER", "BARBELL",    "Ізоляція трапецій"),
                build("Шраги з гантелями",                   "TRAPS", "BEGINNER", "DUMBBELL",   "Варіація шрагів з гантелями"),
                build("Шраги в тренажері Сміта",             "TRAPS", "BEGINNER", "MACHINE",    "Контрольовані шраги в тренажері")
        );
    }

    private ExerciseDocument build(String name, String muscleGroup, String difficulty,
                                   String equipment, String description) {
        return ExerciseDocument.builder()
                .name(name)
                .muscleGroup(muscleGroup)
                .difficulty(difficulty)
                .equipmentType(equipment)
                .description(description)
                .build();
    }
}