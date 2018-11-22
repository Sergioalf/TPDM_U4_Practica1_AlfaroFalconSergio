package com.prueba.oansc.tpdm_u4_practica1_sergioalfarofalcn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView ronda, jugador, ganador;                       //Etiquetas principales de la aplicación
    TextView[] etiquetas, totales;                          //Etiquetas de tiros y totales
    ImageView[] dados;                                      //Imageviews para los 6 dados

    TextView etiquetaActual, totalActual;                   //Variables punteros de control para los arreglos de etiquetas
    ImageView[] dadosActuales;                              //Variable puntero para los dados
    int numeroActual;                                       //Variable que guarda el número a asignar en cada tiro

    Button iniciar;                                         //Boton inicio

    Thread padre, hijo;                                     //Hilos, uno para hacer los cálculos y asignaciones y otro para asignar los valores

    int[] numeros;                                          //Aquí se guardan los números generados en el programa
    int[] total;                                            //Aquí se guardan los resultados totales de cada jugador

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ronda = findViewById(R.id.ronda);
        jugador = findViewById(R.id.jugador);
        ganador = findViewById(R.id.resultado);

        etiquetas = new TextView[12];        //Asignación de las etiquetas de tiro en el orden en que se llenan

        etiquetas[0] = findViewById(R.id.j1t1);
        etiquetas[1] = findViewById(R.id.j2t1);             //Primero el tiro uno de los tres jugadores
        etiquetas[2] = findViewById(R.id.j3t1);

        etiquetas[3] = findViewById(R.id.j1t2);
        etiquetas[4] = findViewById(R.id.j2t2);             //Segundo tiro de los tres jugadores
        etiquetas[5] = findViewById(R.id.j3t2);

        etiquetas[6] = findViewById(R.id.j1t3);
        etiquetas[7] = findViewById(R.id.j2t3);             //Tercer tiro de los tres jugadores
        etiquetas[8] = findViewById(R.id.j3t3);

        etiquetas[9] = findViewById(R.id.j1t4);
        etiquetas[10] = findViewById(R.id.j2t4);            //Cuarto tiro de los tres jugadores
        etiquetas[11] = findViewById(R.id.j3t4);

        totales = new TextView[3];              //Asignación de las etiquetas de totales

        totales[0] = findViewById(R.id.j1to);
        totales[1] = findViewById(R.id.j2to);
        totales[2] = findViewById(R.id.j3to);

        dados = new ImageView[6];               //Asignación de los dados de cada jugador en el orden en que se usarán

        dados[0] = findViewById(R.id.j1d1);     //Jugador 1
        dados[1] = findViewById(R.id.j1d2);

        dados[2] = findViewById(R.id.j2d1);     //Jugador 2
        dados[3] = findViewById(R.id.j2d2);

        dados[4] = findViewById(R.id.j3d1);     //Jugador 3
        dados[5] = findViewById(R.id.j3d2);

        dadosActuales = new ImageView[2];    //Asignación de dos espacios al puntero de dados, por que se usan 2 a la vez

        iniciar = findViewById(R.id.iniciar);   //Asignación del botón

        numeros = new int[12];              //Inicialización del total de números a generar

        total = new int[3];                 //Inicialización del los 3 totales a guardar

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarTodo();            //Limpia los campos para una nueva partida
                inicilizarPadre();          //Asigna el código del hilo padre
                padre.start();              //Inicia el hilo padre
            }
        });
    }

    private void inicilizarPadre() {
        padre = new Thread() {
            public void run() {
                for (int i = 0; i<numeros.length; i++) {
                    numeros[i] =  (int)(Math.random() * (13 - 2) + 2); //Genera los 12 números entre 2 y 12
                }
                for (int i = 0, j = 0, p = 1; i < 12; i++, j+=2, p++){ //Hace que se ejecute 12 veces el hilo hijo
                    if (j == 6) {
                        j = 0;      //Indica el número de dados a utilizar (j y j+1)
                    }
                    if (p == 4) {
                        p = 1;      //Indica el jugador que está en turno
                    }
                    inicializarHijo((i/3)+1, p); //Asigan el código al hilo hijo
                    numeroActual = numeros[i];          //Asigna el número para el turno actual
                    dadosActuales[0] = dados[j];
                    dadosActuales[1] = dados[j+1];      //Asigna los dados que lo van a mostrar
                    etiquetaActual = etiquetas[i];      //Asigna la etiqueta del tiro actual
                    totalActual = totales[p-1];         //Asigna el total a mostrar
                    hijo.start();                       //Inicia elhilo hijo
                    try {
                        sleep(4000);               //Espera 4 segundos a que se ejecute el hilo hijo,
                    } catch (InterruptedException e) {  //para que sea visible para el usuario cada uno de ellos
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ronda.setText("0");             //Cuando se asignan los 12 valores y se conoce el ganador
                        jugador.setText("0");           //Solo se ponen a cero la ronda y el jugador actual
                    }
                });
                anunciarGanador();                      //Indica en pantalla quien(es) fue el ganador
            }
        };
    }

    private void inicializarHijo(final int nRonda, final int nJugador){
        hijo = new Thread(){
            public void run () {
                switch (nJugador) {
                    case 1:
                        total[0] += numeroActual;
                        break;
                    case 2:
                        total[1] += numeroActual;     //Suma el número asignado al total del jugador correspondiente
                        break;
                    case 3:
                        total[2] += numeroActual;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jugador.setText(nJugador+"");       //Escribe el número de jugador y ronda de ese momento
                        ronda.setText(nRonda+"");
                    }
                });
                try {
                    sleep(1000);                        //Espera un segundo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        establecerDados();                   //Cambia los dados al número actual
                    }
                });
                try {
                    sleep(2000);                        //Espera dos segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dadosActuales[0].setImageResource(R.drawable.dado0);   //Borra los dados
                        dadosActuales[1].setImageResource(R.drawable.dado0);
                        etiquetaActual.setText("Tiro "+ nRonda + ": "+ numeroActual);   //Escribe el resultado del tiro
                        totalActual.setText("Total: " + total[nJugador-1]+"");  //Escribe el total
                    }
                });
            }
        };
    }

    private void establecerDados () {
        switch (numeroActual) {     //En base al número recibido, selecciona la combinación de dados
            case 2:
                dadosActuales[0].setImageResource(R.drawable.dado1);
                dadosActuales[1].setImageResource(R.drawable.dado1);
                break;

            case 3:
                dadosActuales[0].setImageResource(R.drawable.dado2);
                dadosActuales[1].setImageResource(R.drawable.dado1);
                break;

            case 4:
                dadosActuales[0].setImageResource(R.drawable.dado2);
                dadosActuales[1].setImageResource(R.drawable.dado2);
                break;

            case 5:
                dadosActuales[0].setImageResource(R.drawable.dado3);
                dadosActuales[1].setImageResource(R.drawable.dado2);
                break;

            case 6:
                dadosActuales[0].setImageResource(R.drawable.dado3);
                dadosActuales[1].setImageResource(R.drawable.dado3);
                break;

            case 7:
                dadosActuales[0].setImageResource(R.drawable.dado4);
                dadosActuales[1].setImageResource(R.drawable.dado3);
                break;

            case 8:
                dadosActuales[0].setImageResource(R.drawable.dado4);
                dadosActuales[1].setImageResource(R.drawable.dado4);
                break;

            case 9:
                dadosActuales[0].setImageResource(R.drawable.dado5);
                dadosActuales[1].setImageResource(R.drawable.dado4);
                break;

            case 10:
                dadosActuales[0].setImageResource(R.drawable.dado5);
                dadosActuales[1].setImageResource(R.drawable.dado5);
                break;

            case 11:
                dadosActuales[0].setImageResource(R.drawable.dado6);
                dadosActuales[1].setImageResource(R.drawable.dado5);
                break;

            case 12:
                dadosActuales[0].setImageResource(R.drawable.dado6);
                dadosActuales[1].setImageResource(R.drawable.dado6);
        }
    }

    private void anunciarGanador () {  //En base a los totales determina quien(es) es el ganador
        if (total[0] > total[1]) {
            if (total[0] > total[2]) {
                ganador.setText("Ganador: J1");
            } else {
                if (total[0] == total[2]) {
                    ganador.setText("Ganador: J1 y J3");
                } else {
                    ganador.setText("Ganador: J3");
                }
            }
        } else {
            if (total[0] == total[1]) {
                if (total[0] > total[2]) {
                    ganador.setText("Ganador: J1 y J2");
                } else {
                    if (total[0] == total[2]){
                        ganador.setText("Ganador: J1, J2 y J3");
                    } else {
                        ganador.setText("Ganador: J3");
                    }
                }
            } else {
                if (total[1] > total[2]) {
                    ganador.setText("Ganador: J2");
                } else {
                    if (total[1] == total[2]) {
                        ganador.setText("Ganador: J2 y J3");
                    } else {
                        ganador.setText("Ganador: J3");
                    }
                }
            }
        }

    }

    private void reiniciarTodo() {                  //Limpia los totales, los tiros y el ganador
        for (int i = 0; i < total.length; i++) {
            total[i] = 0;
        }
        for (int i = 0; i < etiquetas.length; i++) {
            etiquetas[i].setText(etiquetas[i].getText().toString().substring(0,7));
        }
        for (int i = 0; i < totales.length; i++) {
            totales[i].setText("Total: ");
        }
        ganador.setText("Ganador:   ");
    }

}
