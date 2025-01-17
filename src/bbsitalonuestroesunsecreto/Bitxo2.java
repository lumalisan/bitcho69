package agents;

// Exemple de Bitxo
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Bitxo2 extends Agent {

    static final boolean DEBUG = false;

    static final int PARET = 0;
    static final int NAU = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    static final int STD = 5;       // Std. velocitat
    static final int STDD = 150;    // Std. distancia visors
    static final int MAXA = 9;      // Max. velocitat angular
    static final int MAXL = 6;      // Max. velocitat lineal
    static final int MAXD = 400;    // Max. distancia visors
    static final int MAXV = 5;

    Estat estat;

    // Esperes
    int espera = 0;
    int esperaMenjar = 0;

    // Condicions
    boolean bloquejat = false;
    boolean veiaEnemic = false;
    boolean escut = false;
    int impactesAnteriors = 0;
    int psensor = 0;
    boolean enCombat = false;
    int situacio5 = 0;
    int pnbonificacions = 10;

    // Cerca recursos
    Point direccio = new Point(0, 0),
            pdireccio = new Point(0, 0),
            pposicio = new Point(0, 0);
    int objectiu = 0;

    // Comptadors
    int contra7 = 0;
    int colisionat = 6;

    long temps;
    Timer t;

    public Bitxo2(Agents pare) {
        super(pare, "PPEP", "imatges/moyake25.gif");
        t = new Timer(3500, new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (direccio.x == pdireccio.x && direccio.y == pdireccio.y) {
                    double dist = pposicio.distance(estat.posicio.x, estat.posicio.y);
                    if (dist < 50) {
                        objectiu = (objectiu + 1) % estat.nbonificacions;
                        cercaMenjar();
                    }
                }
                if (!bloquejat) {
                    cercaMenjar();
                }
                pdireccio.x = direccio.x;
                pdireccio.y = direccio.y;
                pposicio.x = estat.posicio.x;
                pposicio.y = estat.posicio.y;
            }
        });
        t.setInitialDelay(0);
    }

    @Override
    public void inicia() {
        // Inicialitzacions estàndar
        posaAngleVisors(30);
        posaDistanciaVisors(STDD);
        posaVelocitatLineal(STD);
        posaVelocitatAngular(STD);
        espera = 0;
        temps = 0;

        // Inicialitzacions variables de control
        esperaMenjar = 0;
        veiaEnemic = false;
        direccio = new Point(0, 0);
        objectiu = 0;
        contra7 = 3;
        colisionat = 6;
        enCombat = false;
        situacio5 = 0;
    }

    @Override
    public void avaluaComportament() {
        boolean enemic;
        enemic = false;

        temps++;
        estat = estatCombat();

        // Direcció inicial del bitxo
        if (noTencRecursAssignat()) {
            cercaMenjar();
        }

        if (hePerdutVida()) {
            escut = false;
        }

        if (!enCombat && hePerdutVida()) {
            hyperespai();
        }

        if (espera > 0) {
            espera--;
        } else {
            if (!enCombat) {
                atura();
            }

            int sensor = usaSensors(45);

            if (estat.enCollisio) { // situació de nau bloquejada
                if (sensor == 5) {
                    if (enCombat) {
                        dispara();
                    }
                    enrere();
                    situacio5 = 2;
                    espera = 1;
                } else if (enCombat) {
                    dispara();
                    atura();
                } else {
                    // si veu la nau, dispara
                    if (estat.objecteVisor[CENTRAL] == NAU && estat.impactesRival[0] < 5) {
                        dispara();   //bloqueig per nau, no giris dispara
                    } else // hi ha un obstacle, gira i parteix
                    {
                        // Control de colisions contínues
                        colisionat--;
                        if (colisionat == 3) {
                            // Mira en l'eix vertical cap on estigui el recurs assignat
                            int aux = 0;
                            aux = (direccio.y <= estat.posicio.y) ? (-50) : (50);
                            mira(estat.recurs[0]);
                            endavant();
                        } else if (colisionat == 0) {
                            // Mira en l'eix horitzontal cap on estigui el recurs assignat
                            int aux = 0;
                            aux = (direccio.x <= estat.posicio.x) ? (-50) : (50);
                            mira(estat.recurs[0]);
                            endavant();
                        } else {
                            // Sortida de colisions
                            if (hiHaParedDavant(20)) {
                                enrere();
                            } else {
                                posaVelocitatAngular(MAXA);
                                if (estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]) {
                                    esquerra();
                                } else {
                                    dreta();
                                }
                            }
                        }

                        espera = 3;
                    }
                }

            } else {
                if (situacio5 > 0 && !enCombat) {
                    situacio5();
                } else {
                    sortidaControlColisionsContinues();

                    if (!enCombat) {
                        endavant();
                    }

                    if (estat.veigEnemic[0]) {
                        veiaEnemic = true;
                        this.posaDistanciaVisors(MAXD);
                        this.posaVelocitatAngular(MAXA);

                        if (estat.sector[0] == 2 || estat.sector[0] == 3) {
                            mira(estat.enemic[0]);
                            this.posaVelocitatLineal(MAXL);
                        } else if (estat.sector[0] == 1) {
                            dreta();
                        } else {
                            esquerra();
                        }

                    } else {
                        if (veiaEnemic) {
                            enCombat = false;
                            veiaEnemic = false;
                            // Recalcula posició dels recursos més propers
                            objectiu = 0;
                            cercaMenjar();
                            this.posaVelocitatAngular(STD);
                            this.posaVelocitatLineal(STD);
                            this.posaDistanciaVisors(STDD);
                        }
                    }

                    if (estat.objecteVisor[CENTRAL] == NAU) {
                        enemic = true;
                        enCombat = true;
                    }

                    if (enemic && !estat.disparant && estat.impactesRival[0] < 5) {
                        enrere();
                        dispara();
                    }

                    if (estat.impactesRebuts == 4 && !escut && estat.balaEnemigaDetectada) {
                        dispara();
                        hyperespai();
                    }

                    // Si no veim l'enemic anam a cercar menjar
                    if (!estat.veigEnemic[0] && !bloquejat && !estat.enCollisio) {
                        if (heTrobatRecurs()) {
                            escut = true;
                            objectiu = 0;
                            cercaMenjar();
                        }

                        // L'enemic ha consumit un recurs que potser era al qual anava
                        if (!heTrobatRecurs() && haDesaparescutRecurs()) {
                            objectiu = 0;
                            cercaMenjar();
                        }

                        if (esperaMenjar > 0) {
                            esperaMenjar--;
                        } else {
                            mira(estat.enemic[0]);
                            esperaMenjar = 4;
                        }
                    }

                    // Miram els visors per detectar els obstacles
                    if (!enCombat) {
                        switch (sensor) {
                            // 0 0 0
                            case 0:
                                bloquejat = false;
                                if (psensor == 4) {
                                    esquerra();
                                } else if (psensor == 1) {
                                    dreta();
                                }
                                endavant();
                                break;
                            // 0 0 1
                            case 1:
                                esperaMenjar += 1;
                                esquerra();
                                break;
                            // 0 1 1
                            case 3:  // dreta bloquejada
                                bloquejat = true;
                                esquerra();
                                break;
                            // 1 0 0
                            case 4:
                                esperaMenjar += 1;
                                dreta();
                                break;
                            // 1 1 0
                            case 6:  // esquerra bloquejada
                                bloquejat = true;
                                dreta();
                                break;
                            // 1 0 1
                            case 5:
                                endavant();
                                break;  // centre lliure
                            // 0 1 0
                            case 2:  // paret devant
                            // 1 1 1
                            case 7:  // si estic molt aprop, torna enrere
                                bloquejat = true;
                                double distancia;
                                distancia = minimaDistanciaVisors();

                                if (visorsLateralsMesCurts()) {
                                    objectiu = (objectiu + 1) % estat.nbonificacions;
                                    cercaMenjar();
                                } else if (visorsLateralsLliures()) {
                                    esquerra();
                                } else {
                                    int aux = 0;
                                    if (estat.angle <= 45 && estat.angle >= 315
                                            || estat.angle >= 135 && estat.angle <= 225) {
                                        if (Math.random() * 500 < 250) {
                                            aux = 100;
                                        } else {
                                            aux = -100;
                                        }
                                        direccio.x += aux;
                                    } else if (estat.angle >= 45 && estat.angle <= 135
                                            || estat.angle >= 225 && estat.angle <= 315) {
                                        if (Math.random() * 500 < 250) {
                                            aux = 100;
                                        } else {
                                            aux = -100;
                                        }
                                        direccio.y += aux;
                                    }
                                }

                                if (distancia < 15) {
                                    espera = 8;
                                    enrere();
                                } else // gira aleatòriament a la dreta o a l'esquerra
                                if (distancia < 50) {
                                    if (Math.random() * 500 < 250) {
                                        dreta();
                                    } else {
                                        esquerra();
                                    }
                                }
                                if (estat.nbonificacions == 1) {
                                    esperaMenjar = 20;
                                }
                                break;
                        }
                    }
                }
            }
            psensor = sensor;
        }

        pnbonificacions = estat.nbonificacions;
        impactesAnteriors = estat.impactesRebuts;

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

    void cercaMenjar() {
        mira(estat.recurs[0]);
        t.start();
    }

    boolean noTencRecursAssignat() {
        return (direccio.x == 0 && direccio.y == 0);
    }

    boolean hePerdutVida() {
        return impactesAnteriors < estat.impactesRebuts;
    }

    void sortidaControlColisionsContinues() {
        // Recerca menjar si surts d'una colisió
        if (colisionat <= 3) {
            objectiu = 0;
            cercaMenjar();
        }
        // Reinici del comptador de colisions contínues
        colisionat = 6;
    }

    boolean heTrobatRecurs() {
        return (estat.posicio.x >= direccio.x - 10
                && estat.posicio.x <= direccio.x + 10
                && estat.posicio.y >= direccio.y - 10
                && estat.posicio.y <= direccio.y + 10);
    }

    int usaSensors(int dist) {
        int sensor = 0;

        if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < dist) {
            sensor += 1;
        }
        if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < dist) {
            sensor += 2;
        }
        if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < dist) {
            sensor += 4;
        }

        return sensor;
    }

    boolean visorsLateralsMesCurts() {
        return (estat.distanciaVisors[DRETA] < estat.distanciaVisors[CENTRAL]
                && estat.distanciaVisors[ESQUERRA] < estat.distanciaVisors[CENTRAL]);
    }

    boolean dreta = false;

    void situacio5() {
        if (situacio5 == 2) {
            if (estat.distanciaVisors[DRETA] < estat.distanciaVisors[ESQUERRA]) {
                gira(10);
                dreta = false;
            } else {
                gira(-10);
                dreta = true;
            }
            endavant();
            situacio5 = 1;
            espera = 1;
        } else if (situacio5 == 1) {
            if (dreta) {
                gira(10);
            } else {
                gira(-10);
            }
            endavant();
            situacio5 = 0;
        }
    }

    boolean haDesaparescutRecurs() {
        return pnbonificacions > estat.nbonificacions;
    }

    boolean visorsLateralsLliures() {
        return (estat.distanciaVisors[DRETA] > 90
                && estat.distanciaVisors[ESQUERRA] > 90);
    }
}
