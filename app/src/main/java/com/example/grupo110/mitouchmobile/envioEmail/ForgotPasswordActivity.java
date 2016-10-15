package com.example.grupo110.mitouchmobile.envioEmail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.aplicacion.LoginActivity;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.sql.ResultSet;
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
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {


    Session session = null;
    ProgressDialog pdialog = null;

    // Credenciales de usuario
    private static String direccionCorreo = "grupo110unlam@gmail.com";   // Dirección de correo
    private static String contrasenyaCorreo = "mitouch110";                 // Contraseña

    // Correo al que enviaremos el mensaje
    private static String destintatarioCorreo = "jonathan.barth05@gmail.com";
    String subject= "Tu cuenta de MiTouch: verificación de la dirección de email";
    String email;

    // Layout
    private TextInputLayout textinputlayout;
    private Button restablecerContraseña;
    private Button aceptarCodigoDeVerificacion;
    private EditText emailGmail;
    private EditText codigoDeVerificacion;
    private String usuario;

    private Context context;
    private String random;
    private int idUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));

        restablecerContraseña = (Button) findViewById(R.id.email_restablecer_contraseña_button);
        emailGmail = (EditText) findViewById(R.id.email_restablecer_contraseña_editText);
        aceptarCodigoDeVerificacion = (Button) findViewById(R.id.aceptar_restablecer_contraseña_button);
        codigoDeVerificacion = (EditText) findViewById(R.id.codigoVerificacion_restablecer_contraseña_editText);
        textinputlayout = (TextInputLayout) findViewById(R.id.CodigoVerificacion_login);


        context = getApplicationContext();
        EmailIdentifierGenerator randomGenerator = new EmailIdentifierGenerator();
        random = randomGenerator.nextSessionId();
        System.out.println("El random es: " + random);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(siguiente);
                finish();
            }
        });

        /*
        * Verifico que el mail sea un mail correcto
        * Busco que el mail exista en la bd
        * Envio el mail
         */
        restablecerContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailGmail.getText().toString();
                System.out.println("el mail es:" + email);
                if(validarEmail(email))
                    if(BuscaremailenBD(email))
                    {
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
                        textinputlayout.setVisibility(View.VISIBLE);
                        codigoDeVerificacion.setVisibility(View.VISIBLE);
                        aceptarCodigoDeVerificacion.setVisibility(View.VISIBLE);
                    }
            }
        });
        /*
        * Verifico que el codigo que ingrese sea correcto
        * Si es correcto ...
         */
        aceptarCodigoDeVerificacion.setOnClickListener(new View.OnClickListener() {
            Toast toast;
            @Override
            public void onClick(View v) {
                if (codigoDeVerificacion.getText().toString().equals(random)) {
                    ActualizarUsuario(random);
                    toast = Toast.makeText(getApplicationContext(), "la contraseña fue restablecida, ingrese el codigo de verificación", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                } else {
                    toast = Toast.makeText(getApplicationContext(), "El codigo de verificación es invalido", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void iniciarpdialog() {
        pdialog = ProgressDialog.show(this, "", "Sending Mail...", true);
        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    /*
* Actualizar la contraseña en la Base de Datos
*/
    private void ActualizarUsuario(String nuevaContraseña) {
        String comando;
        comando ="UPDATE \"MiTouch\".t_usuarios SET usu_password = '"+nuevaContraseña+"' WHERE usu_id = " + idUsuario + ";";
        PostgrestBD baseDeDatos = new PostgrestBD();
        baseDeDatos.execute(comando);
    }
    /*

       /*
    * Verifico que el mail sea un mail de gmail
     */

    public boolean validarEmail(String email) {
        Toast toast;
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@gmail.com");

        Matcher mather = pattern.matcher(email);
        if (mather.find()) {
            toast = Toast.makeText(getApplicationContext(), "El email ingresado es válido.", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else {
            toast = Toast.makeText(getApplicationContext(), "El email ingresado es inválido.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

    }

    private boolean BuscaremailenBD(String email) {
            String comando;
            comando =  "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_mail='"+email+"';";
            PostgrestBD baseDeDatos = new PostgrestBD();
            ResultSet resultSet = baseDeDatos.execute(comando);
            try {
                while (resultSet.next()) {
                    usuario = resultSet.getString("usu_nombre_usuario");
                    idUsuario = resultSet.getInt("usu_id");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error busqyeda grupos de usuario: " + e );
            }
        return false;
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(direccionCorreo));

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destintatarioCorreo));

                message.setSubject(subject);

                message.setContent(getBodyMSG(usuario,
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
