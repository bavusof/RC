package jokempo.rede;

import java.io.*;
import java.net.*;

import jokempo.jogo.Jogo;
import jokempo.jogo.Jogada;
import jokempo.jogo.Rodada;
import jokempo.utils.Mensagens;
import jokempo.utils.Timeout;

//classe para gerenciar cada cliente conectado em uma thread separada, possibilitando múltiplos jogadores
public class Gerenciador implements Runnable{
	private Socket clienteSocket;	//conexão de socket do cliente
	private BufferedReader in;	//ler as mensagens do cliente
	private PrintWriter out;	//enviar mensagens para o cliene
	private int playerid;	//identificador do cliente
	private static Jogo jogo = new Jogo();	//instância atual do jogo
	private static Jogada jogadaplayer1;	//armazena as jogadas do jogador 1
	private static Jogada jogadaplayer2;	//armazena as jogadas do jogador 2
	private static PrintWriter msgplayer1;	//enviar mensagens específicas para um cliente
	private static PrintWriter msgplayer2;	//enviar mensagens específicas para outro cliente
    private static final long TIMEOUT = 60000;   //timeout - tempo para enviar jogada
    private static Timeout timeoutPlayer1; // Timeout para o jogador 1
    private static Timeout timeoutPlayer2; // Timeout para o jogador 2
    
