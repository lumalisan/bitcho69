package agents;

//Bitcho69
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

    public Bitxo1(Agents pare) {
        super(pare, "Bitcho69", "imatges/moyake25.gif");
    }

    @Override
    public void inicia() {
        posaAngleVisors(45);
        posaDistanciaVisors(250);
        posaVelocitatLineal(3);
        posaVelocitatAngular(9);
        espera = 0;
        temps = 0;
    }

    void modoDiablo() {  //Estado terminator cuando ve enemigo
        posaAngleVisors(30);
        posaDistanciaVisors(300);
        posaVelocitatLineal(6);
        //if (!estat.disparant ) {
            if (estat.objecteVisor[CENTRAL]== NAU && estat.distanciaVisor<40) {
                dispara();
            //}
            
        }
    }

    void modoPusi() { //Estado pasivo inicial
        posaAngleVisors(45);
        posaDistanciaVisors(250);
        posaVelocitatLineal(3);
    }

    void getTheFuckAwayOfMyMangoDisecadoMan() { //Huye en el caso que no queden balas
        gira(180);
        posaVelocitatLineal(6);
        espera = 3;
    }

    void rawNoodles() { //Esquivar minas
        gira(360);
        gira(360);
        gira(360);
        gira(360);
    }

    void veganFood() { //Coger potenciadores verdes
        if (estat.fuel > 5000) {
            posaVelocitatLineal(6);
        }
    }

    @Override
    public void avaluaComportament() {
//        boolean enemic;
//
//        enemic = false;

//        int dir;
        temps++;
        estat = estatCombat();
        if (espera > 0) {
            espera--;
        } else {
            atura();
            
            if (estat.numEnemics > 0) {
                mira(estat.enemic[0]);
                
                
               
                
            }

            if (estat.enCollisio) { // situació de nau bloquejada
                // si veu la nau, dispara
                if (estat.objecteVisor[CENTRAL] == NAU) {
                    dispara();
                } else { // hi ha un obstacle, gira i parteix
                    gira(20); // 20 graus
                    if (hiHaParedDavant(20)) {
                        enrere();
                    } else {
                        endavant();
                    }
                    espera = 3;
                }
            } else {
                endavant();

                if (estat.objecteVisor[CENTRAL] == NAU && estat.bales != 0) {
//                    enemic = true;
                    if (estat.bales == 0) {
                        getTheFuckAwayOfMyMangoDisecadoMan();
                    } else {
                        modoDiablo();
                    }
                } else if (estat.objecteVisor[ESQUERRA] == NAU || estat.objecteVisor[DRETA] == NAU) {
                    mira(estat.enemic[0]);
                    modoDiablo();
                } else {
//                    enemic = false;
                    modoPusi();
                }

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
        double minim;

        minim = Double.POSITIVE_INFINITY;
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
