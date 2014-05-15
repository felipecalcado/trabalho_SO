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

    static int max_memoria = 1024;  // máximo de memória - C
    static int max_impressora = 2;  // máximo de impressoras - C
    static int max_scanner = 1;     // máximo de scanners - C
    static int max_modem = 1;       // máximo de modems - C
    static int max_cd = 2;          // máximo de cds - C
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

    public boolean setMemoria(Processo m) {
        if (!(this.memoria + m.getMemoria() > max_memoria)) {
            this.memoria += m.getMemoria();
            return true;
        } else {
            return false;
        }
    }

    public boolean setImpressora(Processo m) {
        if (!(this.impressora + m.getImpressora() > max_impressora)) {
            this.impressora += m.getImpressora();
            return true;
        } else {
            return false;
        }
    }

    public boolean setScanner(Processo m) {
        if (!(this.scanner + m.getScanner() > max_scanner)) {
            this.scanner += m.getScanner();
            return true;
        } else {
            return false;
        }
    }

    public boolean setModem(Processo m) {
        if (!(this.modem + m.getModem() > max_modem)) {
            this.modem += m.getModem();
            return true;
        } else {
            return false;
        }
    }

    public boolean setCD(Processo m) {
        if (!(this.cd + m.getCD() > max_cd)) {
            this.cd += m.getCD();
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

    public boolean removerMemoria(Processo m) {
        if (this.memoria > m.getMemoria()) {
            this.memoria -= m.getMemoria();
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
	 * @param Processo m
	 * @return boolean
	 */
    public boolean testarRecursos(Processo m) {
        if (!(m.getCD() + this.cd > max_cd)) {
            if (!(m.getImpressora() + this.impressora > max_impressora)) {
                if (!(m.getModem() + this.modem > max_modem)) {
                    if (!(m.getScanner() + this.scanner > max_scanner)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addRecursos(Processo m, int i) {
        setCD(m);
        setImpressora(m);
        setModem(m);
        setScanner(m);

        if (m.getCD() == 1) {
            if (iniCD1 == 0) {
                iniCD1 = i;
            } else {
                iniCD2 = i;
            }
        }

        if (m.getCD() == 2) {
            iniCD1 = i;
            iniCD2 = i;
        }

        if (m.getImpressora() == 1) {
            if (iniImpressora1 == 0) {
                iniImpressora1 = i;
            } else {
                iniImpressora2 = i;
            }
        }

        if (m.getImpressora() == 2) {
            iniImpressora1 = i;
            iniImpressora2 = i;
        }

        if (m.getModem() == 1) {
            iniModem = i;
        }

        if (m.getScanner() == 1) {
            iniScanner = i;
        }
    }

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
