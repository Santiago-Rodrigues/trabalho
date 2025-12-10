package org.springframework.samples.petclinic.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes funcionais Selenium: funcionalidade de cadastro/edição de Pets.
 * Casos: 1) Criação válida, 2) Nome em branco, 3) Tipo ausente,
 * 4) Data inválida, 5) Edição com duplicidade de nome
 */
public class PetFunctionalIT {

	private WebDriver driver;

	@BeforeEach
	void setUp() {
		driver = new ChromeDriver();
	}

	@AfterEach
	void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	void createPetValid() {
		driver.get("http://localhost:8080/owners/1/pets/new");
		driver.findElement(By.name("name")).sendKeys("Betty");
		driver.findElement(By.name("birthDate")).sendKeys("2015-02-12");
		driver.findElement(By.name("type")).sendKeys("hamster");
		driver.findElement(By.cssSelector("button[type=submit]")).click();
		assertThat(driver.getCurrentUrl()).contains("/owners/1");
	}

	@Test
	void createPetBlankName() {
		driver.get("http://localhost:8080/owners/1/pets/new");
		driver.findElement(By.name("name")).sendKeys("   ");
		driver.findElement(By.cssSelector("button[type=submit]")).click();
		assertThat(driver.getPageSource()).contains("required");
	}

	@Test
	void createPetMissingType() {
		driver.get("http://localhost:8080/owners/1/pets/new");
		driver.findElement(By.name("name")).sendKeys("Betty");
		driver.findElement(By.name("birthDate")).sendKeys("2015-02-12");
		driver.findElement(By.cssSelector("button[type=submit]")).click();
		assertThat(driver.getPageSource()).contains("required");
	}

	@Test
	void createPetInvalidBirthDate() {
		driver.get("http://localhost:8080/owners/1/pets/new");
		driver.findElement(By.name("name")).sendKeys("Betty");
		driver.findElement(By.name("birthDate")).sendKeys("2015/02/12");
		driver.findElement(By.cssSelector("button[type=submit]")).click();
		assertThat(driver.getPageSource()).contains("typeMismatch");
	}

	@Test
	void editPetDuplicateName() {
		driver.get("http://localhost:8080/owners/1/pets/1/edit");
		WebElement name = driver.findElement(By.name("name"));
		name.clear();
		name.sendKeys("petty"); // duplicado
		driver.findElement(By.cssSelector("button[type=submit]")).click();
		assertThat(driver.getPageSource()).contains("duplicate");
	}
}