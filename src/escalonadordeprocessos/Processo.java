/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escalonadordeprocessos;

/**
 *
 * @author CG
 */

//Classe que implementa um Processo
public class Processo {

    private String nome;
    private int tempChegada;
    private int prioridade;
    private int tempProcesso;
    private int tempRestante;
    public int memoria;
    public int iniMemoria;
    public int iniRecursos;
    private int impressora;
    private int scanner;
    private int modem;
    private int cd;
    private int cpu; //Cpu Utilizada
    public int tempfim;
    public String memUsada;
	
	@Override
	public String toString() {
		
		String resp;
		
		resp = "Nome: " + nome + " | Tempo de chegada: " + tempChegada + " | Prioridade: " + prioridade + " | Tempo do Processo: " + tempProcesso +
				" | Memória: " + memoria + " | Impressora(s): " + impressora + " | Scanner(s): " + scanner + 
				" | Modem(ns): " + modem + " | CD(s): " + cd + "\n"; 
		
		return resp;
		
	}

    //Métodos para a atribuição de valores às variáveis
    public void setNome(String n, int i){
        this.nome = "P" + Integer.toString(i);
    }

    public String setTempChegada(String t){
        int i = 0;
        while (t.charAt(i) != ','){
            i++;
        }
        this.tempChegada = Integer.parseInt(t.substring(0, i));
        return t.substring(i+2);
    }

    public String setPrioridade(String p){
        int i = 0;
        while (!(p.charAt(i) == ',')){
            i++;
        }
        this.prioridade = Integer.parseInt(p.substring(0, i));
        return p.substring(i+2);
    }

    public String setTempProcesso(String t){
        int i = 0;
        while (!(t.charAt(i) == ',')){
            i++;
        }
        this.tempProcesso = Integer.parseInt(t.substring(0, i));
        this.setTempRestante(Integer.parseInt(t.substring(0, i)));
        return t.substring(i+2);
    }

    public void setTempRestante(int t){
        this.tempRestante = t;
    }

    public String setMemoria(String m){
        int i = 0;
        while (!(m.charAt(i) == ',')){
            i++;
        }
        this.memoria = Integer.parseInt(m.substring(0, i));
        return m.substring(i+2);
    }

    public String setImpressora(String i){
        int j = 0;
        while (!(i.charAt(j) == ',')){
            j++;
        }
        this.impressora = Integer.parseInt(i.substring(0, j));
        return i.substring(j+2);
    }

    public String setScanner(String s){
        int i = 0;
        while (!(s.charAt(i) == ',')){
            i++;
        }
        this.scanner = Integer.parseInt(s.substring(0, i));
        return s.substring(i+2);
    }

    public String setModem(String m){
        int i = 0;
        while (!(m.charAt(i) == ',')){
            i++;
        }
        this.modem = Integer.parseInt(m.substring(0, i));
        return m.substring(i+2);
    }
    
    public void setCD(String c){
        this.cd = Integer.parseInt(c.substring(0, 1));
    }

    public void setCPU(int p){
        this.cpu = p;
    }

    //Métodos para a obtenção dos valores das variáveis
    public String getnome(){
        return this.nome;
    }

    public int getTempChegada(){
        return this.tempChegada;
    }

    public int getPrioridade(){
        return this.prioridade;
    }

    public int getTempProcesso(){
        return this.tempProcesso;
    }

    public int getTempRestante(){
        return this.tempRestante;
    }

    public int getMemoria(){
        return this.memoria;
    }

    public int getImpressora(){
        return this.impressora;
    }

    public int getScanner(){
        return this.scanner;
    }

    public int getModem(){
        return this.modem;
    }

    public int getCD(){
        return this.cd;
    }

    public int getCPU(){
        return this.cpu;
    }
}
