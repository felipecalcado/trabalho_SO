/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escalonadordeprocessos;

/**
 *
 * @author CG
 */
public class ElementoDeMemoria {

    private String nome;
    private int tamanho;
    private int começo;
    private int fim;
    public boolean vazio = false;

    public void setNome(String i) {
        this.nome = i;
    }

    public void setTamanho(int i) {
        this.tamanho = i;
    }

    public void setComeço(int i) {
        this.começo = i;
    }

    public void setFim() {
        this.fim = this.começo + this.tamanho - 1;
    }

    public String getNome() {
        return this.nome;
    }

    public int getTamanho() {
        return this.tamanho;
    }

    public int getComeço() {
        return this.começo;
    }

    public int getFim() {
        return this.fim;
    }
}
