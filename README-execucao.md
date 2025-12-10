# Roteiro de Execução (Screenshots e Relatórios)

Pré-requisitos:
- JDK 17
- Maven 3.6+
- Chrome e ChromeDriver (no PATH)
- Projeto spring-petclinic rodando localmente (para testes Selenium) ou uso de MockMvc nos testes web.

## 1) Executar testes unitários JUnit
mvn -DskipTests=false clean test

Screenshot a capturar:
- Terminal com "BUILD SUCCESS"
- Opcional: resumo de testes na IDE

## 2) Gerar relatório JaCoCo (Cobertura)
mvn clean test jacoco:report

Abra target/site/jacoco/index.html e capture:
- Visão geral mostrando os pacotes testados e cobertura 100% para os métodos selecionados

## 3) Executar testes Selenium
- Configure o ChromeDriver no PATH ou via -Dwebdriver.chrome.driver
- Execute apenas a classe Selenium:
mvn -Dtest=org.springframework.samples.petclinic.selenium.PetFunctionalIT test

Screenshots:
- Asserções de sucesso nos pontos do teste (os testes salvam screenshots em target/selenium-screenshots/)

## 4) Testes com Mockito (Mock)
- Execute testes de PetController com repositórios mockados:
mvn -Dtest=org.springframework.samples.petclinic.owner.PetControllerMockTests test

Screenshots:
- Terminal com BUILD SUCCESS

## 5) Teste de Integração
- Execute:
mvn -Dtest=org.springframework.samples.petclinic.PetClinicIntegrationTests test

Screenshots:
- Terminal com BUILD SUCCESS

## 6) Conversão do Relatório para PDF
- Abra docs/relatorio.md e converta para PDF (ex.: pandoc ou impressora PDF da IDE).
- NÃO incluir este README no PDF final; o PDF deve conter apenas o conteúdo do relatório.

## Estrutura esperada de screenshots
- screenshots/
  - junit-build-success.png
  - jacoco-coverage.png
  - selenium-case1.png ... selenium-case5.png