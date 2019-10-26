package agents;

//Bitcho69
// APUNTES
// - Cuando se recibe daño y no hay enemigos en los sensores --> Hiperespacio babyyy
// - Ver si se pueden saber cuántos recursos hay en el campo, si no quedan --> modo huir bich
// - sosig
import java.util.Random;

public class Bitxo1 extends Agent {

    static final boolean DEBUG = true;

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
        modoRecolector();
        espera = 0;
        temps = 0;
    }

    void modoRecolector() { //Estado recolector de recursos y evitar minas
        posaAngleVisors(40);
        posaDistanciaVisors(250);
        posaVelocitatLineal(6);
        posaVelocitatAngular(9);
    }

    void modoHuida() { //Huye en el caso que vea enemigos
        if (estat.numEnemics > 0) {
            if (estat.enemic[0].agafaSector() == 3 && estat.enemic[0].agafaDistancia() <= 200) {
                dreta();
                System.out.println("ou shit me escapo del enemico izquierdo");
            } else if (estat.enemic[0].agafaSector() == 2 && estat.enemic[0].agafaDistancia() <= 200) {
                esquerra();
                System.out.println("ou shit me escapo del enemico derecho");
            }
        }
    }

    //Segundo intento de hacer esquivar al puto bicho.
    //Puede girar a la izq o a la der (poner que gire 180?)
    void esquivar() {
        int selector = rng.nextInt(2);
        if (selector == 0) {
            dreta();
        } else {
            esquerra();
        }
    }

    void movimiento() {
        // Miram els visors per detectar els obstacles
        int sensor = 0;
        if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < 45) {
            sensor += 1;
        }
        if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < 45) {
            sensor += 2;
        }
        if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < 45) {
            sensor += 4;
        }

        switch (sensor) {
            case 0: //000
                endavant();
                break;
            case 1: //001
            case 2:  // paret devant (010)
            case 3:  // esquerra bloquejada (011)
                dreta();
                break;
            case 4: //100
            case 5: //101
                endavant();
                break;  // centre lliure
            case 6:  // dreta bloquejada (110)
                esquerra();
                break;
            case 7:  // si estic molt aprop, torna enrere (111)
                double distancia;
                distancia = minimaDistanciaVisors();
                if (distancia < 15) {
                    espera = 5;
                    enrere();
                } else {
                    esquerra();
                }
                break;
        }
    }

    @Override
    public void avaluaComportament() {
        boolean enemic = false;

        temps++;
        estat = estatCombat();
        if (espera > 0) {
            espera--;
        } else {
            atura();

            //Nuevo sistema de esquivar minas, funciona un pelin mejor (queda mejorar el sistema de recursos)
            if (estat.numMines > 0) {
                
                
                
                
                if (estat.numMines == 1) {
                    System.out.println("Solo veo una MINA");
                    if (estat.mina[0].agafaSector() == 3 && estat.mina[0].agafaDistancia() <= 40) {
                        dreta();
//                        espera = 3;
                        System.out.println("Veo mina izq");
                    } else if (estat.mina[0].agafaSector() == 2 && estat.mina[0].agafaDistancia() <= 40) {
                        esquerra();
//                        espera = 3;
                        System.out.println("Veo mina der");
                    }
                } else {
//                    System.out.printf("Veo %d minas, distancia 1: %d, distancia 2: %d\n", estat.numMines, estat.mina[0].agafaDistancia(), estat.mina[1].agafaDistancia());
//                    int num_mina = 0;
                    for (int i = 0; i < estat.numMines - 1; i++) { //Buscamos la mina mas cercana
                        if (estat.mina[i].agafaDistancia() <= estat.mina[i + 1].agafaDistancia()) {
                            if (estat.mina[i].agafaSector() == 3 && estat.mina[i].agafaDistancia() <= 40) {
                                dreta();
//                        espera = 3;
                                System.out.println("Veo mina 1 izq");
                            } else if (estat.mina[i].agafaSector() == 2 && estat.mina[i].agafaDistancia() <= 40) {
                                esquerra();
//                        espera = 3;
                                System.out.println("Veo mina 1 der");
                            }
                        } else if (i == estat.numMines - 1) {
                            if (estat.mina[i + 1].agafaSector() == 3 && estat.mina[i + 1].agafaDistancia() <= 40) {
                                dreta();
//                        espera = 3;
                                System.out.println("Veo mina 1 izq");
                            } else if (estat.mina[i + 1].agafaSector() == 2 && estat.mina[i + 1].agafaDistancia() <= 40) {
                                esquerra();
//                        espera = 3;
                                System.out.println("Veo mina 1 der");
                            }
                        }
                    }

                }
            } //Si no vemos minas, buscamos recusrsos
                if (DEBUG) {
//                    System.out.println("Estoy buscando recursos bich");
                }

                if (estat.numRecursos > 0) {
                    if (estat.numRecursos == 1) {
                        if (!estat.enCollisio) {
                            mira(estat.recurs[0]);
                        }
                    } else {
                        for (int i = 0; i < estat.numRecursos - 1; i++) {
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
            

                if (estat.hyperespaiDisponibles > 0) {
                    if (estat.impactesRebuts > 0) {
                        hyperespai();
                        if (DEBUG) {
                            System.out.println("HYPERESPAI");
                        }
                    }
                }

                if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] <= 20) {
                    enrere();
                    gira(180);
                    System.out.println("smoke 180 bich");
                } else if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] <= 20) {
                    dreta();
                    System.out.println("esquivar dreta");
                } else if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] <= 20) {
                    esquerra();
                    System.out.println("esquivar skere");
                } else {
                    endavant();
                }

                if (estat.enCollisio) { //Estoy en colision
                    // si veu la nau, dispara
                    if (estat.objecteVisor[CENTRAL] == NAU) {
                        dispara();
                        enrere(); //For clapping cheeks
                        System.out.println("I'm clapping this nigga cheeks");
                    }
                    if (DEBUG) {
                        System.out.println("Estoy en colision FUCK");
                    }
                } else { //Si no estoy en colision
                    endavant();
                    if (estat.objecteVisor[CENTRAL] == NAU) {
                        enemic = true;
                        //modoHuida();
                        dreta();
                        //espera = 2;
                    } else if (estat.objecteVisor[ESQUERRA] == NAU) {
                        if (!estat.enCollisio) {
                            dreta();
//                            espera = 2;
                        }
                        if (DEBUG) {
                            System.out.println("i'm seeing a nigga in my ESQUERRA");
                        }
                    } else if (estat.objecteVisor[DRETA] == NAU) {
                        if (!estat.enCollisio) {
                            esquerra();
//                            espera = 2;
                        }
                        if (DEBUG) {
                            System.out.println("i'm seeing a nigga in my DRETA");
                        }
                    } else {
                        enemic = false;
                        modoRecolector();
                    }

                    movimiento();

                }

            }
        
    }

    boolean hiHaParedDavant(int dist) {
        if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] <= dist) {
            return true;
        }
        if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] <= dist) {
            return true;
        }
        if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] <= dist) {
            return true;
        }
        return false;
    }

    double minimaDistanciaVisors() {
        double minim = Double.POSITIVE_INFINITY;
        if (estat.objecteVisor[ESQUERRA] == PARET) {
            minim = estat.distanciaVisors[ESQUERRA];
        }
        if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < minim) {
            minim = estat.distanciaVisors[CENTRAL];
        }
        if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < minim) {
            minim = estat.distanciaVisors[DRETA];
        }
        return minim;
    }

}
