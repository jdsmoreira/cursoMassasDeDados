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
	//1 usuário
	// 1 conta
	// 1 transação
	
	//+deve considerar transações do mesmo usuário
	//+deve considerar apenas as transações da mesma conta
	//+deve considerar apenas as transações pagas
	//+deve considerar apenas as transações até a data corrente
	//deve somar receitas
	//deve subtrair despesas
	
	@Test
	public void deveCalcularSaldoCorreto() throws Exception {
		UsuarioService usuarioService = new UsuarioService();
		ContaService contaService =  new ContaService();
		TransacaoService transacaoService = new TransacaoService();
		
		//usuários
		Usuario usuario = usuarioService.salvar(new Usuario("Usuário", "email@email.com", "123"));
		Usuario usuarioAlternativo = usuarioService.salvar(new Usuario("Usuário Alternativo", "email2@qualquer.com", "123"));	
		
		//contas
		Conta conta =  contaService.salvar(new Conta("Conta principal", usuario.getId()));
		Conta contaSecundaria =  contaService.salvar(new Conta("Conta secundaria", usuario.getId()));
		Conta contaUsuarioAlternativo =  contaService.salvar(new Conta("Conta usuário alternativo", usuarioAlternativo.getId()));
		
		//transações
		
		//2
		//Transação Correta / Saldo = 2
		transacaoService.salvar(new Transacao("Transação inicial", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 2d, true, conta, usuario));
		
		//Transação usuário alternativo
		transacaoService.salvar(new Transacao("Transação outro usuário", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 4d, true, contaUsuarioAlternativo, usuarioAlternativo));
		
		//Transação de outro conta Saldo permanece 2
		transacaoService.salvar(new Transacao("Transação outro conta", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 8d, true, contaSecundaria, usuario));
		
		//Transação pendente Saldo permanece 2
		transacaoService.salvar(new Transacao("Transação pendente", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 16d, false, conta, usuario));
		
		//Transação dia anterior Saldo muda para 34
		transacaoService.salvar(new Transacao("Transação passada", "Envolvido", TipoTransacao.RECEITA,
				DataUtils.obterDataComDiferencaDias(-1), 32d, true, conta, usuario));

		//Transação dia posterior Saldo permanece 34
		transacaoService.salvar(new Transacao("Transação futura", "Envolvido", TipoTransacao.RECEITA,
				DataUtils.obterDataComDiferencaDias(1), 64d, true, conta, usuario));	
		
		//Transação despesa Saldo  34 - 128 = -94 
		transacaoService.salvar(new Transacao("Transação futura", "Envolvido", TipoTransacao.DESPESA,
				new Date(), 128d, true, conta, usuario));
		
		//Testes de saldo com valor negativo dá azar Saldo -94 + 256 = 162 
		transacaoService.salvar(new Transacao("Transação da sorte", "Envolvido", TipoTransacao.RECEITA,
				new Date(), 256d, true, conta, usuario));
		
		SaldoDAO saldoDao = new SaldoDAOImpl();
		Assert.assertEquals(new Double(162d), saldoDao.getSaldoConta(conta.getId()));
		Assert.assertEquals(new Double(8d), saldoDao.getSaldoConta(contaSecundaria.getId()));
		Assert.assertEquals(new Double(4d), saldoDao.getSaldoConta(contaUsuarioAlternativo.getId()));
		
	}
	
}
