package com.example.gbiprint.backend;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.StrictMode;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.example.gbiprint.R;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;

import static com.starmicronics.starioextension.StarIoExt.Emulation.StarGraphic;

public class ReadEmails {

    public enum Result {
        Success,
        ErrorUnknown,
        ErrorOpenPort,
        ErrorBeginCheckedBlock,
        ErrorEndCheckedBlock,
        ErrorWritePort,
        ErrorReadPort,
    }

    public static void readEmails(Activity activity, Context context) throws IOException {
        System.out.println("WE ARE IN THE MAIN METHOD");
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "gbidevilspizzeria@gmail.com", "DevilsP123");

            for(int i = 0; i < 999999; i++){

                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_ONLY);
                Message messages[] = inbox.getMessages();

                for(Message message:messages) {
                    System.out.println("New Message ");
                    String contentType = message.getContentType();
                    String content="";

                    if (contentType.contains("multipart")) {
                        Multipart multiPart = (Multipart) message.getContent();
                        int numberOfParts = multiPart.getCount();
                        for (int partCount = 0; partCount < numberOfParts; partCount++) {
                            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                            content = part.getContent().toString();
                            System.out.println(content);
                        }
                    }
                    else {
                        content = message.getContent().toString();
                        System.out.println(content);
                    }

                    int index = content.indexOf("<img src");
                    if(index != -1){
                        content = content.substring(0, index);
                    }
                    content = content.replaceAll("<br>", "\n");
                    content = content.replaceAll("\\<[^>]*>","");

                    TextToGraphics.convert(content,context);
                    String[] array = context.fileList();


                    //gets file from internal storage and converts to bits
                    File f =new File(context.getExternalFilesDir(null), "data.png");
                    FileInputStream fis = new FileInputStream(f);
                    Bitmap b = BitmapFactory.decodeStream(fis);
                    fis.close();
                    TextView tv = activity.findViewById(R.id.textView);
                    tv.setText(content);

//                    Calls the print method below to print to star printer
                    StarPrinterFunctions.print(b,context);
                    StarPrinterFunctions.print(b,context);

                }
                Folder trash = store.getFolder("[Gmail]/Trash");
                for(Message message:messages) {
                    inbox.copyMessages(new Message[] {message}, trash);
                }
////
                try{
                    Thread.sleep(30000);
                }
                catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}