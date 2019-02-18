package com.example.jlc_r.carrofinal;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.rule.Variable;


import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.UUID;

import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;




public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;

    /**
     * variables para la conexion del bluetooth
     */
    //Identificador de servicio
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;

    //Si se apreta una vez el boton de conectar
    boolean estado = false;

    //Handler es un control para mensajes
    Handler bluetoothIn;

    //Estado del manejador
    final int handlerState = 0;

    //Esto es simplemente un String normal a diferencia que al agregar una sentancia en un bucle se agrega los espacios automaticamente
//for(hasta 20 veces)
//String cadena += " " + "Dato" ---> En un string normal se debe crear el espacio y luego agregar el dato
//Con esto se traduce a = DataStringIN.append(dato);
    private StringBuilder DataStringIN = new StringBuilder();

    //Llama a la sub- clase y llamara los metodos que se encuentran dentro de esta clase
    ConexionThread MyConexionBT;

    DecimalFormat formato1 = new DecimalFormat("#.00");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bfuzzi= findViewById(R.id.btn_fuzzy);
        bfuzzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuzific();
            }
        });




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////Manejador de mensajes y llamara al metodo Run///////////////////////////////
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    //Toast.makeText(MainActivity.this, "Dato Recibido Entero: " + readMessage, Toast.LENGTH_SHORT).show();

                    String[] parts = readMessage.split(";");
                    //Toast.makeText(MainActivity.this,"Tamaño:="+ parts.length, Toast.LENGTH_SHORT).show();
                    Log.d("Total: " , String.valueOf(parts.length));
                    if (parts.length==2){
                        String derecha = parts[0];
                        String[] derpartes=derecha.split("=");
                        String derechafinal = derpartes[1];
                        Log.d("Derecha: " , derechafinal);
                        //cortar la primera

                        String izquierda = parts[1];
                        String[] izquierdapartes=izquierda.split("=");
                        String izquierdafinal = izquierdapartes[1];

                        Log.d("Izquierda: " , izquierdafinal);

                        if (derecha.length() !=0  && izquierda.length() != 0){
                            MyConexionBT.defuzenvioBT(izquierdafinal,derechafinal);


                        }
                        Log.d("________Fin__________" , "---------------------------");
                        parts = null;
                    }else {
                        Toast.makeText(MainActivity.this,"No tiene dos valores", Toast.LENGTH_SHORT).show();

                    }

                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        //   Toast.makeText(MainActivity.this, "Dato Recibido: " +dataInPrint, Toast.LENGTH_SHORT).show();
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }

        };
        ///////////////////////////////////////////////////


        //BOTON ENVIAR
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.editText);
                if (estado) {
                    String dato = editText.getText().toString();
                    dato += "#";
                    //String datof = MyConexionBT.defuzi();
                    MyConexionBT.write(dato);
                    Toast.makeText(MainActivity.this, "Dato Enviado: " + dato, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Solo se puede enviar datos si el dispositivo esta vinculado", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //BOTON CONECTAR
        Button btnConectar = findViewById(R.id.btnConectar);
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btAdapter = BluetoothAdapter.getDefaultAdapter();

                //Direccion mac del dispositivo a conectar
                BluetoothDevice device = btAdapter.getRemoteDevice("98:D3:31:F5:6B:26");

                try {
                    //Crea el socket sino esta conectado
                    if (!estado) {
                        btSocket = createBluetoothSocket(device);

                        estado = btSocket.isConnected();
                    }

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
                }

                // Establece la conexión con el socket Bluetooth.
                try {
                    //Realiza la conexion si no se a hecho
                    if (!estado) {
                        btSocket.connect();
                        estado = true;
                        MyConexionBT = new ConexionThread(btSocket);
                        MyConexionBT.start();
                        Toast.makeText(MainActivity.this, "Conexion Realizada Exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Ya esta vinculado", Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                    try {
                        Toast.makeText(MainActivity.this, "Error:", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        btSocket.close();
                    } catch (IOException e2) {
                    }
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Creacion de los metodos de difucificar
     *
     *
     */

    public void fuzific()  {
        InputStream in = null;
        try {
            in = getApplicationContext().getAssets().open("datos.fcl");
        }catch (IOException ef){
            ef.getLocalizedMessage();
            ef.getCause();
        }
        try {
            FIS fis = FIS.load(in, true);
            fis.setVariable("izquierda", 0);
            fis.setVariable("derecha", 60);
            fis.evaluate();
            Variable v = fis.getVariable("angulo");
            System.out.print("valor: "+ v.getValue());
            //Toast.makeText(getBaseContext(),"Centrode gravedad:  "+v.getValue(), Toast.LENGTH_LONG).show();
            Log.d("________Ini__________" , "---------------------------" );

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
            e.getLocalizedMessage();
        }
    }

    /**
     * Crear metodos para conectar y tambien enviar datos;
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }


    //Se debe crear una sub-clase para tambien heredar los metodos de CompaActivity y Thread juntos
//Ademas  en Run se debe ejecutar el subproceso(interrupcion)
    private class ConexionThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConexionThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                // Se mantiene en modo escucha para determinar el ingreso de datos
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler


                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Enviar los datos
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        public String defuzenvioBT(String izq , String der ){
            Double izqui = Double.valueOf(izq);
            Double dere = Double.valueOf(der);
            String valor ="";
            InputStream in = null;
            try {
                in = getApplicationContext().getAssets().open("datos.fcl");
            }catch (IOException ef){
                ef.getLocalizedMessage();
                ef.getCause();
            }
            try {
                FIS fis = FIS.load(in, true);
                fis.setVariable("izquierda", izqui);
                fis.setVariable("derecha", dere);
                fis.evaluate();
                Variable v = fis.getVariable("angulo");
                System.out.print("valor: "+ v.getValue());
                Double valointer = Double.valueOf(formato1.format(v.getValue()));
                valor = String.valueOf(formato1.format(v.getValue()));

                    MyConexionBT.write(valor);
                    Log.d("________COG__________" , "---------------------------:  "+valor);

                //Toast.makeText(getBaseContext(),"Centrode gravedad:  "+v.getValue(), Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
                e.getCause();
                e.getLocalizedMessage();
            }

            return valor;

        }

    }
}
