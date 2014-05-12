/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escalonadordeprocessos;

/**
 *
 * @author CG
 */
//Classe que implementa a execução dos processos
public class Executar {

    public Controle_Tamanho controle = new Controle_Tamanho(); //Controle dos recursos disponíveis
    int iniRecursos1;
    int iniRecursos2;
    int iniRecursos3;
    int iniRecursos4;
    //CPUs
    public String cpu1 = "";
    public String cpu2 = "";
    public String cpu3 = "";
    public String cpu4 = "";
    //Memórias
    public Memoria mem1 = new Memoria();
    public Memoria mem2 = new Memoria();
    //Processos em execussão
    public Processo process1 = new Processo();
    public Processo process2 = new Processo();
    public Processo process3 = new Processo();
    public Processo process4 = new Processo();
    public boolean fbAtivo = false;
    boolean fimFB = false;
    public int ciclos = -1;
//    public int checaFE;
    private Processo aux1 = new Processo();
    private Processo aux2 = new Processo();
    private Processo aux3 = new Processo();
    private Processo aux4 = new Processo();
    int lista1;
    int lista2;
    int lista3;
    int lista4;
    //Fila de Executando
    public Fila fExec = new Fila();
    //Fila de Suspensos
    public Fila fSusp = new Fila();
    //Fila de Bloqueados
    public Fila fBloq = new Fila();
    //Fila de Entrada
    public Fila fe = new Fila();
    //Filas de Submissão
    public Fila ftr = new Fila();
    public Fila fu = new Fila();
    //As três filas do FeedBack (Memória 1)
    public Fila fila0 = new Fila();
    public Fila fila1 = new Fila();
    public Fila fila2 = new Fila();
    //As três filas do FeedBack (Memória 2)
    public Fila fila00 = new Fila();
    public Fila fila01 = new Fila();
    public Fila fila02 = new Fila();
    //Fila de Processos Terminados
    public Fila ft = new Fila();

    //Adicionar processo à FE
    public void addFE(Processo p) {
        fe.lista.add(p);
    }

