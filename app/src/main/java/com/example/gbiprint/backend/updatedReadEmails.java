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

import com.starmicronics.starioextension.StarIoExtManager;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;

import static com.starmicronics.starioextension.StarIoExt.Emulation.StarGraphic;

public class updatedReadEmails {

    public enum Result {
        Success,
        ErrorUnknown,
        ErrorOpenPort,
        ErrorBeginCheckedBlock,
        ErrorEndCheckedBlock,
        ErrorWritePort,
        ErrorReadPort,
    }

    public static void runMain (Activity activity, Context context) throws IOException {
        System.out.println("WE ARE IN THE MAIN METHOD");
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try {

            for(int i = 0; i < 999999; i++){
                Session session = Session.getDefaultInstance(props, null);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Store store = session.getStore("imaps");
                store.connect("imap.gmail.com", "gbidevilspizzeria@gmail.com", "DevilsP123");
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
                        }
                    }
                    else {
                        content = message.getContent().toString();
                    }

                    int index = content.indexOf("<img src");
                    if(index != -1){
                        content = content.substring(0, index);
                    }
                    content = content.replaceAll("<br>", "\n");
                    content = content.replaceAll("\\<[^>]*>","");

                    TextToGraphics.foo(content,context);
                    String[] array = context.fileList();
                    System.out.println(array);

                    File f =new File(context.getFilesDir(), "data.png");
                    FileInputStream fis = new FileInputStream(f);
                    Bitmap b = BitmapFactory.decodeStream(fis);
                    fis.close();
                    ImageView img=(ImageView)activity.findViewById(R.id.imageView);
                    img.setImageBitmap(b);

                    String input = message.getContent().toString();

//                    // Create the print job
//                    DocPrintJob job = service.createPrintJob();
//                    //Create the Doc. You can pass set of attributes(type of PrintRequestAttributeSet) as the
//                    //3rd parameter specifying the page setup, orientation, no. of copies, etc instead of null.
//                    File file = new File("order" + h + "_2.txt");
//                    InputStream is = new BufferedInputStream(new FileInputStream(file));
//                    Doc doc = new SimpleDoc(is, flavor, null);
//
//                    int numCopies = 2;
//                    // set up the attributes
//                    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//                    attributes.add(new Copies(numCopies));
//
//                    //Order to print, (can pass attributes instead of null)
//                    try {
//                        job.print(doc, attributes);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                }

//                Folder trash = store.getFolder("[Gmail]/Old Orders");
//                for(Message message:messages) {
//                    inbox.copyMessages(new Message[] {message}, trash);
//                }


                try{
                    Thread.sleep(30000);
                }
                catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                store.close();
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * Creates the
     * @param bitImage
     */
    public void print(Bitmap bitImage, Context context) throws StarIOPortException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarGraphic);
        builder.beginDocument();
        builder.append(data);
        builder.append((byte) 0x0a);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();



        //get the port
        StarIOPort port = null;
        port = StarIOPort.getPort("TCP:192.168.1.130", "", 10000, context);

        // Print end monitoring -Start
        StarPrinterStatus status = port.beginCheckedBlock();


        //this is what actually sends the print request
        Result res = sendCommands(builder.getCommands(), port, context);
        System.out.println(res);
    }



    /**
     * This sends a command to the printer and receives a response of status back.
     * @param commands
     * @param port
     * @param context
     * @return
     */
    public static Result sendCommands(byte[] commands, StarIOPort port, Context context) {

        Result result = Result.ErrorUnknown;

        try {
            if (port == null) {
                result = Result.ErrorOpenPort;
                return result;
            }

            StarPrinterStatus status;

            result = Result.ErrorBeginCheckedBlock;

            status = port.beginCheckedBlock();

            if (status.offline) {
                throw new StarIOPortException("A printer is offline");
            }

            result = Result.ErrorWritePort;

            port.writePort(commands, 0, commands.length);

            result = Result.ErrorEndCheckedBlock;

            port.setEndCheckedBlockTimeoutMillis(30000); // 30000mS!!!

            status = port.endCheckedBlock();

            if (status.coverOpen) {
                throw new StarIOPortException("Printer cover is open");
            }
            else if (status.receiptPaperEmpty) {
                throw new StarIOPortException("Receipt paper is empty");
            }
            else if (status.offline) {
                throw new StarIOPortException("Printer is offline");
            }

            result = Result.Success;
        } catch (StarIOPortException e) {
            System.out.println("There is an error with the print.");
        }

        return result;
    }
}