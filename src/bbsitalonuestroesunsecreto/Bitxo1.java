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
        super(pare, "Bitcho69", "imatges/moyake25.gif");
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
            if (estat.enemic[0].agafaSector() == 3) {
                dreta();
            } else if (estat.enemic[0].agafaSector() == 2) {
                esquerra();
            }
        }
    }

//    void esquivar() {
//        int giro = rng.nextInt(360) + 1;
//        gira(giro);
//    }
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
                    espera = 8;
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

            if (estat.numRecursos > 0) {
                if (estat.numRecursos == 1) {
                    if (!estat.enCollisio) {
                        mira(estat.recurs[0]);
                    }
                } else {
                    if (estat.recurs[0].agafaDistancia() > estat.recurs[1].agafaDistancia()) {
                        if (!estat.enCollisio) {
                            mira(estat.recurs[0]);
                        }
                    } else {
                        if (!estat.enCollisio) {
                            mira(estat.recurs[1]);
                        }
                    }
                }
            }

            //Nuevo sistema de esquivar minas, funciona un pelin mejor (queda mejorar el sistema de recursos)
            if (estat.numMines > 0) {
                if (estat.numMines == 1) {
                    if (estat.mina[0].agafaSector() == 3) {
                        if (estat.mina[0].agafaDistancia() <= 40) {
                            dreta();
                            espera = 3;
                        }
                    } else if (estat.mina[0].agafaSector() == 2) {
                        if (estat.mina[0].agafaDistancia() <= 40) {
                            esquerra();
                            espera = 3;
                        }
                    }
                } else {
                    if (estat.mina[0].agafaDistancia() <= estat.mina[1].agafaDistancia()) {
                        if (estat.mina[0].agafaSector() == 3) {
                            if (estat.mina[0].agafaDistancia() <= 40) {
                                dreta();
                                espera = 3;
                            }
                        } else if (estat.mina[0].agafaSector() == 2) {
                            if (estat.mina[0].agafaDistancia() <= 40) {
                                esquerra();
                                espera = 3;
                            }
                        }
                    } else {
                        if (estat.mina[1].agafaSector() == 3) {
                            if (estat.mina[1].agafaDistancia() <= 40) {
                                dreta();
                                espera = 3;
                            }
                        } else if (estat.mina[1].agafaSector() == 2) {
                            if (estat.mina[1].agafaDistancia() <= 40) {
                                esquerra();
                                espera = 3;
                            }
                        }
                    }
                }
            }

//            if (estat.numMines > 0) {
//                if (estat.numMines == 1) {
//                    mira(estat.mina[0]);
//                    if (DEBUG) {
//                        System.out.println("estoy mirando la mina 0");
//                    }
//                    if (estat.mina[0].agafaDistancia() <= 40) {
//                        esquivar();
//                        if (DEBUG) {
//                            System.out.println("esquivar en caso 1");
//                        }
//                        espera = 3;
//                    }
//                } else {
//                    if (estat.mina[0].agafaDistancia() < estat.mina[1].agafaDistancia()) {
//                        mira(estat.mina[0]);
//                        if (DEBUG) {
//                            System.out.println("estoy mirando la mina 0 bich");
//                        }
//                        if (estat.mina[0].agafaDistancia() <= 40) {
//                            esquivar();
//                            if (DEBUG) {
//                                System.out.println("esquivar en caso 2");
//                            }
//                            espera = 3;
//                        }
//                    } else {
//                        mira(estat.mina[1]);
//                        if (DEBUG) {
//                            System.out.println("estoy mirando la mina 1 bich");
//                        }
//                        if (estat.mina[1].agafaDistancia() <= 40) {
//                            esquivar();
//                            if (DEBUG) {
//                                System.out.println("esquivar en caso 3");
//                            }
//                            espera = 3;
//                        }
//                    }
//                }
//            }
            if (estat.hyperespaiDisponibles > 0) {
                if (estat.impactesRebuts > 0) {
                    hyperespai();
                    if (DEBUG) {
                        System.out.println("HYPERESPAI");
                    }
                }
            }

            if (estat.enCollisio) { // situació de nau bloquejada
                // si veu la nau, dispara
                if (estat.objecteVisor[CENTRAL] == NAU) {
                    dispara();
                    enrere();
                } else { // hi ha un obstacle, gira i parteix
                    //esquivar();
                    if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] <= 20) {
                        gira(180);
                    } else if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] <= 20) {
                        dreta();
                    } else if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] <= 20) {
                        esquerra();
                    }
                    if (DEBUG) {
                        System.out.println("esquivar en colision");
                    }
                    if (hiHaParedDavant(20)) {
                        enrere();
                    } else {
                        endavant();
                    }

                    espera = 3;
                }
            } else {
                endavant();

                if (estat.objecteVisor[CENTRAL] == NAU) {
                    enemic = true;
                    //modoHuida();
                    dreta();
                    espera = 2;
                } else if (estat.objecteVisor[ESQUERRA] == NAU) {
                    if (!estat.enCollisio) {
                        dreta();
                        espera = 2;
                    }
                    if (DEBUG) {
                        System.out.println("i'm seeing a nigga in my ESQUERRA");
                    }
                } else if (estat.objecteVisor[DRETA] == NAU) {
                    if (!estat.enCollisio) {
                        esquerra();
                        espera = 2;
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
