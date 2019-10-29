package agents;

//Bitcho69
import java.util.Random;

public class Bitxo1 extends Agent {

    static final boolean DEBUG = false;

    static final int PARET = 0;
    static final int NAU = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    Estat estat;
    int espera = 0;

    long temps;
    Random rng;

    public Bitxo1(Agents pare) {
        super(pare, "Bitcho69", "imatges/moyake25ANGRY.gif");
    }

    @Override
    public void inicia() {
        rng = new Random();
        posaAngleVisors(40);
        posaDistanciaVisors(400);
        posaVelocitatLineal(6);
        posaVelocitatAngular(9);
        espera = 0;
        temps = 0;
    }

    @Override
    public void avaluaComportament() {
        temps++;
        estat = estatCombat();
        if (espera > 0) {
            enrere();
            if (espera < 5) {
                if (estat.distanciaVisors[DRETA] >= estat.distanciaVisors[ESQUERRA]) {
                    endavant();
                    dreta();
                } else {
                    endavant();
                    esquerra();
                }
            }
            espera--;
        } else {
            atura();

            if (estat.enCollisio) { //Estoy en colision
                // si veu la nau, dispara
                if (estat.objecteVisor[CENTRAL] == NAU) {
                    dispara();
                    enrere();
                }
                espera = 7;
                if (DEBUG) {
                    System.out.println("Estoy en colision");
                }
            } else { //Si no estoy en colision
                if (estat.fuel < 10000) {
                    posaVelocitatLineal(3);
                } else {
                    posaVelocitatLineal(6);
                }
                if (estat.objecteVisor[CENTRAL] == NAU && estat.distanciaVisors[CENTRAL] <= 50) {
                    if (estat.bales > 0 && !estat.disparant) {
                        dispara();
                    }
                    enrere();
                } else if (estat.objecteVisor[DRETA] == NAU && estat.distanciaVisors[DRETA] <= 50) {
                    dreta();
                    endavant();
                } else if (estat.objecteVisor[ESQUERRA] == NAU && estat.distanciaVisors[ESQUERRA] <= 50) {
                    esquerra();
                    endavant();
                } else {
                    endavant();
                }

                if (estat.numMines > 0 && estat.numRecursos > 0) {
                    int distMin = 4000;
                    int posMina = 0;
                    int posRecurs = 0;
                    for (int i = 0; i < estat.numMines; i++) { //Buscamos la mina mas cercana
                        if (estat.mina[i].agafaDistancia() <= distMin) {
                            distMin = estat.mina[i].agafaDistancia();
                            posMina = i;
                        }
                    }
                    distMin = 4000;
                    for (int i = 0; i < estat.numRecursos; i++) { //Buscamos la mina mas cercana
                        if (estat.recurs[i].agafaDistancia() <= distMin) {
                            distMin = estat.recurs[i].agafaDistancia();
                            posRecurs = i;
                        }
                    }

                    //Si la mina esta mas cerca que el recurso, esquivamos la mina. Sino cogemos el recurso
                    if (estat.mina[posMina].agafaDistancia() <= estat.recurs[posRecurs].agafaDistancia()) {
                        esquivarMina(posMina);
                    } else {
                        mira(estat.recurs[posRecurs]);
                    }

                } else {
                    //Nuevo sistema de esquivar minas, funciona un pelin mejor (queda mejorar el sistema de recursos)
                    if (estat.numMines > 0) {
                        int distMin = 4000;
                        int posMina = 0;
                        for (int i = 0; i < estat.numMines; i++) { //Buscamos la mina mas cercana
                            if (estat.mina[i].agafaDistancia() <= distMin) {
                                distMin = estat.mina[i].agafaDistancia();
                                posMina = i;
                            }
                        }

                        //Esquivamos la mina más cercana
                        esquivarMina(posMina);
                    }

                    //Si no vemos minas, buscamos recusrsos
                    if (estat.numRecursos > 0) {
                        if (estat.numRecursos == 1) {
                            if (!estat.enCollisio) {
                                mira(estat.recurs[0]);
                            }
                        } else {
                            for (int i = 0; i < estat.numRecursos - 1; i++) {
                                if ((estat.recurs[i].agafaSector() == 2 || estat.recurs[i].agafaSector() == 3)
                                        && (estat.recurs[i + 1].agafaSector() == 2 || estat.recurs[i + 1].agafaSector() == 3)) {
                                    if (estat.recurs[i].agafaDistancia() <= estat.recurs[i + 1].agafaDistancia()) {
                                        if (!estat.enCollisio) {
                                            mira(estat.recurs[i]);
                                        }
                                    } else if (i == estat.numRecursos - 1) {
                                        if (!estat.enCollisio) {
                                            mira(estat.recurs[i + 1]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //Si hay hyperespacio disponibles y nos disparan, los usamos.
                if (estat.hyperespaiDisponibles > 0) {
                    if (estat.impactesRebuts > 0) {
                        hyperespai();
                        if (DEBUG) {
                            System.out.println("HYPERESPAI");
                        }
                    }
                }

                if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < 30
                        && estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < 30
                        && estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < 30) {
                    int x = rng.nextInt(3) + 1;
                    switch (x) {
                        case 1:
                            gira(180);
                            break;
                        case 2:
                            gira(140);
                            break;
                        default:
                            gira(-140);
                            break;
                    }
                    if (DEBUG) {
                        System.out.println("Pegado a la pared");
                    }
                }

                if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < 100
                        && estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]) {
                    dreta();
                    if (DEBUG) {
                        System.out.println("Veo pared izquierda, giro derecha");
                    }
                } else if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < 100
                        && estat.distanciaVisors[DRETA] <= estat.distanciaVisors[ESQUERRA]) {
                    esquerra();
                    if (DEBUG) {
                        System.out.println("Veo pared derecha, giro izquierda");
                    }
                } else if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < 50) {
                    if (estat.distanciaVisors[DRETA] <= estat.distanciaVisors[ESQUERRA]) {
                        gira(-45);
                        if (DEBUG) {
                            System.out.println("Veo pared delante, pero la derecha esta mas cerca, giro izq");
                        }
                    } else {
                        gira(45);
                        if (DEBUG) {
                            System.out.println("Veo pared delante, pero la izquierda esta mas cerca, giro der");
                        }
                    }
                }
            }
        }
    }

    public void esquivarMina(int posMina) {
        if (estat.mina[posMina].agafaSector() == 2) { // Minas a la derecha
            if (estat.mina[posMina].agafaDistancia() < 50 && estat.distanciaVisors[DRETA] < 60 && estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[ESQUERRA] > estat.distanciaVisors[DRETA]) {
                gira(30);
            } else if (estat.mina[posMina].agafaDistancia() < 50 && estat.distanciaVisors[ESQUERRA] < 60 && estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]) {
                gira(-45);
            } else if (estat.mina[posMina].agafaDistancia() < 50) {
                gira(30);
            }

        } else if (estat.mina[posMina].agafaSector() == 3) { // Minas a la izquierda
            if (estat.mina[posMina].agafaDistancia() < 50 && estat.distanciaVisors[ESQUERRA] < 60 && estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[DRETA]) {
                gira(-30);
            } else if (estat.mina[posMina].agafaDistancia() < 50 && estat.distanciaVisors[DRETA] < 60 && estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[ESQUERRA] > estat.distanciaVisors[DRETA]) {
                gira(45);
            } else if (estat.mina[posMina].agafaDistancia() < 50) {
                gira(-30);
            }
        }
    }

}
