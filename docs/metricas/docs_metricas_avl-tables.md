```markdown
# Tabelas de Análise de Valor Limite (AVL)

As tabelas abaixo apresentam os valores de entrada (limites mínimos/máximos, valores válidos/inválidos e fronteiras) para os métodos selecionados do relatório. Cada linha é um caso de teste proposto com o resultado esperado e observações sobre como executá-lo (unit test ou teste funcional).

Observação: nos controllers que recebem objetos validados (@Valid / BindingResult), os casos descrevem o estado do objeto (por ex. campo vazio) ou do BindingResult (erros presentes). Para testes unitários use MockMvc/Mocked Repositories ou chamadas diretas com BindingResult simulados (BeanPropertyBindingResult). Para testes funcionais (Selenium) use os formulários da UI.

-------------------------------------------------------------------------------
1) Método: PetController.processCreationForm(Owner owner, Pet pet, BindingResult result, int ownerId, ModelMap model)
Arquivo: src/main/java/.../owner/PetController.java
Comportamento principal: valida pet (nome, tipo, birthDate), verifica duplicidade do nome para o owner, salva e redireciona em sucesso; retorna formulário em caso de erro.

Campos/entradas relevantes:
- pet.name (String)
- pet.type (PetType) — null ou válido
- pet.birthDate (String/LocalDate) — null, formato inválido, data futura
- ownerId (int) — owner existente ou não
- Estado do BindingResult (tem erros já ou não)

Tabela AVL (casos de teste)

| TC | pet.name        | pet.type       | pet.birthDate      | ownerId | BindingResult (pré) | Resultado esperado / Observações |
|----:|-----------------|----------------|--------------------|--------:|---------------------|-----------------------------------|
| TC-P1 | "Betty"         | PetType válido | "2015-02-12"       | 1       | sem erros           | Sucesso: redirect para /owners/{id}. (Caminho nominal) |
| TC-P2 | "" (vazio)      | PetType válido | "2015-02-12"       | 1       | sem erros           | Erro: BindingResult marca campo 'name' → retorna form (mensagem 'required'). |
| TC-P3 | "   " (espaços) | PetType válido | "2015-02-12"       | 1       | sem erros           | Mesmo que TC-P2 → erro de validação (trim/hasText) → retorna form. |
| TC-P4 | "B" (1 char)    | PetType válido | "2015-02-12"       | 1       | sem erros           | Válido (limite inferior do nome) → redirect. |
| TC-P5 | nome muito longo (ex.: 300 chars) | PetType válido | "2015-02-12" | 1 | sem erros | Dependendo de restrição de UI/DB: pode aceitar ou gerar erro (ver validação de tamanho). Esperado: erro de validação ou falha de persistência — verificar regra do modelo. |
| TC-P6 | "Betty"         | null           | "2015-02-12"       | 1       | sem erros           | Se pet.isNew() && pet.getType()==null → Validator rejeita 'type' → retorna form (mensagem 'required'). |
| TC-P7 | "Betty"         | PetType válido | null               | 1       | sem erros           | birthDate null → Validator rejeita 'birthDate' → retorna form. |
| TC-P8 | "Betty"         | PetType válido | data futura (hoje+1) | 1     | sem erros           | Se há verificação de data futura (UI/Formatter) → tipoMismatch ou validação → retorna form. |
| TC-P9 | "petty" (duplicado) | PetType válido | "2015-02-12" | 1       | sem erros           | Duplicidade detectada (owner já tem pet com mesmo nome) → adiciona erro 'duplicate' → retorna form. |
| TC-P10| "Betty"         | PetType válido | "2015-02-12"       | owner inválido (não existe) | sem erros | Se owner não existir → exceção/IllegalArgumentException ao buscar owner; testar fluxo de erro/tratamento. |

Recomendações de execução:
- Unit tests: use BeanPropertyBindingResult para simular BindingResult e repos mockados (OwnerRepository, PetTypeRepository).
- Selenium tests: preencher formulário via UI e verificar mensagens na página; capturar screenshots dos erros e do redirect.
- Cobertura: garantir que todos ramos (erros por campo, duplicidade, success) sejam cobertos.

-------------------------------------------------------------------------------
2) Método: OwnerController.processFindForm(String lastNameFragment, BindingResult result, Model model)
Arquivo: src/main/java/.../owner/OwnerController.java
Comportamento: busca owners por fragmento de nome; três caminhos:
 - fragmento em branco → lista paginada (muitos owners)
 - 0 resultados → retorna formulário com erro ("notFound")
 - 1 resultado → redirect para detalhes daquele owner
 - >1 resultados → exibe lista com paginação

Entradas relevantes:
- lastNameFragment (String)
- Resultado da consulta repository.findByLastNameLike(...) — 0, 1, múltiplos

Tabela AVL (casos de teste)

| TC | lastNameFragment           | Resultado da busca (simular) | Resultado esperado / Observações |
|----:|---------------------------|-------------------------------|-----------------------------------|
| TC-O1 | "" (vazio)                | N/A                           | Retorna lista paginada de owners (exibir página com vários owners). |
| TC-O2 | " " (espaços)             | N/A                           | Igual a vazio → lista paginada. |
| TC-O3 | "A" (1 char)              | 0 results                     | Mostra form de busca com erro 'notFound'. |
| TC-O4 | "NonexistentName"         | 0 results                     | Mostra form de busca com erro 'notFound'. |
| TC-O5 | "Douglas"                 | 1 result                      | Redirect para /owners/{id} (detalhe do único owner). |
| TC-O6 | "Da"                      | 2+ results                    | Mostra página com lista de resultados (paginada). |
| TC-O7 | string com case diferente ("dOuGlAs") | 1 result (case-insensitive) | Verificar comportamento de busca (case-insensitive) → redirect. |
| TC-O8 | nome muito longo (200 chars) | 0 results / possivelmente truncado | Geralmente 0 results → erro 'notFound' ou tratamento específico. |

Recomendações de execução:
- Unit tests: mock do repository para retornar coleções com 0/1/muitos owners.
- Selenium: testar a UI de busca de proprietários com inputs correspondentes e verificar redirecionamentos e mensagens.
- Focar nos limites: vazio vs 1 char vs resultado único vs múltiplos.

-------------------------------------------------------------------------------
3) Método: VisitController.processNewVisitForm(Owner owner, int petId, @Valid Visit visit, BindingResult result, RedirectAttributes)
Arquivo: src/main/java/.../owner/VisitController.java
Comportamento: se BindingResult tem erros → retorna form; se OK → owner.addVisit(petId, visit), owners.save(owner), flash message e redirect.

Entradas relevantes:
- owner existente (simulado no ModelAttribute loadPetWithVisit)
- petId válido ou inválido
- visit.description (String) — NotBlank
- visit.date (LocalDate) — se presente/ausente

Tabela AVL (casos de teste)

| TC | petId | visit.description | visit.date        | Resultado esperado / Observações |
|----:|------:|-------------------|-------------------|-----------------------------------|
| TC-V1 | válido (ex. 1) | "Vacina"          | hoje (ou padrão)   | Sucesso: owner.addVisit + save + redirect com flash message. |
| TC-V2 | válido         | "" (vazio)        | hoje              | Validação falha (description required) → retorna form. |
| TC-V3 | válido         | "   " (espaços)   | hoje              | Falha de validação → retorna form. |
| TC-V4 | inválido (pet não existe) | "Consulta" | hoje           | loadPetWithVisit deveria lançar IllegalArgumentException antes → teste de fluxo de erro (esperar exceção). |
| TC-V5 | petId null / omitido | "Consulta"  | hoje              | Dependendo do call-site, pode não ocorrer; teste de integração para confirmar comportamento. |

Recomendações:
- Unit test do controller: simular ownerRepository.findById(ownerId) retornando owner com pet existente ou Optional.empty para TC-V4.
- Selenium: testar envio do formulário de nova visita com descrição vazia e válida; verificar mensagens e redirect.

-------------------------------------------------------------------------------
4) Método: PetValidator.validate(Object obj, Errors errors)
Arquivo: src/main/java/.../owner/PetValidator.java
Comportamento: rejeita name vazio, se pet.isNew() && type==null rejeita type, se birthDate==null rejeita birthDate.

Entradas relevantes:
- pet.name
- pet.type (null / válido)
- pet.birthDate (null / valido)

Tabela AVL (casos de teste)

| TC | pet.name        | pet.isNew() | pet.type       | pet.birthDate | Resultado esperado/Observações |
|----:|-----------------|------------:|----------------|---------------|---------------------------------|
| TC-VD1 | "Betty"         | true        | PetType válido | 2015-02-12    | Sem rejeições (validator não adiciona erros). |
| TC-VD2 | "" (vazio)      | true        | PetType válido | 2015-02-12    | errors.rejectValue("name","required") → error presente. |
| TC-VD3 | "Betty"         | true        | null           | 2015-02-12    | errors.rejectValue("type","required") → error presente (quando isNew). |
| TC-VD4 | "Betty"         | true        | PetType válido | null          | errors.rejectValue("birthDate","required") → error presente. |
| TC-VD5 | "Betty"         | false       | null           | null          | Se !isNew, tipo null não deve ser rejeitado pelo type check (apenas isNew condiciona type check). birthDate null ainda rejeitado. |

Recomendações:
- Unit test direto do validator; usar BeanPropertyBindingResult para captar erros e asserções.

-------------------------------------------------------------------------------
5) Método: Owner.addVisit(Integer petId, Visit visit)
Arquivo: src/main/java/.../owner/Owner.java
Comportamento: Valida não-nulo de petId e visit (Assert.notNull), busca pet por id (getPet) e se encontrado chama pet.addVisit(visit); caso contrário lança Assert exception / IllegalState.

Entradas relevantes:
- petId: null, id existente, id inexistente
- visit: null, visit válido

Tabela AVL (casos de teste)

| TC | petId      | visit (obj) | Resultado esperado / Observações |
|----:|-----------:|-------------|-----------------------------------|
| TC-A1 | 7 (existe) | Visit válido | Sucesso: visita adicionada ao pet. |
| TC-A2 | null       | Visit válido | Assert.notNull(petId) lança IllegalArgumentException (ou IllegalState) — testar exceção. |
| TC-A3 | 999 (não existe) | Visit válido | getPet retorna null → Assert.notNull(pet) lança erro "Invalid Pet identifier!" → testar exceção. |
| TC-A4 | 7 (existe) | null         | Assert.notNull(visit) lança exceção → testar exceção. |

Recomendações:
- Unit tests diretamente na classe Owner; construa Owner com pets e invoque addVisit para comportamento nominal; teste exceções via assertThrows.

-------------------------------------------------------------------------------
Observações gerais sobre AVL e priorização
- Para cada método priorizado, os casos de teste acima cobrem:
  - Valores limites (nome vazio vs 1 char vs muito longo)
  - Condições de fronteira (duplicidade: existir 1 pet com mesmo nome → ramo diferente)
  - Entradas inválidas (null, dados em formato incorreto para datas)
  - Estados excepcionais (owner/pet inexistente → exceção)
- Priorize a criação de testes que cubram todos os ramos condicionais identificados na análise de complexidade (cada if/else/branch).
- Ao implementar JUnit tests, associe cada TC a um teste unitário ou de integração (MockMvc para controllers, testes diretos para validators e modelos).
- Para requisitos de cobertura 100% dos métodos testados (item 7 do relatório), certifique-se de que cada ramo lógico do método tenha seu caso de teste correspondente (incluindo exceções).

===============================================================================
Anexo: mapeamento sugerido entre TCs e arquivos de teste
- PetController: TC-P1 .. TC-P10 → src/test/java/.../owner/PetControllerTests.java (usar MockMvc e Mockito; e/ou PetControllerMockTests.java para chamadas diretas)
- OwnerController: TC-O1 .. TC-O8 → src/test/java/.../owner/OwnerControllerTests.java (mock repository)
- VisitController: TC-V1 .. TC-V5 → src/test/java/.../owner/VisitControllerTests.java (mock repository or integration)
- PetValidator: TC-VD1 .. TC-VD5 → src/test/java/.../owner/PetValidatorTests.java (teste unitário do validator)
- Owner.addVisit: TC-A1 .. TC-A4 → src/test/java/.../owner/OwnerUnitTests.java (teste direto no modelo)

Fim das Tabelas AVL
```