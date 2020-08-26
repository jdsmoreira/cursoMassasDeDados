package br.ce.wcaquin.estrategia3;

import java.sql.SQLException;

public class EstoqueBatch {
	
	public static final Integer ESTOQUE_MINIMO = 5;
	public static final String TIPO_MONITORADO = GeradorMassas.CHAVE_CONTA_SB;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
		MassaDAOimpl dao =  new MassaDAOimpl();
		GeradorMassas gerador = new GeradorMassas();
		 while(true) {
			 int estoqueAtual = dao.obterEstoque(TIPO_MONITORADO);
			 System.out.println(estoqueAtual);
			 if(estoqueAtual < ESTOQUE_MINIMO) {
				 gerador.gerarContaSeuBarriga();
			 } else {
				 Thread.sleep(10000);
			 }
			 
		 }
	}

}
