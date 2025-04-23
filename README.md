# Exemplo de Arquitetura em Camadas com Verificação via ArchUnit

Este projeto demonstra a implementação de uma arquitetura em camadas e o uso do ArchUnit para verificar automaticamente a integridade dessa arquitetura.

## Sobre o Projeto

Este é um exemplo prático que acompanha o artigo "ArchUnit: Garantindo a Integridade Arquitetural de Aplicações Java". O projeto implementa uma simples aplicação de gerenciamento de usuários seguindo princípios de arquitetura em camadas.

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas clássica:

```
br.com.diegobrandao.archunit.ArchUnit/
├── controller/      (Camada de apresentação)
├── service/         (Camada de serviço/negócio)
├── repository/      (Camada de acesso a dados - interfaces)  
└── domain/          (Camada de domínio - modelos)
```

### Camadas e Responsabilidades

1. **Domain (Domínio)**: Entidades que representam conceitos do domínio do negócio
   - Exemplo: `User.java`

2. **Repository (Repositório)**: Interfaces que definem o contrato para acesso a dados
   - Exemplo: `UserRepository.java` (interface)   

3. **Service (Serviço)**: Regras de negócio e orquestração
   - Exemplo: `UserService.java`

4. **Controller (Controlador)**: Interação com o usuário/cliente
   - Exemplo: `UserController.java`

## Padrões Arquiteturais Implementados

### Separação de Interface e Implementação

Conforme discutido no artigo, a camada Repository segue o princípio de separação entre interface e implementação. Isso proporciona:

- **Inversão de Dependência**: As camadas superiores dependem de abstrações, não de implementações concretas
- **Testabilidade**: Facilita a criação de mocks para testes unitários
- **Flexibilidade**: Permite trocar implementações (banco de dados, em memória, etc.) sem afetar as camadas superiores

Exemplo da interface Repository:

```java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
}
```

## Verificação da Arquitetura com ArchUnit

O projeto inclui testes automatizados usando ArchUnit que verificam se a arquitetura em camadas está sendo respeitada:

1. **Dependências entre camadas**: Garantindo que a camada de serviço não acesse controladores, repositórios não acessem serviços, etc.

2. **Convenções de nomenclatura**: Verificando se as classes de cada camada seguem padrões de nomenclatura estabelecidos

3. **Acesso a repositórios**: Garantindo que repositórios só são acessados pela camada de serviço

4. **Detecção de ciclos**: Prevenindo ciclos de dependência entre componentes

### Mensagens de Erro Personalizadas

Um dos recursos mais úteis implementados é a personalização de mensagens de erro quando uma regra arquitetural é violada. Isso torna os testes muito mais informativos e facilita a correção dos problemas.

Exemplo de implementação:

```java
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
            // Regras de acesso entre camadas...

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
```

Quando uma violação é detectada, uma mensagem de erro estruturada é exibida, contendo:

1. Um cabeçalho chamativo para identificar rapidamente o problema
2. Lista detalhada de todas as violações encontradas
3. Um lembrete das regras arquiteturais que devem ser seguidas
4. Formatação visual clara que destaca as informações importantes

Esta abordagem transforma mensagens de erro genéricas em documentação útil, ajudando a equipe a entender rapidamente o que está errado e como corrigir.

## Aprendizados Importantes

Durante o desenvolvimento deste exemplo, encontramos algumas lições importantes:

1. **Autoacesso em verificações arquiteturais**: O ArchUnit identifica quando uma classe acessa seus próprios campos ou métodos, o que pode gerar falsos positivos em regras como "repositories só devem ser acessados por services"

2. **Importância da separação interface/implementação**: A abordagem com interfaces para repositórios não apenas melhora o design arquitetural, mas também evita problemas na verificação com ArchUnit

3. **Padrões de pacotes específicos**: É crucial ajustar os padrões de pacotes nos testes ArchUnit para corresponderem à estrutura real do projeto

4. **Mensagens de erro informativas**: Investir tempo para criar mensagens de erro personalizadas melhora drasticamente a experiência do desenvolvedor e acelera a correção de problemas

### Exemplo de Erro Personalizado

Quando uma regra arquitetural é violada, o teste gera uma saída como esta:

```
======= VIOLAÇÕES DE ARQUITETURA DETECTADAS! =======
As seguintes violações da arquitetura em camadas foram encontradas:

• Method br.com.diegobrandao.archunit.ArchUnit.service.UserService.getControllerData() 
  calls method br.com.diegobrandao.archunit.ArchUnit.controller.UserController.getData()

=== LEMBRETE DAS REGRAS DE ARQUITETURA: ===
- Controllers não podem ser acessados por nenhuma camada
- Services só podem ser acessados por Controllers
- Repositories só podem ser acessados por Services
- Domain pode ser acessado por Controllers, Services e Repositories
===============================================
```

Essa formatação rica e informativa torna imediatamente claro:
- Qual método está violando a arquitetura
- Qual regra específica foi quebrada
- Quais são as regras arquiteturais corretas que deveriam ser seguidas

## Relacionamento com o Artigo

Este exemplo serve como demonstração prática dos conceitos apresentados no artigo "ArchUnit: Garantindo a Integridade Arquitetural de Aplicações Java", especialmente:

- Implementação de uma arquitetura em camadas
- Configuração de testes com ArchUnit
- Resolução de problemas comuns ao usar ArchUnit
- Aplicação de padrões arquiteturais como Repository Pattern

## Como Executar

1. Clone o repositório
2. Execute `mvn clean test` para rodar os testes de arquitetura
3. Explore os testes em `src/test/java/br/com/diegobrandao/archunit/ArchUnit/architecture/LayerArchitectureTest.java`

## Conclusão

Este projeto demonstra como é possível garantir a integridade de uma arquitetura em camadas usando ArchUnit. Ao integrar esses testes no seu pipeline de CI/CD, você pode detectar violações arquiteturais precocemente e manter a qualidade do código ao longo do tempo.