    //Testar se a FE está vazia
    public boolean feVazia() {
        if (fe.lista.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //Testar se a FTR está vazia
    public boolean ftrVazia() {
        if (ftr.lista.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //Testar se a FU está vazia
    public boolean fuVazia() {
        if (fu.lista.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    //Examinar a FE e encaminhar os processos às suas devidas listas

    public void examinarFE() {
        if (!fe.lista.isEmpty()) {
            while (!fe.lista.isEmpty()) {
                //Adiciona processo à Lista de processos de Tempo Real
                if (fe.lista.getFirst().getPrioridade() == 0) {
                    Processo aux = new Processo();
                    aux = fe.removerProcesso();
                    aux.memUsada = "Memória 1";
                    if (mem1.addElemento(aux)) {
                        ftr.adicionarProcesso(aux);
                    } else {
                        aux.memUsada = "Memória 2";
                        if (mem2.addElemento(aux)) {
                            ftr.adicionarProcesso(aux);
                        } else {
                            if (!fu.lista.isEmpty()) {
                                aux.memUsada = "Memória 1";
                                while (!mem1.addElemento(aux)) {
                                    fSusp.adicionarProcesso(fu.removerProcesso());
                                    mem1.removerElemento(fSusp.lista.getLast());
                                }
                            }
                        }
                    }
                } //Adiciona processo à Lista de processos do Usuário
                else {
                    int i = 0;
                    Processo aux = new Processo();
                    while (i < fSusp.lista.size()) {
                        if (controle.testarRecursos(aux)) {
                            aux = fSusp.lista.get(i);
                            aux.memUsada = "Memória 1";
                            if (mem1.addElemento(aux)) {
                                fSusp.removerProcesso();
                                fu.adicionarProcesso(aux);
                            } else {
                                aux.memUsada = "Memória 2";
                                if (mem2.addElemento(aux)) {
                                    aux = fSusp.removerProcesso();
                                    fu.adicionarProcesso(aux);
                                }
                            }
                            i++;
                        }
                        i++;
                    }
                    aux = fe.removerProcesso();
                    if (controle.testarRecursos(aux)) {
                        aux.memUsada = "Memória 1";
                        if (mem1.addElemento(aux)) {
                            fu.adicionarProcesso(aux);
                        } else {
                            aux.memUsada = "Memória 2";
                            if (mem2.addElemento(aux)) {
                                fu.adicionarProcesso(aux);
                            } else {
                                fSusp.adicionarProcesso(aux);
                            }
                        }
                    } else {
                        fSusp.adicionarProcesso(aux);
                    }

                }
            }
        }
    }

    public void executarTReal(int i) {
        if (i == 1) {
            process1.setTempRestante(process1.getTempRestante() - 1);
            if (process1.getTempRestante() == 0) {
                process1.setCPU(0);
                process1.tempfim = ciclos + 1;
                cpu1 = "fim";
                ft.adicionarProcesso(process1);
                mem1.removerElemento(process1);
            }
        }
        if (i == 2) {
            process2.setTempRestante(process2.getTempRestante() - 1);
            if (process2.getTempRestante() == 0) {
                process2.setCPU(0);
                process2.tempfim = ciclos + 1;
                cpu2 = "fim";
                ft.adicionarProcesso(process2);
                mem1.removerElemento(process2);
            }
        }

        if (i == 3) {
            process3.setTempRestante(process3.getTempRestante() - 1);
            if (process3.getTempRestante() == 0) {
                process3.setCPU(0);
                process3.tempfim = ciclos + 1;
                cpu3 = "fim";
                ft.adicionarProcesso(process3);
                mem2.removerElemento(process3);
            }
        }

        if (i == 4) {
            process4.setTempRestante(process4.getTempRestante() - 1);
            if (process4.getTempRestante() == 0) {
                process4.setCPU(0);
                process4.tempfim = ciclos + 1;
                cpu4 = "fim";
                ft.adicionarProcesso(process4);
                mem2.removerElemento(process4);
            }
        }
    }

    public void atualizaFilasFB(int i) {
        //Atualiza Filas do Feedback
        if (i == 1) {
            if (aux1 != null) {
                if (aux1.getTempRestante() != 0) {
                    if (aux1.getTempRestante() == aux1.getTempProcesso() / 2) {
                        if (controle.usaRecursos(aux1)) {
                            if (controle.testarRecursos(aux1)) {
                                fBloq.adicionarProcesso(aux1);
                                controle.addRecursos(aux1, ciclos);
                                aux1.iniRecursos = ciclos;
                            } else {
                                fBloq.adicionarProcesso(aux1);
                            }
                        } else {
                            if (lista1 == 0) {
                                fila1.adicionarProcesso(aux1);
                            } else {
                                if (lista1 == 1) {
                                    fila2.adicionarProcesso(aux1);
                                } else {
                                    fila0.adicionarProcesso(aux1);
                                }
                            }
                        }
                    } else {
                        if (lista1 == 0) {
                            fila1.adicionarProcesso(aux1);
                        } else {
                            if (lista1 == 1) {
                                fila2.adicionarProcesso(aux1);
                            } else {
                                fila0.adicionarProcesso(aux1);
                            }
                        }
                    }
                } else {
                    aux1.tempfim = ciclos;
                    ft.adicionarProcesso(aux1);
                    if (aux1.memUsada.equals("Memória 1")) {
                        mem1.removerElemento(aux1);
                    }
                }
                aux1 = null;
            }
        }
        if (i == 2) {
            if (aux2 != null) {
                if (aux2.getTempRestante() != 0) {
                    if (aux2.getTempRestante() == aux2.getTempProcesso() / 2) {
                        if (controle.usaRecursos(aux2)) {
                            if (controle.testarRecursos(aux2)) {
                                fBloq.adicionarProcesso(aux2);
                                controle.addRecursos(aux2, ciclos);
                                aux2.iniRecursos = ciclos;
                            } else {
                                fBloq.adicionarProcesso(aux2);
                            }
                        } else {
                            if (lista2 == 0) {
                                fila1.adicionarProcesso(aux2);
                            } else {
                                if (lista2 == 1) {
                                    fila2.adicionarProcesso(aux2);
                                } else {
                                    fila0.adicionarProcesso(aux2);
                                }
                            }
                        }
                    } else {
                        if (lista2 == 0) {
                            fila1.adicionarProcesso(aux2);
                        } else {
                            if (lista2 == 1) {
                                fila2.adicionarProcesso(aux2);
                            } else {
                                fila0.adicionarProcesso(aux2);
                            }
                        }
                    }
                } else {
                    aux2.tempfim = ciclos;
                    ft.adicionarProcesso(aux2);
                    if (aux2.memUsada.equals("Memória 1")) {
                        mem1.removerElemento(aux2);
                    }
                }
                aux2 = null;
            }
        }

        if (i == 3) {
            if (aux3 != null) {
                if (aux3.getTempRestante() != 0) {
                    if (aux3.getTempRestante() == aux3.getTempProcesso() / 2) {
                        if (controle.usaRecursos(aux3)) {
                            if (controle.testarRecursos(aux3)) {
                                fBloq.adicionarProcesso(aux3);
                                controle.addRecursos(aux3, ciclos);
                                aux3.iniRecursos = ciclos;
                            } else {
                                fBloq.adicionarProcesso(aux3);
                            }
                        } else {
                            if (lista3 == 0) {
                                fila01.adicionarProcesso(aux3);
                            } else {
                                if (lista3 == 1) {
                                    fila02.adicionarProcesso(aux3);
                                } else {
                                    fila00.adicionarProcesso(aux3);
                                }
                            }
                        }
                    } else {
                        if (lista3 == 0) {
                            fila01.adicionarProcesso(aux3);
                        } else {
                            if (lista3 == 1) {
                                fila02.adicionarProcesso(aux3);
                            } else {
                                fila00.adicionarProcesso(aux3);
                            }
                        }
                    }
                } else {
                    aux3.tempfim = ciclos;
                    ft.adicionarProcesso(aux3);
                    if (aux3.memUsada.equals("Memória 2")) {
                        mem2.removerElemento(aux3);
                    }
                }
                aux3 = null;
            }
        }

        if (i == 4) {
            if (aux4 != null) {
                if (aux4.getTempRestante() != 0) {
                    if (aux4.getTempRestante() == aux4.getTempProcesso() / 2) {
                        if (controle.usaRecursos(aux4)) {
                            if (controle.testarRecursos(aux4)) {
                                fBloq.adicionarProcesso(aux4);
                                controle.addRecursos(aux4, ciclos);
                                aux4.iniRecursos = ciclos;
                            } else {
                                fBloq.adicionarProcesso(aux4);
                            }
                        } else {
                            if (lista4 == 0) {
                                fila01.adicionarProcesso(aux4);
                            } else {
                                if (lista4 == 1) {
                                    fila02.adicionarProcesso(aux4);
                                } else {
                                    fila00.adicionarProcesso(aux4);
                                }
                            }
                        }
                    } else {
                        if (lista4 == 0) {
                            fila01.adicionarProcesso(aux4);
                        } else {
                            if (lista4 == 1) {
                                fila02.adicionarProcesso(aux4);
                            } else {
                                fila00.adicionarProcesso(aux4);
                            }
                        }
                    }
                } else {
                    aux4.tempfim = ciclos;
                    ft.adicionarProcesso(aux4);
                    if (aux4.memUsada.equals("Memória 2")) {
                        mem2.removerElemento(aux4);
                    }
                }
                aux3 = null;
            }
        }
    }

    public void atualizarRecursos() {
        if (ciclos - controle.iniCD1 == 2) {
            controle.removerCD();
            controle.iniCD1 = 0;
        }
        if (ciclos - controle.iniCD2 == 2) {
            controle.removerCD();
            controle.iniCD2 = 0;
        }
        if (ciclos - controle.iniImpressora1 == 2) {
            controle.removerImpressora();
            controle.iniImpressora1 = 0;
        }
        if (ciclos - controle.iniImpressora2 == 2) {
            controle.removerImpressora();
            controle.iniImpressora2 = 0;
        }
        if (ciclos - controle.iniModem == 2) {
            controle.removerModem();
            controle.iniModem = 0;
        }
        if (ciclos - controle.iniScanner == 2) {
            controle.removerScanner();
            controle.iniScanner = 0;
        }
        int i = 0;
        while (i < fBloq.lista.size()) {
            if (ciclos - fBloq.lista.get(i).iniRecursos == 2) {
                if (fBloq.lista.get(i).memUsada.equals("Memória 1")) {
                    fila0.adicionarProcesso(fBloq.lista.remove(i));
                } else {
                    fila00.adicionarProcesso(fBloq.lista.remove(i));
                }
            }
            i++;
        }

        i = 0;
        while (i < fBloq.lista.size()) {
            if (controle.testarRecursos(fBloq.lista.get(i))) {
                controle.addRecursos(fBloq.lista.get(i), ciclos);
                fBloq.lista.get(i).iniRecursos = ciclos;
            }
            i++;
        }
    }

    public void executarNormal(int i) {
        //Executar processo da Fila 0
        if (i == 1) {
            if (fila0.lista.size() > 0) {
                lista1 = 0;
                fimFB = true;
                aux1 = fila0.removerProcesso();
                aux1.setTempRestante((aux1.getTempRestante() - 1));
            } else {
                //Executar processo da Fila 1
                if (fila1.lista.size() > 0) {
                    lista1 = 1;
                    fimFB = true;
                    aux1 = fila1.removerProcesso();
                    aux1.setTempRestante((aux1.getTempRestante() - 1));
                } else {
                    //Executar processo fa Fila 2
                    if (fila2.lista.size() > 0) {
                        lista1 = 2;
                        fimFB = true;
                        aux1 = fila2.removerProcesso();
                        aux1.setTempRestante((aux1.getTempRestante() - 1));
                    }
                }
            }
        }

        if (i == 2) {
            if (fila0.lista.size() > 0) {
                lista2 = 0;
                fimFB = true;
                aux2 = fila0.removerProcesso();
                aux2.setTempRestante((aux2.getTempRestante() - 1));
            } else {
                //Executar processo da Fila 1
                if (fila1.lista.size() > 0) {
                    lista2 = 1;
                    fimFB = true;
                    aux2 = fila1.removerProcesso();
                    aux2.setTempRestante((aux2.getTempRestante() - 1));
                } else {
                    //Executar processo fa Fila 2
                    if (fila2.lista.size() > 0) {
                        lista2 = 2;
                        fimFB = true;
                        aux2 = fila2.removerProcesso();
                        aux2.setTempRestante((aux2.getTempRestante() - 1));
                    }
                }
            }
        }

        if (i == 3) {
            if (fila00.lista.size() > 0) {
                lista3 = 0;
                fimFB = true;
                aux3 = fila00.removerProcesso();
                aux3.setTempRestante((aux3.getTempRestante() - 1));
            } else {
                //Executar processo da Fila 1
                if (fila01.lista.size() > 0) {
                    lista3 = 1;
                    fimFB = true;
                    aux3 = fila01.removerProcesso();
                    aux3.setTempRestante((aux3.getTempRestante() - 1));
                } else {
                    //Executar processo fa Fila 2
                    if (fila02.lista.size() > 0) {
                        lista3 = 2;
                        fimFB = true;
                        aux3 = fila02.removerProcesso();
                        aux3.setTempRestante((aux3.getTempRestante() - 1));
                    }
                }
            }
        }

        if (i == 4) {
            if (fila00.lista.size() > 0) {
                lista4 = 0;
                fimFB = true;
                aux4 = fila00.removerProcesso();
                aux4.setTempRestante((aux4.getTempRestante() - 1));
            } else {
                //Executar processo da Fila 1
                if (fila01.lista.size() > 0) {
                    lista4 = 1;
                    fimFB = true;
                    aux4 = fila01.removerProcesso();
                    aux4.setTempRestante((aux4.getTempRestante() - 1));
                } else {
                    //Executar processo fa Fila 2
                    if (fila02.lista.size() > 0) {
                        lista4 = 2;
                        fimFB = true;
                        aux4 = fila02.removerProcesso();
                        aux4.setTempRestante((aux4.getTempRestante() - 1));
                    }
                }
            }
        }

    }
}
