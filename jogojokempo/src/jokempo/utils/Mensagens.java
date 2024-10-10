package jokempo.utils;

//classe para as Strings das mensagens
public class Mensagens {
	
	//controle do servidor
	public static final String ABRESERVIDOR = ("Servidor Jokempo iniciado na porta ");
	public static final String CONECTACLIENTE = ("\nCliente conectado: ");
	public static final String REMOVECLIENTE = ("\nCliente removido: ");
	public static final String CONECINVALIDA = ("\nUm terceiro cliente tentou se juntar ao jogo, mas o servidor já está cheio. Número máximo de clientes conectados.");
	public static final String START = ("\n------------ Ambos os clientes conectaram. O jogo foi iniciado. ------------");
	public static final String JOGOU1 = ("\nO cliente 1 jogou ");
	public static final String JOGOU2 = (" e o cliente 2 jogou ");
	public static final String GANHOU1 = (". O cliente 1 ganhou a rodada. (Placar: Cliente 1 - ");
	public static final String GANHOU2 = (". O cliente 2 ganhou a rodada. (Placar: Cliente 1 - ");
	public static final String EMPT = (". A rodada empatou. (Placar: Cliente 1 - ");
	public static final String C2 = (" - Cliente 2)");
	public static final String ENDGAMESV = ("\n---------------- Foi decretado um vencedor. Jogo encerrado. ----------------");
	public static final String CLIENTE = ("\nCliente ");
	public static final String TIMEOUT = (" não enviou mensagens em ");
	public static final String SEGUNDOS = (" segundos. Desconectando...");
	
	//controle do cliente
	public static final String SALACHEIA = ("\nConexão recusada. A sala está cheia.");
	public static final String TENTACONECTAR = ("Tentando conectar ao servidor Jokempo.");
	public static final String CONEC1 = ("\nConexão aceita. Aguardando um oponente se conectar para começar...");
	public static final String CONEC2 = ("\nConexão aceita. Seu adversário já estava te esperando.");
	public static final String CONECATT = ("\nOponente conectado.");
	public static final String STARTGAME = ("\nO jogo vai começar!");
	public static final String INFORMATIVO = ("\nVocê tem 1 minuto entre as suas jogadas válidas para não ser considerado inativo.");
	public static final String WAIT = ("\nAguardando jogada do oponente...");
	public static final String WAIT2 = ("\nSeu adversário jogou.");
	public static final String VENCEU = ("\nVocê venceu a rodada!");
	public static final String PERDEU = ("\nVocê perdeu a rodada!");
	public static final String EMPATE = ("\nA rodada foi empate!");
	public static final String RODADA = ("\n-------------------- Rodada ");
	public static final String FORMAT = (" --------------------");
	public static final String PLACAR = ("\nPlacar: Você ");
	public static final String OPONENTE = (" Oponente");
	public static final String JOGADA = ("\nInsira sua jogada (PEDRA, PAPEL ou TESOURA): ");
	public static final String PLACARFINAL = ("\nPlacar Final: Você ");
	public static final String VCVENCEU = ("\n--------------- Você venceu o jogo! ---------------");
	public static final String VCPERDEU = ("\n--------------- Você perdeu o jogo. ---------------");
	public static final String ENDGAME = ("\nO jogo acabou, você será desconectado.");
	public static final String INATIVIDADE = ("\nTempo limite esgotado. Você será desconectado por inatividade.");
	public static final String INATIVIDADE2 = ("\nO seu oponente foi desconectado por inatividade.");
	
	//erros
	public static final String X_SERVIDOR = ("\nErro no servidor: ");
	public static final String X_SOCKET = ("\nErro ao fechar socket: ");
	public static final String X_CONEXAO = ("\nErro ao conectar ao servidor: ");
	public static final String X_STREAMS = ("\nErro ao configurar streams: ");
	public static final String X_MOVE = ("\nJogada inválida. Use: PEDRA, PAPEL ou TESOURA.");
	public static final String PERDECONEXAO = ("\nConexão com o servidor perdida.");
	public static final String X_COMUN = ("\nErro na comunicação com o cliente: ");
	public static final String X_TIMEOUT = ("Erro ao configurar o timeout no socket: ");
}
