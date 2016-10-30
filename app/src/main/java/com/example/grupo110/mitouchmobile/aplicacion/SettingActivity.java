package com.example.grupo110.mitouchmobile.aplicacion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.envioEmail.EmailIdentifierGenerator;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;
import com.example.grupo110.mitouchmobile.aplicacion.SettingActivity;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import static com.example.grupo110.mitouchmobile.R.id.CustomViewEmail;

public class SettingActivity extends AppCompatActivity {

    /************ Variables validacion largo usuario *********************/
    private final static int  USUARIOMINIMO = 6;
    private final static int  USUARIOMAXIMO = 15;

    private final static int  NOMBRECOMPLETOMINIMO = 5;
    private final static int  NOMBRECOMPLETOMAXIMO = 51;

    /************ FIN Variables validacion largo usuario y contraseña*********************/

    /***************** Variables Layout ***************************/
    private Button cambiarContraseña;
    private Button ActualizarBasedeDatos;
    private EditText nombreUsuario;
    private EditText nombreCompleto;
    private EditText direccionEmail;
    private EditText codigoVerificacion;
    private TextInputLayout CustomViewEmail;
    private ImageButton imagenEditUsuario;
    private ImageButton imagenEditNombre;
    private ImageButton imagenEditEmail;
    private ImageButton imagenlvExp;

    /***************** FIN Variables Layout ***************************/

    /************ Variables Expandable List*********************/
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    List<String> grupodeUsuarioid;
    /************ FIN Variables Expandable List*********************/

    /************ Variables Envio del mail *********************/
    Session session = null;
    ProgressDialog pdialog = null;

    // Credenciales de usuario
    private static String direccionCorreo = "grupo110unlam@gmail.com";   // Dirección de correo origen
    private static String contrasenyaCorreo = "mitouch110";                 // Contraseña del correo electronico origen
    private static String destintatarioCorreo; // Dirección de correo destino
    String subject= "Tu cuenta de MiTouch: verificación de la dirección de email"; // Asunto del mail
    private String random;
    /************ FIN Variables Envio del mail *********************/

    String usu_nombre_usuario;
    String usu_nombre_completo;
    String usu_mail;
    int id_usuario;

    String grupoUsuario=null;

    private String NombreCompletoString;
    /************ Variables para las validaciones *********************/
    private boolean nombreValido = true;
    private boolean nombreCompletoValido = true;
    private boolean codigoValido = true;
    private boolean emailValido = true;
    private Context context;
    private boolean aux=false;
    private boolean aux2=false;
    private boolean aux3=false;

