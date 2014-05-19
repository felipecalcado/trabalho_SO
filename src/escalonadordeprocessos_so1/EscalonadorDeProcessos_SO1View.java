/*
 * EscalonadorDeProcessos_SO1View.java
 */
package escalonadordeprocessos_so1;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import escalonadordeprocessos.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;

/**
 * The application's main frame.
 */
public class EscalonadorDeProcessos_SO1View extends FrameView {

    Executar exec = new Executar();
	
	// arquivo é lido apenas uma vez e é guardado na lista auxiliar (aux)
    Fila aux = new Fila(); 
	
	// Controle dos recursos disponíveis
    ControleRecursos controle = new ControleRecursos(); 
	
    Processo auxiliar = new Processo();
    String nome1;
    String nome2;
    String nome3;
    String nome4;
    boolean normal1;
    boolean normal2;
    boolean normal3;
    boolean normal4;
    boolean examinaFE = false;

	// inicio da aplicacao
    public EscalonadorDeProcessos_SO1View(SingleFrameApplication app) {
        super(app);

		// tenta ler o arquivo de entrada
        try {
            BufferedReader in = null;
			// diretório corrente
            File dir = new File(System.getProperty("user.dir"));
            JFileChooser fc = new JFileChooser(dir);
            if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
                File f = fc.getSelectedFile();
                FileInputStream fis = new FileInputStream(f);
                InputStreamReader is = new InputStreamReader(fis);
                in = new BufferedReader(is);
            }

            String linha = in.readLine();
            int i = 1;
            while (!linha.isEmpty()) {
                // monta o processo a partir do arquivo de entrada
                Processo p = new Processo();
                p.setNome(linha, i);
                linha = p.setTempChegada(linha);
                linha = p.setPrioridade(linha);
                linha = p.setTempProcesso(linha);
                linha = p.setMemoria(linha);
                linha = p.setImpressora(linha);
                linha = p.setScanner(linha);
                linha = p.setModem(linha);
                p.setCD(linha);
                p.setCPU(0);

                // adiciona processo à lista auxiliar
                if (aux.lista.size() > 1) {
					// se esse processo tem o mesmo tempo de chegada do ultimo da lista auxiliar
                    if (p.getTempChegada() == aux.lista.getLast().getTempChegada()) {
						// se esse processo tem prioridade menor (que, nesse caso, significa ter maior prioridade) que o ultimo da lista
                        if (p.getPrioridade() < aux.lista.getLast().getPrioridade()) {
							// adiciona esse processo na frente do ultimo na lista auxiliar
                            Processo aux1 = new Processo();
                            aux1 = aux.lista.getLast();
                            aux.lista.removeLast();
                            aux.lista.add(p);
                            aux.lista.add(aux1);
                        } else {
							// se tem prioridade maior (menor), adiciona normalmente
                            aux.adicionarProcesso(p);
                        }
                    } else {
						// se o tempo de chegada é diferente, adiciona normalmente
                        aux.adicionarProcesso(p);
                    }
                } else {
					// se lista esta vazia, adiciona normalmente
                    aux.adicionarProcesso(p);
                }
				
				// detalhes sobre os processos trazidos do arquivo
//				if(!in.ready())
//					System.out.println(aux.toString());


                //Ler próxima linha do arquivo texto
                i++;
                if (in.ready()) {
                    linha = in.readLine();
                } else {
                    linha = "";
                }
            }
            in.close();
        } catch (IOException e) {
            System.exit(0);
        }

        //Processo auxiliar (Flag final), adiciona ao final da lista auxiliar
        Processo p = new Processo();
        p.setNome("Fim", 1);
        p.setTempChegada("100000, ");
        p.setPrioridade("4, ");
        p.setTempProcesso("0, ");
        p.setMemoria("0, ");
        p.setImpressora("0, ");
        p.setScanner("0, ");
        p.setModem("0, ");
        p.setCD("0, ");
        p.setCPU(0);
        aux.adicionarProcesso(p);
        auxiliar = p;

		// monta view da aplicacao
        initComponents();
        mainPanel.setSize(800, 574);
        mainPanel.setPreferredSize(mainPanel.getSize());

        jTextField4.setText("0");
        jTextField5.setText("0");
        jTextField6.setText("0");
        jTextField7.setText("0");
        jLabel4.setText("Segundos");
        jButton3.setText("Continuar");
        jButton3.setFont(jButton1.getFont());
        jButton3.hide();
        jLabel6.setText("Suspensos");
        jLabel34.setText("As CPUs 1 e 2 Compartilham a memória 1. E as CPUs 3 e 4, a memória 2 -- Cada dispositivo solicitado fica disponível para o processo por 2s -- A memória é realocada sempre que um processo couber em seu espaço fragmentado");
        jLabel10.setText("CPU 2");
        jLabel9.setText("Nome do Processo:");
        jLabel23.setText("CPU 3");
        jLabel23.setFont(jLabel2.getFont());
        jLabel24.setText("CPU 4");
        jLabel24.setFont(jLabel2.getFont());
        jTextField10.setFont(jTextField2.getFont());
        jTextField3.setFont(jTextField2.getFont());
        jTextField11.setFont(jTextField2.getFont());
        jLabel32.setText("Memória Principal 2");
        jLabel32.setFont(jLabel2.getFont());
        jTextField12.setFont(jTextField8.getFont());
        jLabel33.setText("Utilizada:");
        jLabel33.setFont(jLabel2.getFont());
        jLabel17.setText("Mb");
        jLabel17.setFont(jLabel2.getFont());
        jLabel25.setText("Bloqueados");
        jLabel25.setFont(jLabel2.getFont());

        while (!((aux.lista.getFirst().getTempChegada()) > exec.ciclos)) {
            jTextArea1.append(aux.lista.getFirst().getnome());
            if (aux.lista.getFirst().getPrioridade() == 0) {
                jTextArea1.append(" (Tempo Real)\n");
            } else {
                jTextArea1.append(" (Normal)\n");
            }
            exec.addFE(aux.removerProcesso());
        }

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = EscalonadorDeProcessos_SO1App.getApplication().getMainFrame();
            aboutBox = new EscalonadorDeProcessos_SO1AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        EscalonadorDeProcessos_SO1App.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea7 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea8 = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea9 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jLabel34 = new javax.swing.JLabel();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(escalonadordeprocessos_so1.EscalonadorDeProcessos_SO1App.class).getContext().getResourceMap(EscalonadorDeProcessos_SO1View.class);
        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jScrollPane2.setViewportView(jTextArea2);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jTextArea3.setName("jTextArea3"); // NOI18N
        jScrollPane3.setViewportView(jTextArea3);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setName("jSeparator1"); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setFont(resourceMap.getFont("jTextField1.font")); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N

