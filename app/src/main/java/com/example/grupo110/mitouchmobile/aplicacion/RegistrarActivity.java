package com.example.grupo110.mitouchmobile.aplicacion;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.DeleteFileDrive;
import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.envioEmail.EmailIdentifierGenerator;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static java.lang.Integer.parseInt;

public class RegistrarActivity extends AppCompatActivity {


    /************ Variables Expandable List*********************/
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    List<String> grupodeUsuarioid;
    /************ FIN Variables Expandable List*********************/


    /************ Variables validacion largo usuario y contraseña*********************/
    private final static int  LARGO_CONTRASEÑA = 6;
    private final static int  USUARIOMINIMO = 6;
    private final static int  USUARIOMAXIMO = 15;
	
	private final static int  NOMBRECOMPLETOMINIMO = 5;
    private final static int  NOMBRECOMPLETOMAXIMO = 51;

    private boolean nombreValido = false;
    private boolean contraseñaValida = false;
    private boolean contraseñaRepiteValida = false;
    private boolean nombreCompletoValido = false;
    private boolean emailValido = false;
    /************ FIN Variables validacion largo usuario y contraseña*********************/


    String grupoUsuario=null;
    final String PATH_SERVIDOR="queres smoke tomaaaaaaaaaaaa!";

    private boolean codigoValido = false;
    String usuario= "";
    String NombreCompletoString="usuario";


    /************ Variables Envio del mail *********************/
    Session session = null;
    ProgressDialog pdialog = null;

    // Credenciales de usuario
    private static String direccionCorreo = "grupo110unlam@gmail.com";   // Dirección de correo origen
    private static String contrasenyaCorreo = "mitouch110";                 // Contraseña del correo electronico origen

    private static String destintatarioCorreo="as"; // Dirección de correo destino
    String subject= "Tu cuenta de MiTouch: verificación de la dirección de email"; // Asunto del mail
    private String random;
    /************ FIN Variables Envio del mail *********************/

    /***************** Variables Layout ***************************/
    private EditText nombreUsuario;
    private EditText nombreCompleto;
    private EditText direccionEmail;
    private EditText contraseña;
    private EditText repetirContraseña;
    private EditText codigoDeVerificacion;
    private Button botonRegistrar;
    /***************** FIN Variables Layout ***************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(RegistrarActivity.this, LoginActivity.class);
                startActivity(siguiente);
                finish();

            }
        });
        /*
        * Asociar las variables del layout con las variables creadas en esta clase
        */
        nombreUsuario = (EditText) findViewById(R.id.nombreUsuarioRegistrar);
        nombreCompleto = (EditText) findViewById(R.id.nombreCompletoRegistrar);
        direccionEmail = (EditText) findViewById(R.id.emailgmailRegistrar);
        contraseña = (EditText) findViewById(R.id.passwordRegistrar);
        repetirContraseña = (EditText) findViewById(R.id.repitepasswordRegistrar);
        codigoDeVerificacion = (EditText) findViewById(R.id.CodigoVerificacion_Registro);
        botonRegistrar = (Button) findViewById(R.id.buttonregistrarUsuario);

