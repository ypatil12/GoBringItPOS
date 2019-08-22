package com.example.gbiprint.backend;

import android.content.Context;
import android.graphics.Bitmap;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import java.io.ByteArrayOutputStream;

import static com.starmicronics.starioextension.StarIoExt.Emulation.StarGraphic;

/**
 * Handles the Star SDK functionality.
 *
 *
 */
public class StarPrinterFunctions {

    /**
     * Creates the
     * @param bitImage
     */
    public static void print(Bitmap bitImage, Context context) throws StarIOPortException {

        //converts the bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        //Builds the command with the data we have
        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarGraphic);
        builder.beginDocument();
        builder.append(data);
        builder.append((byte) 0x0a);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        //get the port
        StarIOPort port = null;

        //todo: input legitimate port name
        port = StarIOPort.getPort("TCP:192.168.1.243", "", 10000, context);

        //this is what actually sends the print request
        ReadEmails.Result res = sendCommands(builder.getCommands(), port, context);
        System.out.println(res);
    }
    /**
     * This sends a command to the printer and receives a response of status back.
     * @param commands
     * @param port
     * @param context
     * @return
     */
    private static ReadEmails.Result sendCommands(byte[] commands, StarIOPort port, Context context) {

        ReadEmails.Result result = ReadEmails.Result.ErrorUnknown;

        try {
            if (port == null) {
                result = ReadEmails.Result.ErrorOpenPort;
                return result;
            }

            StarPrinterStatus status;
            result = ReadEmails.Result.ErrorBeginCheckedBlock;
            status = port.beginCheckedBlock();
            if (status.offline) {
                throw new StarIOPortException("A printer is offline");
            }
            result = ReadEmails.Result.ErrorWritePort;

            port.writePort(commands, 0, commands.length);

            result = ReadEmails.Result.ErrorEndCheckedBlock;

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

            result = ReadEmails.Result.Success;
        } catch (StarIOPortException e) {
            System.out.println("There is an error with the print.");
        }

        return result;
    }
}
