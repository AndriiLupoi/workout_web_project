package org.lupoi.workoutapp.architectureTest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private static final String BASE_PACKAGE   = "org.lupoi.workoutapp";
    private static final String DOMAIN         = BASE_PACKAGE + ".domain..";
    private static final String APPLICATION    = BASE_PACKAGE + ".application..";
    private static final String INFRASTRUCTURE = BASE_PACKAGE + ".infrastructure..";
    private static final String PRESENTATION   = BASE_PACKAGE + ".presentation..";

    private static JavaClasses classes;     // без тестів
    private static JavaClasses mainClasses; // з тестами

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);

        mainClasses = new ClassFileImporter()
                .importPackages(BASE_PACKAGE);
    }

    @Test
    @DisplayName("domain не залежить від application, infrastructure, presentation")
    void domain_shouldNotDependOnOtherLayers() {
        noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATION, INFRASTRUCTURE, PRESENTATION)
                .check(classes);
    }

    @Test
    @DisplayName("application не залежить від infrastructure і presentation")
    void application_shouldNotDependOnInfrastructureOrPresentation() {
        noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat()
                .resideInAnyPackage(INFRASTRUCTURE, PRESENTATION)
                .check(classes);
    }

    @Test
    @DisplayName("Layered architecture (тільки залежності між шарами)")
    void layeredArchitecture_shouldBeRespected() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy(DOMAIN)
                .layer("Application").definedBy(APPLICATION)
                .layer("Infrastructure").definedBy(INFRASTRUCTURE)
                .layer("Presentation").definedBy(PRESENTATION)
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application")
                .whereLayer("Presentation").mayOnlyAccessLayers("Application", "Domain")
                .check(classes);
    }

    @Test
    @DisplayName("UseCase класи живуть тільки в application.usecase")
    void useCases_shouldResideInApplicationUseCasePackage() {
        classes().that().haveSimpleNameEndingWith("UseCase")
                .should().resideInAPackage(BASE_PACKAGE + ".application.usecase..")
                .check(mainClasses);
    }

    @Test
    @DisplayName("UseCase класи не є інтерфейсами")
    void useCases_shouldNotBeInterfaces() {
        classes().that().haveSimpleNameEndingWith("UseCase")
                .should().notBeInterfaces()
                .check(mainClasses);
    }

    @Test
    @DisplayName("Domain entities не мають Spring-анотацій")
    void domainEntities_shouldNotHaveSpringAnnotations() {
        noClasses().that().resideInAPackage(BASE_PACKAGE + ".domain.entity")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .orShould().beAnnotatedWith(org.springframework.stereotype.Repository.class)
                .orShould().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .check(classes);
    }

    @Test
    @DisplayName("Domain exceptions розширюють RuntimeException")
    void domainExceptions_shouldExtendRuntimeException() {
        classes().that().resideInAPackage(BASE_PACKAGE + ".domain.exception")
                .should().beAssignableTo(RuntimeException.class)
                .check(classes);
    }

    @Test
    @DisplayName("Infrastructure репозиторії реалізують domain репозиторії")
    void infrastructureRepos_shouldImplementDomainRepositories() {
        com.tngtech.archunit.lang.ArchCondition<com.tngtech.archunit.core.domain.JavaClass> implementsDomainRepo =
                new com.tngtech.archunit.lang.ArchCondition<>("implement a domain.repository interface") {
                    @Override
                    public void check(com.tngtech.archunit.core.domain.JavaClass javaClass,
                                      com.tngtech.archunit.lang.ConditionEvents events) {
                        boolean implements_domain_repo = javaClass.getAllRawInterfaces().stream()
                                .anyMatch(i -> i.getPackageName().startsWith(BASE_PACKAGE + ".domain.repository"));
                        if (!implements_domain_repo) {
                            events.add(com.tngtech.archunit.lang.SimpleConditionEvent.violated(
                                    javaClass,
                                    javaClass.getName() + " does not implement a domain repository interface"
                            ));
                        }
                    }
                };

        classes().that().resideInAPackage(BASE_PACKAGE + ".infrastructure.repoImplement")
                .should(implementsDomainRepo)
                .check(classes);
    }

    @Test
    @DisplayName("Controllers живуть тільки в presentation")
    void controllers_shouldResideInPresentationLayer() {
        classes().that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .should().resideInAPackage(PRESENTATION)
                .check(classes);
    }

    @Test
    @DisplayName("Controllers не звертаються до infrastructure")
    void controllers_shouldNotAccessInfrastructureDirectly() {
        noClasses().that().resideInAPackage(BASE_PACKAGE + ".presentation..")
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                .check(classes);
    }

    @Test
    @DisplayName("@Service тільки в application і infrastructure")
    void springService_shouldNotBeInDomainOrPresentation() {
        noClasses().that().resideInAnyPackage(DOMAIN, PRESENTATION)
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(classes);
    }

    @Test
    @DisplayName("@Repository тільки в infrastructure")
    void springRepository_shouldOnlyBeInInfrastructure() {
        noClasses().that().resideOutsideOfPackage(INFRASTRUCTURE)
                .should().beAnnotatedWith(org.springframework.stereotype.Repository.class)
                .check(classes);
    }
}