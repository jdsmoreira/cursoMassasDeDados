package br.ce.wcaquin.estrategia1;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContaTesteWeb {

	public ChromeDriver driver;
	
	@Before
	public void login() {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get("http://seubarriga.wcaquino.me");

		driver.findElement(By.id("email")).sendKeys("jdwather@gmail.com");
		driver.findElement(By.id("senha")).sendKeys("Dexter&d1d1");
		driver.findElement(By.tagName("button")).click();

	
	}
	
	@Test
	public void test_1_inserir() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Adicionar")).click();
		driver.findElement(By.id("nome")).sendKeys("Conta estratégia #1");
		driver.findElement(By.tagName("button")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta adicionada com sucesso!", msg);

	}
	
	@Test
	public void test_2_consultar() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();		
		driver.findElement(By.xpath("//td[contains(text(), 'Conta estratégia #1')]/..//a")).click();
		String nomeConta = driver.findElement(By.id("nome")).getAttribute("value");
		Assert.assertEquals("Conta estratégia #1", nomeConta);
	}
	
	@Test
	public void test_3_alterar() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();		
		driver.findElement(By.xpath("//td[contains(text(), 'Conta estratégia #1')]/..//a")).click();
		driver.findElement(By.id("nome")).clear();
		driver.findElement(By.id("nome")).sendKeys("Conta estratégia #1 Alterada");
		driver.findElement(By.tagName("button")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta alterada com sucesso!", msg);
	}
	
	@Test
	public void test_4_excluir() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();		
		driver.findElement(By.xpath("//td[contains(text(), 'Conta estratégia #1')]/..//a[2]")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta removida com sucesso!", msg);
	}
	
	@After
	public void fechar() {
		driver.quit();
	}
}
