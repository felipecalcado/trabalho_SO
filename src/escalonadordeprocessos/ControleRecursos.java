/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escalonadordeprocessos;

/**
 *
 * @author CG
 */
// Classe para controle de uso dos recursos
public class ControleRecursos {

    static int max_memoria = 1024;  
    static int max_impressora = 2;  
    static int max_scanner = 1;     
    static int max_modem = 1;       
    static int max_cd = 2;
	
    private int memoria = 0;
    private int impressora = 0;
    private int scanner = 0;
    private int modem = 0;
    private int cd = 0;
	
    public int iniImpressora1 = 0;
    public int iniImpressora2 = 0;
    public int iniScanner = 0;
    public int iniModem = 0;
    public int iniCD1 = 0;
    public int iniCD2 = 0;

    public boolean setMemoria(Processo p) {
        if (!(this.memoria + p.getMemoria() > max_memoria)) {
            this.memoria += p.getMemoria();
            return true;
        } else {
            return false;
        }
    }

    public boolean setImpressora(Processo p) {
        if (!(this.impressora + p.getImpressora() > max_impressora)) {
            this.impressora += p.getImpressora();
            return true;
        } else {
            return false;
        }
    }

    public boolean setScanner(Processo p) {
        if (!(this.scanner + p.getScanner() > max_scanner)) {
            this.scanner += p.getScanner();
            return true;
        } else {
            return false;
        }
    }

    public boolean setModem(Processo p) {
        if (!(this.modem + p.getModem() > max_modem)) {
            this.modem += p.getModem();
            return true;
        } else {
            return false;
        }
    }

    public boolean setCD(Processo p) {
        if (!(this.cd + p.getCD() > max_cd)) {
            this.cd += p.getCD();
            return true;
        } else {
            return false;
        }
    }

    public int getMemoria() {
        return this.memoria;
    }

    public int getImpressora() {
        return this.impressora;
    }

    public int getScanner() {
        return this.scanner;
    }

    public int getModem() {
        return this.modem;
    }

    public int getCD() {
        return this.cd;
    }

    public boolean removerMemoria(Processo p) {
        if (this.memoria > p.getMemoria()) {
            this.memoria -= p.getMemoria();
            return true;
        } else {
            return false;
        }
    }

    public void removerImpressora() {
        if (this.impressora > 0) {
            this.impressora -= 1;
        }
    }

    public void removerScanner() {
        if (this.scanner > 0) {
            this.scanner -= 1;
        }
    }

    public void removerModem() {
        if (this.modem > 0) {
            this.modem -= 1;
        }
    }

    public void removerCD() {
        if (this.cd > 0) {
            this.cd -= 1;
        }
    }

	/**
	 * Valida quantidade de recursos no processo
	 * Para cada recurso, verifica se a quantidade do recurso no processo estourou limite estabelecido
	 * @param Processo p
	 * @return boolean
	 */
    public boolean testarRecursos(Processo p) {
        if (!(p.getCD() + this.cd > max_cd)) {
            if (!(p.getImpressora() + this.impressora > max_impressora)) {
                if (!(p.getModem() + this.modem > max_modem)) {
                    if (!(p.getScanner() + this.scanner > max_scanner)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addRecursos(Processo p, int i) {
        setCD(p);
        setImpressora(p);
        setModem(p);
        setScanner(p);

        if (p.getCD() == 1) {
            if (iniCD1 == 0) {
                iniCD1 = i;
            } else {
                iniCD2 = i;
            }
        }

        if (p.getCD() == 2) {
            iniCD1 = i;
            iniCD2 = i;
        }

        if (p.getImpressora() == 1) {
            if (iniImpressora1 == 0) {
                iniImpressora1 = i;
            } else {
                iniImpressora2 = i;
            }
        }

        if (p.getImpressora() == 2) {
            iniImpressora1 = i;
            iniImpressora2 = i;
        }

        if (p.getModem() == 1) {
            iniModem = i;
        }

        if (p.getScanner() == 1) {
            iniScanner = i;
        }
    }

	/**
	 * Verifica se o processo 'p' está utilizando algum recurso
	 * @param p
	 * @return 
	 */
	public boolean usaRecursos(Processo p) {
        if (p.getCD() > 0) {
            return true;
        } else {
            if (p.getImpressora() > 0) {
                return true;
            } else {
                if (p.getModem() > 0) {
                    return true;
                } else {
                    if (p.getScanner() > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }
}
