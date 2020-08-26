package br.ce.wcaquin.estrategia2;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.dao.SaldoDAO;
import br.ce.wcaquino.dao.impl.SaldoDAOImpl;
import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.TipoTransacao;
import br.ce.wcaquino.entidades.Transacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.TransacaoService;
import br.ce.wcaquino.service.UsuarioService;
import br.ce.wcaquino.utils.DataUtils;

public class CalculoSaldoTest {
	//1 usu�rio
	// 1 conta
	// 1 transa��o
	
	//+deve considerar transa��es do mesmo usu�rio
	//+deve considerar apenas as transa��es da mesma conta
	//+deve considerar apenas as transa��es pagas
	//+deve considerar apenas as transa��es at� a data corrente
	//deve somar receitas
	//deve subtrair despesas
	
	@Test
	public void deveCalcularSaldoCorreto() throws Exception {
		UsuarioService usuarioService = new UsuarioService();
		ContaService contaService =  new ContaService();
		TransacaoService transacaoService = new TransacaoService();
		
		//usu�rios
		Usuario usuario = usuarioService.salvar(new Usuario("Usu�rio", "email@email.com", "123"));
		Usuario usuarioAlternativo = usuarioService.salvar(new Usuario("Usu�rio Alternativo", "email2@qualquer.com", "123"));	
		
		//contas
		Conta conta =  contaService.salvar(new Conta("Conta principal", usuario.getId()));
		Conta contaSecundaria =  contaService.salvar(new Conta("Conta secundaria", usuario.getId()));
		Conta contaUsuarioAlternativo =  contaService.salvar(new Conta("Conta usu�rio alternativo", usuarioAlternativo.getId()));
		
		//transa��es
		
		//2
		//Transa��o Correta / Saldo = 2
		transacaoService.salvar(new Transacao("Transa��o inicial", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 2d, true, conta, usuario));
		
		//Transa��o usu�rio alternativo
		transacaoService.salvar(new Transacao("Transa��o outro usu�rio", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 4d, true, contaUsuarioAlternativo, usuarioAlternativo));
		
		//Transa��o de outro conta Saldo permanece 2
		transacaoService.salvar(new Transacao("Transa��o outro conta", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 8d, true, contaSecundaria, usuario));
		
		//Transa��o pendente Saldo permanece 2
		transacaoService.salvar(new Transacao("Transa��o pendente", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 16d, false, conta, usuario));
		
		//Transa��o dia anterior Saldo muda para 34
		transacaoService.salvar(new Transacao("Transa��o passada", "Envolvido", TipoTransacao.RECEITA,
				DataUtils.obterDataComDiferencaDias(-1), 32d, true, conta, usuario));

		//Transa��o dia posterior Saldo permanece 34
		transacaoService.salvar(new Transacao("Transa��o futura", "Envolvido", TipoTransacao.RECEITA,
				DataUtils.obterDataComDiferencaDias(1), 64d, true, conta, usuario));	
		
		//Transa��o despesa Saldo  34 - 128 = -94 
		transacaoService.salvar(new Transacao("Transa��o futura", "Envolvido", TipoTransacao.DESPESA,
				new Date(), 128d, true, conta, usuario));
		
		//Testes de saldo com valor negativo d� azar Saldo -94 + 256 = 162 
		transacaoService.salvar(new Transacao("Transa��o da sorte", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 256d, true, conta, usuario));
		
		SaldoDAO saldoDao = new SaldoDAOImpl();
		Assert.assertEquals(new Double(162d), saldoDao.getSaldoConta(conta.getId()));
		Assert.assertEquals(new Double(8d), saldoDao.getSaldoConta(contaSecundaria.getId()));
		Assert.assertEquals(new Double(4d), saldoDao.getSaldoConta(contaUsuarioAlternativo.getId()));
		
	}
	
}