    /************ FIN Variables para las validaciones *********************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        id_usuario = getIntent().getExtras().getInt("id");
        context=this;
        LlenarCampos();
        AgregarImagenes();
        imagenEditUsuario.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                nombreUsuario.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
                puedesEditar(imagenEditUsuario,"Nombre Usuario");
                aux=true;

            }
        });

        imagenEditNombre.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                nombreCompleto.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
                puedesEditar(imagenEditNombre,"Nombre Completo");
                aux2=true;

            }
        });

        imagenEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                direccionEmail.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
                puedesEditar(imagenEditEmail,"Direccion de email");
                codigoVerificacion.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
                codigoVerificacion.setVisibility(View.VISIBLE);
                CustomViewEmail.setVisibility(View.VISIBLE);
                aux3=true;
            }
        });

        imagenlvExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidarSolicitudGrupoDeUsuario()) {
                    enviarSolicitudGrupo();
                    prepareListData();
                    listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                    // setting list adapter
                    expListView.setAdapter(listAdapter);
                    imagenlvExp.setVisibility(View.INVISIBLE);

                }



            }
        });

        nombreUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(aux && !nombreUsuario.getText().toString().equals(usu_nombre_usuario) ) {
                        boolean respuesta = validarUsuario(nombreUsuario.getText().toString());
                        System.out.println("El usuario esta: " + respuesta);
                        insertarImagenNombreUsuario(respuesta);
                    }
                    if (!aux2) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(nombreUsuario.getWindowToken(), 0);
                    }
                }

            }
        });

        nombreCompleto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!nombreCompleto.getText().toString().equals(usu_nombre_completo)) {
                        NombreCompletoString = nombreCompleto.getText().toString();
                        if (validarNombreCompleto(NombreCompletoString))
                            insertarImagenNombreCompleto(true);
                        else
                            insertarImagenNombreCompleto(false);
                    }
                    if(!aux3) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(nombreUsuario.getWindowToken(), 0);
                    }
                }

            }
        });

        direccionEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(nombreValido  && !direccionEmail.getText().toString().equals(usu_mail) && !destintatarioCorreo.equals(direccionEmail.getText().toString()))
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
                            Toast toast = Toast.makeText(getApplicationContext(),"el nombre de usuario no es valido o se a modificado",Toast.LENGTH_LONG);
                            toast.show();
                            direccionEmail.setText("");
                            codigoVerificacion.setText("");
                            codigoValido = false;
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(direccionEmail.getWindowToken(), 0);
                            direccionEmail.clearFocus();
                        }

                }
                if (hasFocus) {
                    codigoVerificacion.setText("");
                }

            }
        });


        codigoVerificacion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    try {
                        if (random.equals(codigoVerificacion.getText().toString())) {
                            Toast toast = Toast.makeText(getApplicationContext(), "El codigo de verificacion es correcto", Toast.LENGTH_LONG);
                            toast.show();
                            codigoValido = true;
                            insertarImagenEmail(true);

                        } else {
                            insertarImagenEmail(false);
                            Toast toast = Toast.makeText(getApplicationContext(), "El codigo de verificacion es incorrecto", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }catch (Exception e){System.out.println("Error: "+e);}
                }
            }
        });




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        cambiarContraseña =(Button)findViewById(R.id.buttonAceptarCambios);
        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingActivity.this, CambiarPasswordActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });


        ActualizarBasedeDatos =(Button)findViewById(R.id.buttonActualizarDatos);
        ActualizarBasedeDatos.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                actualizarBasedeDatos();

            }
        });

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExpNuevoGrupoSetting);
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
                imagenlvExp.setVisibility(View.VISIBLE);
                return false;
            }
        });

    }

    private void enviarSolicitudGrupo() {
        String comando;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat diahora = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String fechadiahora = diahora.format(c.getTime());
        PostgrestBD baseDeDatos = new PostgrestBD();

        try {
                comando = "INSERT INTO \"MiTouch\".t_solicitud_acceso (sol_id_usuario,sol_id_grupo,sol_fecha_hora,sol_fecha_hora_respuesta,sol_estado) VALUES (" + id_usuario + ",'" + BuscarGruposdeUsuarioenArray() + "','" + fechadiahora + "',null,0);";
                baseDeDatos.execute(comando);
            Toast toast = Toast.makeText(getApplicationContext(),"Solicitud enviada",Toast.LENGTH_LONG);
            toast.show();
            } catch (Exception e) {
            }
    }

    private void puedesEditar(ImageButton imagenEditUsuario, String s) {
        imagenEditUsuario.setVisibility(View.INVISIBLE);
        Toast toast = Toast.makeText(getApplicationContext(),"Ya podes editar tu: "+s,Toast.LENGTH_LONG);
        toast.show();
    }


    private void actualizarBasedeDatos() {
        String comando;
        if( validaciones()) {
            comando = "UPDATE \"MiTouch\".t_usuarios SET usu_nombre_usuario = '" + nombreUsuario.getText().toString() + "', usu_nombre_completo = '" + nombreCompleto.getText().toString() + "', usu_mail = '" + direccionEmail.getText().toString() + "'" + " WHERE usu_id = " + id_usuario + ";";
            PostgrestBD baseDeDatos = new PostgrestBD();
            baseDeDatos.execute(comando);
        }
        if(grupodeUsuario!=null)
            if( ValidarSolicitudGrupoDeUsuario() ) {
                enviarSolicitudGrupo();
                finish(); // No se si deberia quedar en el menu o no!!
             }



    }


    private void AgregarImagenes() {
        String uri = "@drawable/edit";  // where myresource (without the extension) is the file
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        imagenEditUsuario= (ImageButton)findViewById(R.id.editarNombreUsuario);
        imagenEditNombre= (ImageButton)findViewById(R.id.editarNombreCompleto);
        imagenEditEmail= (ImageButton)findViewById(R.id.editaremailgmailSetting);
        imagenlvExp = (ImageButton) findViewById(R.id.lvExpNuevoGrupoSettingButton);

    }

    public void LlenarCampos()
    {
        buscarDatos();
        nombreUsuario =(EditText)findViewById(R.id.nombreUsuarioSetting);
        nombreCompleto =(EditText)findViewById(R.id.nombreCompletoSetting);
        direccionEmail =(EditText)findViewById(R.id.emailgmailSettings);
        codigoVerificacion = (EditText)findViewById(R.id.CodigoVerificacion_setting);
        CustomViewEmail = (TextInputLayout)findViewById(R.id.CustomViewCodigoVerificacion);

        // No permitir edición
        nombreUsuario.setKeyListener(null);
        nombreCompleto.setKeyListener(null);
        direccionEmail.setKeyListener(null);
        codigoVerificacion.setKeyListener(null);

        // Buscar en base de datos datos del usuario:
        nombreUsuario.setText(usu_nombre_usuario);
        nombreCompleto.setText(usu_nombre_completo);
        direccionEmail.setText(usu_mail);

    }

    public int buscarDatos() {
        String comando;

        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";";

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                usu_nombre_usuario = resultSet.getString("usu_nombre_usuario");
                usu_nombre_completo = resultSet.getString("usu_nombre_completo");
                usu_mail = resultSet.getString("usu_mail");
            }
        }catch(Exception e){System.out.println("Error");}
        return 0;
    }

    private boolean validarUsuario(String usuario) {
        Toast toast;
        if(USUARIOMINIMO>usuario.length())
        {
            toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser mayor que 5",Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        if(usuario.length()> USUARIOMAXIMO){
            toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser menor que 16",Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        for(int i = 0; i < usuario.length(); ++i) {
            char caracter = usuario.charAt(i);

            if(i == 0 && ! Character.isLetter(caracter))
            {
                toast = Toast.makeText(getApplicationContext(), "El primer caracter debe ser una letra", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }


            if(!Character.isLetterOrDigit(caracter) && caracter != '.') {
                System.out.println("El caracter es:" + caracter);
                toast = Toast.makeText(getApplicationContext(), "La contraseña no puede tener simbolos ni letras", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }

        String comando;
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario='" + usuario + "';";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            if (resultSet.next()){
                toast = Toast.makeText(getApplicationContext(),"El nombre de usuario no esta disponible",Toast.LENGTH_LONG);
                toast.show();
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return true;
    }


    private void prepareListData() {
        System.out.println("preparando lista");
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        // Adding child data
        listDataHeader.add("Solicitar Acceso a Grupo de usuario..");
        // Adding child data
        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioid = new ArrayList<>();
        BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
    }


    private void BuscarGruposdeUsuario(List<String> grupodeUsuario,List<String> grupodeUsuarioid) {
        String comando;
        comando = "SELECT gru_id,gru_nombre FROM  \"MiTouch\".t_grupos;";
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

    private boolean ValidarSolicitudGrupoDeUsuario() {
        String comando;
        PostgrestBD baseDeDatos = new PostgrestBD();
        comando = "SELECT * FROM \"MiTouch\".t_solicitud_acceso WHERE sol_id_usuario="+id_usuario+" AND " +
                "sol_id_grupo="+BuscarGruposdeUsuarioenArray()+";";
        ResultSet resultSet = baseDeDatos.execute(comando);

        try {
            while (resultSet.next()) {
                if( resultSet.getInt("sol_estado") == 0 )
                {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "El acceso ya esta pendiente", Toast.LENGTH_SHORT);
                    toast2.show();
                    return false;
                }else if( resultSet.getInt("sol_estado") == 1)
                {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "Ya tiene acceso al grupo de usuario", Toast.LENGTH_SHORT);
                    toast2.show();
                    return false;
                }
            }
            listDataChild.put(listDataHeader.get(0), grupodeUsuario); // Header, Child data
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario: " + e );
        }

        return true;
    }

    private String BuscarGruposdeUsuarioenArray() {
        try{
            return grupodeUsuarioid.get(grupodeUsuario.indexOf(grupoUsuario));
        }catch (Exception e){
            return null;
        }
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
*   true si el email existe
*   false si el mail no existe
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
    /*
    * Si el nombre de usuario no esta en la bd y
    * Si el mail esta escrito correctamente y
    * Si el mail no esta asociado a otro usuario
    * => return true ... actualizar!
    *
     */


