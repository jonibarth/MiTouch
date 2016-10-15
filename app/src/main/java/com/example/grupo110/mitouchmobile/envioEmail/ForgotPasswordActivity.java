package com.example.grupo110.mitouchmobile.envioEmail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.aplicacion.LoginActivity;

import java.util.Properties;

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
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {


    Session session = null;
    ProgressDialog pdialog = null;
    EditText reciep, sub, msg;



    // Credenciales de usuario
    private static String direccionCorreo = "grupo110unlam@gmail.com";   // Dirección de correo
    private static String contrasenyaCorreo = "mitouch110";                 // Contraseña

    // Correo al que enviaremos el mensaje
    private static String destintatarioCorreo = "alan.lopez1991@gmail.com";

    String subject= "Reestablecer contraseña";
    String textMessage ="Bobo estas programando ??";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
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

        pdialog = ProgressDialog.show(this, "", "Sending Mail...", true);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(siguiente);
                finish();

            }
        });
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(direccionCorreo));

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destintatarioCorreo));

                message.setSubject(subject);

                message.setContent(textMessage, "text/html");
                message.setText(textMessage);
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

}