        /*
        * Cuando apreto sobre nombre usuario:
            * Valida que el nombre de usuario no exista en la BD.
                  * si existe en la bd pone una X
                  * Si no existe en la bd pone una tilde
         */
        nombreUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    usuario = nombreUsuario.getText().toString();
                    boolean respuesta = validarUsuario(usuario);
                    insertarImagenNombreUsuario(respuesta);
                }
            }
        });

        nombreCompleto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    NombreCompletoString = nombreCompleto.getText().toString();
                    if(validarNombreCompleto(NombreCompletoString))
                        insertarImagenNombreCompleto(true);
                    else
                        insertarImagenNombreCompleto(false);
                }
            }
        });
        /*
        * Cuando apreto Direccion del email:
            * Valida que el nombre sea valido.
            * Busca que el mail no este en la bd
                * Si existe en la BD pone X
                * Si no existe en la BD envia un mail, en el cual le va a llegar un mail
         */
        direccionEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(!destintatarioCorreo.equals(direccionEmail.getText().toString()))
                        if(!nombreCompleto.getText().toString().equals(""))
                            if(validarEmail(direccionEmail.getText().toString()))
                                if(!buscarEmail(direccionEmail.getText().toString())) {
                                    codigoValido=false;
                                    destintatarioCorreo =direccionEmail.getText().toString();
                                    EmailIdentifierGenerator randomGenerator = new EmailIdentifierGenerator();
                                    random = randomGenerator.nextSessionId();
                                    System.out.println("El numero random es:"+random);
                                    enviarEmail();
                                }
                                else{
                                    insertarImagenEmail(false);
                                    Toast toast = Toast.makeText(getApplicationContext(),"Direccion de correo ya esta registrada",Toast.LENGTH_LONG);
                                    toast.show();

                                }
                            else {
                                insertarImagenEmail(false);
                                Toast toast = Toast.makeText(getApplicationContext(),"Direccion de correo es invalida",Toast.LENGTH_LONG);
                                toast.show();
                                direccionEmail.clearFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(direccionEmail.getWindowToken(), 0);
                            }
                        else
                        {
                            Toast toast = Toast.makeText(getApplicationContext(),"Ingrese nombre completo ",Toast.LENGTH_LONG);
                            toast.show();
                            direccionEmail.setText("");
                            codigoDeVerificacion.setText("");
                            codigoValido = false;
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(direccionEmail.getWindowToken(), 0);
                            direccionEmail.clearFocus();
                        }

                }
                if (hasFocus) {
                    codigoDeVerificacion.setText("");
                }

            }
        });


        codigoDeVerificacion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    if (!hasFocus) {
                        if (random.equals(codigoDeVerificacion.getText().toString())) {
                            Toast toast = Toast.makeText(getApplicationContext(), "El codigo de verificacion es correcto", Toast.LENGTH_LONG);
                            toast.show();

                            codigoValido = true;
                            insertarImagenEmail(true);

                        } else {
                            insertarImagenEmail(false);
                            Toast toast = Toast.makeText(getApplicationContext(), "El codigo de verificacion es incorrecto", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }catch (Exception e){System.out.println("Error codigo verificacion: " + e);}
            }
        });



        contraseña.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    boolean respuesta = validarContraseña(contraseña.getText().toString());
                    insertarImagenContraseña(respuesta);
                }

            }
        });

        repetirContraseña.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(repetirContraseña.getText().toString().equals(contraseña.getText().toString()) && validarContraseña(repetirContraseña.getText().toString()))
                        insertarImagenRepiteContraseña(true);
                    else
                        insertarImagenRepiteContraseña(false);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(repetirContraseña.getWindowToken(), 0);
                }
            }
        });

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * Voy a perder los focos de todos los editText
                 */
                nombreUsuario.clearFocus();
                nombreCompleto.clearFocus();
                direccionEmail.clearFocus();
                codigoDeVerificacion.clearFocus();
                contraseña.clearFocus();
                repetirContraseña.clearFocus();
                if((nombreValido) && (contraseñaRepiteValida) && (nombreCompletoValido)&& (contraseñaValida) && (emailValido) && codigoValido) {
                    CrearUsuario();
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Usuario Registrado",Toast.LENGTH_LONG);
                    toast2.show();
                    Intent siguiente = new Intent(RegistrarActivity.this, LoginActivity.class);
                    startActivity(siguiente);
                    finish();
                }
                else{
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Datos no validos",Toast.LENGTH_LONG);
                    toast2.show();
                }
            }
        });

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                grupoUsuario = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
                listDataHeader.clear();
                listDataHeader.add( grupoUsuario);
                grupodeUsuario = new ArrayList<>();
                BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
                expListView.collapseGroup(groupPosition);
                return false;
            }
        });
    }

    private void insertarImagenNombreCompleto(boolean respuesta) {
        ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreCompletoRegistro);

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            nombreCompletoValido = false;
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            nombreCompletoValido = true;
        }
    }

    private void enviarEmail() {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(direccionCorreo, contrasenyaCorreo);
            }
        });
        iniciarpdialog();
    }
    /*
     * Preparing the list data
     */

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        // Adding child data
        listDataHeader.add("Grupo de Usuario");
        // Adding child data
        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioid = new ArrayList<>();
        BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
    }


    private void BuscarGruposdeUsuario(List<String> grupodeUsuario,List<String> grupodeUsuarioid) {
        String comando;
        comando =  "SELECT * FROM  \"MiTouch\".t_grupos" + ";";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                grupodeUsuario.add(resultSet.getString("gru_nombre"));
                grupodeUsuarioid.add(resultSet.getArray("gru_id").toString());
            }
            listDataChild.put(listDataHeader.get(0), grupodeUsuario); // Header, Child data
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario: " + e );
        }

    }

    private void CrearUsuario(){
        String comando;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat diahora = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        SimpleDateFormat dia = new SimpleDateFormat("yyyyMMdd");
        String fechadia = dia.format(c.getTime());
        String fechadiahora = diahora.format(c.getTime());
        String id_usuadioGenerado=null;
        String id_carpetaGenerada=null;
        PostgrestBD baseDeDatos = new PostgrestBD();


        // Cuando creo un usuario primero voy a crear la carpeta de la galeria y del drive!
        comando = "INSERT INTO \"MiTouch\".t_carpetas_galeria ( cg_path, cg_fecha_baja) VALUES ('"+PATH_SERVIDOR+ nombreUsuario.getText().toString() +"',null) RETURNING cg_id;";
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()){
                id_carpetaGenerada=resultSet.getArray(1).toString();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        System.out.println("el id de la carpeta generada es: " + id_carpetaGenerada);

        comando = "INSERT INTO \"MiTouch\".t_usuarios ( usu_fecha_alta, usu_id_carpeta, usu_ultimo_log_in,usu_ultimo_log_out, usu_password, usu_nombre_usuario, usu_nombre_completo,usu_fecha_baja, usu_administrador, usu_mail,usu_id_galeria) VALUES ('"+ fechadia +"','id Drive', null ,null, '"+contraseña.getText().toString() +"', '"+nombreUsuario.getText().toString() +"', '"+nombreCompleto.getText().toString()+"',null, false, '"+direccionEmail.getText().toString()+"',"+parseInt(id_carpetaGenerada)+") RETURNING usu_id;";

        // Inserto usuario en la tabla usuarios
        // Busco el ID del usuario que genere para insertarlo en la tabla de solicitud de acceso!
        resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()){
                id_usuadioGenerado=resultSet.getArray(1).toString();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
        /*
        * Solicitud a grupo!
         */
        if(grupoUsuario!=null) {
            try {
                comando = "INSERT INTO \"MiTouch\".t_solicitud_acceso (sol_id_usuario,sol_id_grupo,sol_fecha_hora,sol_fecha_hora_respuesta,sol_estado) VALUES (" + id_usuadioGenerado + ",'" + BuscarGruposdeUsuarioenArray() + "','" + fechadiahora + "',null,0);";
                baseDeDatos.execute(comando);
            } catch (Exception e) {
                System.out.println("no selecciono grupo de usuario");
            }
        }

        final String id_user = id_usuadioGenerado;

        new AsyncTask<Void, Void, Void>(){
            protected Void doInBackground(Void... params) {

                if(id_user != null){
                    try {
                        GoogleAccountCredential account;

                        account = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE));

                        account.setSelectedAccountName(direccionCorreo);

                        Drive driveService =
                                new Drive.Builder(
                                        AndroidHttp.newCompatibleTransport(),
                                        JacksonFactory.getDefaultInstance(),
                                        account
                                ).setApplicationName("MiTouch").build();

                        File filemetadata = new File();

                        filemetadata.setName(id_user);
                        filemetadata.setMimeType("application/vnd.google-apps.folder");

                        File file = driveService.files().create(filemetadata)
                                .setFields("id").execute();

                        if (file != null){

                            String com;
                            PostgrestBD bdd = new PostgrestBD();

                            com = "UPDATE \"MiTouch\".t_usuarios " +
                            "SET usu_id_carpeta = " + "'" + file.getId() + "'" +
                            " WHERE usu_nombre_usuario = " + "'" + id_user + "';";

                            try{
                                bdd.execute(com);

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    }catch (UserRecoverableAuthIOException e) {
                        startActivityForResult(e.getIntent(), 2);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                return null;
            }

            protected void onPostExecute(Void param){

            }

        }.execute();

    }



    private String BuscarGruposdeUsuarioenArray() {
        return grupodeUsuarioid.get(grupodeUsuario.indexOf(grupoUsuario));
    }

    private boolean validarUsuario(String usuario) {
        Toast toast;
        if(USUARIOMINIMO>usuario.length())
        {
            toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser mayor que 5",Toast.LENGTH_LONG);
            toast.show();
            return true;
        }
        if(usuario.length()> USUARIOMAXIMO){
            toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser menor que 16",Toast.LENGTH_LONG);
            toast.show();
            return true;
        }

        for(int i = 0; i < usuario.length(); ++i) {
            char caracter = usuario.charAt(i);

            if(i == 0 && ! Character.isLetter(caracter))
            {
                toast = Toast.makeText(getApplicationContext(), "El primer caracter debe ser una letra", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }


            if(!Character.isLetterOrDigit(caracter) && caracter != '.') {
                toast = Toast.makeText(getApplicationContext(), "El nombre de usuario no puede tener simbolos ni letras", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        }

        String comando;
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario='" + usuario + "';";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next())
                return true;
        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return false;
    }

    private boolean validarNombreCompleto(String cadena) {
        boolean nombreVa=false;
        Toast toast;

        if(cadena.length()< NOMBRECOMPLETOMINIMO  ){
            toast = Toast.makeText(getApplicationContext(), "El nombre debe tener 6 caracteres minimamente", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(cadena.length() > NOMBRECOMPLETOMAXIMO){
            toast = Toast.makeText(getApplicationContext(), "El nombre debe tener menos de 50 caracteres", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);

            if(i==0 && !Character.isLetter(caracter))
            {
                toast = Toast.makeText(getApplicationContext(), "El nombre debe comenzar con una letra", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }

            if(!Character.isLetter(caracter) && !Character.isSpaceChar(caracter)) {
                toast = Toast.makeText(getApplicationContext(), "El nombre no puede tener simbolos ni numeros", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            if(Character.isLetter(caracter))
                nombreVa=true;

        }
        return nombreVa;
    }


    private void insertarImagenNombreUsuario(boolean respuesta) {

        if(respuesta == true)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreUsuarioRegistro);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreUsuarioRegistro);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            nombreValido = true;
        }
    }

    private void insertarImagenEmail(boolean respuesta) {

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewEmail);
            Drawable res = getResources().getDrawable(imageResource);
            assert imagen != null;
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewEmail);
            Drawable res = getResources().getDrawable(imageResource);
            assert imagen != null;
            imagen.setImageDrawable(res);
            emailValido = true;
        }
    }
    /*
     * Si la contraseña no es valida le pongo X
     * si la contraseña es valida le pongo tilde
     */
    private void insertarImagenContraseña(boolean respuesta) {

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            assert imagen != null;
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            assert imagen != null;
            imagen.setImageDrawable(res);
            contraseñaValida = true;
        }
    }

    private void insertarImagenRepiteContraseña(boolean respuesta) {

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreRepiteContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreRepiteContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            contraseñaRepiteValida = true;

        }
    }
    /*
       * Metodo para validar la contraseña que se ingresa
       * Valido que no contenga simbolos, que no tenga espacios y que tenga por lo menos una letra y por lo menos un numero,
       * Valido El largo de la contraseña
        */
    private boolean validarContraseña(String cadena) {
        boolean numero= false;
        boolean letra=false;
        Toast toast;
        if(cadena.length() < LARGO_CONTRASEÑA)
        {
            toast = Toast.makeText(getApplicationContext(), "La cantidad minima de caracteres es 6 ", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);
            if(!Character.isLetterOrDigit(caracter)) {
                toast = Toast.makeText(getApplicationContext(), "La contraseña no puede tener simbolos y contener espacios", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }else
            if(Character.isDigit(caracter))
                numero = true;
            else
                letra = true;
        }
        if(numero && letra)
            return true;
        toast = Toast.makeText(getApplicationContext(), "La contraseña debe tener por lo menos un digito y una letra", Toast.LENGTH_SHORT);
        toast.show();
        return false;
    }

    /*
    * Metodo que me valida que el email ingreso se de gmail
    * Llama a otro metodo que me valdia si el mail ingresado existe en la bd o no
    * Si el email es invalido retorna false
    * Si el email es valido retorna true
     */
    public boolean validarEmail(String email) {
        Toast toast;
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@gmail.com");

        Matcher mather = pattern.matcher(email);
        if (mather.find()) {
            return true;
        } else {
            System.out.println("");
            toast = Toast.makeText(getApplicationContext(), "El email ingresado es inválido.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

    }
/*
* Metodo que me devuelve:
*   True si el email existe
*   False si el mail no existe
 */
    private boolean buscarEmail(String email) {
        String comando;
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_mail='" + email + "';";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()){
                Toast toast = Toast.makeText(getApplicationContext(), "El email ya existe.", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return false;
    }


    private void iniciarpdialog() {
        pdialog = ProgressDialog.show(this, "", "Sending Mail...", true);
        RegistrarActivity.RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }
    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(direccionCorreo));

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destintatarioCorreo));

                message.setSubject(subject);


                message.setContent(getBodyMSG(NombreCompletoString,
                        "Con el fin de ayudar a mantener la seguridad de tu cuenta de MiTouch, por favor, verifica tu dirección de email.",
                        "Su código de verificación es: " + random,
                        "Verificar tu dirección de email te permitirá: aprovecharte de la seguridad de MiTouch, cambiar los datos personales de tu cuenta de MiTouch, recibir notificaciones de MiTouch, recuperar el acceso a tu cuenta de MiTouch en caso de que lo pierdas u olvides tu contraseña.",
                        "Gracias por ayudarnos a mantener la seguridad de tu cuenta."), "text/html; charset=utf-8");

                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pdialog.dismiss();
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
        }
    }
    private static String getBodyMSG(String nombreUsuario,String parrafo1,String parrafo2,String parrafo3,String parrafo4) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><html><head><meta content=\"text/html;charset=UTF-8\" http-equiv=\"Content-Type\"><style media=\"all\" type=\"text/css\">td, p, h1, h3, a {font-family: Helvetica, Arial, sans-serif;}</style></head><body LINK=\"#00D861\" ALINK=\"#00D861\" VLINK=\"#00D861\" TEXT=\"#00D861\" style=\"font-family: Helvetica, Arial, sans-serif; font-size: 14px; color: #00D861;\" ><table style=\"width: 538px; background-color: #000000;\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"height: 65px; background-color: #000000; border-bottom: 1px solid #000000; padding: 0px;\">              <img src=\"https://s10.postimg.org/824eha4yh/email_header_logo.png\" width=\"538\" height=\"65\" alt=\"MiTouch\">        </td></tr><tr><td bgcolor=\"#222222\"><table width=\"470\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding-left: 5px; padding-right: 5px; padding-bottom: 10px;\"><tr bgcolor=\"#222222\"><td style=\"padding-top: 32px; padding-bottom: 16px;\"><span style=\"font-size: 24px; color: #009F3D; font-family: Arial, Helvetica, sans-serif; font-weight: bold;\">Hola "
                + nombreUsuario + ",</span><br></td></tr><tr bgcolor=\"#111111\"><td style=\"padding: 20px; font-size: 12px; line-height: 17px; color: #00D861; font-family: Arial, Helvetica, sans-serif;\"><p>" +
                parrafo1 + " " + parrafo2 + "</p></td></tr><tr><td style=\"padding-top: 16px; font-size: 12px; line-height: 17px; color: #006D2D; font-family: Arial, Helvetica, sans-serif;\"><p>" +
                parrafo3 + "</p></td></tr><tr><td style=\"font-size: 12px; color: #006D2D; padding-top: 16px; padding-bottom: 60px;\"><p>" +
                parrafo4 + "</br></br>El equipo del Soporte de MiTouch</br><br></td></tr></table></td></tr><tr><td bgcolor=\"#000000\"><table width=\"460\" height=\"55\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"><tr valign=\"top\"><td width=\"110\"><a href=\"http://www.valvesoftware.com/\" target=\"_blank\" style=\"color: #8B8B8B; font-size: 10px; font-family: Trebuchet MS, Verdana, Arial, Helvetica, sans-serif; text-transform: uppercase;><span style=\"font-size: 10px; color: #8B8B8B; font-family: Trebuchet MS,Verdana,Arial,Helvetica,sans-serif; text-transform: uppercase\"><img src=\"https://s21.postimg.org/4he32aalj/nombre_Logo.png\" alt=\"MiTouch Argentina\" width=\"122\" height=\"41\" hspace=\"0\" vspace=\"0\" border=\"0\" align=\"top\"></a></td><td width=\"350\" valign=\"top\"><span style=\"color: #006D2D; font-size: 9px; font-family: Verdana, Arial, Helvetica, sans-serif;\">© MiTouch. Todos los derechos reservados. Todas las marcas registradas pertenecen a sus respectivos dueños en Argentina y otros países.</span></td></tr></table></td></tr></table></body></html>";
    }


}