    private boolean validaciones() {

        if(nombreValido)
            if(nombreCompletoValido)
                if(emailValido )
                    if(codigoValido)
                        return true;
                    else
                        print("El codigo no es valido");
                else
                    print("El email no es valido");
            else
                print("El nombre no es valido");
        else
            print("El nombre de usuario no es valido");
        return false;
    }

    private boolean validarNombreCompleto(String cadena) {
        boolean nombreVa=false;
        nombreCompletoValido = nombreVa;
        Toast toast;

        if(cadena.length()< NOMBRECOMPLETOMINIMO  ){
            toast = Toast.makeText(getApplicationContext(), "El nombre de usuario debe tener 6 caracteres minimamente", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(cadena.length() > NOMBRECOMPLETOMAXIMO){
            toast = Toast.makeText(getApplicationContext(), "El nombre de usuario debe tener menos de 50 caracteres", Toast.LENGTH_SHORT);
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
        nombreCompletoValido = true;
        return nombreVa;
    }

    private void print(String s) {
        Toast toast = Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG);
        toast.show();
    }


    private void insertarImagenNombreUsuario(boolean respuesta) {
        ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreUsuarioSetting);

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            nombreValido = false;
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            nombreValido = true;
        }
    }

    private void insertarImagenNombreCompleto(boolean respuesta) {
        ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreCompletoSetting);

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            emailValido = false;
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            emailValido = true;
        }
    }

    private void insertarImagenEmail(boolean respuesta) {
        ImageView imagen= (ImageView)findViewById(R.id.ImagenViewdireccionEmailSetting);

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            emailValido = false;
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            imagen.setVisibility(View.VISIBLE);
            emailValido = true;
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

    private void iniciarpdialog() {
        pdialog = ProgressDialog.show(this, "", "Sending Mail...", true);
        SettingActivity.RetreiveFeedTask task = new RetreiveFeedTask();
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
