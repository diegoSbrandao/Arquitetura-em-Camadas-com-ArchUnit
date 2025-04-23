package br.com.diegobrandao.archunit.ArchUnit.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.FailureReport;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayerArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("br.com.diegobrandao.archunit.ArchUnit");

    @Test
    @DisplayName("A arquitetura em camadas deve ser respeitada")
    void testLayerDependencies() {
        // Definindo nossa arquitetura em camadas
        Architectures.LayeredArchitecture layeredArchitecture = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .layer("Domain").definedBy("..domain..")

                // Definindo as regras de acesso entre camadas
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository");

        EvaluationResult result = layeredArchitecture.evaluate(importedClasses);

        if (result.hasViolation()) {
            FailureReport report = result.getFailureReport();
            // Personalizando a mensagem de erro
            StringBuilder message = new StringBuilder();
            message.append("\n\n======= VIOLAÇÕES DE ARQUITETURA DETECTADAS! =======\n");
            message.append("As seguintes violações da arquitetura em camadas foram encontradas:\n\n");

            report.getDetails().forEach(detail -> {
                message.append("• ").append(detail).append("\n");
            });

            message.append("\n=== LEMBRETE DAS REGRAS DE ARQUITETURA: ===\n");
            message.append("- Controllers não podem ser acessados por nenhuma camada\n");
            message.append("- Services só podem ser acessados por Controllers\n");
            message.append("- Repositories só podem ser acessados por Services\n");
            message.append("- Domain pode ser acessado por Controllers, Services e Repositories\n");
            message.append("===============================================\n");

            assertTrue(false, message.toString());
        }
    }

    @Test
    @DisplayName("Classes de Repository não devem acessar Controllers")
    void repositoriesShouldNotAccessControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..repository..")
                .should().accessClassesThat().resideInAPackage("..controller..")
                .because("Repositories pertencem à camada de dados e não devem conhecer a camada de apresentação!");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Classes de Service não devem acessar Controllers")
    void servicesShouldNotAccessControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().accessClassesThat().resideInAPackage("..controller..")
                .because("Services pertencem à camada de negócios e não devem conhecer a camada de apresentação!");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repositories só devem ser acessados por Services")
    void repositoriesShouldOnlyBeAccessedByServices() {
        ArchRule rule = classes()
                .that().resideInAPackage("..repository..")
                .should().onlyBeAccessed().byClassesThat().resideInAPackage("..service..")
                .because("Repositories só devem ser acessados pela camada de serviço para manter o encapsulamento!");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Convenções de nomenclatura devem ser seguidas")
    void namingConventionsShouldBeFollowed() {
        // Verificando se classes controller terminam com Controller
        ArchRule controllerNaming = classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .because("Classes na camada controller devem ter nomes terminados com 'Controller'");

        // Verificando se classes service terminam com Service
        ArchRule serviceNaming = classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .because("Classes na camada service devem ter nomes terminados com 'Service'");

        // Verificando se classes repository terminam com Repository
        ArchRule repositoryNaming = classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .because("Classes na camada repository devem ter nomes terminados com 'Repository'");

        controllerNaming.check(importedClasses);
        serviceNaming.check(importedClasses);
        repositoryNaming.check(importedClasses);
    }

    @Test
    @DisplayName("Detectar ciclos de dependência entre pacotes")
    void noCyclicDependencies() {
        // Padrão de pacote ajustado para corresponder à estrutura atual
        ArchRule rule = slices()
                .matching("br.com.diegobrandao.archunit.ArchUnit.(*)..")
                .should().beFreeOfCycles()
                .because("Ciclos de dependência tornam o código difícil de manter e evoluir!");

        rule.check(importedClasses);
    }
}