        jTextField2.setEditable(false);
        jTextField2.setFont(resourceMap.getFont("jTextField2.font")); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setName("jSeparator3"); // NOI18N

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jTextArea4.setName("jTextArea4"); // NOI18N
        jScrollPane4.setViewportView(jTextArea4);

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jTextArea5.setName("jTextArea5"); // NOI18N
        jScrollPane5.setViewportView(jTextArea5);

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextArea6.setColumns(20);
        jTextArea6.setRows(5);
        jTextArea6.setName("jTextArea6"); // NOI18N
        jScrollPane6.setViewportView(jTextArea6);

        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel20.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setFont(resourceMap.getFont("jLabel21.font")); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jTextField3.setEditable(false);
        jTextField3.setFont(resourceMap.getFont("jTextField3.font")); // NOI18N
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N

        jLabel26.setFont(resourceMap.getFont("jLabel26.font")); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jLabel27.setFont(resourceMap.getFont("jLabel29.font")); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        jLabel28.setFont(resourceMap.getFont("jLabel29.font")); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        jLabel29.setFont(resourceMap.getFont("jLabel29.font")); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        jTextField4.setEditable(false);
        jTextField4.setFont(resourceMap.getFont("jTextField4.font")); // NOI18N
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField4.setName("jTextField4"); // NOI18N

        jTextField5.setEditable(false);
        jTextField5.setFont(resourceMap.getFont("jTextField5.font")); // NOI18N
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.setName("jTextField5"); // NOI18N

        jTextField6.setEditable(false);
        jTextField6.setFont(resourceMap.getFont("jTextField6.font")); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.setName("jTextField6"); // NOI18N

        jTextField7.setEditable(false);
        jTextField7.setFont(resourceMap.getFont("jTextField7.font")); // NOI18N
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.setName("jTextField7"); // NOI18N

        jLabel30.setFont(resourceMap.getFont("jLabel31.font")); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N

        jTextField8.setEditable(false);
        jTextField8.setFont(resourceMap.getFont("jTextField8.font")); // NOI18N
        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField8.setName("jTextField8"); // NOI18N

        jLabel31.setFont(resourceMap.getFont("jLabel31.font")); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jTextArea7.setColumns(20);
        jTextArea7.setRows(5);
        jTextArea7.setName("jTextArea7"); // NOI18N
        jScrollPane7.setViewportView(jTextArea7);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        jTextArea8.setColumns(20);
        jTextArea8.setRows(5);
        jTextArea8.setName("jTextArea8"); // NOI18N
        jScrollPane8.setViewportView(jTextArea8);

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jTextField9.setEditable(false);
        jTextField9.setFont(resourceMap.getFont("jTextField9.font")); // NOI18N
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField9.setText(resourceMap.getString("jTextField9.text")); // NOI18N
        jTextField9.setName("jTextField9"); // NOI18N

        jTextField10.setEditable(false);
        jTextField10.setFont(resourceMap.getFont("jTextField10.font")); // NOI18N
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.setName("jTextField10"); // NOI18N

        jTextField11.setEditable(false);
        jTextField11.setFont(resourceMap.getFont("jTextField11.font")); // NOI18N
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setName("jTextField11"); // NOI18N

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N

        jLabel32.setFont(resourceMap.getFont("jLabel32.font")); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        jLabel33.setFont(resourceMap.getFont("jLabel32.font")); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        jTextField12.setEditable(false);
        jTextField12.setFont(resourceMap.getFont("jLabel32.font")); // NOI18N
        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField12.setName("jTextField12"); // NOI18N

