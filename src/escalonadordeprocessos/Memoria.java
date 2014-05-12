/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escalonadordeprocessos;

import java.util.LinkedList;

/**
 *
 * @author CG
 */
public class Memoria {

    public LinkedList<escalonadordeprocessos.ElementoDeMemoria> mem = new LinkedList();
    public int totalUsada = 0;

    public Memoria() {
        ElementoDeMemoria m = new ElementoDeMemoria();

        m.setNome("");
        m.setTamanho(1024);
        m.vazio = true;
        m.setComeço(0);
        m.setFim();
        this.mem.add(m);
    }

    public boolean addElemento(Processo p) {
        int i = 0;
        int memVazia = 0;
        boolean fim = false;
        ElementoDeMemoria aux = new ElementoDeMemoria();
        ElementoDeMemoria aux1 = new ElementoDeMemoria();

        //Varre a lista até o fim ou até encontrar espaço vazio
        while (!fim) {
            aux = mem.get(i);
            if (aux.vazio) {
                if (aux.getTamanho() < p.memoria) {
                    i++;
                    memVazia += aux.getTamanho();
                    if (i > mem.size() - 1) {
                        fim = true;
                    }
                } else {
                    fim = true;
                }
            } else {
                i++;
                if (i > mem.size() - 1) {
                    fim = true;
                }
            }
        }

        //Se encontrar espaço vazio maior ou igual ao necessário
        if (!(i > mem.size() - 1)) {
            aux = mem.get(i);
            //Se espaço for igual ao necessário
            if (aux.getTamanho() == p.memoria) {
                aux.setNome(p.getnome());
                aux.vazio = false;
                mem.set(i, aux);
                totalUsada += p.memoria;
                p.iniMemoria = aux.getComeço();
                return true;
            } else {
                int j = mem.size() - 1;
                mem.add(aux);
                while (j > i + 1) {   //Cada elemento após i, tem seu índice incrementado
                    mem.set(j, mem.get(j - 1));
                    j--;
                }
                //Adicionar valores para mem[i]
                aux1.setNome(p.getnome());
                aux1.setTamanho(p.memoria);
                aux1.vazio = false;
                aux1.setFim();
                mem.set(i, aux1);
                p.iniMemoria = aux1.getComeço();

                //Adicionar valores para mem[i+1]
                j = aux.getTamanho();
                aux.setNome("");
                aux.setTamanho(j - aux1.getTamanho());
                aux.vazio = true;
                aux.setComeço(aux1.getFim() + 1);
                aux.setFim();
                mem.set(i + 1, aux);

                totalUsada += p.memoria;
                return true;
            }
        } else {
            //Se o espaço vazio fragmentado for suficiente para alocar o processo,
            //realoca a memória e aloca o processo
            if (!(memVazia < p.memoria)) {
                realocarMem();
                addElemento(p);
                totalUsada += p.memoria;
                return true;
            }
            return false;
        }
    }

    public void removerElemento(Processo p) {
        int i = 0;
        String teste = p.getnome();
        String teste1;
        while (i < mem.size()) {
            teste1 = mem.get(i).getNome();
            if (mem.get(i).getNome().equals(p.getnome())) {
                mem.get(i).setNome("");
                mem.get(i).vazio = true;
                totalUsada -= mem.get(i).getTamanho();
                break;
            }
            else{
                i++;
            }
        }
    }

    public void realocarMem() {
        int i = 0;
        //Varre a lista decrementando os índices consecutivos à um espaço vazio
        while (i != mem.size() - 1) {
            if (mem.get(i).vazio == true) {
                mem.set(i, mem.get(i + 1));
                mem.get(i + 1).vazio = true;
            }
            i++;
        }
        mem.removeLast();
    }
}
