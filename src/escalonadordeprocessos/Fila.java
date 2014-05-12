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

//Classe que implementa a fila de processos do FeedBack
public class Fila {
    int numero; //número da lista
    //Lista de processos da fila
    public LinkedList<escalonadordeprocessos.Processo> lista = new LinkedList<Processo>();

    //Adicionar processo à lista
    public void adicionarProcesso(Processo p){
        lista.add(p);
    }

    //Obter e remover primeiro processo da lista
    public Processo removerProcesso(){
        Processo proc = new Processo();
        proc = lista.pop();
        return proc;
    }
}