	public Gerenciador (Socket socket) {
        this.clienteSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            out = new PrintWriter(clienteSocket.getOutputStream(), true);
            conectarJogador(out);
        } catch (IOException e) {
            System.err.println(Mensagens.X_STREAMS + e.getMessage());
        } 
    }
	
	//gerencia a interação entre o servidor e um cliente
	public void run() {
        try {
            String input;
            while ((input = in.readLine()) != null) {
                try {
                    processarJogada(input);
                    synchronized (jogo) {
                        if (jogadaplayer1 != null && jogadaplayer2 != null) {
                        	resolverRodada();
                            if (jogo.isGameOver()) {
                                encerrarJogo();
                                break;
                            }
                            resetarJogadas();
                            solicitarJogada();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    out.println(Mensagens.X_MOVE);
                }
            }
        } catch (SocketTimeoutException e) {
        	timeoutAction();
        } catch (IOException e) {
            if (!clienteSocket.isClosed()) {
                System.err.println(Mensagens.X_COMUN + e.getMessage());
            }
        } finally {
        	ServidorJokempo.removeClient(clienteSocket);
        }
    }
	
	//méotodo que diz o que acontece quando dá timeout
    private void timeoutAction() {
    	System.out.println(Mensagens.CLIENTE + playerid + Mensagens.TIMEOUT + (TIMEOUT / 1000) + Mensagens.SEGUNDOS);
        if (playerid == 1) {
            msgplayer1.println(Mensagens.INATIVIDADE);
            msgplayer2.println(Mensagens.INATIVIDADE2);
        } else if (playerid == 2) {
            msgplayer2.println(Mensagens.INATIVIDADE);
            msgplayer1.println(Mensagens.INATIVIDADE2);
        }        
        
        // atraso de 1 segundo para dar tempo de as mensagens chegarem
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ServidorJokempo.removeBothClients(); 
    }
	
	//método para comunicar os clientes sobre a existência (ou não) da jogada do seu adversário
	private void comunicarEspera() {
	    if (playerid == 1) {
	        if (jogadaplayer2 == null) {
	        	msgplayer1.println(Mensagens.WAIT);
	        	if (msgplayer2 != null) msgplayer2.println(Mensagens.WAIT2);
	        }
	    } else {
	        if (jogadaplayer1 == null) {
	        	msgplayer2.println(Mensagens.WAIT);
	       	if (msgplayer1 != null) msgplayer1.println(Mensagens.WAIT2);
	        }
	    }
	}
	
	//método que processa a jogada do cliente
	private void processarJogada(String input) throws IllegalArgumentException {
	    Jogada jogadaplayer = Jogada.valueOf(input.toUpperCase());
	    synchronized (jogo) {
	        if (playerid == 1 && jogadaplayer1 == null) {
	            jogadaplayer1 = jogadaplayer;
	            timeoutPlayer1.reset();
	            comunicarEspera();
	        } else if (playerid == 2 && jogadaplayer2 == null) {
	            jogadaplayer2 = jogadaplayer;
	            timeoutPlayer2.reset();
	            comunicarEspera();
	        }
	    }
	}
	
	//método que resolve as rodadas e envia as mensagens de resultado
	private void resolverRodada() {
    	Rodada resultadoplayer1 = jogo.jogar(jogadaplayer1, jogadaplayer2);
        Rodada resultadoplayer2 = (resultadoplayer1 == Rodada.VITORIA) ? Rodada.DERROTA : (resultadoplayer1 == Rodada.DERROTA ? Rodada.VITORIA : Rodada.EMPATE);
        enviaResultadoSv(resultadoplayer1, resultadoplayer2);
        enviaResultado(msgplayer1, resultadoplayer1);
        enviaResultado(msgplayer2, resultadoplayer2);
        timeoutPlayer1.reset();
        timeoutPlayer2.reset();
	}
	
	//método para resetar as jogadas para a próxima rodada
	private void resetarJogadas() {
        jogadaplayer1 = null;
        jogadaplayer2 = null;
	}
	
	//gerencia a conexão de um novo jogador ao jogo
	public void conectarJogador(PrintWriter out) {
        synchronized (jogo) {
            if (msgplayer1 == null) {
                msgplayer1 = out;
                playerid = 1;
                timeoutPlayer1 = new Timeout(clienteSocket, TIMEOUT, this::timeoutAction);
                msgplayer1.println(Mensagens.CONEC1);
            } else if (msgplayer2 == null) {
                msgplayer2 = out;
                playerid = 2;
                timeoutPlayer2 = new Timeout(clienteSocket, TIMEOUT, this::timeoutAction);
                msgplayer2.println(Mensagens.CONEC2);
                msgplayer2.println(Mensagens.STARTGAME);
                msgplayer2.println(Mensagens.INFORMATIVO);
                msgplayer1.println(Mensagens.CONECATT);
                msgplayer1.println(Mensagens.STARTGAME);
                msgplayer1.println(Mensagens.INFORMATIVO);
                System.out.println(Mensagens.START);
                solicitarJogada();
            }
        }
	}
	
	//método que envia os resultados para os clientes
	private void enviaResultado(PrintWriter msgplayer, Rodada resultado) {
    	if (resultado == Rodada.VITORIA) {
            msgplayer.println(Mensagens.VENCEU);
        } else if (resultado == Rodada.DERROTA) {
            msgplayer.println(Mensagens.PERDEU);
        } else {
            msgplayer.println(Mensagens.EMPATE);
        }
	}
	
	//método que envia os resultados para o servidor
	private void enviaResultadoSv(Rodada resultado1, Rodada resultado2) {
        if (resultado1 == Rodada.VITORIA) {
        	System.out.println(Mensagens.JOGOU1 + jogadaplayer1 + Mensagens.JOGOU2 + jogadaplayer2 + Mensagens.GANHOU1 + jogo.getPontosplayer1() + " X " + jogo.getPontosplayer2() + Mensagens.C2);
        } else if (resultado2 == Rodada.VITORIA) {
        	System.out.println(Mensagens.JOGOU1 + jogadaplayer1 + Mensagens.JOGOU2 + jogadaplayer2 + Mensagens.GANHOU2 + jogo.getPontosplayer1() + " X " + jogo.getPontosplayer2() + Mensagens.C2);
        } else {
        	System.out.println(Mensagens.JOGOU1 + jogadaplayer1 + Mensagens.JOGOU2 + jogadaplayer2 + Mensagens.EMPT + jogo.getPontosplayer1() + " X " + jogo.getPontosplayer2() + Mensagens.C2);
        }
	}
	
	//método que pede que os jogadores façam as suas jogadas
    private void solicitarJogada() {
        jogo.incrementarRodada();
        int rodadaAtual = jogo.getNumeroRodada();
        int placarPlayer1 = jogo.getPontosplayer1();
        int placarPlayer2 = jogo.getPontosplayer2();
        if (msgplayer1 != null) {
        	timeoutPlayer1.start();
            enviarSolicitacaoDeJogada(msgplayer1, placarPlayer1, placarPlayer2, rodadaAtual);
        }
        if (msgplayer2 != null) {
        	timeoutPlayer2.start();
            enviarSolicitacaoDeJogada(msgplayer2, placarPlayer2, placarPlayer1, rodadaAtual);
        }  
    }
    
    //método que envia as mensagens de solicitação das jogadas
    private void enviarSolicitacaoDeJogada(PrintWriter msgPlayer, int placar, int placarOponente, int rodadaAtual) {
        msgPlayer.println(Mensagens.RODADA + rodadaAtual + Mensagens.FORMAT);
        msgPlayer.println(Mensagens.PLACAR + placar + " x " + placarOponente + Mensagens.OPONENTE);
        msgPlayer.println(Mensagens.JOGADA);
    }

    //método que encerra o jogo e envia as mensagens finais
    private void encerrarJogo() {
        int placarPlayer1 = jogo.getPontosplayer1();
        int placarPlayer2 = jogo.getPontosplayer2();
        if (placarPlayer1 >= 3) {
            enviarMensagemFinal(msgplayer1, Mensagens.VCVENCEU, placarPlayer1, placarPlayer2);
            enviarMensagemFinal(msgplayer2, Mensagens.VCPERDEU, placarPlayer2, placarPlayer1);
        } else if (placarPlayer2 >= 3) {
            enviarMensagemFinal(msgplayer2, Mensagens.VCVENCEU, placarPlayer2, placarPlayer1);
            enviarMensagemFinal(msgplayer1, Mensagens.VCPERDEU, placarPlayer1, placarPlayer2);
        }
        encerrarParaTodos();
        ServidorJokempo.removeBothClients();
    }
    
    //método para enviar as mensagens finais
    private void enviarMensagemFinal(PrintWriter jogador, String resultado, int placar, int placarOponente) {
        jogador.println(resultado);
        jogador.println(Mensagens.PLACARFINAL + placar + " x " + placarOponente + Mensagens.OPONENTE);
    }
    
    //método que envia as mensagens de encerramento para todo mundo
    private void encerrarParaTodos() {
        msgplayer1.println(Mensagens.ENDGAME);
        msgplayer2.println(Mensagens.ENDGAME);
        System.out.println(Mensagens.ENDGAMESV);
    }
}
