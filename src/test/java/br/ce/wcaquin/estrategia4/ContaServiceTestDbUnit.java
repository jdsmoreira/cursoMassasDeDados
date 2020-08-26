package br.ce.wcaquin.estrategia4;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.assertion.DiffCollectingFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquin.dbunit.ImportExport;
import br.ce.wcaquino.dao.utils.ConnectionFactory;
import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.UsuarioService;

public class ContaServiceTestDbUnit {

	ContaService service = new ContaService();
	UsuarioService userService = new UsuarioService();
	private List diffList;

	@Test
	public void testInserir() throws Exception {
		ImportExport.importarBanco("estrategia4_inserirConta.xml");
		service.printAll();
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		Conta contaSalva = service.salvar(conta);
		Assert.assertNotNull(contaSalva.getId());
		userService.printAll();
		service.printAll();
	}

	@Test
	public void testInserir_Filter() throws Exception {
		ImportExport.importarBanco("estrategia4_inserirConta.xml");
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		service.salvar(conta);

		// estado atual do banco
		DatabaseConnection dbCon = new DatabaseConnection(ConnectionFactory.getConnection());
		IDataSet estadoFinalBanco = dbCon.createDataSet();

		// estado esperado (XML)
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		FlatXmlDataSet dataSetEsperado = builder
				.build(new FileInputStream("massas" + File.separator + "estrategia4_inserirConta_saida.xml"));

		// Comparar os dois estados
		ITable contasAtualFiltrada = DefaultColumnFilter.excludedColumnsTable(estadoFinalBanco.getTable("contas"), new String[] {"id"});
		ITable contasEsperadasFiltradas = DefaultColumnFilter.excludedColumnsTable(dataSetEsperado.getTable("contas"), new String[] {"id"});

		
		ITable usuarioAtualFiltrada = DefaultColumnFilter.excludedColumnsTable(estadoFinalBanco.getTable("usuarios"), new String[] {"conta_principal_id"});
		ITable usuarioEsperadasFiltradas = DefaultColumnFilter.excludedColumnsTable(dataSetEsperado.getTable("usuarios"), new String[] {"conta_principal_id"});
		
		Assertion.assertEquals(contasEsperadasFiltradas, contasAtualFiltrada);
		Assertion.assertEquals(usuarioEsperadasFiltradas, usuarioAtualFiltrada);
		
	}

	@Test
	public void testInserir_Assertion() throws Exception {
		ImportExport.importarBanco("estrategia4_inserirConta.xml");
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		Conta contaSalva = service.salvar(conta);

		// estado atual do banco
		DatabaseConnection dbCon = new DatabaseConnection(ConnectionFactory.getConnection());
		IDataSet estadoFinalBanco = dbCon.createDataSet();

		// estado esperado (XML)
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		FlatXmlDataSet dataSetEsperado = builder
				.build(new FileInputStream("massas" + File.separator + "estrategia4_inserirConta_saida.xml"));

		// Comparar os dois estados
//		Assertion.assertEquals(dataSetEsperado, estadoFinalBanco);
		DiffCollectingFailureHandler handler = new DiffCollectingFailureHandler();
		Assertion.assertEquals(dataSetEsperado, estadoFinalBanco, handler);
		List<Difference> erros = handler.getDiffList();
		boolean erroReal = false;
		for (Difference erro : erros) {
			System.out.println(erro.toString());
			if (erro.getActualTable().getTableMetaData().getTableName().equals("contas")) {
				if (erro.getColumnName().equals("id")) {
					if (erro.getActualValue().toString().equals(contaSalva.getId().toString())) {
						continue;
					} else {
						System.out.println("Id errado mesmo!");
						erroReal = true;
					}
				} else {
					erroReal = true;
				}
			} else {
				erroReal = true;
			}
		}
		Assert.assertFalse(erroReal);
	}

	@Test
	public void testAlterar() throws Exception {
		ImportExport.importarBanco("estrategia4_umaConta.xml");
		Conta contaTeste = service.findByName("Conta para testes");
		contaTeste.setNome("Conta alterada");
		service.printAll();
		Conta contaAlterada = service.salvar(contaTeste);
		Assert.assertEquals("Conta alterada", contaAlterada.getNome());
		service.printAll();
	}

	@Test
	public void testConsultar() throws Exception {
		ImportExport.importarBanco("estrategia4_umaConta.xml");
		Conta contaBuscada = service.findById(1L);
		Assert.assertEquals("Conta para testes", contaBuscada.getNome());
		service.printAll();
	}

	@Test
	public void testExcluir() throws Exception {
		ImportExport.importarBanco("estrategia4_umaConta.xml");
		Conta contaTeste = service.findByName("Conta para testes");
		service.printAll();
		service.delete(contaTeste);
		Conta contaBuscada = service.findById(contaTeste.getId());
		Assert.assertNull(contaBuscada);
		service.printAll();

	}

}
