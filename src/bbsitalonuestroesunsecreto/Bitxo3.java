//
// Decompiled by Procyon v0.5.36
//
package agents;

import java.util.Random;

public class Bitxo3 extends Agent {

    static final boolean DEBUG = false;
    static final int PARET = 0;
    static final int NAU = 1;
    static final int RES = -1;
    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    Estat estat;
    int auxdistmina;
    int espera;
    int esperamina;
    int contador;
    int mindist;
    int mindistm;
    int r;
    int m;
    int recursonumero;
    int minanumero;
    int distanciadisparo;
    int sectorEX;
    int casos;
    long temps;

    public Bitxo3(final Agents pare) {
        super(pare, "Espitchança", "imatges/sosig.gif");
        auxdistmina = 30;
        espera = 0;
        esperamina = 0;
        contador = 100;
        mindist = 1000;
        mindistm = 1000;
        distanciadisparo = 40;
    }

    public void inicia() {
        posaAngleVisors(45);
        posaDistanciaVisors(400);
        posaVelocitatLineal(6);
        posaVelocitatAngular(6);
        espera = 0;
        temps = 0L;
        contador = 40;
    }

    public void avaluaComportament() {
        ++temps;
        estat = estatCombat();
        if (espera > 0) {
            enrere();
            if (espera < 3) {
                if (estatCombat().distanciaVisors[2] >= estatCombat().distanciaVisors[0]) {
                    dreta();
                } else {
                    esquerra();
                }
            }
            --espera;
        } else {
            atura();
            if (estatCombat().enCollisio) {
                espera = 7;
            } else {
                //System.out.println(estat.fuel);
                if (estat.fuel < 15000L) {
                    posaVelocitatLineal(3);
                } else {
                    posaVelocitatLineal(6);
                }
                if (estatCombat().objecteVisor[1] == 1 && estatCombat().distanciaVisors[1] < 30.0) {
                    enrere();
                } else {
                    endavant();
                }
                if (mindistm > 45) {
                    if (estatCombat().balaEnemigaDetectada && estatCombat().distanciaBalaEnemiga < 40 && estatCombat().objecteVisor[1] == 1 && estatCombat().hyperespaiDisponibles > 0) {
                        dispara();
                        hyperespai();
                    }
                    if (estatCombat().bales > 25) {
                        distanciadisparo = 400;
                    }
                    if (estatCombat().objecteVisor[1] == 1 && !estatCombat().disparant && estatCombat().distanciaVisors[1] < distanciadisparo && estatCombat().bales > 0) {
                        dispara();
                    }
                    if (estatCombat().numEnemics > 0) {
                        if (estatCombat().numEnemics == 1) {
                            if (estatCombat().enemic[0].agafaSector() == 4) {
                                gira(45);
                                mira(estatCombat().enemic[0]);
                            } else if (estatCombat().enemic[0].agafaSector() == 1) {
                                gira(-45);
                                mira(estatCombat().enemic[0]);
                            }
                        } else if (estatCombat().numEnemics == 2 && estatCombat().enemic[0].agafaDistancia() < estatCombat().enemic[1].agafaDistancia()) {
                            if (estatCombat().numEnemics == 1) {
                                if (estatCombat().enemic[0].agafaSector() == 4) {
                                    gira(45);
                                } else if (estatCombat().enemic[0].agafaSector() == 1) {
                                    gira(-45);
                                }
                                mira(estatCombat().enemic[0]);
                            } else if (estatCombat().numEnemics == 1) {
                                if (estatCombat().enemic[1].agafaSector() == 4) {
                                    gira((int) estatCombat().angleVisors);
                                } else if (estatCombat().enemic[1].agafaSector() == 1) {
                                    gira(-(int) estatCombat().angleVisors);
                                }
                                mira(estatCombat().enemic[1]);
                            }
                        }
                    }
                    if (estatCombat().objecteVisor[2] == 1) {
                        gira(-(int) estatCombat().angleVisors);
                    }
                    if (estatCombat().objecteVisor[0] == 1) {
                        gira((int) estatCombat().angleVisors);
                    }
                    if (estatCombat().objecteVisor[1] == 0 && estatCombat().distanciaVisors[1] < 30.0
                            && estatCombat().objecteVisor[2] == 0 && estatCombat().distanciaVisors[2] < 30.0
                            && estatCombat().objecteVisor[0] == 0 && estatCombat().distanciaVisors[0] < 30.0) {
                        final Random r = new Random();
                        final int aleatorio = r.nextInt(3);
                        if (aleatorio == 0) {
                            gira(180);
                        } else if (aleatorio == 1) {
                            gira(135);
                        } else {
                            gira(225);
                        }
                    }
                    if (estatCombat().objecteVisor[0] == 0 && estatCombat().distanciaVisors[0] < 130.0
                            && estatCombat().distanciaVisors[0] < estatCombat().distanciaVisors[2]) {
                        dreta();
                    } else if (estatCombat().objecteVisor[2] == 0 && estatCombat().distanciaVisors[2] < 130.0
                            && estatCombat().distanciaVisors[2] <= estatCombat().distanciaVisors[0]) {
                        esquerra();
                    } else if (estatCombat().objecteVisor[1] == 0 && estatCombat().distanciaVisors[1] < 80.0) {
                        if (estatCombat().distanciaVisors[2] <= estatCombat().distanciaVisors[0]) {
                            gira(-45);
                        } else {
                            gira(45);
                        }
                    }
                    if ((estat.distanciaVisors[1] >= 25.0 || estat.objecteVisor[1] != 0)
                            && (estat.distanciaVisors[2] >= 25.0 || estat.objecteVisor[2] != 0)
                            && (estat.distanciaVisors[0] >= 25.0 || estat.objecteVisor[0] != 0)
                            && (estatCombat().objecteVisor[0] != 0 || estatCombat().distanciaVisors[0] >= 35.0)
                            && (estatCombat().objecteVisor[2] != 0 || estatCombat().distanciaVisors[2] >= 35.0)
                            && estatCombat().numRecursos > 0) {
                        r = estatCombat().numRecursos - 1;
                        mindist = estatCombat().recurs[r].agafaDistancia();
                        recursonumero = r;
                        --r;
                        while (r != -1) {
                            if (estatCombat().recurs[r].agafaDistancia() > 5 && mindist > estatCombat().recurs[r].agafaDistancia()) {
                                mindist = estatCombat().recurs[r].agafaDistancia();
                                recursonumero = r;
                                sectorEX = estatCombat().recurs[r].agafaSector();
                            }
                            --r;
                        }
                        if (sectorEX == 4) {
                            gira((int) estatCombat().angleVisors);
                        } else if (sectorEX == 1) {
                            gira(-(int) estatCombat().angleVisors);
                        }
                        mira(estatCombat().recurs[recursonumero]);
                    }
                    if (estatCombat().bales == 0 && estatCombat().objecteVisor[1] == 1) {
                        gira(180);
                    }
                }
                if (estatCombat().numMines > 0) {
                    m = estatCombat().numMines - 1;
                    mindistm = estatCombat().mina[m].agafaDistancia();
                    minanumero = m;
                    --m;
                    while (m != -1) {
                        if (mindistm > estatCombat().mina[m].agafaDistancia()) {
                            mindistm = estatCombat().mina[m].agafaDistancia();
                            minanumero = m;
                        }
                        --m;
                    }
                    if (estatCombat().mina[minanumero].agafaSector() == 2 && estatCombat().mina[minanumero].agafaDistancia() < 50 && estatCombat().distanciaVisors[2] < 60.0 && estatCombat().objecteVisor[2] == 0 && estatCombat().distanciaVisors[0] > estatCombat().distanciaVisors[2]) {
                        gira(30);
                    } else if (estatCombat().mina[minanumero].agafaSector() == 2 && estatCombat().mina[minanumero].agafaDistancia() < 50 && estatCombat().distanciaVisors[0] < 60.0 && estatCombat().objecteVisor[0] == 0 && estatCombat().distanciaVisors[0] < estatCombat().distanciaVisors[2]) {
                        gira(-45);
                    } else if (estatCombat().mina[minanumero].agafaSector() == 3 && estatCombat().mina[minanumero].agafaDistancia() < 50 && estatCombat().distanciaVisors[0] < 60.0 && estatCombat().objecteVisor[0] == 0 && estatCombat().distanciaVisors[0] < estatCombat().distanciaVisors[2]) {
                        gira(-30);
                    } else if (estatCombat().mina[minanumero].agafaSector() == 3 && estatCombat().mina[minanumero].agafaDistancia() < 50 && estatCombat().distanciaVisors[2] < 60.0 && estatCombat().objecteVisor[2] == 0 && estatCombat().distanciaVisors[0] > estatCombat().distanciaVisors[2]) {
                        gira(45);
                    } else if (estatCombat().mina[minanumero].agafaSector() == 2 && estatCombat().mina[minanumero].agafaDistancia() < 50) {
                        gira(30);
                    } else if (estatCombat().mina[minanumero].agafaSector() == 3 && estatCombat().mina[minanumero].agafaDistancia() < 50) {
                        gira(-30);
                    }
                    if (estatCombat().bales >= 30) {
                        auxdistmina = 80;
                    } else {
                        auxdistmina = 30;
                    }
                    if (estatCombat().mina[minanumero].agafaDistancia() < auxdistmina && estatCombat().bales > 0 && !estatCombat().disparant) {
                        while (!disparant()) {
                            atura();
                            switch (estatCombat().mina[minanumero].agafaSector()) {
                                case 4: {
                                    gira((int) estatCombat().angleVisors);
                                    mira(estatCombat().mina[minanumero]);
                                    dispara();
                                    continue;
                                }
                                case 1: {
                                    gira(-(int) estatCombat().angleVisors);
                                    mira(estatCombat().mina[minanumero]);
                                    dispara();
                                    continue;
                                }
                                default: {
                                    mira(estatCombat().mina[minanumero]);
                                    dispara();
                                    continue;
                                }
                            }
                        }
                    }
                } else {
                    mindistm = 1000;
                }
            }
        }
    }

    boolean hiHaParedDavant(final int dist) {
        return (estatCombat().objecteVisor[0] == 0
                && estatCombat().distanciaVisors[0] <= dist)
                || (estatCombat().objecteVisor[1] == 0
                && estatCombat().distanciaVisors[1] <= dist)
                || (estatCombat().objecteVisor[2] == 0
                && estatCombat().distanciaVisors[2] <= dist);
    }

    double minimaDistanciaVisors() {
        double minim = Double.POSITIVE_INFINITY;
        if (estatCombat().objecteVisor[0] == 0) {
            minim = estatCombat().distanciaVisors[0];
        }
        if (estatCombat().objecteVisor[1] == 0 && estatCombat().distanciaVisors[1] < minim) {
            minim = estatCombat().distanciaVisors[1];
        }
        if (estatCombat().objecteVisor[2] == 0 && estatCombat().distanciaVisors[2] < minim) {
            minim = estatCombat().distanciaVisors[2];
        }
        return minim;
    }
}
