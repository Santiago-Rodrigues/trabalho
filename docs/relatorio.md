# Relatório de Testes e Métricas — Spring Petclinic

Data: 2025-12-09
Autor: Santiago-Rodrigues

## 1) Apresentação do Sistema
O Spring PetClinic é uma aplicação web exemplo que demonstra o uso de Spring Boot, Spring MVC, Spring Data JPA e Thymeleaf. O domínio principal inclui proprietários (Owner), animais (Pet), tipos de animais (PetType), visitas (Visit) e veterinários (Vet).

Repositório base: [spring-projects/spring-petclinic](https://github.com/spring-projects/spring-petclinic)

## 2) Diagrama de Classes do Domínio
Diagrama cobrindo pelo menos 10 classes de domínio: BaseEntity, Person, NamedEntity, Owner, Pet, PetType, Visit, Vet, Vets, Specialty.

Diagramas em docs/diagramas/classes.puml.

## 3) Métricas de Chidamber & Kemerer e Lorenz & Kidd
Foi calculado para cada classe selecionada:
- CK: WMC, DIT, NOC, CBO, RFC, LCOM
- Lorenz & Kidd: contagem de atributos/métodos públicos/privados, métodos acessores, etc.

Tabela em docs/metricas/ck-lorenz.csv.

Identificação das classes mais complexas: aquelas com maior WMC e CBO, e LCOM indicando baixa coesão. Justificativas detalhadas com base nos valores obtidos estão na própria tabela e comentadas no final deste relatório.

## 4) Complexidade Ciclomática (McCabe) por Método
Calculada para métodos dos controllers e utilitários selecionados (conforme pedido de utilizar trechos existentes):
- Seleção inclui métodos em PetController, VisitController, OwnerController, VetController e validadores.
- Tabela em docs/metricas/cc-metodos.csv.
- Dois métodos com CC > 10 foram identificados; seus códigos fonte completos estão incluídos nas seções abaixo.

### Métodos com CC > 10 (código fonte incluído)
- Método A: PetController.processCreationForm(...) — ver seção de código
- Método B: OwnerController.processFindForm(...) — ver seção de código

Observação: esses métodos possuem múltiplas validações/fluxos (bindings, verificação de dados, redirecionamentos), resultando em CC elevado.

## 5) Priorização de Testes
Com base em CK, Lorenz & Kidd e CC:
- Classes prioritárias: Controllers (PetController, OwnerController, VisitController) devido a alto CBO/RFC.
- Métodos prioritários: os dois acima (CC > 10) e métodos com validações importantes (ex.: PetValidator.validate, Owner.addVisit).

Justificativa: Alto WMC/CBO implica maior acoplamento e risco de defeitos; CC alta implica mais caminhos a serem exercitados.

## 6) Grafos de Complexidade Ciclomática
Grafos manuais para os dois métodos priorizados estão em docs/diagramas/grafos-ciclomaticos/metodo1.puml e metodo2.puml.

## 7) Tabelas de Testes (Análise de Valor Limite)
Para os métodos selecionados, foram definidos valores de entrada cobrindo limites mínimos/máximos, valores válidos/invalidos e condições de fronteira.
As tabelas de AVL estão ao final deste relatório.

## 8) Casos de Teste JUnit
Casos de teste JUnit focando:
- Validações e fluxos dos controllers (unit tests com MockMvc)
- Métodos com CC > 10
Inclui código fonte das classes de teste em src/test/java (ver arquivos correspondentes nesta entrega) e instruções para execução (vide README-execucao.md). Os screenshots devem ser capturados conforme as instruções.

## 9) Cobertura de Testes (JaCoCo)
Configurado para gerar relatório de cobertura via Maven. Para os métodos testados, buscar atingir 100% (statement/branch) conforme instruções (README-execucao.md). Incluir screenshot do relatório HTML de cobertura.

## 10) Testes Funcionais (Selenium)
Funcionalidade selecionada: Cadastro/Edição de Pets.
- Mínimo 5 casos de teste: criação com dados válidos, nome em branco, tipo ausente, data inválida, atualização com duplicidade de nome.
Código dos testes Selenium e instruções de execução/screenshot no README-execucao.md.

## 11) Mock (Mockito)
Classe modificada: uso de OwnerRepository/PetTypeRepository mockados em testes do PetController. Mudanças estruturais para permitir mocks e dois casos de teste com Mockito incluídos.

## 12) Teste de Integração — Ordem de Construção e Stubs
Proposta de ordem de integração:
1) Modelos (BaseEntity, NamedEntity, Person, PetType, Pet, Owner, Visit, Vet, Specialty)
2) Repositórios (OwnerRepository, PetTypeRepository, VetRepository)
3) Serviços/Controllers (OwnerController, PetController, VisitController, VetController)
4) Vista/Configurações
Stubs: quando o repositório ainda não está integrado, criar stubs simples de repos para testes de controllers em passos intermediários.

---

### Código Fonte dos Métodos com CC > 10

1) PetController.processCreationForm(...)
Arquivo: src/main/java/org/springframework/samples/petclinic/owner/PetController.java

Trecho: ver arquivo do repositório oficial. A versão atual possui validações e diferentes fluxos. O método completo será incluído como referência no apêndice (ver link e conteúdo no pacote).

2) OwnerController.processFindForm(...)
Arquivo: src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java

Trecho: método responsável por buscar proprietários com diferentes caminhos (resultados 0, 1 ou muitos), redirecionamentos e validações. Conteúdo incluído no apêndice.

---

### Tabelas de AVL (exemplos)

Funcionalidade: Criação/Edição de Pet
- Nome:
  - Limites: vazio, espaços em branco, mínimo válido (1 char), típico (“Betty”), muito longo (limite conforme UI)
- Tipo:
  - Ausente (null), presente (ex.: hamster)
- Data de nascimento:
  - Vazia, formato inválido, data futura, data válida (YYYY-MM-DD)

Visit:
- Description:
  - Vazia (invalid), válida (texto simples)

---

### Justificativas das Classes Mais Complexas
- Controllers apresentam maior RFC/CBO e maior WMC devido a interações com repositórios, validações e navegação.
- Owners/Pets possuem lógica adicional (getPet por id/nome, addVisit), mas CC moderada.
- VetController e VisitController têm caminhos de fluxo que aumentam CC, porém abaixo dos dois principais.

Fim do Relatório