        jLabel17.setFont(resourceMap.getFont("jLabel32.font")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel25.setFont(resourceMap.getFont("jLabel32.font")); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        jTextArea9.setColumns(20);
        jTextArea9.setRows(5);
        jTextArea9.setName("jTextArea9"); // NOI18N
        jScrollPane9.setViewportView(jTextArea9);

        jButton3.setFont(resourceMap.getFont("jButton3.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField11)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(jTextField2)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                            .addComponent(jTextField10)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel17)))
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel7)))
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane7)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)))
                .addGap(169, 169, 169))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addGap(30, 30, 30)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel29)
                                .addGap(1, 1, 1)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel22)
                                        .addGap(11, 11, 11)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel10)
                                        .addGap(11, 11, 11)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(escalonadordeprocessos_so1.EscalonadorDeProcessos_SO1App.class).getContext().getActionMap(EscalonadorDeProcessos_SO1View.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1239, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusMessageLabel)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 686, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel)
                            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3))
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

	// botao de execucao
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        jButton1.setText("Próximo Ciclo");
        exec.ciclos++;

        nome1 = ".";
        nome2 = "..";
        nome3 = ".";
        nome4 = "..";

        // testa se precisa checar a FE
		// fila de entrada é examinada a cada 2 ciclos
        if ((exec.ciclos % 2) == 0) {
			// e fila de entrada nao estiver vazia
            if (!exec.feVazia()) {
                examinaFE = true;
            }
            exec.examinarFE();
        }

        exec.atualizarRecursos();

        if (normal1) {
            exec.atualizaFilasFB(1);
        }
        if (normal2) {
            exec.atualizaFilasFB(2);
        }
        if (normal3) {
            exec.atualizaFilasFB(3);
        }
        if (normal4) {
            exec.atualizaFilasFB(4);
        }

        normal1 = false;
        normal2 = false;
        normal3 = false;
        normal4 = false;

        jTextField1.setText(Integer.toString(exec.ciclos));
        if (!(exec.mem1.totalUsada > 0)) {
            jLabel21.setText("Desativado");
        }

        int i = 0;
        jTextArea9.setText(null);
        while (!(i > exec.fBloq.lista.size() - 1)) {
            jTextArea9.append(exec.fBloq.lista.get(i).getnome());
            if (exec.fBloq.lista.get(i).iniRecursos == 0) {
                jTextArea9.append(" (aguardando recursos)");
            }
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fBloq.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fBloq.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fBloq.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fBloq.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fBloq.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fBloq.lista.get(i).getCD()) + "\n";
            jTextArea9.append(inf1);
            jTextArea9.append(inf2);
            jTextArea9.append(inf3);
            jTextArea9.append(inf6);
            jTextArea9.append(inf4);
            jTextArea9.append(inf5);
            i++;
        }

        jTextField4.setText(Integer.toString(exec.controle.getImpressora()));
        jTextField5.setText(Integer.toString(exec.controle.getScanner()));
        jTextField6.setText(Integer.toString(exec.controle.getModem()));
        jTextField7.setText(Integer.toString(exec.controle.getCD()));

        i = 0;
        jTextArea4.setText(null);
        while (!(i > exec.fila0.lista.size() - 1)) {
            jTextArea4.append(exec.fila0.lista.get(i).getnome());
            jTextArea4.append(" " + exec.fila0.lista.get(i).memUsada + "\n");
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fila0.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fila0.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fila0.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fila0.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fila0.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fila0.lista.get(i).getCD()) + "\n";
            jTextArea4.append(inf1);
            jTextArea4.append(inf2);
            jTextArea4.append(inf3);
            jTextArea4.append(inf6);
            jTextArea4.append(inf4);
            jTextArea4.append(inf5);
            i++;
        }
        i = 0;
        while (!(i > exec.fila00.lista.size() - 1)) {
            jTextArea4.append(exec.fila00.lista.get(i).getnome());
            jTextArea4.append(" " + exec.fila00.lista.get(i).memUsada + "\n");
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fila00.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fila00.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fila00.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fila00.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fila00.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fila00.lista.get(i).getCD()) + "\n";
            jTextArea4.append(inf1);
            jTextArea4.append(inf2);
            jTextArea4.append(inf3);
            jTextArea4.append(inf6);
            jTextArea4.append(inf4);
            jTextArea4.append(inf5);
            i++;
        }
        jTextArea5.setText(null);
        i = 0;
        while (!(i > exec.fila1.lista.size() - 1)) {
            jTextArea5.append(exec.fila1.lista.get(i).getnome());
            jTextArea5.append(" " + exec.fila1.lista.get(i).memUsada + "\n");
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fila1.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fila1.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fila1.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fila1.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fila1.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fila1.lista.get(i).getCD()) + "\n";
            jTextArea5.append(inf1);
            jTextArea5.append(inf2);
            jTextArea5.append(inf3);
            jTextArea5.append(inf6);
            jTextArea5.append(inf4);
            jTextArea5.append(inf5);
            i++;
        }
        i = 0;
        while (!(i > exec.fila01.lista.size() - 1)) {
            jTextArea5.append(exec.fila01.lista.get(i).getnome());
            jTextArea5.append(" " + exec.fila01.lista.get(i).memUsada + "\n");
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fila01.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fila01.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fila01.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fila01.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fila01.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fila01.lista.get(i).getCD()) + "\n";
            jTextArea5.append(inf1);
            jTextArea5.append(inf2);
            jTextArea5.append(inf3);
            jTextArea5.append(inf6);
            jTextArea5.append(inf4);
            jTextArea5.append(inf5);
            i++;
        }
        jTextArea6.setText(null);
        i = 0;
        while (!(i > exec.fila2.lista.size() - 1)) {
            jTextArea6.append(exec.fila2.lista.get(i).getnome());
            jTextArea6.append(" " + exec.fila2.lista.get(i).memUsada + "\n");
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fila2.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fila2.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fila2.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fila2.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fila2.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fila2.lista.get(i).getCD()) + "\n";
            jTextArea6.append(inf1);
            jTextArea6.append(inf2);
            jTextArea6.append(inf3);
            jTextArea6.append(inf6);
            jTextArea6.append(inf4);
            jTextArea6.append(inf5);
            i++;
        }
        i = 0;
        while (!(i > exec.fila02.lista.size() - 1)) {
            jTextArea6.append(exec.fila02.lista.get(i).getnome());
            jTextArea6.append(" " + exec.fila02.lista.get(i).memUsada + "\n");
            String inf;
            String inf1;
            String inf2;
            String inf3;
            String inf4;
            String inf5;
            String inf6;

            inf1 = "   Temp. Processo: " + Integer.toString(exec.fila02.lista.get(i).getTempProcesso()) + "\n";
            inf2 = "   Tamanho: " + Integer.toString(exec.fila02.lista.get(i).getMemoria()) + "Mb\n";
            inf3 = "   Impressoras: " + Integer.toString(exec.fila02.lista.get(i).getImpressora()) + "\n";
            inf6 = "   Scanner: " + Integer.toString(exec.fila02.lista.get(i).getScanner()) + "\n";
            inf4 = "   Modem: " + Integer.toString(exec.fila02.lista.get(i).getModem()) + "\n";
            inf5 = "   CDs: " + Integer.toString(exec.fila02.lista.get(i).getCD()) + "\n";
            jTextArea6.append(inf1);
            jTextArea6.append(inf2);
            jTextArea6.append(inf3);
            jTextArea6.append(inf6);
            jTextArea6.append(inf4);
            jTextArea6.append(inf5);
            i++;
        }

        jTextArea8.setText(null);
        i = 0;
        while (!(i > exec.ft.lista.size() - 1)) {
            jTextArea8.append(exec.ft.lista.get(i).getnome());
            jTextArea8.append(" concluído em: ");
            jTextArea8.append(Integer.toString(exec.ft.lista.get(i).tempfim));
            jTextArea8.append("\n");
            i++;
        }

        //Examinar se há processos a serem submetidos à FE e os submete, caso haja
        if (!aux.lista.isEmpty()) {
            while ((aux.lista.getFirst().getTempChegada()) < exec.ciclos + 1) {
                jTextArea1.append(aux.lista.getFirst().getnome());
                String inf;
                String inf1;
                String inf2;
                String inf3;
                String inf4;
                String inf5;
                String inf6;
                if (aux.lista.getFirst().getPrioridade() == 0) {
                    inf = " (Tempo Real)\n";
                } else {
                    inf = (" (Normal)\n");
                }
                inf1 = "   Temp. Processo: " + Integer.toString(aux.lista.getFirst().getTempProcesso()) + "\n";
                inf2 = "   Tamanho: " + Integer.toString(aux.lista.getFirst().getMemoria()) + "Mb\n";
                inf3 = "   Impressoras: " + Integer.toString(aux.lista.getFirst().getImpressora()) + "\n";
                inf6 = "   Scanner: " + Integer.toString(aux.lista.getFirst().getScanner()) + "\n";
                inf4 = "   Modem: " + Integer.toString(aux.lista.getFirst().getModem()) + "\n";
                inf5 = "   CDs: " + Integer.toString(aux.lista.getFirst().getCD()) + "\n\n";
                jTextArea1.append(inf);
                jTextArea1.append(inf1);
                jTextArea1.append(inf2);
                jTextArea1.append(inf3);
                jTextArea1.append(inf6);
                jTextArea1.append(inf4);
                jTextArea1.append(inf5);

                exec.addFE(aux.removerProcesso());
            }
            jTextArea7.setText(null);
            i = 0;
            while (!(i > exec.fSusp.lista.size() - 1)) {
                jTextArea7.append(exec.fSusp.lista.get(i).getnome());
                String inf;
                String inf1;
                String inf2;
                String inf3;
                String inf4;
                String inf5;
                String inf6;

                inf1 = "   Temp. Processo: " + Integer.toString(exec.fSusp.lista.get(i).getTempProcesso()) + "\n";
                inf2 = "   Tamanho: " + Integer.toString(exec.fSusp.lista.get(i).getMemoria()) + "Mb\n";
                inf3 = "   Impressoras: " + Integer.toString(exec.fSusp.lista.get(i).getImpressora()) + "\n";
                inf6 = "   Scanner: " + Integer.toString(exec.fSusp.lista.get(i).getScanner()) + "\n";
                inf4 = "   Modem: " + Integer.toString(exec.fSusp.lista.get(i).getModem()) + "\n";
                inf5 = "   CDs: " + Integer.toString(exec.fSusp.lista.get(i).getCD()) + "\n";
                jTextArea7.append(inf1);
                jTextArea7.append(inf2);
                jTextArea7.append(inf3);
                jTextArea7.append(inf6);
                jTextArea7.append(inf4);
                jTextArea7.append(inf5);
                i++;
            }

            jTextArea1.setText(null);
            i = 0;
            while (!(i > exec.fe.lista.size() - 1)) {
                String inf;
                String inf1;
                String inf2;
                String inf3;
                String inf4;
                String inf5;
                String inf6;

                jTextArea1.append(exec.fe.lista.get(i).getnome());
                if (exec.fe.lista.get(i).getPrioridade() == 0) {
                    inf = " (Tempo Real)\n";
                } else {
                    inf = (" (Normal)\n");
                }
                inf1 = "   Temp. Processo: " + Integer.toString(exec.fe.lista.get(i).getTempProcesso()) + "\n";
                inf2 = "   Tamanho: " + Integer.toString(exec.fe.lista.get(i).getMemoria()) + "Mb\n";
                inf3 = "   Impressoras: " + Integer.toString(exec.fe.lista.get(i).getImpressora()) + "\n";
                inf6 = "   Scanner: " + Integer.toString(exec.fe.lista.get(i).getScanner()) + "\n";
                inf4 = "   Modem: " + Integer.toString(exec.fe.lista.get(i).getModem()) + "\n";
                inf5 = "   CDs: " + Integer.toString(exec.fe.lista.get(i).getCD()) + "\n\n";
                jTextArea1.append(inf);
                jTextArea1.append(inf1);
                jTextArea1.append(inf2);
                jTextArea1.append(inf3);
                jTextArea1.append(inf6);
                jTextArea1.append(inf4);
                jTextArea1.append(inf5);

                i++;
            }

            jTextArea3.setText(null);
            i = 0;
            while (!(i > exec.fu.lista.size() - 1)) {
                jTextArea3.append(exec.fu.lista.get(i).getnome());
                jTextArea3.append(" " + exec.fu.lista.get(i).memUsada + "\n");
                String inf;
                String inf1;
                String inf2;
                String inf3;
                String inf4;
                String inf5;
                String inf6;

                inf1 = "   Temp. Processo: " + Integer.toString(exec.fu.lista.get(i).getTempProcesso()) + "\n";
                inf2 = "   Tamanho: " + Integer.toString(exec.fu.lista.get(i).getMemoria()) + "Mb\n";
                inf3 = "   Impressoras: " + Integer.toString(exec.fu.lista.get(i).getImpressora()) + "\n";
                inf6 = "   Scanner: " + Integer.toString(exec.fu.lista.get(i).getScanner()) + "\n";
                inf4 = "   Modem: " + Integer.toString(exec.fu.lista.get(i).getModem()) + "\n";
                inf5 = "   CDs: " + Integer.toString(exec.fu.lista.get(i).getCD()) + "\n";
                jTextArea3.append(inf1);
                jTextArea3.append(inf2);
                jTextArea3.append(inf3);
                jTextArea3.append(inf6);
                jTextArea3.append(inf4);
                jTextArea3.append(inf5);

                i++;
            }
        }

        if (exec.process1.getCPU() != 1) {
            jTextField2.setText(null);
        }
        if (exec.process2.getCPU() != 2) {
            jTextField3.setText(null);
        }
        if (exec.process3.getCPU() != 3) {
            jTextField10.setText(null);
        }
        if (exec.process4.getCPU() != 4) {
            jTextField11.setText(null);
        }

        jTextField8.setText(Integer.toString(exec.mem1.totalUsada));
        jTextField12.setText(Integer.toString(exec.mem2.totalUsada));

        jButton3.setVisible(true);
        jButton1.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

	/**
	 * Onclick do botão principal
	 * Executa todo o processo de escalonamento
	 * @param evt 
	 */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (examinaFE) {
            int ind = 0;
            jTextArea2.setText(null);
            while (!((ind > (exec.ftr.lista.size() - 1)))) {
                jTextArea2.append(exec.ftr.lista.get(ind).getnome());
                jTextArea2.append(" " + exec.ftr.lista.get(ind).memUsada + "\n");
                String inf;
                String inf1;
                String inf2;
                String inf3;
                String inf4;
                String inf5;
                String inf6;

                inf1 = "   Temp. Processo: " + Integer.toString(exec.ftr.lista.get(ind).getTempProcesso()) + "\n";
                inf2 = "   Tamanho: " + Integer.toString(exec.ftr.lista.get(ind).getMemoria()) + "Mb\n";
                inf3 = "   Impressoras: " + Integer.toString(exec.ftr.lista.get(ind).getImpressora()) + "\n";
                inf6 = "   Scanner: " + Integer.toString(exec.ftr.lista.get(ind).getScanner()) + "\n";
                inf4 = "   Modem: " + Integer.toString(exec.ftr.lista.get(ind).getModem()) + "\n";
                inf5 = "   CDs: " + Integer.toString(exec.ftr.lista.get(ind).getCD()) + "\n";
                jTextArea2.append(inf1);
                jTextArea2.append(inf2);
                jTextArea2.append(inf3);
                jTextArea2.append(inf6);
                jTextArea2.append(inf4);
                jTextArea2.append(inf5);
                ind++;
            }

            ind = 0;
            jTextArea3.setText(null);
            while (!((ind > (exec.fu.lista.size() - 1)))) {
                jTextArea3.append(exec.fu.lista.get(ind).getnome());
                jTextArea3.append(" " + exec.fu.lista.get(ind).memUsada + "\n");
                String inf;
                String inf1;
                String inf2;
                String inf3;
                String inf4;
                String inf5;
                String inf6;

                inf1 = "   Temp. Processo: " + Integer.toString(exec.fu.lista.get(ind).getTempProcesso()) + "\n";
                inf2 = "   Tamanho: " + Integer.toString(exec.fu.lista.get(ind).getMemoria()) + "Mb\n";
                inf3 = "   Impressoras: " + Integer.toString(exec.fu.lista.get(ind).getImpressora()) + "\n";
                inf6 = "   Scanner: " + Integer.toString(exec.fu.lista.get(ind).getScanner()) + "\n";
                inf4 = "   Modem: " + Integer.toString(exec.fu.lista.get(ind).getModem()) + "\n";
                inf5 = "   CDs: " + Integer.toString(exec.fu.lista.get(ind).getCD()) + "\n";
                jTextArea3.append(inf1);
                jTextArea3.append(inf2);
                jTextArea3.append(inf3);
                jTextArea3.append(inf6);
                jTextArea3.append(inf4);
                jTextArea3.append(inf5);
                ind++;
            }
            jTextArea1.setText(null);
            examinaFE = false;
        } else {
            int i;
            jButton3.setVisible(false);
            jButton1.setVisible(true);

            // Testar se executa processo TR ou Usuário
            if ((!exec.ftrVazia())) {
                jLabel21.setText("Desativado");
                i = 0;
                while (!(i > exec.ftr.lista.size() - 1)) {
                    if (exec.ftr.lista.get(i).memUsada.equals("Memória 1")) {
                        if ((exec.cpu1.equals(""))) {
                            exec.cpu1 = exec.ftr.lista.get(i).getnome();
                            exec.ftr.lista.get(i).setCPU(1);
                            exec.process1 = exec.ftr.lista.get(i);
                            exec.ftr.lista.remove(i);
                            jTextField2.setText(exec.process1.getnome() + " (Tempo Restante: " + Integer.toString(exec.process1.getTempRestante()) + "s)");
                        }
                        if ((!exec.ftrVazia()) && (exec.cpu2.equals(""))) {
                            exec.cpu2 = exec.ftr.lista.get(i).getnome();
                            exec.ftr.lista.get(i).setCPU(2);
                            exec.process2 = exec.ftr.lista.get(i);
                            exec.ftr.lista.remove(i);
                            jTextField3.setText(exec.process2.getnome() + " (Tempo Restante: " + Integer.toString(exec.process2.getTempRestante()) + "s)");
                        }

                    } else {
                        if (exec.ftr.lista.get(i).memUsada.equals("Memória 2")) {
                            if ((exec.cpu3.equals(""))) {
                                exec.cpu3 = exec.ftr.lista.get(i).getnome();
                                exec.ftr.lista.get(i).setCPU(3);
                                exec.process3 = exec.ftr.lista.get(i);
                                exec.ftr.lista.remove(i);
                                jTextField10.setText(exec.process3.getnome() + " (Tempo Restante: " + Integer.toString(exec.process3.getTempRestante()) + "s)");
                            }
                            if ((!exec.ftrVazia()) && (exec.cpu4.equals(""))) {
                                exec.cpu4 = exec.ftr.lista.get(i).getnome();
                                exec.ftr.lista.get(i).setCPU(4);
                                exec.process4 = exec.ftr.lista.get(i);
                                exec.ftr.lista.remove(i);
                                jTextField11.setText(exec.process4.getnome() + " (Tempo Restante: " + Integer.toString(exec.process4.getTempRestante()) + "s)");
                            }
                        }
                    }
                    i++;
                }

                if (exec.process1.getCPU() == 1) {
                    jTextField2.setText(exec.process1.getnome() + " (Tempo Restante: " + Integer.toString(exec.process1.getTempRestante()) + "s)");
                    exec.executarTReal(1);
                } else {
                    exec.cpu1 = "";
                    exec.process1.setCPU(0);
                }

                if (exec.process2.getCPU() == 2) {
                    jTextField3.setText(exec.process2.getnome() + " (Tempo Restante: " + Integer.toString(exec.process2.getTempRestante()) + "s)");
                    exec.executarTReal(2);
                } else {
                    exec.cpu2 = "";
                    exec.process2.setCPU(0);
                }

                if (exec.process3.getCPU() == 3) {
                    jTextField10.setText(exec.process3.getnome() + " (Tempo Restante: " + Integer.toString(exec.process3.getTempRestante()) + "s)");
                    exec.executarTReal(3);
                } else {
                    exec.cpu3 = "";
                    exec.process3.setCPU(0);
                }

                if (exec.process4.getCPU() == 4) {
                    jTextField11.setText(exec.process4.getnome() + " (Tempo Restante: " + Integer.toString(exec.process4.getTempRestante()) + "s)");
                    exec.executarTReal(4);
                } else {
                    exec.cpu4 = "";
                    exec.process4.setCPU(0);
                }

                if (exec.cpu1.equals("")) {
                    jTextField2.setText(null);
                }
                if (exec.cpu2.equals("")) {
                    jTextField3.setText(null);
                }
                if (exec.cpu3.equals("")) {
                    jTextField10.setText(null);
                }
                if (exec.cpu4.equals("")) {
                    jTextField11.setText(null);
                }

                if (exec.cpu1.equals("fim")) {
                    exec.cpu1 = "";
                }
                if (exec.cpu2.equals("fim")) {
                    exec.cpu2 = "";
                }
                if (exec.cpu3.equals("fim")) {
                    exec.cpu3 = "";
                }
                if (exec.cpu4.equals("fim")) {
                    exec.cpu4 = "";
                }

                int ind = 0;
                jTextArea2.setText(null);
                while (!((ind > (exec.ftr.lista.size() - 1)))) {
                    jTextArea2.append(exec.ftr.lista.get(ind).getnome());
                    jTextArea2.append(" " + exec.ftr.lista.get(ind).memUsada + "\n");
                    String inf;
                    String inf1;
                    String inf2;
                    String inf3;
                    String inf4;
                    String inf5;
                    String inf6;

                    inf1 = "   Temp. Processo: " + Integer.toString(exec.ftr.lista.get(ind).getTempProcesso()) + "\n";
                    inf2 = "   Tamanho: " + Integer.toString(exec.ftr.lista.get(ind).getMemoria()) + "Mb\n";
                    inf3 = "   Impressoras: " + Integer.toString(exec.ftr.lista.get(ind).getImpressora()) + "\n";
                    inf6 = "   Scanner: " + Integer.toString(exec.ftr.lista.get(ind).getScanner()) + "\n";
                    inf4 = "   Modem: " + Integer.toString(exec.ftr.lista.get(ind).getModem()) + "\n";
                    inf5 = "   CDs: " + Integer.toString(exec.ftr.lista.get(ind).getCD()) + "\n";
                    jTextArea2.append(inf1);
                    jTextArea2.append(inf2);
                    jTextArea2.append(inf3);
                    jTextArea2.append(inf6);
                    jTextArea2.append(inf4);
                    jTextArea2.append(inf5);
                    ind++;
                }
            } else {
                //Executar, se houver,  Processos do Usuário
                if (exec.process1.getCPU() == 0) {
                    jTextField2.setText(null);
                }
                if (exec.process2.getCPU() == 0) {
                    jTextField3.setText(null);
                }
                if (exec.process3.getCPU() == 0) {
                    jTextField10.setText(null);
                }
                if (exec.process4.getCPU() == 0) {
                    jTextField11.setText(null);
                }

                if ((exec.mem1.totalUsada > 0) || (exec.mem2.totalUsada > 0)) {
                    if ((!exec.fuVazia()) || (!exec.fila0.lista.isEmpty()) || (!exec.fila1.lista.isEmpty()) || (!exec.fila2.lista.isEmpty())) {
                        if (exec.process1.getCPU() == 0) { //CPU 1
                            while (!exec.fuVazia()) {
                                if (exec.fu.lista.getFirst().memUsada.equals("Memória 1")) {
                                    exec.fila0.adicionarProcesso(exec.fu.removerProcesso());
                                } else {
                                    exec.fila00.adicionarProcesso(exec.fu.removerProcesso());
                                }
                            }
                            jTextArea3.setText(null);

                            int fila = 3;
                            if (!exec.fila0.lista.isEmpty()) {
                                jTextField2.setText(exec.fila0.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila0.lista.getFirst().getTempRestante()) + "s)");
                                nome1 = exec.fila0.lista.getFirst().getnome();
                                fila = 0;
                            } else {
                                if (!exec.fila1.lista.isEmpty()) {
                                    jTextField2.setText(exec.fila1.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila1.lista.getFirst().getTempRestante()) + "s)");
                                    nome1 = exec.fila1.lista.getFirst().getnome();
                                    fila = 1;
                                } else {
                                    if (!exec.fila2.lista.isEmpty()) {
                                        jTextField2.setText(exec.fila2.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila2.lista.getFirst().getTempRestante()) + "s)");
                                        nome1 = exec.fila2.lista.getFirst().getnome();
                                        fila = 2;
                                    }
                                }
                            }

                            if (!nome1.equals(nome2)) {
                                if (exec.mem1.totalUsada > 0) {
                                    jLabel21.setText("Ativado");
                                }
                                if ((!exec.fila0.lista.isEmpty()) || (!exec.fila1.lista.isEmpty()) || (!exec.fila2.lista.isEmpty())) {
                                    exec.executarNormal(1);
                                    normal1 = true;
                                }

                                i = 0;
                                jTextArea4.setText(null);
                                while (!(i > exec.fila0.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila0.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila0.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila0.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila0.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila0.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila0.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila0.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila0.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila00.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila00.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila00.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila00.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila00.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila00.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila00.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila00.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila00.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                jTextArea5.setText(null);
                                i = 0;
                                while (!(i > exec.fila1.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila1.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila1.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila1.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila1.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila1.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila1.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila1.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila1.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila01.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila01.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila01.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila01.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila01.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila01.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila01.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila01.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila01.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                jTextArea6.setText(null);
                                i = 0;
                                while (!(i > exec.fila2.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila2.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila2.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila2.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila2.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila2.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila2.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila2.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila2.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila02.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila02.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila02.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila02.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila02.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila02.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila02.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila02.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila02.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }

                            } else {
                                jTextField2.setText(null);
                            }
                        }
                        if (exec.process2.getCPU() == 0) { //CPU 2
                            while (!exec.fuVazia()) {
                                if (exec.fu.lista.getFirst().memUsada.equals("Memória 1")) {
                                    exec.fila0.adicionarProcesso(exec.fu.removerProcesso());
                                } else {
                                    exec.fila00.adicionarProcesso(exec.fu.removerProcesso());
                                }
                            }
                            jTextArea3.setText(null);

                            int fila = 3;
                            if (!exec.fila0.lista.isEmpty()) {
                                jTextField3.setText(exec.fila0.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila0.lista.getFirst().getTempRestante()) + "s)");
                                nome2 = exec.fila0.lista.getFirst().getnome();
                                fila = 0;
                            } else {
                                if (!exec.fila1.lista.isEmpty()) {
                                    jTextField3.setText(exec.fila1.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila1.lista.getFirst().getTempRestante()) + "s)");
                                    nome2 = exec.fila1.lista.getFirst().getnome();
                                    fila = 1;
                                } else {
                                    if (!exec.fila2.lista.isEmpty()) {
                                        jTextField3.setText(exec.fila2.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila2.lista.getFirst().getTempRestante()) + "s)");
                                        nome2 = exec.fila2.lista.getFirst().getnome();
                                        fila = 2;
                                    }
                                }
                            }

                            if (!(nome1.equals(nome2))) {
                                if (exec.mem1.totalUsada > 0) {
                                    jLabel21.setText("Ativado");
                                }
                                if ((!exec.fila0.lista.isEmpty()) || (!exec.fila1.lista.isEmpty()) || (!exec.fila2.lista.isEmpty())) {
                                    exec.executarNormal(2);
                                    normal2 = true;
                                }

                                i = 0;
                                jTextArea4.setText(null);
                                while (!(i > exec.fila0.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila0.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila0.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila0.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila0.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila0.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila0.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila0.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila0.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila00.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila00.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila00.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila00.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila00.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila00.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila00.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila00.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila00.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                jTextArea5.setText(null);
                                i = 0;
                                while (!(i > exec.fila1.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila1.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila1.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila1.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila1.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila1.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila1.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila1.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila1.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila01.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila01.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila01.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila01.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila01.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila01.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila01.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila01.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila01.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                jTextArea6.setText(null);
                                i = 0;
                                while (!(i > exec.fila2.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila2.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila2.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila2.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila2.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila2.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila2.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila2.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila2.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila02.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila02.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila02.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila02.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila02.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila02.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila02.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila02.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila02.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                            } else {
                                jTextField3.setText(null);
                            }
                        }
                    }

                    if ((!exec.fuVazia()) || (!exec.fila00.lista.isEmpty()) || (!exec.fila01.lista.isEmpty()) || (!exec.fila02.lista.isEmpty())) {
                        if (exec.process3.getCPU() == 0) { //CPU 3
                            while (!exec.fuVazia()) {
                                if (exec.fu.lista.getFirst().memUsada.equals("Memória 1")) {
                                    exec.fila0.adicionarProcesso(exec.fu.removerProcesso());
                                } else {
                                    exec.fila00.adicionarProcesso(exec.fu.removerProcesso());
                                }
                            }
                            jTextArea3.setText(null);

                            int fila = 3;
                            if (!exec.fila00.lista.isEmpty()) {
                                jTextField10.setText(exec.fila00.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila00.lista.getFirst().getTempRestante()) + "s)");
                                nome3 = exec.fila00.lista.getFirst().getnome();
                                fila = 0;
                            } else {
                                if (!exec.fila01.lista.isEmpty()) {
                                    jTextField10.setText(exec.fila01.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila01.lista.getFirst().getTempRestante()) + "s)");
                                    nome3 = exec.fila01.lista.getFirst().getnome();
                                    fila = 1;
                                } else {
                                    if (!exec.fila02.lista.isEmpty()) {
                                        jTextField10.setText(exec.fila02.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila02.lista.getFirst().getTempRestante()) + "s)");
                                        nome3 = exec.fila02.lista.getFirst().getnome();
                                        fila = 2;
                                    }
                                }
                            }

                            if (!nome3.equals(nome4)) {
                                if (exec.mem2.totalUsada > 0) {
                                    jLabel21.setText("Ativado");
                                }
                                if ((!exec.fila00.lista.isEmpty()) || (!exec.fila01.lista.isEmpty()) || (!exec.fila02.lista.isEmpty())) {
                                    exec.executarNormal(3);
                                    normal3 = true;
                                }
                                i = 0;
                                jTextArea4.setText(null);
                                while (!(i > exec.fila0.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila0.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila0.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila0.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila0.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila0.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila0.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila0.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila0.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila00.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila00.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila00.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila00.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila00.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila00.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila00.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila00.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila00.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                jTextArea5.setText(null);
                                i = 0;
                                while (!(i > exec.fila1.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila1.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila1.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila1.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila1.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila1.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila1.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila1.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila1.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila01.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila01.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila01.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila01.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila01.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila01.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila01.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila01.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila01.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                jTextArea6.setText(null);
                                i = 0;
                                while (!(i > exec.fila2.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila2.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila2.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila2.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila2.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila2.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila2.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila2.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila2.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila02.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila02.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila02.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila02.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila02.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila02.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila02.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila02.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila02.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                            } else {
                                jTextField10.setText(null);
                            }
                        }
                        if (exec.process4.getCPU() == 0) { //CPU 4
                            while (!exec.fuVazia()) {
                                if (exec.fu.lista.getFirst().memUsada.equals("Memória 1")) {
                                    exec.fila0.adicionarProcesso(exec.fu.removerProcesso());
                                } else {
                                    exec.fila00.adicionarProcesso(exec.fu.removerProcesso());
                                }
                            }
                            jTextArea3.setText(null);

                            int fila = 3;
                            if (!exec.fila00.lista.isEmpty()) {
                                jTextField11.setText(exec.fila00.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila00.lista.getFirst().getTempRestante()) + "s)");
                                nome4 = exec.fila00.lista.getFirst().getnome();
                                fila = 0;
                            } else {
                                if (!exec.fila01.lista.isEmpty()) {
                                    jTextField11.setText(exec.fila01.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila01.lista.getFirst().getTempRestante()) + "s)");
                                    nome4 = exec.fila01.lista.getFirst().getnome();
                                    fila = 1;
                                } else {
                                    if (!exec.fila02.lista.isEmpty()) {
                                        jTextField11.setText(exec.fila02.lista.getFirst().getnome() + " (Tempo Restante: " + Integer.toString(exec.fila02.lista.getFirst().getTempRestante()) + "s)");
                                        nome4 = exec.fila02.lista.getFirst().getnome();
                                        fila = 2;
                                    }
                                }
                            }

                            if (!(nome3.equals(nome4))) {
                                if (exec.mem2.totalUsada > 0) {
                                    jLabel21.setText("Ativado");
                                }
                                if ((!exec.fila00.lista.isEmpty()) || (!exec.fila01.lista.isEmpty()) || (!exec.fila02.lista.isEmpty())) {
                                    exec.executarNormal(4);
                                    normal4 = true;
                                }

                                i = 0;
                                jTextArea4.setText(null);
                                while (!(i > exec.fila0.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila0.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila0.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila0.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila0.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila0.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila0.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila0.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila0.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila00.lista.size() - 1)) {
                                    jTextArea4.append(exec.fila00.lista.get(i).getnome());
                                    jTextArea4.append(" " + exec.fila00.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila00.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila00.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila00.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila00.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila00.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila00.lista.get(i).getCD()) + "\n";
                                    jTextArea4.append(inf1);
                                    jTextArea4.append(inf2);
                                    jTextArea4.append(inf3);
                                    jTextArea4.append(inf6);
                                    jTextArea4.append(inf4);
                                    jTextArea4.append(inf5);
                                    i++;
                                }
                                jTextArea5.setText(null);
                                i = 0;
                                while (!(i > exec.fila1.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila1.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila1.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila1.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila1.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila1.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila1.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila1.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila1.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila01.lista.size() - 1)) {
                                    jTextArea5.append(exec.fila01.lista.get(i).getnome());
                                    jTextArea5.append(" " + exec.fila01.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila01.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila01.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila01.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila01.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila01.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila01.lista.get(i).getCD()) + "\n";
                                    jTextArea5.append(inf1);
                                    jTextArea5.append(inf2);
                                    jTextArea5.append(inf3);
                                    jTextArea5.append(inf6);
                                    jTextArea5.append(inf4);
                                    jTextArea5.append(inf5);
                                    i++;
                                }
                                jTextArea6.setText(null);
                                i = 0;
                                while (!(i > exec.fila2.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila2.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila2.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila2.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila2.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila2.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila2.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila2.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila2.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                                i = 0;
                                while (!(i > exec.fila02.lista.size() - 1)) {
                                    jTextArea6.append(exec.fila02.lista.get(i).getnome());
                                    jTextArea6.append(" " + exec.fila02.lista.get(i).memUsada + "\n");
                                    String inf;
                                    String inf1;
                                    String inf2;
                                    String inf3;
                                    String inf4;
                                    String inf5;
                                    String inf6;

                                    inf1 = "   Temp. Processo: " + Integer.toString(exec.fila02.lista.get(i).getTempProcesso()) + "\n";
                                    inf2 = "   Tamanho: " + Integer.toString(exec.fila02.lista.get(i).getMemoria()) + "Mb\n";
                                    inf3 = "   Impressoras: " + Integer.toString(exec.fila02.lista.get(i).getImpressora()) + "\n";
                                    inf6 = "   Scanner: " + Integer.toString(exec.fila02.lista.get(i).getScanner()) + "\n";
                                    inf4 = "   Modem: " + Integer.toString(exec.fila02.lista.get(i).getModem()) + "\n";
                                    inf5 = "   CDs: " + Integer.toString(exec.fila02.lista.get(i).getCD()) + "\n";
                                    jTextArea6.append(inf1);
                                    jTextArea6.append(inf2);
                                    jTextArea6.append(inf3);
                                    jTextArea6.append(inf6);
                                    jTextArea6.append(inf4);
                                    jTextArea6.append(inf5);
                                    i++;
                                }
                            } else {
                                jTextField11.setText(null);
                            }
                        }
                    }

                    //Executar, se houver,  Processos de Tempo Real
                    if (exec.process1.getCPU() == 1) {
                        jTextField2.setText(exec.process1.getnome() + " (Tempo Restante: " + Integer.toString(exec.process1.getTempRestante()) + "s)");
                        exec.executarTReal(1);
                    } else {
                        exec.cpu1 = "";
                        exec.process1.setCPU(0);
                    }

                    if (exec.process2.getCPU() == 2) {
                        jTextField3.setText(exec.process2.getnome() + " (Tempo Restante: " + Integer.toString(exec.process2.getTempRestante()) + "s)");
                        exec.executarTReal(2);
                    } else {
                        exec.cpu2 = "";
                        exec.process2.setCPU(0);
                    }

                    if (exec.process3.getCPU() == 3) {
                        jTextField10.setText(exec.process3.getnome() + " (Tempo Restante: " + Integer.toString(exec.process3.getTempRestante()) + "s)");
                        exec.executarTReal(3);
                    } else {
                        exec.cpu3 = "";
                        exec.process3.setCPU(0);
                    }

                    if (exec.process4.getCPU() == 4) {
                        jTextField11.setText(exec.process4.getnome() + " (Tempo Restante: " + Integer.toString(exec.process4.getTempRestante()) + "s)");
                        exec.executarTReal(4);
                    } else {
                        exec.cpu4 = "";
                        exec.process4.setCPU(0);
                    }

                    if (exec.cpu1.equals("fim")) {
                        exec.cpu1 = "";
                    }
                    if (exec.cpu2.equals("fim")) {
                        exec.cpu2 = "";
                    }
                }
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextArea jTextArea7;
    private javax.swing.JTextArea jTextArea8;
    private javax.swing.JTextArea jTextArea9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
